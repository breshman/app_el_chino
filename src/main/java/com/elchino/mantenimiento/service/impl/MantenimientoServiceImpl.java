package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IFallaDAO;
import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.dao.IMantenimientoDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.MantenimientoDTO;
import com.elchino.mantenimiento.exception.HorarioConflictoException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Falla;
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
 *
 * <p>Valida estrictamente que la fecha programada no sea pasada y que el técnico asignado
 * no tenga cruce de horario en otros mantenimientos {@code PROGRAMADO} o {@code EN_PROCESO}.</p>
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Service
public class MantenimientoServiceImpl implements IMantenimientoService {

    private final IMantenimientoDAO mantenimientoDAO;
    private final IUnidadDAO unidadDAO;
    private final IUsuarioDAO usuarioDAO;
    private final IFallaDAO fallaDAO;
    private final IHistorialMantenimientoDAO historialDAO;

    /**
     * Constructor con inyección de dependencias DAO.
     *
     * @param mantenimientoDAO DAO de mantenimientos.
     * @param unidadDAO        DAO de unidades.
     * @param usuarioDAO       DAO de usuarios.
     * @param fallaDAO         DAO de fallas.
     * @param historialDAO     DAO de historial de mantenimiento.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Mantenimiento> listarTodos() {
        return mantenimientoDAO.listarTodos();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Mantenimiento> listarPendientesOEnProceso() {
        return mantenimientoDAO.listarPendientesOEnProceso();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mantenimiento obtenerPorId(Integer idMantenimiento) {
        return mantenimientoDAO.buscarPorId(idMantenimiento)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el mantenimiento con ID: " + idMantenimiento));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Mantenimiento programarMantenimiento(MantenimientoDTO dto, String usernameResponsable) {
        // Regla CUS06: Validar que la fecha programada no sea pasada (margen de 5 minutos)
        if (dto.getFechaProgramada() == null || dto.getFechaProgramada().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new HorarioConflictoException("No se puede programar un mantenimiento en una fecha/hora pasada.");
        }

        Unidad unidad = unidadDAO.buscarPorId(dto.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la unidad con ID: " + dto.getIdUnidad()));

        Usuario tecnico = usuarioDAO.buscarPorId(dto.getIdTecnico())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el técnico con ID: " + dto.getIdTecnico()));

        // Regla CUS06: Validar en el DAO que el técnico no tenga cruce de horario en mantenimientos PROGRAMADO o EN_PROCESO
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

        // Cambiar estado de la unidad a EN_MANTENIMIENTO y registrar en historial_mantenimiento (QA-05)
        String estadoAnterior = unidad.getEstado();
        unidadDAO.actualizarEstado(unidad.getIdUnidad(), "EN_MANTENIMIENTO");

        HistorialMantenimiento h = new HistorialMantenimiento();
        h.setIdUnidad(unidad.getIdUnidad());
        h.setIdMantenimiento(guardado.getIdMantenimiento());
        h.setIdUsuario(responsable.getIdUsuario());
        h.setTipoEvento("MANTENIMIENTO_PROGRAMADO");
        h.setEstadoAnteriorUnidad(estadoAnterior);
        h.setEstadoNuevoUnidad("EN_MANTENIMIENTO");
        h.setDescripcionEvento("Mantenimiento " + guardado.getTipoMantenimiento() +
                " programado con técnico " + tecnico.getNombreCompleto() + ": " + guardado.getDescripcionTrabajo());
        historialDAO.insertar(h);

        return guardado;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void iniciarMantenimiento(Integer idMantenimiento, String usernameEjecutor) {
        Mantenimiento m = obtenerPorId(idMantenimiento);
        mantenimientoDAO.actualizarEstadoYFechas(m.getIdMantenimiento(), "EN_PROCESO", LocalDateTime.now(), null);
    }
}
