package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dto.InspeccionDTO;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Inspeccion;

import java.util.List;

/**
 * Interfaz de lógica de negocio para el registro y evaluación de inspecciones técnicas
 * sobre las unidades de transporte (Implementa CUS04 y flujo CUS04 -> CUS05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IInspeccionService {

    /**
     * Lista todas las inspecciones técnicas registradas.
     *
     * @return lista de {@link Inspeccion}.
     */
    List<Inspeccion> listarTodas();

    /**
     * Lista las inspecciones técnicas de una unidad específica.
     *
     * @param idUnidad ID de la unidad.
     * @return lista de inspecciones de la unidad.
     */
    List<Inspeccion> listarPorUnidad(Integer idUnidad);

    /**
     * Obtiene una inspección por su identificador único.
     *
     * @param idInspeccion ID de la inspección.
     * @return entidad {@link Inspeccion}.
     * @throws ResourceNotFoundException si no existe la inspección.
     */
    Inspeccion obtenerPorId(Integer idInspeccion);

    /**
     * Registra una nueva inspección técnica, actualiza el kilometraje de la unidad si es superior
     * al actual y determina si requiere encadenar al registro de falla cuando el resultado es
     * {@code NO_CONFORME} o {@code CON_OBSERVACIONES} (Implementa CUS04).
     *
     * @param dto               datos de la inspección.
     * @param usernameInspector usuario autenticado que realiza la inspección.
     * @return entidad {@link Inspeccion} persistida.
     * @throws ResourceNotFoundException si la unidad o el inspector no existen.
     */
    Inspeccion registrarInspeccion(InspeccionDTO dto, String usernameInspector);

    /**
     * Evalúa si el resultado de una inspección exige redirigir o encadenar automáticamente
     * al formulario de registro de falla (Regla CUS04 -> CUS05).
     *
     * @param resultado resultado de la inspección ({@code CONFORME}, {@code CON_OBSERVACIONES}, {@code NO_CONFORME}).
     * @return {@code true} si el resultado es {@code NO_CONFORME} o {@code CON_OBSERVACIONES}.
     */
    boolean requiereRegistroFalla(String resultado);
}
