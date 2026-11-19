package com.elchino.mantenimiento.dao;

import com.elchino.mantenimiento.model.Unidad;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz DAO para la persistencia y consulta de la tabla {@code unidad} en PostgreSQL.
 * Encapsula las consultas SQL parametrizadas para la flota "El Chino" (Implementa CUS02 y CUS03).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IUnidadDAO {

    /**
     * Lista todas las unidades de transporte ordenadas por código interno (CUS02).
     *
     * @return lista de {@link Unidad}.
     */
    List<Unidad> listarTodas();

    /**
     * Lista las unidades filtradas por su estado operativo ({@code OPERATIVA}, {@code EN_MANTENIMIENTO}, {@code FUERA_DE_SERVICIO}).
     *
     * @param estado estado operativo a filtrar.
     * @return lista de unidades que coinciden con el estado.
     */
    List<Unidad> listarPorEstado(String estado);

    /**
     * Busca una unidad por su identificador primario.
     *
     * @param idUnidad ID de la unidad.
     * @return {@link Optional} con la unidad encontrada.
     */
    Optional<Unidad> buscarPorId(Integer idUnidad);

    /**
     * Busca una unidad por su código interno único (ej. {@code CH-101}) o placa vehicular.
     *
     * @param codigoInterno código interno de flota.
     * @param placa         placa de rodaje.
     * @return {@link Optional} con la unidad si existe coincidencia.
     */
    Optional<Unidad> buscarPorCodigoOPlaca(String codigoInterno, String placa);

    /**
     * Registra una nueva unidad de transporte en la base de datos (CUS02).
     *
     * @param unidad entidad con los datos de la unidad.
     * @return entidad {@link Unidad} con su ID autogenerado.
     */
    Unidad insertar(Unidad unidad);

    /**
     * Actualiza los datos técnicos y el kilometraje de una unidad existente (CUS03).
     *
     * @param unidad entidad con los datos actualizados.
     * @return número de filas afectadas.
     */
    int actualizar(Unidad unidad);

    /**
     * Actualiza exclusivamente el estado operativo de una unidad (CUS03, CUS05, CUS07, CUS09).
     *
     * @param idUnidad    ID de la unidad a modificar.
     * @param nuevoEstado nuevo estado ({@code OPERATIVA}, {@code EN_MANTENIMIENTO}, {@code FUERA_DE_SERVICIO}).
     * @return número de filas afectadas.
     */
    int actualizarEstado(Integer idUnidad, String nuevoEstado);

    /**
     * Actualiza el kilometraje actual de la unidad cuando una inspección registra un odómetro mayor (CUS04).
     *
     * @param idUnidad         ID de la unidad.
     * @param nuevoKilometraje lectura actualizada en kilómetros.
     * @return número de filas afectadas.
     */
    int actualizarKilometraje(Integer idUnidad, Integer nuevoKilometraje);
}
