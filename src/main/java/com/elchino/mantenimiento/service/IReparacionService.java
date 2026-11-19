package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dto.ReparacionDTO;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.exception.StockInsuficienteException;
import com.elchino.mantenimiento.model.Reparacion;

import java.util.List;

/**
 * Interfaz de lógica de negocio para la ejecución transaccional de reparaciones,
 * descuento de stock de repuestos y verificación de calidad (Implementa CUS07, CUS08 y CUS09).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IReparacionService {

    /**
     * Lista todas las reparaciones registradas incluyendo sus detalles de repuestos.
     *
     * @return lista de {@link Reparacion}.
     */
    List<Reparacion> listarTodas();

    /**
     * Obtiene una reparación por su ID con todos sus repuestos asociados.
     *
     * @param idReparacion ID de la reparación.
     * @return entidad {@link Reparacion}.
     * @throws ResourceNotFoundException si la reparación no existe.
     */
    Reparacion obtenerPorId(Integer idReparacion);

    /**
     * Ejecuta la **Transacción Maestra de Reparación** (Implementa CUS07 + CUS08 + CUS09 + QA-04):
     * <ol>
     *     <li>Registra la cabecera de {@link Reparacion} vinculada al mantenimiento.</li>
     *     <li>Por cada repuesto solicitado en {@code dto.getRepuestos()}, valida y descuenta atómicamente
     *     el stock. Si {@code stock < cantidad}, lanza {@link StockInsuficienteException} haciendo
     *     rollback completo de la reparación y de todos los repuestos previos (QA-04).</li>
     *     <li>Si el resultado de verificación es {@code SATISFACTORIO}: finaliza el mantenimiento
     *     ({@code FINALIZADO}), marca la falla asociada como {@code RESUELTA}, cambia la unidad a
     *     {@code OPERATIVA} e inserta el registro de auditoría en {@code historial_mantenimiento} (QA-05).</li>
     *     <li>Si el resultado es {@code REQUIERE_REVISION}: mantiene el mantenimiento en {@code EN_PROCESO}
     *     y la unidad en {@code EN_MANTENIMIENTO}.</li>
     * </ol>
     *
     * @param dto             datos de la reparación, repuestos y verificación.
     * @param usernameTecnico usuario autenticado que registra la reparación.
     * @return entidad {@link Reparacion} registrada con sus detalles.
     * @throws StockInsuficienteException si algún repuesto no cuenta con stock suficiente.
     * @throws ResourceNotFoundException  si el mantenimiento o algún repuesto no existe.
     */
    Reparacion registrarReparacionTransaccional(ReparacionDTO dto, String usernameTecnico);

    /**
     * Actualiza la verificación de calidad de una reparación existente (CUS09).
     *
     * @param idReparacion        ID de la reparación.
     * @param resultado           {@code SATISFACTORIO} o {@code REQUIERE_REVISION}.
     * @param observaciones       comentarios del jefe de taller.
     * @param usernameVerificador usuario que realiza la verificación.
     * @return entidad {@link Reparacion} actualizada.
     * @throws ResourceNotFoundException si la reparación no existe.
     */
    Reparacion verificarReparacion(Integer idReparacion, String resultado, String observaciones, String usernameVerificador);
}
