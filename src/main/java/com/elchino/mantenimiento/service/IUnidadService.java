package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dto.UnidadDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Unidad;

import java.util.List;

/**
 * Interfaz de lógica de negocio para la gestión de unidades de transporte
 * y auditoría automática de cambios de estado (Implementa CUS02, CUS03 y QA-05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IUnidadService {

    /**
     * Lista todas las unidades de transporte de la flota.
     *
     * @return lista de {@link Unidad}.
     */
    List<Unidad> listarTodas();

    /**
     * Lista las unidades filtradas por su estado operativo.
     *
     * @param estado estado operativo ({@code OPERATIVA}, {@code EN_MANTENIMIENTO}, {@code FUERA_DE_SERVICIO}).
     * @return lista de unidades filtradas.
     */
    List<Unidad> listarPorEstado(String estado);

    /**
     * Obtiene una unidad por su identificador primario.
     *
     * @param idUnidad ID de la unidad.
     * @return entidad {@link Unidad}.
     * @throws ResourceNotFoundException si la unidad no existe.
     */
    Unidad obtenerPorId(Integer idUnidad);

    /**
     * Registra una nueva unidad en la flota validando unicidad de placa y código interno,
     * y generando su primer registro de trazabilidad en {@code historial_mantenimiento} (CUS02 / QA-05).
     *
     * @param dto             datos de la nueva unidad.
     * @param usernameEjecutor nombre de usuario que ejecuta la operación.
     * @return entidad {@link Unidad} registrada.
     * @throws BusinessException si ya existe otra unidad con el mismo código interno o placa.
     */
    Unidad registrarUnidad(UnidadDTO dto, String usernameEjecutor);

    /**
     * Actualiza los datos técnicos de una unidad y audita cualquier cambio de estado en el historial (CUS03 / QA-05).
     *
     * @param idUnidad         ID de la unidad a modificar.
     * @param dto              nuevos datos de la unidad.
     * @param usernameEjecutor usuario que realiza la modificación.
     * @return entidad {@link Unidad} actualizada.
     * @throws ResourceNotFoundException si la unidad no existe.
     */
    Unidad actualizarUnidad(Integer idUnidad, UnidadDTO dto, String usernameEjecutor);

    /**
     * Cambia el estado operativo de una unidad y registra automáticamente una fila
     * en {@code historial_mantenimiento} para trazabilidad completa (CUS03, CUS10, QA-05).
     *
     * @param idUnidad         ID de la unidad.
     * @param nuevoEstado      nuevo estado ({@code OPERATIVA}, {@code EN_MANTENIMIENTO}, {@code FUERA_DE_SERVICIO}).
     * @param motivo           motivo o descripción del cambio de estado.
     * @param idMantenimiento  ID del mantenimiento relacionado (puede ser {@code null}).
     * @param usernameEjecutor usuario que origina el cambio.
     * @throws ResourceNotFoundException si la unidad no existe.
     */
    void cambiarEstadoConAuditoria(Integer idUnidad, String nuevoEstado, String motivo, Integer idMantenimiento, String usernameEjecutor);
}
