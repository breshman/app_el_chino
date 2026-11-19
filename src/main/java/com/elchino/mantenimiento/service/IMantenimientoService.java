package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dto.MantenimientoDTO;
import com.elchino.mantenimiento.exception.HorarioConflictoException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Mantenimiento;

import java.util.List;

/**
 * Interfaz de lógica de negocio para la programación y control de órdenes
 * de mantenimiento preventivo y correctivo (Implementa CUS06 y CUS11).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IMantenimientoService {

    /**
     * Lista todas las órdenes de mantenimiento registradas.
     *
     * @return lista de {@link Mantenimiento}.
     */
    List<Mantenimiento> listarTodos();

    /**
     * Lista las órdenes de mantenimiento en estado {@code PROGRAMADO} o {@code EN_PROCESO} (CUS11).
     *
     * @return lista de mantenimientos pendientes o en ejecución.
     */
    List<Mantenimiento> listarPendientesOEnProceso();

    /**
     * Obtiene una orden de mantenimiento por su identificador único.
     *
     * @param idMantenimiento ID del mantenimiento.
     * @return entidad {@link Mantenimiento}.
     * @throws ResourceNotFoundException si el mantenimiento no existe.
     */
    Mantenimiento obtenerPorId(Integer idMantenimiento);

    /**
     * Programa una nueva orden de mantenimiento cumpliendo las validaciones críticas de CUS06:
     * <ul>
     *     <li>Verifica que la fecha programada no sea anterior al momento actual.</li>
     *     <li>Verifica en el DAO que el técnico asignado no tenga otro mantenimiento {@code PROGRAMADO}
     *     o {@code EN_PROCESO} en la misma ventana de fecha/hora.</li>
     *     <li>Si está vinculada a una falla, cambia el estado de la falla a {@code EN_ATENCION}.</li>
     *     <li>Cambia el estado de la unidad a {@code EN_MANTENIMIENTO} y registra en {@code historial_mantenimiento}.</li>
     * </ul>
     *
     * @param dto                 datos de programación de mantenimiento.
     * @param usernameResponsable usuario responsable que programa la orden.
     * @return entidad {@link Mantenimiento} creada.
     * @throws HorarioConflictoException si la fecha es pasada o el técnico tiene cruce de horario.
     * @throws ResourceNotFoundException si la unidad o el técnico no existen.
     */
    Mantenimiento programarMantenimiento(MantenimientoDTO dto, String usernameResponsable);

    /**
     * Inicia formalmente la ejecución de un mantenimiento cambiando su estado a {@code EN_PROCESO}.
     *
     * @param idMantenimiento  ID del mantenimiento.
     * @param usernameEjecutor usuario que inicia la orden.
     */
    void iniciarMantenimiento(Integer idMantenimiento, String usernameEjecutor);
}
