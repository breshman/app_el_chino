package com.elchino.mantenimiento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración maestra de seguridad con Spring Security 6 (Implementa CUS01, RNF01 y QA-03).
 *
 * <p>Define el cifrado de contraseñas con {@link BCryptPasswordEncoder}, habilita
 * {@code @PreAuthorize} a nivel de métodos y establece las matrices de autorización
 * HTTP por rol ({@code ADMINISTRADOR}, {@code RESPONSABLE_MANTENIMIENTO}, {@code TECNICO},
 * {@code OPERACIONES}, {@code CONDUCTOR}), además del acceso a Swagger UI ({@code /swagger-ui.html}).</p>
 *
 * @author Arquitecto de Software (@ArchitectAgent)
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Define el codificador de contraseñas basado en el algoritmo BCrypt (fuerza 10).
     *
     * @return instancia de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP, rutas públicas, restricciones
     * por rol (QA-03), formulario de login {@code FrmLogin} y cierre de sesión.
     *
     * @param http constructor de seguridad HTTP de Spring.
     * @return cadena {@link SecurityFilterChain} compilada.
     * @throws Exception si ocurre un error al configurar las políticas de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/v3/api-docs/**", "/swagger-ui/**")
            )
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos y página de login
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/error").permitAll()
                // Documentación Swagger / OpenAPI 3 habilitada para desarrolladores y administradores (QA-07)
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // QA-03: Módulo de Usuarios exclusivo para ADMINISTRADOR
                .requestMatchers("/usuarios/**", "/api/v1/usuarios/**").hasRole("ADMINISTRADOR")
                // QA-03: Programación de Mantenimiento exclusiva para ADMINISTRADOR y RESPONSABLE_MANTENIMIENTO
                .requestMatchers("/mantenimientos/programar/**", "/api/v1/mantenimientos/programar/**")
                    .hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO")
                // Módulo de Reparaciones y Repuestos: Taller (ADMINISTRADOR, RESPONSABLE_MANTENIMIENTO, TECNICO)
                .requestMatchers("/reparaciones/**", "/api/v1/reparaciones/**", "/repuestos/**", "/api/v1/repuestos/**")
                    .hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO", "TECNICO")
                // Módulo de Inspecciones: Taller y Operaciones
                .requestMatchers("/inspecciones/**", "/api/v1/inspecciones/**")
                    .hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO", "TECNICO", "OPERACIONES")
                // Módulo de Reportes Gerenciales: ADMINISTRADOR, RESPONSABLE_MANTENIMIENTO, OPERACIONES
                .requestMatchers("/reportes/**", "/api/v1/reportes/**")
                    .hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO", "OPERACIONES")
                // Fallas, Unidades, Historial y Dashboard accesibles para usuarios autenticados según su rol
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
