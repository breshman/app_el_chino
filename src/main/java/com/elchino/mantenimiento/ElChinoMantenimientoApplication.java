package com.elchino.mantenimiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque para el Sistema de Gestión de Mantenimiento
 * y Control de Unidades de Transporte de la Empresa "El Chino" (Lima, Perú).
 *
 * <p>Implementa una arquitectura en capas estricta (Presentation/MVC -> Service -> DAO -> PostgreSQL)
 * con documentación OpenAPI 3 (Swagger) y control de acceso basado en roles.</p>
 *
 * @author Equipo de Ingeniería IA - El Chino
 * @version 1.0.0
 */
@SpringBootApplication
public class ElChinoMantenimientoApplication {

    /**
     * Punto de entrada principal de la aplicación Spring Boot.
     *
     * @param args argumentos de línea de comandos pasados al iniciar el servidor.
     */
    public static void main(String[] args) {
        SpringApplication.run(ElChinoMantenimientoApplication.class, args);
    }
}
