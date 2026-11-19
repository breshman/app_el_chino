package com.elchino.mantenimiento.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración corporativa de Swagger / OpenAPI 3 para la documentación
 * interactiva de los endpoints REST del sistema "El Chino".
 *
 * <p>Cumple con el estándar de documentación técnica (QA-07), exponiendo la interfaz
 * en {@code /swagger-ui.html} y la especificación JSON en {@code /v3/api-docs}.</p>
 *
 * @author Arquitecto de Software (@ArchitectAgent)
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Construye y configura el descriptor maestro de OpenAPI 3 con información
     * de la empresa "El Chino", contactos de soporte y esquema de autenticación.
     *
     * @return instancia de {@link OpenAPI} configurada para SpringDoc.
     */
    @Bean
    public OpenAPI elChinoOpenAPI() {
        final String securitySchemeName = "cookieAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("API Sistema de Gestión de Mantenimiento y Control de Unidades - El Chino")
                        .version("1.0.0")
                        .description("Especificación OpenAPI 3 para los módulos de Unidades, Inspecciones, " +
                                "Fallas, Programación de Mantenimientos, Ejecución de Reparaciones, " +
                                "Inventario de Repuestos, Historial y Reportes Gerenciales (CUS01 - CUS12).")
                        .contact(new Contact()
                                .name("Área de TI y Mantenimiento de Flota - Empresa El Chino (Lima, Perú)")
                                .email("soporte.ti@elchino.pe")
                                .url("https://www.elchino.pe"))
                        .license(new License()
                                .name("Uso Interno Corporativo - El Chino S.A.")
                                .url("https://www.elchino.pe/licencia")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name("JSESSIONID")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("Sesión autenticada de Spring Security (BCrypt + Roles).")));
    }
}
