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
import java.util.Set;

/**
 * Implementación de la capa de servicio para la gestión de usuarios y seguridad
 * con cifrado BCrypt (Implementa CUS01 y RNF01).
 */
@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private static final Set<String> ROLES_VALIDOS = Set.of(
            "ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO", "TECNICO", "OPERACIONES", "CONDUCTOR"
    );
    private static final Set<String> ESTADOS_VALIDOS = Set.of("ACTIVO", "INACTIVO");

    private final IUsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(IUsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    @Override
    public List<Usuario> listarTecnicosActivos() {
        return usuarioDAO.listarActivosPorRol("TECNICO");
    }

    @Override
    public Usuario obtenerPorUsername(String username) {
        return usuarioDAO.buscarPorUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    public Usuario obtenerPorId(Integer idUsuario) {
        return usuarioDAO.buscarPorId(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("No existe usuario con ID: " + idUsuario));
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(UsuarioDTO dto) {
        String usernameLimpio = dto.getUsername().trim().toLowerCase();
        String emailLimpio = dto.getEmail().trim().toLowerCase();

        if (usuarioDAO.buscarPorUsername(usernameLimpio).isPresent()) {
            throw new BusinessException("El nombre de usuario '" + usernameLimpio + "' ya está registrado.");
        }

        boolean emailDuplicado = usuarioDAO.listarTodos().stream()
                .anyMatch(u -> u.getEmail() != null && u.getEmail().trim().equalsIgnoreCase(emailLimpio));
        if (emailDuplicado) {
            throw new BusinessException("El correo electrónico '" + emailLimpio + "' ya está asociado a otro usuario.");
        }

        String rolNormalizado = dto.getRol() != null ? dto.getRol().trim().toUpperCase() : "";
        if (!ROLES_VALIDOS.contains(rolNormalizado)) {
            throw new BusinessException("El rol seleccionado '" + dto.getRol() + "' no es válido.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsername(usernameLimpio);
        nuevo.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        nuevo.setNombres(dto.getNombres().trim());
        nuevo.setApellidos(dto.getApellidos().trim());
        nuevo.setEmail(emailLimpio);
        nuevo.setRol(rolNormalizado);
        nuevo.setEstado("ACTIVO");
        nuevo.setIntentosFallidos(0);

        return usuarioDAO.insertar(nuevo);
    }

    @Override
    @Transactional
    public void cambiarEstadoYDesbloquear(Integer idUsuario, String nuevoEstado) {
        String estadoNormalizado = nuevoEstado != null ? nuevoEstado.trim().toUpperCase() : "";
        if (!ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            throw new BusinessException("Estado de usuario no válido: '" + nuevoEstado + "'.");
        }

        Usuario u = obtenerPorId(idUsuario);
        usuarioDAO.actualizarEstado(idUsuario, estadoNormalizado);
        usuarioDAO.actualizarIntentosYBloqueo(u.getUsername(), 0, null);
    }
}