package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IFallaDAO;
import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.FallaDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Falla;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IFallaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Implementación de la capa de servicio para la gestión y priorización de fallas (Implementa CUS05).
 */
@Service
public class FallaServiceImpl implements IFallaService {

    private static final Set<String> PRIORIDADES_VALIDAS = Set.of("BAJA", "MEDIA", "ALTA", "CRITICA");
    private static final Set<String> ESTADOS_VALIDOS = Set.of("REPORTADA", "EN_ATENCION", "RESUELTA", "CERRADA");

    private final IFallaDAO fallaDAO;
    private final IUnidadDAO unidadDAO;
    private final IUsuarioDAO usuarioDAO;
    private final IHistorialMantenimientoDAO historialDAO;

    public FallaServiceImpl(IFallaDAO fallaDAO,
                            IUnidadDAO unidadDAO,
                            IUsuarioDAO usuarioDAO,
                            IHistorialMantenimientoDAO historialDAO) {
        this.fallaDAO = fallaDAO;
        this.unidadDAO = unidadDAO;
        this.usuarioDAO = usuarioDAO;
        this.historialDAO = historialDAO;
    }

    @Override
    public List<Falla> listarTodas() {
        return fallaDAO.listarTodas();
    }

    @Override
    public List<Falla> listarActivas() {
        return fallaDAO.listarActivas();
    }

    @Override
    public Falla obtenerPorId(Integer idFalla) {
        return fallaDAO.buscarPorId(idFalla)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la falla con ID: " + idFalla));
    }

    @Override
    @Transactional
    public Falla registrarFalla(FallaDTO dto, String usernameReporta) {
        Unidad unidad = unidadDAO.buscarPorId(dto.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la unidad con ID: " + dto.getIdUnidad()));

        Usuario reportante = usuarioDAO.buscarPorUsername(usernameReporta)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario reportante no encontrado: " + usernameReporta));

        String prioridadSolicitada = dto.getPrioridad() != null ? dto.getPrioridad().trim().toUpperCase() : "MEDIA";
        if (!PRIORIDADES_VALIDAS.contains(prioridadSolicitada)) {
            throw new BusinessException("Prioridad de falla no válida: " + prioridadSolicitada);
        }

        // Regla de seguridad: Un CONDUCTOR reporta fallas con prioridad inicial máxima MEDIA
        // para que el Responsable de Mantenimiento evalúe si amerita sacar la unidad de ruta.
        String prioridadFinal = prioridadSolicitada;
        if ("CONDUCTOR".equalsIgnoreCase(reportante.getRol())
                && ("ALTA".equals(prioridadSolicitada) || "CRITICA".equals(prioridadSolicitada))) {
            prioridadFinal = "MEDIA";
        }

        Falla falla = new Falla();
        falla.setIdUnidad(unidad.getIdUnidad());
        falla.setIdUsuarioReporta(reportante.getIdUsuario());
        falla.setIdInspeccion(dto.getIdInspeccion());
        falla.setTitulo(dto.getTitulo().trim());
        falla.setDescripcion(dto.getDescripcion().trim());
        falla.setSistemaAfectado(dto.getSistemaAfectado().trim());
        falla.setPrioridad(prioridadFinal);
        falla.setEstado("REPORTADA");

        Falla guardada = fallaDAO.insertar(falla);

        // Solo cambia la unidad a EN_MANTENIMIENTO si estaba OPERATIVA y la prioridad es ALTA/CRITICA
        aplicarCambioEstadoPorPrioridadAltaOCritica(unidad, prioridadFinal, guardada.getTitulo(), reportante.getIdUsuario());

        return guardada;
    }

    @Override
    @Transactional
    public Falla priorizarFalla(Integer idFalla, String nuevaPrioridad, String nuevoEstado, String usernameEjecutor) {
        Falla falla = obtenerPorId(idFalla);
        Unidad unidad = unidadDAO.buscarPorId(falla.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("Unidad asociada no encontrada"));
        Usuario ejecutor = usuarioDAO.buscarPorUsername(usernameEjecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado"));

        String prioridadFinal = (nuevaPrioridad != null && !nuevaPrioridad.isBlank())
                ? nuevaPrioridad.trim().toUpperCase()
                : falla.getPrioridad();
        String estadoFinal = (nuevoEstado != null && !nuevoEstado.isBlank())
                ? nuevoEstado.trim().toUpperCase()
                : falla.getEstado();

        if (!PRIORIDADES_VALIDAS.contains(prioridadFinal)) {
            throw new BusinessException("Prioridad no válida: " + prioridadFinal);
        }
        if (!ESTADOS_VALIDOS.contains(estadoFinal)) {
            throw new BusinessException("Estado de falla no válido: " + estadoFinal);
        }

        fallaDAO.actualizarPrioridadYEstado(idFalla, prioridadFinal, estadoFinal);
        falla.setPrioridad(prioridadFinal);
        falla.setEstado(estadoFinal);

        aplicarCambioEstadoPorPrioridadAltaOCritica(unidad, prioridadFinal, falla.getTitulo(), ejecutor.getIdUsuario());

        return falla;
    }

    private void aplicarCambioEstadoPorPrioridadAltaOCritica(Unidad unidad,
                                                             String prioridad,
                                                             String tituloFalla,
                                                             Integer idUsuario) {
        if ("ALTA".equalsIgnoreCase(prioridad) || "CRITICA".equalsIgnoreCase(prioridad)) {
            // Solo se traslada a EN_MANTENIMIENTO si estaba OPERATIVA (no sobrescribe FUERA_DE_SERVICIO)
            if ("OPERATIVA".equalsIgnoreCase(unidad.getEstado())) {
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