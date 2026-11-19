package com.elchino.mantenimiento.dao;

import com.elchino.mantenimiento.model.Falla;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz DAO para el acceso a datos de la tabla {@code falla} en PostgreSQL.
 * Permite registrar, priorizar y consultar averías de las unidades (Implementa CUS05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IFallaDAO {

    /**
     * Lista todas las fallas registradas con información de la unidad y usuario reportante.
     *
     * @return lista de {@link Falla}.
     */
    List<Falla> listarTodas();

    /**
     * Lista las fallas pendientes o en atención ({@code REPORTADA} o {@code EN_ATENCION})
     * para ser vinculadas a órdenes de mantenimiento correctivo (CUS06).
     *
     * @return lista de fallas activas.
     */
    List<Falla> listarActivas();

    /**
     * Busca una falla por su identificador único.
     *
     * @param idFalla ID de la falla.
     * @return {@link Optional} con la falla encontrada.
     */
    Optional<Falla> buscarPorId(Integer idFalla);

    /**
     * Inserta una nueva falla mecánica o eléctrica en la base de datos (CUS05).
     *
     * @param falla entidad con los datos de la falla.
     * @return entidad {@link Falla} con su ID autogenerado.
     */
    Falla insertar(Falla falla);

    /**
     * Actualiza la prioridad y/o el estado de una falla existente (CUS05, CUS06, CUS09).
     *
     * @param idFalla   ID de la falla a modificar.
     * @param prioridad nueva prioridad ({@code BAJA}, {@code MEDIA}, {@code ALTA}, {@code CRITICA}).
     * @param estado    nuevo estado ({@code REPORTADA}, {@code EN_ATENCION}, {@code RESUELTA}, {@code CERRADA}).
     * @return número de filas afectadas.
     */
    int actualizarPrioridadYEstado(Integer idFalla, String prioridad, String estado);
}
