package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.model.HistorialMantenimiento;
import java.util.List;

/**
 * Interfaz de lógica de negocio para la consulta de trazabilidad e historial
 * técnico de las unidades de transporte (Implementa CUS10 y QA-05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IHistorialService {

    /**
     * Lista la bitácora completa de eventos de mantenimiento de toda la flota.
     *
     * @return lista de {@link HistorialMantenimiento}.
     */
    List<HistorialMantenimiento> listarTodo();

    /**
     * Consulta la línea de tiempo histórica de una unidad de transporte específica (CUS10).
     *
     * @param idUnidad ID de la unidad a consultar.
     * @return lista cronológica de eventos de la unidad.
     */
    List<HistorialMantenimiento> listarPorUnidad(Integer idUnidad);
}
