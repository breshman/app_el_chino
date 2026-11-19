package com.elchino.mantenimiento.exception;

/**
 * Excepción lanzada cuando una entidad solicitada (Unidad, Falla, Mantenimiento,
 * Repuesto, Usuario, etc.) no existe en la base de datos PostgreSQL.
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public class ResourceNotFoundException extends BusinessException {

    /**
     * Construye la excepción indicando el mensaje de recurso no encontrado.
     *
     * @param message detalle de la entidad e identificador no localizado.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
