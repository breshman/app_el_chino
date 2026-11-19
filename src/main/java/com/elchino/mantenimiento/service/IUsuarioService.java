package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dto.UsuarioDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Usuario;

import java.util.List;

/**
 * Interfaz de lógica de negocio para la gestión de usuarios, seguridad
 * y control de accesos del sistema "El Chino" (Implementa CUS01 y RNF01).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IUsuarioService {

    /**
     * Lista todos los usuarios registrados en el sistema.
     *
     * @return lista completa de {@link Usuario}.
     */
    List<Usuario> listarTodos();

    /**
     * Lista los técnicos activos disponibles para asignación de órdenes de mantenimiento (CUS06).
     *
     * @return lista de usuarios con rol {@code TECNICO} y estado {@code ACTIVO}.
     */
    List<Usuario> listarTecnicosActivos();

    /**
     * Obtiene un usuario por su nombre de usuario (username).
     *
     * @param username nombre de usuario único.
     * @return entidad {@link Usuario}.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    Usuario obtenerPorUsername(String username);

    /**
     * Obtiene un usuario por su identificador único.
     *
     * @param idUsuario ID del usuario.
     * @return entidad {@link Usuario}.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    Usuario obtenerPorId(Integer idUsuario);

    /**
     * Registra un nuevo usuario cifrando su contraseña con {@code BCryptPasswordEncoder} (CUS01 / RNF01).
     *
     * @param dto datos validados del usuario a crear.
     * @return entidad {@link Usuario} creada.
     * @throws BusinessException si el username ya se encuentra registrado.
     */
    Usuario registrarUsuario(UsuarioDTO dto);

    /**
     * Cambia el estado de un usuario ({@code ACTIVO} / {@code INACTIVO}) y desbloquea su cuenta si estaba bloqueada.
     *
     * @param idUsuario   ID del usuario a actualizar.
     * @param nuevoEstado nuevo estado ({@code ACTIVO} o {@code INACTIVO}).
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    void cambiarEstadoYDesbloquear(Integer idUsuario, String nuevoEstado);
}
