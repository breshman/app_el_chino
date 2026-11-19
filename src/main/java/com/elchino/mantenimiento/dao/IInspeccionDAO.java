package com.elchino.mantenimiento.dao;

import com.elchino.mantenimiento.model.Inspeccion;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz DAO para la gestión de registros en la tabla {@code inspeccion} (Implementa CUS04).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IInspeccionDAO {

    /**
     * Lista todas las inspecciones técnicas con datos de unidad e inspector ordenadas por fecha descendente.
     *
     * @return lista de {@link Inspeccion}.
     */
    List<Inspeccion> listarTodas();

    /**
     * Lista las inspecciones pertenecientes a una unidad específica.
     *
     * @param idUnidad ID de la unidad.
     * @return lista de inspecciones de la unidad.
     */
    List<Inspeccion> listarPorUnidad(Integer idUnidad);

    /**
     * Busca una inspección técnica por su identificador único.
     *
     * @param idInspeccion ID de la inspección.
     * @return {@link Optional} con la inspección encontrada.
     */
    Optional<Inspeccion> buscarPorId(Integer idInspeccion);

    /**
     * Inserta un nuevo registro de inspección técnica en PostgreSQL (CUS04).
     *
     * @param inspeccion entidad con los datos de la inspección.
     * @return entidad {@link Inspeccion} con el ID autogenerado.
     */
    Inspeccion insertar(Inspeccion inspeccion);
}
