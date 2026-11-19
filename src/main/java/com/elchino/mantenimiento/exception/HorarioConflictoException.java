package com.elchino.mantenimiento.exception;

/**
 * Excepción de negocio lanzada durante el Caso de Uso CUS06 (Programar Mantenimiento)
 * cuando el técnico asignado ya posee otro mantenimiento en estado {@code PROGRAMADO}
 * o {@code EN_PROCESO} en la misma ventana de fecha/hora, o cuando la fecha es pasada.
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public class HorarioConflictoException extends BusinessException {

    /**
     * Construye la excepción con el detalle del cruce de horario del técnico.
     *
     * @param message descripción del conflicto de agenda.
     */
    public HorarioConflictoException(String message) {
        super(message);
    }
}
