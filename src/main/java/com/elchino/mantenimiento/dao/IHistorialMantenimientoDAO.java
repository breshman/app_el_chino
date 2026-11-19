package com.elchino.mantenimiento.dao;

import com.elchino.mantenimiento.model.HistorialMantenimiento;
import java.util.List;

/**
 * Interfaz DAO para el registro y consulta de la bitácora inmutable
 * {@code historial_mantenimiento} (Implementa CUS10 y QA-05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IHistorialMantenimientoDAO {

    /**
     * Lista todos los eventos del historial ordenados del más reciente al más antiguo.
     *
     * @return lista completa de {@link HistorialMantenimiento}.
     */
    List<HistorialMantenimiento> listarTodo();

    /**
     * Lista la línea de tiempo cronológica del historial de una unidad específica (CUS10).
     *
     * @param idUnidad ID de la unidad de transporte.
     * @return lista de eventos de la unidad ordenados por fecha descendente.
     */
    List<HistorialMantenimiento> listarPorUnidad(Integer idUnidad);

    /**
     * Inserta un nuevo evento de auditoría y cambio de estado en el historial (QA-05).
     *
     * @param historial entidad con los detalles de trazabilidad.
     * @return entidad {@link HistorialMantenimiento} con su ID autogenerado.
     */
    HistorialMantenimiento insertar(HistorialMantenimiento historial);
}
