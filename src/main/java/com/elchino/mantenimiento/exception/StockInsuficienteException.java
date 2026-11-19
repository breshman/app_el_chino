package com.elchino.mantenimiento.exception;

/**
 * Excepción de negocio crítica para los casos de uso CUS07 y CUS08.
 * Se lanza dentro de una transacción de reparación cuando la cantidad solicitada
 * de un repuesto supera su stock actual en almacén, provocando un rollback automático.
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public class StockInsuficienteException extends BusinessException {

    /**
     * Construye la excepción detallando el repuesto, stock disponible y cantidad requerida.
     *
     * @param message mensaje explicativo del déficit de inventario.
     */
    public StockInsuficienteException(String message) {
        super(message);
    }
}
