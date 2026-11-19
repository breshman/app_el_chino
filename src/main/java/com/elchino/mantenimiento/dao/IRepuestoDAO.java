package com.elchino.mantenimiento.dao;

import com.elchino.mantenimiento.model.Repuesto;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz DAO para la gestión del inventario en la tabla {@code repuesto} (Implementa CUS08).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IRepuestoDAO {

    /**
     * Lista todos los repuestos del catálogo ordenados por categoría y nombre.
     *
     * @return lista de {@link Repuesto}.
     */
    List<Repuesto> listarTodos();

    /**
     * Lista todos los repuestos cuyo stock actual es menor o igual a su stock mínimo ({@code stock <= stock_minimo}).
     *
     * @return lista de repuestos en estado de alerta de reposición (CUS08).
     */
    List<Repuesto> listarBajoStock();

    /**
     * Busca un repuesto por su identificador único.
     *
     * @param idRepuesto ID del repuesto.
     * @return {@link Optional} con el repuesto encontrado.
     */
    Optional<Repuesto> buscarPorId(Integer idRepuesto);

    /**
     * Inserta un nuevo repuesto en el almacén del taller.
     *
     * @param repuesto entidad con los datos del repuesto.
     * @return entidad {@link Repuesto} con el ID autogenerado.
     */
    Repuesto insertar(Repuesto repuesto);

    /**
     * Actualiza los datos y el stock de un repuesto existente.
     *
     * @param repuesto entidad con los datos actualizados.
     * @return número de filas afectadas.
     */
    int actualizar(Repuesto repuesto);

    /**
     * Descuenta atómicamente una cantidad del stock de un repuesto siempre que exista
     * saldo suficiente ({@code stock >= cantidad}). Forma parte de la transacción maestra CUS07/CUS08.
     *
     * @param idRepuesto ID del repuesto a descontar.
     * @param cantidad   cantidad a restar del inventario.
     * @return número de filas actualizadas (1 si hubo stock suficiente, 0 si no pudo descontar).
     */
    int descontarStock(Integer idRepuesto, Integer cantidad);
}
