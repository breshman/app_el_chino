package com.elchino.mantenimiento.dao;

import com.elchino.mantenimiento.model.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz DAO para el acceso y persistencia de la entidad {@link Usuario} en PostgreSQL.
 * Implementa las operaciones de consulta de credenciales, bloqueo por intentos fallidos
 * y administración de usuarios (CUS01 y RF01).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IUsuarioDAO {

    /**
     * Busca un usuario por su nombre de inicio de sesión único (Implementa CUS01).
     *
     * @param username nombre de usuario a consultar.
     * @return {@link Optional} con el usuario encontrado o vacío si no existe.
     */
    Optional<Usuario> buscarPorUsername(String username);

    /**
     * Busca un usuario por su clave primaria {@code id_usuario}.
     *
     * @param idUsuario identificador único del usuario.
     * @return {@link Optional} con el usuario encontrado.
     */
    Optional<Usuario> buscarPorId(Integer idUsuario);

    /**
     * Lista todos los usuarios registrados ordenados por apellidos y nombres.
     *
     * @return lista completa de {@link Usuario}.
     */
    List<Usuario> listarTodos();

    /**
     * Lista únicamente los usuarios activos que poseen un rol específico (ej. {@code TECNICO} para CUS06).
     *
     * @param rol nombre del rol operativo a filtrar.
     * @return lista de usuarios activos con dicho rol.
     */
    List<Usuario> listarActivosPorRol(String rol);

    /**
     * Inserta un nuevo usuario en la tabla {@code usuario} con contraseña cifrada en BCrypt.
     *
     * @param usuario entidad con los datos del nuevo usuario.
     * @return entidad {@link Usuario} con el {@code id_usuario} autogenerado.
     */
    Usuario insertar(Usuario usuario);

    /**
     * Actualiza el estado operativo ({@code ACTIVO} / {@code INACTIVO}) de un usuario.
     *
     * @param idUsuario identificador del usuario.
     * @param estado    nuevo estado a establecer.
     * @return número de filas afectadas.
     */
    int actualizarEstado(Integer idUsuario, String estado);

    /**
     * Actualiza el contador de intentos fallidos y la marca temporal de bloqueo de seguridad (CUS01 / RNF01).
     *
     * @param username         nombre de usuario afectado.
     * @param intentosFallidos nuevo contador de intentos fallidos.
     * @param bloqueadoHasta   fecha/hora límite de bloqueo temporal o {@code null} para desbloquear.
     * @return número de filas actualizadas.
     */
    int actualizarIntentosYBloqueo(String username, int intentosFallidos, LocalDateTime bloqueadoHasta);
}
