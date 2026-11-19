package com.elchino.mantenimiento.config;

import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.model.Usuario;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Listener de eventos de autenticación de Spring Security para hacer cumplir
 * la regla crítica de seguridad CUS01 y RNF01:
 * <ul>
 *     <li>Incrementa el contador de intentos fallidos ante credenciales inválidas.</li>
 *     <li>Al alcanzar 3 intentos fallidos consecutivos, bloquea temporalmente la cuenta por 15 minutos.</li>
 *     <li>Al iniciar sesión exitosamente, restablece el contador de intentos fallidos a 0.</li>
 * </ul>
 *
 * @author Arquitecto de Software (@ArchitectAgent)
 * @version 1.0.0
 */
@Component
public class AuthenticationEventListener {

    /**
     * Límite máximo de intentos fallidos antes de bloquear la cuenta (CUS01).
     */
    public static final int MAX_INTENTOS_FALLIDOS = 3;

    /**
     * Duración del bloqueo temporal en minutos.
     */
    public static final int MINUTOS_BLOQUEO = 15;

    private final IUsuarioDAO usuarioDAO;

    /**
     * Constructor con inyección de {@link IUsuarioDAO}.
     *
     * @param usuarioDAO DAO de usuarios.
     */
    public AuthenticationEventListener(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Intercepta los fallos de autenticación por contraseña incorrecta e incrementa
     * el contador o bloquea la cuenta al tercer intento (CUS01).
     *
     * @param event evento de fallo de credenciales.
     */
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        Optional<Usuario> opt = usuarioDAO.buscarPorUsername(username);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            int nuevosIntentos = (u.getIntentosFallidos() != null ? u.getIntentosFallidos() : 0) + 1;
            LocalDateTime bloqueadoHasta = u.getBloqueadoHasta();
            if (nuevosIntentos >= MAX_INTENTOS_FALLIDOS) {
                bloqueadoHasta = LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO);
            }
            usuarioDAO.actualizarIntentosYBloqueo(username, nuevosIntentos, bloqueadoHasta);
        }
    }

    /**
     * Intercepta los inicios de sesión exitosos para limpiar el contador de intentos fallidos.
     *
     * @param event evento de autenticación exitosa.
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        usuarioDAO.actualizarIntentosYBloqueo(username, 0, null);
    }
}
