package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.UsuarioDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IUsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicio para la gestión de usuarios y seguridad
 * con cifrado BCrypt (Implementa CUS01 y RNF01).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor con inyección de dependencias DAO y codificador BCrypt.
     *
     * @param usuarioDAO      DAO de usuarios.
     * @param passwordEncoder codificador BCrypt configurado en {@code SecurityConfig}.
     */
    public UsuarioServiceImpl(IUsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Usuario> listarTecnicosActivos() {
        return usuarioDAO.listarActivosPorRol("TECNICO");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Usuario obtenerPorUsername(String username) {
        return usuarioDAO.buscarPorUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Usuario obtenerPorId(Integer idUsuario) {
        return usuarioDAO.buscarPorId(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("No existe usuario con ID: " + idUsuario));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Usuario registrarUsuario(UsuarioDTO dto) {
        if (usuarioDAO.buscarPorUsername(dto.getUsername()).isPresent()) {
            throw new BusinessException("El nombre de usuario '" + dto.getUsername() + "' ya está registrado.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsername(dto.getUsername().trim());
        // RNF01: Cifrado obligatorio de contraseña mediante BCryptPasswordEncoder
        nuevo.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        nuevo.setNombres(dto.getNombres().trim());
        nuevo.setApellidos(dto.getApellidos().trim());
        nuevo.setEmail(dto.getEmail().trim());
        nuevo.setRol(dto.getRol());
        nuevo.setEstado("ACTIVO");
        nuevo.setIntentosFallidos(0);

        return usuarioDAO.insertar(nuevo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cambiarEstadoYDesbloquear(Integer idUsuario, String nuevoEstado) {
        Usuario u = obtenerPorId(idUsuario);
        usuarioDAO.actualizarEstado(idUsuario, nuevoEstado);
        usuarioDAO.actualizarIntentosYBloqueo(u.getUsername(), 0, null);
    }
}
