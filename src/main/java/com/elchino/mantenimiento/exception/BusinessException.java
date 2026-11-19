package com.elchino.mantenimiento.exception;

/**
 * Excepción base para violaciones de reglas de negocio en el sistema "El Chino".
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public class BusinessException extends RuntimeException {

    /**
     * Crea una nueva excepción de negocio con un mensaje descriptivo.
     *
     * @param message descripción técnica o funcional del error de negocio.
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Crea una nueva excepción de negocio encadenando la causa original.
     *
     * @param message descripción del error de negocio.
     * @param cause   excepción raíz que originó el fallo.
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
