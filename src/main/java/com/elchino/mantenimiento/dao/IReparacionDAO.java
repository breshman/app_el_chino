package com.elchino.mantenimiento.dao;

import com.elchino.mantenimiento.model.Reparacion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz DAO para la persistencia y consulta de la tabla {@code reparacion} (Implementa CUS07 y CUS09).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IReparacionDAO {

    /**
     * Lista todas las reparaciones ejecutadas con información de unidad y técnico.
     *
     * @return lista de {@link Reparacion}.
     */
    List<Reparacion> listarTodas();

    /**
     * Busca una reparación por su identificador único.
     *
     * @param idReparacion ID de la reparación.
     * @return {@link Optional} con la reparación encontrada.
     */
    Optional<Reparacion> buscarPorId(Integer idReparacion);

    /**
     * Busca una reparación asociada a un ID de mantenimiento específico.
     *
     * @param idMantenimiento ID de la orden de mantenimiento.
     * @return {@link Optional} con la reparación encontrada.
     */
    Optional<Reparacion> buscarPorIdMantenimiento(Integer idMantenimiento);

    /**
     * Inserta un nuevo registro de reparación en la base de datos (CUS07).
     *
     * @param reparacion entidad con los datos técnicos de la reparación.
     * @return entidad {@link Reparacion} con el ID autogenerado.
     */
    Reparacion insertar(Reparacion reparacion);

    /**
     * Actualiza el resultado de verificación de calidad y observaciones de una reparación (CUS09).
     *
     * @param idReparacion              ID de la reparación.
     * @param resultado                 nuevo resultado ({@code PENDIENTE}, {@code SATISFACTORIO}, {@code REQUIERE_REVISION}).
     * @param observacionesVerificacion comentarios del verificador.
     * @return número de filas afectadas.
     */
    int actualizarVerificacion(Integer idReparacion, String resultado, String observacionesVerificacion);

    /**
     * Calcula el costo total acumulado en reparaciones (mano de obra + repuestos) para reportes gerenciales (CUS12).
     *
     * @return suma total en Soles (PEN).
     */
    BigDecimal calcularCostoTotalMantenimiento();
}
