package com.elchino.mantenimiento.config;

import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.model.Usuario;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Servicio personalizado de Spring Security encargado de cargar los datos de
 * autenticación, roles y estado de bloqueo temporal de los usuarios desde PostgreSQL (RNF01).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IUsuarioDAO usuarioDAO;

    /**
     * Constructor con inyección del DAO de usuarios.
     *
     * @param usuarioDAO DAO de acceso a la tabla {@code usuario}.
     */
    public CustomUserDetailsService(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Carga los detalles de autenticación de un usuario a partir de su {@code username}[cite: 1].
     *
     * @param username nombre de usuario ingresado en el formulario de login[cite: 1].
     * @return instancia de {@link UserDetails} con authorities y estado de bloqueo[cite: 1].
     * @throws UsernameNotFoundException si el usuario no existe[cite: 1].
     * @throws LockedException           si la cuenta está temporalmente bloqueada tras 3 intentos fallidos[cite: 1].
     * @throws DisabledException         si la cuenta se encuentra en estado {@code INACTIVO}.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDAO.buscarPorUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            throw new DisabledException("La cuenta de usuario se encuentra inactiva.");
        }

        if (usuario.getBloqueadoHasta() != null) {
            if (usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
                throw new LockedException("Cuenta bloqueada temporalmente por múltiples intentos fallidos.");
            } else {
                // El bloqueo ya expiró: reiniciamos contador y limpiamos fecha de bloqueo
                usuarioDAO.actualizarIntentosYBloqueo(usuario.getUsername(), 0, null);
            }
        }

        return new User(
                usuario.getUsername(),
                usuario.getPasswordHash(),
                true,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }
}