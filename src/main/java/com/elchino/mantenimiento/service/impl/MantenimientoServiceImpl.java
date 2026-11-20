package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IFallaDAO;
import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.dao.IMantenimientoDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.MantenimientoDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.exception.HorarioConflictoException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
import com.elchino.mantenimiento.model.Mantenimiento;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IMantenimientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación de la capa de servicio para la programación y gestión de órdenes
 * de mantenimiento preventivo y correctivo (Implementa CUS06 y CUS11).
 */
@Service
public class MantenimientoServiceImpl implements IMantenimientoService {

    private final IMantenimientoDAO mantenimientoDAO;
    private final IUnidadDAO unidadDAO;
    private final IUsuarioDAO usuarioDAO;
    private final IFallaDAO fallaDAO;
    private final IHistorialMantenimientoDAO historialDAO;

    public MantenimientoServiceImpl(IMantenimientoDAO mantenimientoDAO,
                                    IUnidadDAO unidadDAO,
                                    IUsuarioDAO usuarioDAO,
                                    IFallaDAO fallaDAO,
                                    IHistorialMantenimientoDAO historialDAO) {
        this.mantenimientoDAO = mantenimientoDAO;
        this.unidadDAO = unidadDAO;
        this.usuarioDAO = usuarioDAO;
        this.fallaDAO = fallaDAO;
        this.historialDAO = historialDAO;
    }

    @Override
    public List<Mantenimiento> listarTodos() {
        return mantenimientoDAO.listarTodos();
    }

    @Override
    public List<Mantenimiento> listarPendientesOEnProceso() {
        return mantenimientoDAO.listarPendientesOEnProceso();
    }

    @Override
    public Mantenimiento obtenerPorId(Integer idMantenimiento) {
        return mantenimientoDAO.buscarPorId(idMantenimiento)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el mantenimiento con ID: " + idMantenimiento));
    }

    @Override
    @Transactional
    public Mantenimiento programarMantenimiento(MantenimientoDTO dto, String usernameResponsable) {
        if (dto.getFechaProgramada() == null || dto.getFechaProgramada().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new HorarioConflictoException("No se puede programar un mantenimiento en una fecha/hora pasada.");
        }

        Unidad unidad = unidadDAO.buscarPorId(dto.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la unidad con ID: " + dto.getIdUnidad()));

        if ("FUERA_DE_SERVICIO".equalsIgnoreCase(unidad.getEstado())) {
            throw new BusinessException("No se puede programar mantenimiento a una unidad que está FUERA_DE_SERVICIO.");
        }

        Usuario tecnico = usuarioDAO.buscarPorId(dto.getIdTecnico())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el técnico con ID: " + dto.getIdTecnico()));

        if (mantenimientoDAO.existeCruceHorarioTecnico(tecnico.getIdUsuario(), dto.getFechaProgramada())) {
            throw new HorarioConflictoException("Conflicto de horario: El técnico " + tecnico.getNombreCompleto() +
                    " ya tiene otro mantenimiento PROGRAMADO o EN_PROCESO en la fecha/hora seleccionada.");
        }

        Usuario responsable = usuarioDAO.buscarPorUsername(usernameResponsable)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable no encontrado: " + usernameResponsable));

        Mantenimiento m = new Mantenimiento();
        m.setIdUnidad(unidad.getIdUnidad());
        m.setIdFalla(dto.getIdFalla());
        m.setIdTecnico(tecnico.getIdUsuario());
        m.setIdResponsable(responsable.getIdUsuario());
        m.setTipoMantenimiento(dto.getTipoMantenimiento());
        m.setFechaProgramada(dto.getFechaProgramada());
        m.setDescripcionTrabajo(dto.getDescripcionTrabajo().trim());
        m.setEstado("PROGRAMADO");

        Mantenimiento guardado = mantenimientoDAO.insertar(m);

        // Si está asociado a una falla, pasar la falla a estado EN_ATENCION
        if (dto.getIdFalla() != null) {
            fallaDAO.buscarPorId(dto.getIdFalla()).ifPresent(falla ->
                    fallaDAO.actualizarPrioridadYEstado(falla.getIdFalla(), falla.getPrioridad(), "EN_ATENCION")
            );
        }

        // La unidad conserva su estado actual hasta que el mantenimiento se inicie realmente
        HistorialMantenimiento h = new HistorialMantenimiento();
        h.setIdUnidad(unidad.getIdUnidad());
        h.setIdMantenimiento(guardado.getIdMantenimiento());
        h.setIdUsuario(responsable.getIdUsuario());
        h.setTipoEvento("MANTENIMIENTO_PROGRAMADO");
        h.setEstadoAnteriorUnidad(unidad.getEstado());
        h.setEstadoNuevoUnidad(unidad.getEstado());
        h.setDescripcionEvento("Mantenimiento " + guardado.getTipoMantenimiento() +
                " programado para " + guardado.getFechaProgramada() +
                " con técnico " + tecnico.getNombreCompleto() + ": " + guardado.getDescripcionTrabajo());
        historialDAO.insertar(h);

        return guardado;
    }

    @Override
    @Transactional
    public void iniciarMantenimiento(Integer idMantenimiento, String usernameEjecutor) {
        Mantenimiento m = obtenerPorId(idMantenimiento);

        // Impide reabrir mantenimientos FINALIZADOS, CANCELADOS o ya EN_PROCESO
        if (!"PROGRAMADO".equalsIgnoreCase(m.getEstado())) {
            throw new BusinessException("Solo se puede iniciar un mantenimiento en estado PROGRAMADO. Estado actual: " + m.getEstado());
        }

        Unidad unidad = unidadDAO.buscarPorId(m.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("Unidad asociada no encontrada"));
        Usuario ejecutor = usuarioDAO.buscarPorUsername(usernameEjecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + usernameEjecutor));

        mantenimientoDAO.actualizarEstadoYFechas(m.getIdMantenimiento(), "EN_PROCESO", LocalDateTime.now(), null);

        String estadoAnterior = unidad.getEstado();
        String estadoNuevo = "OPERATIVA".equalsIgnoreCase(estadoAnterior) ? "EN_MANTENIMIENTO" : estadoAnterior;
        if (!estadoAnterior.equalsIgnoreCase(estadoNuevo)) {
            unidadDAO.actualizarEstado(unidad.getIdUnidad(), estadoNuevo);
        }

        HistorialMantenimiento h = new HistorialMantenimiento();
        h.setIdUnidad(unidad.getIdUnidad());
        h.setIdMantenimiento(m.getIdMantenimiento());
        h.setIdUsuario(ejecutor.getIdUsuario());
        h.setTipoEvento("MANTENIMIENTO_INICIADO");
        h.setEstadoAnteriorUnidad(estadoAnterior);
        h.setEstadoNuevoUnidad(estadoNuevo);
        h.setDescripcionEvento("Mantenimiento #" + m.getIdMantenimiento() + " iniciado por " + ejecutor.getNombreCompleto());
        historialDAO.insertar(h);
    }
}