package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dto.ReporteResumenDTO;

/**
 * Interfaz de lógica de negocio para la generación de indicadores ejecutivos,
 * tablero de control y reportes para toma de decisiones (Implementa CUS11 y CUS12).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
public interface IReporteService {

    /**
     * Consolida los indicadores operativos de disponibilidad de flota, fallas críticas,
     * mantenimientos pendientes, alertas de stock e inversión acumulada (CUS11 y CUS12).
     *
     * @return objeto {@link ReporteResumenDTO} con las métricas actualizadas.
     */
    ReporteResumenDTO obtenerResumenEjecutivo();
}
