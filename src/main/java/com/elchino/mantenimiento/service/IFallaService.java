package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dto.FallaDTO;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Falla;

import java.util.List;

/**
 * Interfaz de lógica de negocio para el reporte, categorización y priorización
 * de fallas mecánicas o eléctricas (Implementa CUS05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IFallaService {

    /**
     * Lista todas las fallas registradas en el sistema.
     *
     * @return lista de {@link Falla}.
     */
    List<Falla> listarTodas();

    /**
     * Lista las fallas pendientes o en atención ({@code REPORTADA}, {@code EN_ATENCION}).
     *
     * @return lista de fallas activas ordenadas por criticidad.
     */
    List<Falla> listarActivas();

    /**
     * Obtiene una falla por su identificador primario.
     *
     * @param idFalla ID de la falla.
     * @return entidad {@link Falla}.
     * @throws ResourceNotFoundException si la falla no existe.
     */
    Falla obtenerPorId(Integer idFalla);

    /**
     * Registra una nueva falla aplicando las reglas críticas de CUS05:
     * <ul>
     *     <li>Toda falla reportada por un {@code CONDUCTOR} nace con estado {@code REPORTADA}.</li>
     *     <li>Si la prioridad asignada es {@code ALTA} o {@code CRITICA}, cambia automáticamente el estado
     *     de la unidad a {@code EN_MANTENIMIENTO} y registra el evento en {@code historial_mantenimiento}.</li>
     * </ul>
     *
     * @param dto             datos de la falla a reportar.
     * @param usernameReporta usuario autenticado que reporta la incidencia.
     * @return entidad {@link Falla} creada.
     * @throws ResourceNotFoundException si la unidad o el usuario no existen.
     */
    Falla registrarFalla(FallaDTO dto, String usernameReporta);

    /**
     * Permite al {@code RESPONSABLE_MANTENIMIENTO} o {@code ADMINISTRADOR} recategorizar la prioridad
     * y estado de una falla. Si se eleva a {@code ALTA} o {@code CRITICA}, traslada la unidad
     * a {@code EN_MANTENIMIENTO} con auditoría en historial (CUS05).
     *
     * @param idFalla          ID de la falla a priorizar.
     * @param nuevaPrioridad   prioridad asignada ({@code BAJA}, {@code MEDIA}, {@code ALTA}, {@code CRITICA}).
     * @param nuevoEstado      estado asignado ({@code REPORTADA}, {@code EN_ATENCION}, {@code RESUELTA}, {@code CERRADA}).
     * @param usernameEjecutor usuario que realiza la priorización.
     * @return entidad {@link Falla} actualizada.
     * @throws ResourceNotFoundException si la falla no existe.
     */
    Falla priorizarFalla(Integer idFalla, String nuevaPrioridad, String nuevoEstado, String usernameEjecutor);
}
