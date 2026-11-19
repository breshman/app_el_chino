package com.elchino.mantenimiento.dao;

import com.elchino.mantenimiento.model.Mantenimiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz DAO para la gestión de órdenes en la tabla {@code mantenimiento}.
 * Incluye la validación nativa de cruce de horarios de técnicos (Implementa CUS06 y CUS11).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IMantenimientoDAO {

    /**
     * Lista todas las órdenes de mantenimiento ordenadas por fecha programada descendente.
     *
     * @return lista de {@link Mantenimiento}.
     */
    List<Mantenimiento> listarTodos();

    /**
     * Lista los mantenimientos pendientes o en ejecución ({@code PROGRAMADO} o {@code EN_PROCESO})
     * para el tablero de taller y selector de reparaciones (CUS07 y CUS11).
     *
     * @return lista de mantenimientos activos.
     */
    List<Mantenimiento> listarPendientesOEnProceso();

    /**
     * Busca una orden de mantenimiento por su identificador único.
     *
     * @param idMantenimiento ID del mantenimiento.
     * @return {@link Optional} con el mantenimiento encontrado.
     */
    Optional<Mantenimiento> buscarPorId(Integer idMantenimiento);

    /**
     * Verifica en la base de datos si el técnico asignado ya tiene otro mantenimiento
     * en estado {@code PROGRAMADO} o {@code EN_PROCESO} dentro de una ventana de +/- 2 horas
     * respecto a la fecha/hora solicitada (Regla crítica CUS06).
     *
     * @param idTecnico       ID del usuario técnico a verificar.
     * @param fechaProgramada fecha y hora propuesta para el mantenimiento.
     * @return {@code true} si existe cruce de horario, {@code false} si el técnico está disponible.
     */
    boolean existeCruceHorarioTecnico(Integer idTecnico, LocalDateTime fechaProgramada);

    /**
     * Inserta una nueva orden de mantenimiento preventivo o correctivo (CUS06).
     *
     * @param mantenimiento entidad con los datos de la programación.
     * @return entidad {@link Mantenimiento} con el ID autogenerado.
     */
    Mantenimiento insertar(Mantenimiento mantenimiento);

    /**
     * Actualiza el estado, fecha de inicio y/o fecha de finalización de un mantenimiento (CUS07, CUS09).
     *
     * @param idMantenimiento ID del mantenimiento.
     * @param nuevoEstado     nuevo estado ({@code PROGRAMADO}, {@code EN_PROCESO}, {@code FINALIZADO}, {@code CANCELADO}).
     * @param fechaInicio     fecha de inicio real (puede ser {@code null}).
     * @param fechaFin        fecha de finalización real (puede ser {@code null}).
     * @return número de filas afectadas.
     */
    int actualizarEstadoYFechas(Integer idMantenimiento, String nuevoEstado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
