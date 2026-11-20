package com.elchino.mantenimiento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración maestra de seguridad con Spring Security 6 (Implementa CUS01, RNF01 y QA-03).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/v3/api-docs/**", "/swagger-ui/**")
            )
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos y login públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/error").permitAll()

                // Documentación Swagger exclusiva para ADMINISTRADOR
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMINISTRADOR")

                // Gestión de usuarios y reportes ejecutivos
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/reportes/**").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO")

                // Unidades (Web y API): consulta general, modificación restringida
                .requestMatchers(HttpMethod.GET, "/unidades/**", "/api/v1/unidades/**").authenticated()
                .requestMatchers("/unidades/guardar", "/unidades/*/estado").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO")
                .requestMatchers("/api/v1/unidades/**").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO")

                // Mantenimientos: iniciar permitido a técnicos; programar/cancelar solo Admin o Responsable
                .requestMatchers("/mantenimientos/*/iniciar").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO", "TECNICO")
                .requestMatchers("/mantenimientos/guardar", "/mantenimientos/*/cancelar").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO")

                // Reparaciones: verificar exclusivo de Responsable/Admin (el técnico no puede auto-aprobarse)
                .requestMatchers("/reparaciones/*/verificar").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO")
                .requestMatchers("/reparaciones/**").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO", "TECNICO")

                // Fallas: cualquier usuario autenticado reporta; priorizar/cerrar solo Admin o Responsable
                .requestMatchers("/fallas/*/priorizar", "/fallas/*/estado").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO")
                .requestMatchers("/fallas/**", "/api/v1/fallas/**").authenticated()

                // Repuestos: lectura para taller, modificaciones para Admin o Responsable
                .requestMatchers(HttpMethod.GET, "/repuestos/**").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO", "TECNICO")
                .requestMatchers("/repuestos/**").hasAnyRole("ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO")

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
