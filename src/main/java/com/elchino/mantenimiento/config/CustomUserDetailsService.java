package com.elchino.mantenimiento.config;

import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.model.Usuario;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio personalizado de carga de credenciales para Spring Security 6.
 * Consulta la capa DAO ({@link IUsuarioDAO}) para validar estado activo,
 * bloqueo temporal por 3 intentos fallidos y asignación de rol {@code ROLE_<ROL>}
 * (Implementa CUS01 y RNF01).
 *
 * @author Arquitecto de Software (@ArchitectAgent)
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IUsuarioDAO usuarioDAO;

    /**
     * Constructor con inyección de {@link IUsuarioDAO}.
     *
     * @param usuarioDAO interfaz de acceso a datos de usuarios.
     */
    public CustomUserDetailsService(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Carga los detalles de autenticación de un usuario a partir de su {@code username}.
     *
     * @param username nombre de usuario ingresado en el formulario de login.
     * @return instancia de {@link UserDetails} con authorities y estado de bloqueo.
     * @throws UsernameNotFoundException si el usuario no existe.
     * @throws LockedException           si la cuenta está temporalmente bloqueada tras 3 intentos fallidos.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDAO.buscarPorUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        boolean habilitado = "ACTIVO".equalsIgnoreCase(usuario.getEstado());
        boolean cuentaNoBloqueada = !usuario.isCuentaBloqueada();

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .disabled(!habilitado)
                .accountLocked(!cuentaNoBloqueada)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())))
                .build();
    }
}
