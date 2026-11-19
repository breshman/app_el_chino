package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.config.AuthenticationEventListener;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.UsuarioDTO;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para verificar el cifrado BCrypt de contraseñas y el bloqueo
 * automático de cuenta al tercer intento fallido (CUS01, RNF01 y QA-03).
 *
 * @author Ingeniero de QA y Seguridad (@QAAgent)
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private IUsuarioDAO usuarioDAO;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    /**
     * Verifica que al registrar un usuario su contraseña sea cifrada mediante BCrypt (RNF01 / QA-03).
     */
    @Test
    @DisplayName("QA-03: Registro de usuario cifra contraseña con BCryptPasswordEncoder")
    void testRegistrarUsuarioCifraPasswordConBCrypt() {
        when(usuarioDAO.buscarPorUsername("nuevo.tecnico")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("ClaveSegura123")).thenReturn("$2a$10$HashBCryptSimulado");
        when(usuarioDAO.insertar(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsername("nuevo.tecnico");
        dto.setPassword("ClaveSegura123");
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setEmail("jperez@elchino.pe");
        dto.setRol("TECNICO");

        Usuario creado = usuarioService.registrarUsuario(dto);

        assertEquals("$2a$10$HashBCryptSimulado", creado.getPasswordHash());
        verify(passwordEncoder, times(1)).encode("ClaveSegura123");
    }

    /**
     * Verifica que al tercer intento fallido consecutivo se bloquee temporalmente la cuenta (CUS01).
     */
    @Test
    @DisplayName("CUS01: Bloquea temporalmente la cuenta al alcanzar 3 intentos fallidos")
    void testBloqueoTemporalTrasTresIntentosFallidos() {
        Usuario u = new Usuario();
        u.setUsername("conductor1");
        u.setIntentosFallidos(2); // Ya lleva 2 intentos fallidos

        when(usuarioDAO.buscarPorUsername("conductor1")).thenReturn(Optional.of(u));

        AuthenticationEventListener listener = new AuthenticationEventListener(usuarioDAO);
        AuthenticationFailureBadCredentialsEvent event = new AuthenticationFailureBadCredentialsEvent(
                new UsernamePasswordAuthenticationToken("conductor1", "badpass"),
                new AuthenticationException("Credenciales inválidas") {}
        );

        listener.onAuthenticationFailure(event);

        // Al llegar a 3 intentos, debe actualizar intentos = 3 y establecer una fecha bloqueadoHasta no nula
        verify(usuarioDAO, times(1)).actualizarIntentosYBloqueo(
                eq("conductor1"), eq(3), notNull(LocalDateTime.class)
        );
    }
}
