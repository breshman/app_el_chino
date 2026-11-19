-- =============================================================================
-- SCRIPT DDL: SISTEMA DE GESTIÓN DE MANTENIMIENTO Y CONTROL DE UNIDADES
-- EMPRESA DE TRANSPORTE URBANO "EL CHINO" (LIMA, PERÚ)
-- MOTOR: PostgreSQL 14+ | BASE DE DATOS: el_chino_mantenimiento
-- =============================================================================

-- 1. TABLA: usuario
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    rol VARCHAR(40) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO', 'TECNICO', 'OPERACIONES', 'CONDUCTOR')),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    intentos_fallidos INTEGER NOT NULL DEFAULT 0 CHECK (intentos_fallidos >= 0),
    bloqueado_hasta TIMESTAMP NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. TABLA: unidad
CREATE TABLE IF NOT EXISTS unidad (
    id_unidad SERIAL PRIMARY KEY,
    codigo_interno VARCHAR(20) NOT NULL UNIQUE,
    placa VARCHAR(15) NOT NULL UNIQUE,
    marca VARCHAR(60) NOT NULL,
    modelo VARCHAR(60) NOT NULL,
    anio_fabricacion INTEGER NOT NULL CHECK (anio_fabricacion >= 1990 AND anio_fabricacion <= 2100),
    kilometraje_actual INTEGER NOT NULL DEFAULT 0 CHECK (kilometraje_actual >= 0),
    estado VARCHAR(30) NOT NULL DEFAULT 'OPERATIVA' CHECK (estado IN ('OPERATIVA', 'EN_MANTENIMIENTO', 'FUERA_DE_SERVICIO')),
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. TABLA: inspeccion
CREATE TABLE IF NOT EXISTS inspeccion (
    id_inspeccion SERIAL PRIMARY KEY,
    id_unidad INTEGER NOT NULL REFERENCES unidad(id_unidad) ON DELETE RESTRICT,
    id_inspector INTEGER NOT NULL REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
    tipo_inspeccion VARCHAR(30) NOT NULL CHECK (tipo_inspeccion IN ('PREVENTIVA', 'RUTINARIA', 'EXTRAORDINARIA')),
    fecha_inspeccion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    kilometraje_registrado INTEGER NOT NULL CHECK (kilometraje_registrado >= 0),
    observaciones TEXT,
    resultado VARCHAR(30) NOT NULL CHECK (resultado IN ('CONFORME', 'CON_OBSERVACIONES', 'NO_CONFORME'))
);

-- 4. TABLA: falla
CREATE TABLE IF NOT EXISTS falla (
    id_falla SERIAL PRIMARY KEY,
    id_unidad INTEGER NOT NULL REFERENCES unidad(id_unidad) ON DELETE RESTRICT,
    id_usuario_reporta INTEGER NOT NULL REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
    id_inspeccion INTEGER NULL REFERENCES inspeccion(id_inspeccion) ON DELETE SET NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    sistema_afectado VARCHAR(60) NOT NULL,
    prioridad VARCHAR(20) NOT NULL DEFAULT 'MEDIA' CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA')),
    estado VARCHAR(20) NOT NULL DEFAULT 'REPORTADA' CHECK (estado IN ('REPORTADA', 'EN_ATENCION', 'RESUELTA', 'CERRADA')),
    fecha_reporte TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. TABLA: mantenimiento
CREATE TABLE IF NOT EXISTS mantenimiento (
    id_mantenimiento SERIAL PRIMARY KEY,
    id_unidad INTEGER NOT NULL REFERENCES unidad(id_unidad) ON DELETE RESTRICT,
    id_falla INTEGER NULL REFERENCES falla(id_falla) ON DELETE SET NULL,
    id_tecnico INTEGER NOT NULL REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
    id_responsable INTEGER NOT NULL REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
    tipo_mantenimiento VARCHAR(25) NOT NULL CHECK (tipo_mantenimiento IN ('PREVENTIVO', 'CORRECTIVO')),
    fecha_programada TIMESTAMP NOT NULL,
    fecha_inicio TIMESTAMP NULL,
    fecha_fin TIMESTAMP NULL,
    descripcion_trabajo TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADO' CHECK (estado IN ('PROGRAMADO', 'EN_PROCESO', 'FINALIZADO', 'CANCELADO'))
);

-- 6. TABLA: reparacion
CREATE TABLE IF NOT EXISTS reparacion (
    id_reparacion SERIAL PRIMARY KEY,
    id_mantenimiento INTEGER NOT NULL UNIQUE REFERENCES mantenimiento(id_mantenimiento) ON DELETE RESTRICT,
    id_tecnico INTEGER NOT NULL REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
    diagnostico_tecnico TEXT NOT NULL,
    acciones_realizadas TEXT NOT NULL,
    horas_hombre NUMERIC(6,2) NOT NULL DEFAULT 1.00 CHECK (horas_hombre > 0),
    costo_mano_obra NUMERIC(10,2) NOT NULL DEFAULT 0.00 CHECK (costo_mano_obra >= 0),
    resultado VARCHAR(25) NOT NULL DEFAULT 'PENDIENTE' CHECK (resultado IN ('PENDIENTE', 'SATISFACTORIO', 'REQUIERE_REVISION')),
    observaciones_verificacion TEXT,
    fecha_reparacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 7. TABLA: repuesto
CREATE TABLE IF NOT EXISTS repuesto (
    id_repuesto SERIAL PRIMARY KEY,
    codigo_sku VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    categoria VARCHAR(60) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    stock_minimo INTEGER NOT NULL DEFAULT 5 CHECK (stock_minimo >= 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    unidad_medida VARCHAR(20) NOT NULL DEFAULT 'UNIDAD'
);

-- 8. TABLA: detalle_repuesto
CREATE TABLE IF NOT EXISTS detalle_repuesto (
    id_detalle SERIAL PRIMARY KEY,
    id_reparacion INTEGER NOT NULL REFERENCES reparacion(id_reparacion) ON DELETE CASCADE,
    id_repuesto INTEGER NOT NULL REFERENCES repuesto(id_repuesto) ON DELETE RESTRICT,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0),
    CONSTRAINT uq_reparacion_repuesto UNIQUE (id_reparacion, id_repuesto)
);

-- 9. TABLA: historial_mantenimiento
CREATE TABLE IF NOT EXISTS historial_mantenimiento (
    id_historial SERIAL PRIMARY KEY,
    id_unidad INTEGER NOT NULL REFERENCES unidad(id_unidad) ON DELETE RESTRICT,
    id_mantenimiento INTEGER NULL REFERENCES mantenimiento(id_mantenimiento) ON DELETE SET NULL,
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
    tipo_evento VARCHAR(50) NOT NULL,
    estado_anterior_unidad VARCHAR(30),
    estado_nuevo_unidad VARCHAR(30) NOT NULL,
    descripcion_evento TEXT NOT NULL,
    fecha_evento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- CREACIÓN DE LOS 8 ÍNDICES DE RENDIMIENTO (QA-02)
-- =============================================================================
CREATE INDEX IF NOT EXISTS idx_unidad_estado ON unidad(estado);
CREATE INDEX IF NOT EXISTS idx_inspeccion_unidad_fecha ON inspeccion(id_unidad, fecha_inspeccion DESC);
CREATE INDEX IF NOT EXISTS idx_falla_unidad_estado ON falla(id_unidad, estado);
CREATE INDEX IF NOT EXISTS idx_falla_prioridad ON falla(prioridad);
CREATE INDEX IF NOT EXISTS idx_mantenimiento_tecnico_fecha ON mantenimiento(id_tecnico, fecha_programada);
CREATE INDEX IF NOT EXISTS idx_mantenimiento_unidad_estado ON mantenimiento(id_unidad, estado);
CREATE INDEX IF NOT EXISTS idx_repuesto_stock ON repuesto(stock, stock_minimo);
CREATE INDEX IF NOT EXISTS idx_historial_unidad_fecha ON historial_mantenimiento(id_unidad, fecha_evento DESC);
