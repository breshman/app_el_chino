-- =============================================================================
-- SCRIPT DML (DATOS SEMILLA): EMPRESA DE TRANSPORTE "EL CHINO" (LIMA, PERÚ)
-- Contraseña común de prueba para todos los usuarios semilla: ElChino2026!
-- Hash BCrypt ($2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a -> "admin123")
-- =============================================================================

-- 1. USUARIOS POR ROL (Contraseña en texto plano para pruebas: admin123)
INSERT INTO usuario (username, password_hash, nombres, apellidos, email, rol, estado, intentos_fallidos)
VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Carlos Alberto', 'Mendoza Quispe', 'cmendoza@elchino.pe', 'ADMINISTRADOR', 'ACTIVO', 0),
('jefe.taller', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Roberto', 'Huamán Rojas', 'rhuaman@elchino.pe', 'RESPONSABLE_MANTENIMIENTO', 'ACTIVO', 0),
('tecnico1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Miguel Ángel', 'Paredes Soto', 'mparedes@elchino.pe', 'TECNICO', 'ACTIVO', 0),
('tecnico2', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Jorge Luis', 'Chávez Díaz', 'jchavez@elchino.pe', 'TECNICO', 'ACTIVO', 0),
('operaciones', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Lucía Fernanda', 'Salazar Vega', 'lsalazar@elchino.pe', 'OPERACIONES', 'ACTIVO', 0),
('conductor1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Pedro Pablo', 'Cárdenas Silva', 'pcardenas@elchino.pe', 'CONDUCTOR', 'ACTIVO', 0)
ON CONFLICT (username) DO NOTHING;

-- 2. UNIDADES DE TRANSPORTE (FLOTA RUTA EVITAMIENTO - VILLA EL SALVADOR / PUENTE PIEDRA)
INSERT INTO unidad (codigo_interno, placa, marca, modelo, anio_fabricacion, kilometraje_actual, estado)
VALUES
('CH-101', 'A1B-710', 'Mercedes-Benz', 'OF-1721 Euro V', 2021, 148500, 'OPERATIVA'),
('CH-102', 'B4C-825', 'Volkswagen', '17.230 OD', 2020, 182300, 'EN_MANTENIMIENTO'),
('CH-103', 'C9D-912', 'Mercedes-Benz', 'LO-916', 2022, 95400, 'OPERATIVA'),
('CH-104', 'D2F-744', 'King Long', 'XMQ6127G GNV', 2019, 231000, 'FUERA_DE_SERVICIO'),
('CH-105', 'F7H-881', 'Modasa', 'Titán Urbano Euro V', 2023, 62100, 'OPERATIVA')
ON CONFLICT (codigo_interno) DO NOTHING;

-- 3. CATÁLOGO DE REPUESTOS DE TALLER
INSERT INTO repuesto (codigo_sku, nombre, categoria, stock, stock_minimo, precio_unitario, unidad_medida)
VALUES
('REP-FR-001', 'Juego de Pastillas de Freno Delantero MB', 'Frenos', 18, 6, 380.00, 'JUEGO'),
('REP-MT-002', 'Filtro de Aceite Motor OM924LA', 'Motor', 25, 10, 85.50, 'UNIDAD'),
('REP-MT-003', 'Aceite Sintético 15W-40 Diesel (Balde 20L)', 'Lubricantes', 12, 5, 420.00, 'BALDE'),
('REP-SU-004', 'Amortiguador Neumático de Suspensión Trasera', 'Suspensión', 4, 5, 650.00, 'UNIDAD'),
('REP-EL-005', 'Alternador 24V 80A Heavy Duty', 'Sistema Eléctrico', 3, 3, 980.00, 'UNIDAD'),
('REP-TR-006', 'Kit de Embrague Reforzado 395mm', 'Transmisión', 6, 4, 1450.00, 'KIT')
ON CONFLICT (codigo_sku) DO NOTHING;

-- 4. INSPECCIÓN TÉCNICA INICIAL
INSERT INTO inspeccion (id_unidad, id_inspector, tipo_inspeccion, fecha_inspeccion, kilometraje_registrado, observaciones, resultado)
SELECT 2, 2, 'RUTINARIA', CURRENT_TIMESTAMP - INTERVAL '2 days', 182300,
       'Se detecta pérdida de presión en circuito neumático de frenos traseros y desgaste excesivo de pastillas.',
       'NO_CONFORME'
WHERE NOT EXISTS (SELECT 1 FROM inspeccion WHERE id_unidad = 2);

-- 5. FALLAS REPORTADAS
INSERT INTO falla (id_unidad, id_usuario_reporta, id_inspeccion, titulo, descripcion, sistema_afectado, prioridad, estado, fecha_reporte)
SELECT 2, 6, 1, 'Fuga de aire y desgaste en sistema de frenos', 'El pedal de freno requiere mayor recorrido al detenerse en paraderos de Vía de Evitamiento.', 'Frenos', 'ALTA', 'EN_ATENCION', CURRENT_TIMESTAMP - INTERVAL '2 days'
WHERE NOT EXISTS (SELECT 1 FROM falla WHERE id_unidad = 2);

INSERT INTO falla (id_unidad, id_usuario_reporta, id_inspeccion, titulo, descripcion, sistema_afectado, prioridad, estado, fecha_reporte)
SELECT 4, 6, NULL, 'Recalentamiento crítico de motor en subida', 'Temperatura supera los 105°C y pérdida de potencia en ruta.', 'Motor', 'CRITICA', 'REPORTADA', CURRENT_TIMESTAMP - INTERVAL '1 day'
WHERE NOT EXISTS (SELECT 1 FROM falla WHERE id_unidad = 4);

-- 6. MANTENIMIENTOS PROGRAMADOS
INSERT INTO mantenimiento (id_unidad, id_falla, id_tecnico, id_responsable, tipo_mantenimiento, fecha_programada, fecha_inicio, descripcion_trabajo, estado)
SELECT 2, 1, 3, 2, 'CORRECTIVO', CURRENT_TIMESTAMP + INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour',
       'Reemplazo de juego de pastillas de freno, calibración de válvulas neumáticas y purga de línea.', 'EN_PROCESO'
WHERE NOT EXISTS (SELECT 1 FROM mantenimiento WHERE id_unidad = 2);

INSERT INTO mantenimiento (id_unidad, id_falla, id_tecnico, id_responsable, tipo_mantenimiento, fecha_programada, descripcion_trabajo, estado)
SELECT 1, NULL, 4, 2, 'PREVENTIVO', CURRENT_TIMESTAMP + INTERVAL '1 day',
       'Mantenimiento preventivo de 150,000 Km: cambio de aceite 15W-40 y filtros.', 'PROGRAMADO'
WHERE NOT EXISTS (SELECT 1 FROM mantenimiento WHERE id_unidad = 1);

-- 7. HISTORIAL DE MANTENIMIENTO (AUDITORÍA INICIAL)
INSERT INTO historial_mantenimiento (id_unidad, id_mantenimiento, id_usuario, tipo_evento, estado_anterior_unidad, estado_nuevo_unidad, descripcion_evento, fecha_evento)
SELECT 2, 1, 2, 'INGRESO_MANTENIMIENTO', 'OPERATIVA', 'EN_MANTENIMIENTO',
       'Unidad CH-102 pasa a EN_MANTENIMIENTO por falla de prioridad ALTA en sistema de frenos.',
       CURRENT_TIMESTAMP - INTERVAL '2 days'
WHERE NOT EXISTS (SELECT 1 FROM historial_mantenimiento WHERE id_unidad = 2);
