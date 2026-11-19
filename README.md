# 🚌 Sistema de Gestión de Mantenimiento y Control de Unidades de Transporte – Empresa "El Chino" (Lima, Perú)

Sistema Web Enterprise desarrollado en **Java 17/21 LTS + Spring Boot 3.3.5 + PostgreSQL + Thymeleaf + Bootstrap 5 + SpringDoc OpenAPI 3 (Swagger)** implementando estrictamente los patrones **Arquitectura en Capas**, **MVC** y **DAO (`JdbcTemplate`)**.

---

## 🚀 1. Requisitos y Guía de Despliegue

### Prerrequisitos
* **JDK:** Java 17 o Java 21 (LTS).
* **Gestor de Construcción:** Apache Maven 3.9+.
* **Motor de Base de Datos:** PostgreSQL 14+ (instalado en `/Library/PostgreSQL/16/bin/psql` o vía Docker).

### Paso 1: Crear la Base de Datos en PostgreSQL
```bash
psql -U postgres -c "CREATE DATABASE el_chino_mantenimiento;"
```
*(Al iniciar la aplicación, Spring Boot ejecutará automáticamente `src/main/resources/schema.sql` y `src/main/resources/data.sql`).*

### Paso 2: Ejecutar Pruebas Unitarias (JUnit 5 + Mockito)
```bash
mvn clean test
```

### Paso 3: Levantar el Servidor Spring Boot
```bash
mvn spring-boot:run
```

---

## 🔗 2. Accesos Principales y Documentación Swagger (OpenAPI 3)

| Recurso | URL Local | Descripción |
| :--- | :--- | :--- |
| **Aplicación Web MVC** | `http://localhost:8080/login` | Interfaz web adaptativa por rol (escritorio y tablets de taller) |
| **Swagger UI (OpenAPI 3)** | `http://localhost:8080/swagger-ui.html` | Documentación interactiva de los endpoints REST (`CUS01` - `CUS12`) |
| **OpenAPI JSON Spec** | `http://localhost:8080/v3/api-docs` | Especificación JSON estándar de OpenAPI 3.0 |

### 🔑 Credenciales Semilla de Prueba (`data.sql`)
Contraseña para todos los usuarios de prueba: **`admin123`** (almacenada como hash BCrypt `$2a$10$...`):
* **`admin`** -> Rol `ADMINISTRADOR` (Acceso total + Gestión de Usuarios).
* **`jefe.taller`** -> Rol `RESPONSABLE_MANTENIMIENTO` (Programación, Priorización de Fallas, Verificación, Reportes).
* **`tecnico1`** / **`tecnico2`** -> Rol `TECNICO` (Inspecciones, Ejecución de Reparaciones, Consumo de Repuestos).
* **`operaciones`** -> Rol `OPERACIONES` (Control de Unidades, Inspecciones, Historial y Reportes).
* **`conductor1`** -> Rol `CONDUCTOR` (Reporte rápido de fallas y consulta de unidades).

---

## ✅ 3. Matriz de Verificación de Control de Calidad (QA GATES - `@QAAgent`)

| ID Gate | Criterio de Aceptación Técnica y Funcional | Evidencia en el Código | Estado |
| :---: | :--- | :--- | :---: |
| **QA-01** | **Arquitectura:** No existe código SQL en los `Controller` ni lógica de negocio en los `DAO`. Las dependencias fluyen estrictamente `Controller -> Service -> DAO -> DB`. | Paquetes `controller`, `service.impl` y `dao.impl` estrictamente desacoplados mediante interfaces. | `[X]` |
| **QA-02** | **Base de Datos:** El script SQL ejecuta sin errores en PostgreSQL 14+, crea los 8 índices definidos y respeta todos los `CHECK` constraints. | `src/main/resources/schema.sql` con 9 tablas, `CHECK` constraints, `UNIQUE(id_reparacion, id_repuesto)` y 8 índices `idx_*`. | `[X]` |
| **QA-03** | **Seguridad (RNF01):** Un usuario con rol `CONDUCTOR` recibe error `403 Forbidden` si intenta acceder a `/usuarios` o `/mantenimientos/programar`. Las contraseñas en BD son hashes BCrypt. | `SecurityConfig.java`, `@PreAuthorize`, `BCryptPasswordEncoder` y `UsuarioServiceImplTest.java`. | `[X]` |
| **QA-04** | **Integridad Transaccional (CUS07/CUS08):** Al registrar una reparación con 2 repuestos y fallar el segundo por falta de stock, se revierte toda la operación y no se altera el inventario. | `ReparacionServiceImpl.registrarReparacionTransaccional` (`@Transactional`) + `ReparacionServiceImplTest.java`. | `[X]` |
| **QA-05** | **Trazabilidad (CUS10):** Todo cambio de estado de una unidad (`OPERATIVA` <-> `EN_MANTENIMIENTO` <-> `FUERA_DE_SERVICIO`) genera automáticamente una fila en `historial_mantenimiento`. | `UnidadServiceImpl`, `FallaServiceImpl`, `MantenimientoServiceImpl`, `ReparacionServiceImpl`. | `[X]` |
| **QA-06** | **Pruebas Automatizadas:** Cobertura de pruebas unitarias con JUnit 5 y Mockito para todos los métodos críticos de la capa `Service` ejecutándose con `mvn test` en verde. | `ReparacionServiceImplTest`, `MantenimientoServiceImplTest`, `FallaServiceImplTest`, `UsuarioServiceImplTest`. | `[X]` |
| **QA-07** | **Documentación Técnica (Swagger + Javadoc):** La interfaz `/swagger-ui.html` carga sin errores mostrando todos los endpoints con sus `@Operation` y `@Schema`. El 100% de las interfaces y métodos públicos poseen comentarios Javadoc completos (`@param`, `@return`, `@throws`). | `OpenApiConfig.java`, anotaciones `@Tag`, `@Operation`, `@ApiResponses`, `@Schema` y Javadoc en todas las capas. | `[X]` |
