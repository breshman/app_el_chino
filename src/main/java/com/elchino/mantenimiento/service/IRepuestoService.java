package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dto.RepuestoDTO;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Repuesto;

import java.util.List;

/**
 * Interfaz de lógica de negocio para la gestión de inventario de repuestos
 * y alertas de stock mínimo (Implementa CUS08).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IRepuestoService {

    /**
     * Lista todos los repuestos del catálogo.
     *
     * @return lista de {@link Repuesto}.
     */
    List<Repuesto> listarTodos();

    /**
     * Lista los repuestos con stock crítico ({@code stock <= stock_minimo}) para alertas de taller (CUS08).
     *
     * @return lista de repuestos bajo stock mínimo.
     */
    List<Repuesto> listarAlertasBajoStock();

    /**
     * Obtiene un repuesto por su identificador único.
     *
     * @param idRepuesto ID del repuesto.
     * @return entidad {@link Repuesto}.
     * @throws ResourceNotFoundException si el repuesto no existe.
     */
    Repuesto obtenerPorId(Integer idRepuesto);

    /**
     * Registra un nuevo repuesto en el almacén.
     *
     * @param dto datos del repuesto.
     * @return entidad {@link Repuesto} creada.
     */
    Repuesto registrarRepuesto(RepuestoDTO dto);

    /**
     * Actualiza los datos y existencias de un repuesto en almacén.
     *
     * @param idRepuesto ID del repuesto.
     * @param dto        datos actualizados.
     * @return entidad {@link Repuesto} actualizada.
     * @throws ResourceNotFoundException si el repuesto no existe.
     */
    Repuesto actualizarRepuesto(Integer idRepuesto, RepuestoDTO dto);
}
