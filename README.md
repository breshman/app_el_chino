# 🚌 Sistema de Gestión de Mantenimiento y Control de Unidades de Transporte – Empresa "El Chino" (Lima, Perú)

Sistema Web Enterprise desarrollado en **Java 17/21 LTS + Spring Boot 3.3.5 + PostgreSQL + Thymeleaf + Bootstrap 5 + SpringDoc OpenAPI 3 (Swagger)** implementando estrictamente los patrones **Arquitectura en Capas**, **MVC** y **DAO (`JdbcTemplate`)**.

---

## 🚀 1. Requisitos y Guía de Despliegue

### Prerrequisitos
- **JDK:** Java 17 o Java 21 (LTS).
- **Gestor de Construcción:** Apache Maven 3.9+.
- **Motor de Base de Datos:** PostgreSQL 14+ (local o vía Docker).

### Paso 1: Crear la Base de Datos en PostgreSQL
```cmd
psql -U postgres -c "CREATE DATABASE el_chino_mantenimiento;"
```

### Paso 2: Ejecutar Pruebas Unitarias (JUnit 5 + Mockito)
```cmd
mvn clean test
```

### Paso 3: Levantar el Servidor Spring Boot (CMD)
En el **primer arranque**, define `SQL_INIT_MODE=always` para crear las tablas y cargar los datos semilla (`schema.sql` y `data.sql`):
```cmd
set DB_PASSWORD=mantequilla
set SQL_INIT_MODE=always
mvn spring-boot:run
```
*(En arranques posteriores, ejecuta solo `set DB_PASSWORD=mantequilla` y `mvn spring-boot:run` para conservar tus datos).*

---

## 🔗 2. Accesos Principales y Documentación Swagger (OpenAPI 3)

| Recurso | URL Local | Descripción |
| :--- | :--- | :--- |
| **Aplicación Web MVC** | `http://localhost:8080/login` | Interfaz web adaptativa por rol (escritorio y tablets de taller) |
| **Swagger UI (OpenAPI 3)** | `http://localhost:8080/swagger-ui.html` | Documentación interactiva de los endpoints REST (acceso exclusivo rol `ADMINISTRADOR`) |
| **OpenAPI JSON Spec** | `http://localhost:8080/v3/api-docs` | Especificación JSON estándar de OpenAPI 3.0 |

### 🔑 Credenciales Semilla de Prueba (`data.sql`)
Contraseña inicial para todos los usuarios de prueba: **`admin123`** (almacenada como hash BCrypt `$2a$10$...`):
- **`admin`** -> Rol `ADMINISTRADOR` (Acceso total + Gestión de Usuarios + Swagger).
- **`jefe.taller`** -> Rol `RESPONSABLE_MANTENIMIENTO` (Programación, Priorización de Fallas, Verificación de Calidad, Reportes).
- **`tecnico1`** / **`tecnico2`** -> Rol `TECNICO` (Inspecciones, Ejecución de Reparaciones, Consumo de Repuestos).
- **`operaciones`** -> Rol `OPERACIONES` (Consulta de Unidades, Inspecciones, Historial y Reportes).
- **`conductor1`** -> Rol `CONDUCTOR` (Reporte rápido de fallas y consulta de unidades).

---

## ✅ 3. Matriz de Verificación de Control de Calidad (QA GATES)

| ID Gate | Criterio de Aceptación Técnica y Funcional | Evidencia en el Código | Estado |
| :---: | :--- | :--- | :---: |
| **QA-01** | **Arquitectura:** No existe código SQL en los `Controller` ni lógica de negocio en los `DAO`. Las dependencias fluyen estrictamente `Controller -> Service -> DAO -> DB`. | Paquetes `controller`, `service.impl` y `dao.impl` desacoplados mediante interfaces. | `[X]` |
| **QA-02** | **Base de Datos:** El script SQL ejecuta sin errores en PostgreSQL 14+, crea los 9 índices definidos y respeta todos los `CHECK` constraints e integridad referencial. | `src/main/resources/schema.sql` con 9 tablas, `CHECK` constraints, `UNIQUE(id_reparacion, id_repuesto)` y 9 índices `idx_*`. | `[X]` |
| **QA-03** | **Seguridad (RNF01):** Control de acceso granular por rol en rutas web y REST (`403 Forbidden` ante accesos no autorizados), bloqueo temporal por 3 intentos fallidos (15 min) y contraseñas cifradas con BCrypt. | `SecurityConfig.java`, `@PreAuthorize`, `CustomUserDetailsService`, `AuthenticationEventListener` y `BCryptPasswordEncoder`. | `[X]` |
| **QA-04** | **Integridad Transaccional (CUS07/CUS08):** Al registrar una reparación con múltiples repuestos y fallar uno por stock insuficiente, se revierte toda la transacción (`@Transactional`) sin alterar el inventario. | `ReparacionServiceImpl.registrarReparacionTransaccional` + descuento atómico en `RepuestoDAOImpl`. | `[X]` |
| **QA-05** | **Trazabilidad (CUS10):** Todo cambio de estado operativo de una unidad (`OPERATIVA` <-> `EN_MANTENIMIENTO` <-> `FUERA_DE_SERVICIO`) genera automáticamente un registro de auditoría en `historial_mantenimiento`. | `UnidadServiceImpl`, `FallaServiceImpl`, `MantenimientoServiceImpl`, `ReparacionServiceImpl`. | `[X]` |
| **QA-06** | **Pruebas Automatizadas (Alcance Core):** Pruebas unitarias con JUnit 5 y Mockito ejecutándose en verde para los flujos principales de negocio (`Reparación`, `Mantenimiento`, `Falla` y `Usuario`). *(Pendiente ampliar cobertura a `Unidad`, `Inspección`, `Repuesto` y capa web).* | `ReparacionServiceImplTest`, `MantenimientoServiceImplTest`, `FallaServiceImplTest`, `UsuarioServiceImplTest`. | `[X]` |
| **QA-07** | **Documentación Técnica (Swagger + Javadoc):** La interfaz `/swagger-ui.html` carga sin errores mostrando los endpoints REST con sus `@Operation` y `@Schema`, respaldada por comentarios Javadoc en las interfaces y servicios. | `OpenApiConfig.java`, anotaciones `@Tag`, `@Operation`, `@ApiResponse` y Javadoc en capas. | `[X]` |