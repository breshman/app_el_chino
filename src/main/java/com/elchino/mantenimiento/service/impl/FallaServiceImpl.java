package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IFallaDAO;
import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.FallaDTO;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Falla;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IFallaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicio para la gestión y priorización de fallas (Implementa CUS05).
 *
 * <p>Reglas de negocio implementadas:</p>
 * <ul>
 *     <li>Si el usuario que reporta tiene rol {@code CONDUCTOR}, la falla nace obligatoriamente con estado {@code REPORTADA}.</li>
 *     <li>Si la prioridad asignada o actualizada es {@code ALTA} o {@code CRITICA}, la unidad afectada
 *     pasa automáticamente al estado {@code EN_MANTENIMIENTO} y se genera un evento en {@code historial_mantenimiento} (QA-05).</li>
 * </ul>
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Service
public class FallaServiceImpl implements IFallaService {

    private final IFallaDAO fallaDAO;
    private final IUnidadDAO unidadDAO;
    private final IUsuarioDAO usuarioDAO;
    private final IHistorialMantenimientoDAO historialDAO;

    /**
     * Constructor con inyección de dependencias DAO.
     *
     * @param fallaDAO     DAO de fallas.
     * @param unidadDAO    DAO de unidades.
     * @param usuarioDAO   DAO de usuarios.
     * @param historialDAO DAO de historial de mantenimiento.
     */
    public FallaServiceImpl(IFallaDAO fallaDAO,
                            IUnidadDAO unidadDAO,
                            IUsuarioDAO usuarioDAO,
                            IHistorialMantenimientoDAO historialDAO) {
        this.fallaDAO = fallaDAO;
        this.unidadDAO = unidadDAO;
        this.usuarioDAO = usuarioDAO;
        this.historialDAO = historialDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Falla> listarTodas() {
        return fallaDAO.listarTodas();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Falla> listarActivas() {
        return fallaDAO.listarActivas();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Falla obtenerPorId(Integer idFalla) {
        return fallaDAO.buscarPorId(idFalla)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la falla con ID: " + idFalla));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Falla registrarFalla(FallaDTO dto, String usernameReporta) {
        Unidad unidad = unidadDAO.buscarPorId(dto.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la unidad con ID: " + dto.getIdUnidad()));

        Usuario reportante = usuarioDAO.buscarPorUsername(usernameReporta)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario reportante no encontrado: " + usernameReporta));

        Falla falla = new Falla();
        falla.setIdUnidad(unidad.getIdUnidad());
        falla.setIdUsuarioReporta(reportante.getIdUsuario());
        falla.setIdInspeccion(dto.getIdInspeccion());
        falla.setTitulo(dto.getTitulo().trim());
        falla.setDescripcion(dto.getDescripcion().trim());
        falla.setSistemaAfectado(dto.getSistemaAfectado().trim());

        String prioridad = dto.getPrioridad() != null ? dto.getPrioridad() : "MEDIA";
        falla.setPrioridad(prioridad);
        // Regla CUS05: Toda falla reportada (especialmente por CONDUCTOR) nace con estado REPORTADA
        falla.setEstado("REPORTADA");

        Falla guardada = fallaDAO.insertar(falla);

        // Regla CUS05 + QA-05: Si la prioridad es ALTA o CRITICA, cambiar estado de la unidad a EN_MANTENIMIENTO
        aplicarCambioEstadoPorPrioridadAltaOCritica(unidad, prioridad, guardada.getTitulo(), reportante.getIdUsuario());

        return guardada;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Falla priorizarFalla(Integer idFalla, String nuevaPrioridad, String nuevoEstado, String usernameEjecutor) {
        Falla falla = obtenerPorId(idFalla);
        Unidad unidad = unidadDAO.buscarPorId(falla.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("Unidad asociada no encontrada"));
        Usuario ejecutor = usuarioDAO.buscarPorUsername(usernameEjecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado"));

        String prioridadFinal = nuevaPrioridad != null ? nuevaPrioridad : falla.getPrioridad();
        String estadoFinal = nuevoEstado != null ? nuevoEstado : falla.getEstado();

        fallaDAO.actualizarPrioridadYEstado(idFalla, prioridadFinal, estadoFinal);
        falla.setPrioridad(prioridadFinal);
        falla.setEstado(estadoFinal);

        // Regla CUS05: Si el RESPONSABLE_MANTENIMIENTO asigna prioridad ALTA o CRITICA, pasar unidad a EN_MANTENIMIENTO
        aplicarCambioEstadoPorPrioridadAltaOCritica(unidad, prioridadFinal, falla.getTitulo(), ejecutor.getIdUsuario());

        return falla;
    }

    /**
     * Evalúa si la prioridad es {@code ALTA} o {@code CRITICA} y traslada la unidad a {@code EN_MANTENIMIENTO}
     * insertando el registro de trazabilidad en {@code historial_mantenimiento} (CUS05 y QA-05).
     */
    private void aplicarCambioEstadoPorPrioridadAltaOCritica(Unidad unidad,
                                                             String prioridad,
                                                             String tituloFalla,
                                                             Integer idUsuario) {
        if ("ALTA".equalsIgnoreCase(prioridad) || "CRITICA".equalsIgnoreCase(prioridad)) {
            if (!"EN_MANTENIMIENTO".equalsIgnoreCase(unidad.getEstado())) {
                String estadoAnterior = unidad.getEstado();
                unidadDAO.actualizarEstado(unidad.getIdUnidad(), "EN_MANTENIMIENTO");

                HistorialMantenimiento h = new HistorialMantenimiento();
                h.setIdUnidad(unidad.getIdUnidad());
                h.setIdMantenimiento(null);
                h.setIdUsuario(idUsuario);
                h.setTipoEvento("FALLA_" + prioridad.toUpperCase());
                h.setEstadoAnteriorUnidad(estadoAnterior);
                h.setEstadoNuevoUnidad("EN_MANTENIMIENTO");
                h.setDescripcionEvento("Unidad " + unidad.getCodigoInterno() +
                        " pasa automáticamente a EN_MANTENIMIENTO por falla de prioridad " +
                        prioridad + ": " + tituloFalla);
                historialDAO.insertar(h);
            }
        }
    }
}
