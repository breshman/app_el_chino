package com.elchino.mantenimiento.dao;

import com.elchino.mantenimiento.model.DetalleRepuesto;
import java.util.List;

/**
 * Interfaz DAO para gestionar la tabla intermedia {@code detalle_repuesto}
 * que vincula cada reparación con los repuestos utilizados (Implementa CUS08).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IDetalleRepuestoDAO {

    /**
     * Lista todos los repuestos consumidos en una reparación específica.
     *
     * @param idReparacion ID de la reparación.
     * @return lista de {@link DetalleRepuesto} con nombre y SKU del repuesto.
     */
    List<DetalleRepuesto> listarPorReparacion(Integer idReparacion);

    /**
     * Inserta un detalle de consumo de repuesto dentro de la transacción de reparación.
     *
     * @param detalle entidad con cantidad, precio unitario y subtotal.
     * @return entidad {@link DetalleRepuesto} con el ID autogenerado.
     */
    DetalleRepuesto insertar(DetalleRepuesto detalle);
}
