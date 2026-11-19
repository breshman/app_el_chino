# 📋 Plan de Trabajo y Cronograma de Ejecución (`@ArchitectAgent`)

**Proyecto:** Sistema de Gestión de Mantenimiento y Control de Unidades de Transporte – Empresa "El Chino" (Lima, Perú)  
**Arquitectura:** Capas Estrictas (`Presentation/Web` -> `Business/Service` -> `Persistence/DAO` -> `Database`) + MVC + Patrón DAO explícito (`JdbcTemplate`).  
**Stack:** Java 21 LTS, Spring Boot 3.3.5, Spring Security 6 (BCrypt + Bloqueo 3 intentos + `@PreAuthorize`), Spring JDBC, PostgreSQL 14+, Thymeleaf + Bootstrap 5.3 + Vanilla JS, SpringDoc OpenAPI 3 (`2.6.0`), JUnit 5 + Mockito.

---

## 🗓️ Cronograma por Sprints y Orquestación de Agentes

### Sprint 1: Arquitectura Base, Configuración y Seguridad (`@ArchitectAgent`)
* **T1.1:** Definición de `pom.xml` con dependencias enterprise (`spring-boot-starter-web`, `spring-boot-starter-thymeleaf`, `spring-boot-starter-jdbc`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `postgresql`, `springdoc-openapi-starter-webmvc-ui`, `thymeleaf-extras-springsecurity6`, `spring-boot-starter-test`, `spring-security-test`).
* **T1.2:** Configuración de `application.properties` (DataSource PostgreSQL `el_chino_mantenimiento`, inicialización SQL, rutas Swagger UI `/swagger-ui.html` y `/v3/api-docs`).
* **T1.3:** Implementación de `ElChinoMantenimientoApplication.java` y `OpenApiConfig.java` (metadatos corporativos, contacto y esquema de seguridad OpenAPI 3).
* **T1.4:** Configuración de `SecurityConfig.java`, `CustomUserDetailsService.java` y control de intentos fallidos (`AuthenticationEventListener.java`) para cumplir **CUS01** y **RNF01** (bloqueo tras 3 intentos fallidos y control por roles: `ADMINISTRADOR`, `RESPONSABLE_MANTENIMIENTO`, `TECNICO`, `OPERACIONES`, `CONDUCTOR`).
* **T1.5:** Manejo centralizado de excepciones (`GlobalExceptionHandler.java`, `BusinessException.java`, `ResourceNotFoundException.java`, `StockInsuficienteException.java`, `HorarioConflictoException.java`).

### Sprint 2: Diseño Físico de Base de Datos y Modelos de Dominio (`@DatabaseAgent`)
* **T2.1:** Creación de `src/main/resources/schema.sql` con las 9 tablas oficiales (`usuario`, `unidad`, `inspeccion`, `falla`, `mantenimiento`, `reparacion`, `repuesto`, `detalle_repuesto`, `historial_mantenimiento`), restricciones `CHECK`, claves foráneas, `UNIQUE(id_reparacion, id_repuesto)` y los 8 índices de rendimiento.
* **T2.2:** Creación de `src/main/resources/data.sql` con datos semilla realistas de la flota "El Chino" (usuarios con hashes BCrypt válidos para cada rol, buses de ruta Lima, inspecciones, fallas, mantenimientos, repuestos e historial).
* **T2.3:** Codificación de las 9 entidades en `com.elchino.mantenimiento.model` con Javadoc completo y anotaciones `@Schema` de OpenAPI 3.

### Sprint 3: Capa de Persistencia (Patrón DAO) y Lógica de Negocio (`@BackendAgent`)
* **T3.1:** Creación de DTOs en `com.elchino.mantenimiento.dto` con `@Valid`, `@Schema` y Javadoc (`LoginRequestDTO`, `UsuarioDTO`, `UnidadDTO`, `InspeccionDTO`, `FallaDTO`, `MantenimientoDTO`, `ReparacionDTO`, `ItemRepuestoDTO`, `RepuestoDTO`, `ReporteResumenDTO`, `ApiResponseDTO`).
* **T3.2:** Interfaces DAO (`IUsuarioDAO`, `IUnidadDAO`, `IInspeccionDAO`, `IFallaDAO`, `IMantenimientoDAO`, `IReparacionDAO`, `IRepuestoDAO`, `IDetalleRepuestoDAO`, `IHistorialMantenimientoDAO`) e implementaciones concretas en `dao.impl` con `JdbcTemplate` y `RowMapper`s explícitos.
* **T3.3:** Implementación de reglas críticas en `service` y `service.impl`:
  * **CUS01 / RNF01:** Gestión de usuarios, cifrado BCrypt y bloqueo tras 3 intentos fallidos.
  * **CUS02 / CUS03 / CUS10:** Gestión de unidades y trazabilidad automática en `historial_mantenimiento` ante cualquier cambio de estado (`OPERATIVA`, `EN_MANTENIMIENTO`, `FUERA_DE_SERVICIO`).
  * **CUS04 -> CUS05:** Registro de inspección y encadenamiento automático a registro de falla cuando el resultado es `NO_CONFORME` o `CON_OBSERVACIONES`.
  * **CUS05:** Priorización de fallas (`CONDUCTOR` crea en `REPORTADA`; prioridades `ALTA` o `CRITICA` cambian la unidad a `EN_MANTENIMIENTO` con auditoría en historial).
  * **CUS06:** Programación de mantenimiento con validación de fecha no pasada y verificación en DAO de no cruce de horario del técnico asignado (`PROGRAMADO` / `EN_PROCESO`).
  * **CUS07 + CUS08 + CUS09:** Transacción maestra `@Transactional` de reparación: descuento atómico de stock (`StockInsuficienteException` + rollback si `stock < cantidad`), alerta de stock mínimo (`stock <= stock_minimo`), actualización de mantenimiento a `FINALIZADO`, falla a `RESUELTA`, unidad a `OPERATIVA` (si resultado es `SATISFACTORIO`) o mantenimiento en `EN_PROCESO` (si `REQUIERE_REVISION`), más registro en `historial_mantenimiento`.

### Sprint 4: Controladores MVC, API REST con Swagger y Vistas Thymeleaf (`@FullstackMVCAgent`)
* **T4.1:** Implementación de los 10 controladores en `com.elchino.mantenimiento.controller`:
  * `CtrlAutenticacion`, `CtrlUsuarios`, `CtrlUnidades`, `CtrlInspecciones`, `CtrlFallas`, `CtrlMantenimientos`, `CtrlReparaciones`, `CtrlRepuestos`, `CtrlHistorial`, `CtrlReportes`.
  * Endpoints híbridos: vistas MVC Thymeleaf y endpoints REST `/api/v1/...` documentados con `@Tag`, `@Operation`, `@ApiResponses`.
* **T4.2:** Vistas HTML5 + Thymeleaf + Bootstrap 5 + Vanilla JS en `src/main/resources/templates/`:
  * `fragments/layout.html` (navegación lateral por rol, alertas, topbar), `FrmLogin.html`, `FrmDashboard.html`, `FrmUsuarios.html`, `FrmUnidades.html`, `FrmInspeccion.html`, `FrmFalla.html`, `FrmProgramacion.html`, `FrmReparacion.html` (con selector dinámico de repuestos), `FrmRepuestos.html`, `FrmHistorial.html`, `FrmReportes.html`.

### Sprint 5: Pruebas Unitarias, Auditoría de Calidad (QA Gates) y Documentación (`@QAAgent`)
* **T5.1:** Desarrollo de pruebas unitarias con JUnit 5 y Mockito en `src/test/java/com/elchino/mantenimiento/service/` verificando `QA-01` a `QA-07` (incluyendo rollback transaccional por stock insuficiente, cruce de horarios de técnicos, bloqueo de seguridad y trazabilidad en historial).
* **T5.2:** Elaboración de `README.md` con guía de despliegue, credenciales de prueba, arquitectura, matriz de QA Gates firmada y acceso a Swagger UI.

---

## 📂 Árbol de Directorios del Proyecto

```text
el_chino/
├── pom.xml
├── README.md
├── PLAN_DE_TRABAJO.md
└── src/
    ├── main/
    │   ├── java/com/elchino/mantenimiento/
    │   │   ├── ElChinoMantenimientoApplication.java
    │   │   ├── config/
    │   │   │   ├── OpenApiConfig.java
    │   │   │   ├── SecurityConfig.java
    │   │   │   ├── CustomUserDetailsService.java
    │   │   │   └── AuthenticationEventListener.java
    │   │   ├── controller/
    │   │   │   ├── CtrlAutenticacion.java
    │   │   │   ├── CtrlUsuarios.java
    │   │   │   ├── CtrlUnidades.java
    │   │   │   ├── CtrlInspecciones.java
    │   │   │   ├── CtrlFallas.java
    │   │   │   ├── CtrlMantenimientos.java
    │   │   │   ├── CtrlReparaciones.java
    │   │   │   ├── CtrlRepuestos.java
    │   │   │   ├── CtrlHistorial.java
    │   │   │   └── CtrlReportes.java
    │   │   ├── dto/
    │   │   │   ├── ApiResponseDTO.java
    │   │   │   ├── UsuarioDTO.java
    │   │   │   ├── UnidadDTO.java
    │   │   │   ├── InspeccionDTO.java
    │   │   │   ├── FallaDTO.java
    │   │   │   ├── MantenimientoDTO.java
    │   │   │   ├── ReparacionDTO.java
    │   │   │   ├── ItemRepuestoDTO.java
    │   │   │   ├── RepuestoDTO.java
    │   │   │   └── ReporteResumenDTO.java
    │   │   ├── service/
    │   │   │   ├── IUsuarioService.java
    │   │   │   ├── IUnidadService.java
    │   │   │   ├── IInspeccionService.java
    │   │   │   ├── IFallaService.java
    │   │   │   ├── IMantenimientoService.java
    │   │   │   ├── IReparacionService.java
    │   │   │   ├── IRepuestoService.java
    │   │   │   ├── IHistorialService.java
    │   │   │   ├── IReporteService.java
    │   │   │   └── impl/
    │   │   │       ├── UsuarioServiceImpl.java
    │   │   │       ├── UnidadServiceImpl.java
    │   │   │       ├── InspeccionServiceImpl.java
    │   │   │       ├── FallaServiceImpl.java
    │   │   │       ├── MantenimientoServiceImpl.java
    │   │   │       ├── ReparacionServiceImpl.java
    │   │   │       ├── RepuestoServiceImpl.java
    │   │   │       ├── HistorialServiceImpl.java
    │   │   │       └── ReporteServiceImpl.java
    │   │   ├── dao/
    │   │   │   ├── IUsuarioDAO.java
    │   │   │   ├── IUnidadDAO.java
    │   │   │   ├── IInspeccionDAO.java
    │   │   │   ├── IFallaDAO.java
    │   │   │   ├── IMantenimientoDAO.java
    │   │   │   ├── IReparacionDAO.java
    │   │   │   ├── IRepuestoDAO.java
    │   │   │   ├── IDetalleRepuestoDAO.java
    │   │   │   ├── IHistorialMantenimientoDAO.java
    │   │   │   └── impl/
    │   │   │       ├── UsuarioDAOImpl.java
    │   │   │       ├── UnidadDAOImpl.java
    │   │   │       ├── InspeccionDAOImpl.java
    │   │   │       ├── FallaDAOImpl.java
    │   │   │       ├── MantenimientoDAOImpl.java
    │   │   │       ├── ReparacionDAOImpl.java
    │   │   │       ├── RepuestoDAOImpl.java
    │   │   │       ├── DetalleRepuestoDAOImpl.java
    │   │   │       └── HistorialMantenimientoDAOImpl.java
    │   │   ├── model/
    │   │   │   ├── Usuario.java
    │   │   │   ├── Unidad.java
    │   │   │   ├── Inspeccion.java
    │   │   │   ├── Falla.java
    │   │   │   ├── Mantenimiento.java
    │   │   │   ├── Reparacion.java
    │   │   │   ├── Repuesto.java
    │   │   │   ├── DetalleRepuesto.java
    │   │   │   └── HistorialMantenimiento.java
    │   │   └── exception/
    │   │       ├── BusinessException.java
    │   │       ├── ResourceNotFoundException.java
    │   │       ├── StockInsuficienteException.java
    │   │       ├── HorarioConflictoException.java
    │   │       └── GlobalExceptionHandler.java
    │   └── resources/
    │       ├── application.properties
    │       ├── schema.sql
    │       ├── data.sql
    │       ├── static/
    │       │   ├── css/styles.css
    │       │   └── js/app.js
    │       └── templates/
    │           ├── fragments/layout.html
    │           ├── FrmLogin.html
    │           ├── FrmDashboard.html
    │           ├── FrmUsuarios.html
    │           ├── FrmUnidades.html
    │           ├── FrmInspeccion.html
    │           ├── FrmFalla.html
    │           ├── FrmProgramacion.html
    │           ├── FrmReparacion.html
    │           ├── FrmRepuestos.html
    │           ├── FrmHistorial.html
    │           └── FrmReportes.html
    └── test/
        └── java/com/elchino/mantenimiento/service/
            ├── ReparacionServiceImplTest.java
            ├── MantenimientoServiceImplTest.java
            ├── FallaServiceImplTest.java
            └── UsuarioServiceImplTest.java
```
