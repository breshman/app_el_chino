package com.elchino.mantenimiento.config;

import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AuthenticationEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationEventListener.class);
    private static final int MAX_INTENTOS_FALLIDOS = 3;
    private static final int MINUTOS_BLOQUEO = 15;

    private final IUsuarioDAO usuarioDAO;

    public AuthenticationEventListener(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        usuarioDAO.buscarPorUsername(username).ifPresent(usuario -> {
            if ((usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() > 0)
                    || usuario.getBloqueadoHasta() != null) {
                usuarioDAO.actualizarIntentosYBloqueo(username, 0, null);
            }
        });
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal == null) {
            return;
        }

        String username = principal.toString().trim();
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorUsername(username);

        usuarioOpt.ifPresent(usuario -> {
            // Si ya había expirado un bloqueo previo, reiniciamos el contador desde 0
            int intentosPrevios = (usuario.getBloqueadoHasta() != null
                    && usuario.getBloqueadoHasta().isBefore(LocalDateTime.now()))
                    ? 0
                    : (usuario.getIntentosFallidos() != null ? usuario.getIntentosFallidos() : 0);

            int nuevosIntentos = intentosPrevios + 1;
            LocalDateTime bloqueadoHasta = null;

            if (nuevosIntentos >= MAX_INTENTOS_FALLIDOS) {
                bloqueadoHasta = LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO);
                log.warn("SEGURIDAD: Cuenta '{}' bloqueada por {} minutos tras {} intentos fallidos.",
                        username, MINUTOS_BLOQUEO, nuevosIntentos);
            }

            usuarioDAO.actualizarIntentosYBloqueo(username, nuevosIntentos, bloqueadoHasta);
        });
    }
}