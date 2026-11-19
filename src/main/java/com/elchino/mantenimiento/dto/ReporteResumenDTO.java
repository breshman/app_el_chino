package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * DTO que consolida los indicadores clave de rendimiento (KPIs) gerenciales
 * y operativos de la flota "El Chino" para el módulo de Reportes y Dashboard
 * (Implementa CUS11 y CUS12).
 *
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Resumen ejecutivo de indicadores operativos y costos de mantenimiento (CUS11 y CUS12)")
public class ReporteResumenDTO {

    @Schema(description = "Total de unidades registradas en la flota", example = "5")
    private int totalUnidades;

    @Schema(description = "Unidades en estado OPERATIVA", example = "3")
    private int unidadesOperativas;

    @Schema(description = "Unidades actualmente EN_MANTENIMIENTO", example = "1")
    private int unidadesEnMantenimiento;

    @Schema(description = "Unidades FUERA_DE_SERVICIO", example = "1")
    private int unidadesFueraDeServicio;

    @Schema(description = "Total de fallas pendientes o en atención", example = "2")
    private int fallasActivas;

    @Schema(description = "Fallas de prioridad ALTA o CRITICA activas", example = "2")
    private int fallasCriticasOAltas;

    @Schema(description = "Mantenimientos en estado PROGRAMADO o EN_PROCESO", example = "2")
    private int mantenimientosPendientes;

    @Schema(description = "Mantenimientos en estado FINALIZADO", example = "4")
    private int mantenimientosFinalizados;

    @Schema(description = "Cantidad de repuestos con stock menor o igual al stock mínimo", example = "2")
    private int repuestosBajoStock;

    @Schema(description = "Inversión acumulada en mano de obra y repuestos en Soles (PEN)", example = "3450.00")
    private BigDecimal costoTotalMantenimiento = BigDecimal.ZERO;

    /**
     * Constructor por defecto.
     */
    public ReporteResumenDTO() {
    }

    /**
     * Calcula el porcentaje de disponibilidad operativa de la flota.
     *
     * @return porcentaje entre 0.0 y 100.0.
     */
    public double getDisponibilidadFlotaPorcentaje() {
        if (totalUnidades == 0) {
            return 0.0;
        }
        return Math.round((unidadesOperativas * 1000.0) / totalUnidades) / 10.0;
    }

    /**
     * Obtiene el total de unidades.
     *
     * @return total de unidades.
     */
    public int getTotalUnidades() {
        return totalUnidades;
    }

    /**
     * Establece el total de unidades.
     *
     * @param totalUnidades total de unidades.
     */
    public void setTotalUnidades(int totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    /**
     * Obtiene la cantidad de unidades operativas.
     *
     * @return unidades operativas.
     */
    public int getUnidadesOperativas() {
        return unidadesOperativas;
    }

    /**
     * Establece la cantidad de unidades operativas.
     *
     * @param unidadesOperativas unidades operativas.
     */
    public void setUnidadesOperativas(int unidadesOperativas) {
        this.unidadesOperativas = unidadesOperativas;
    }

    /**
     * Obtiene la cantidad de unidades en mantenimiento.
     *
     * @return unidades en mantenimiento.
     */
    public int getUnidadesEnMantenimiento() {
        return unidadesEnMantenimiento;
    }

    /**
     * Establece la cantidad de unidades en mantenimiento.
     *
     * @param unidadesEnMantenimiento unidades en mantenimiento.
     */
    public void setUnidadesEnMantenimiento(int unidadesEnMantenimiento) {
        this.unidadesEnMantenimiento = unidadesEnMantenimiento;
    }

    /**
     * Obtiene la cantidad de unidades fuera de servicio.
     *
     * @return unidades fuera de servicio.
     */
    public int getUnidadesFueraDeServicio() {
        return unidadesFueraDeServicio;
    }

    /**
     * Establece la cantidad de unidades fuera de servicio.
     *
     * @param unidadesFueraDeServicio unidades fuera de servicio.
     */
    public void setUnidadesFueraDeServicio(int unidadesFueraDeServicio) {
        this.unidadesFueraDeServicio = unidadesFueraDeServicio;
    }

    /**
     * Obtiene la cantidad de fallas activas.
     *
     * @return fallas activas.
     */
    public int getFallasActivas() {
        return fallasActivas;
    }

    /**
     * Establece la cantidad de fallas activas.
     *
     * @param fallasActivas fallas activas.
     */
    public void setFallasActivas(int fallasActivas) {
        this.fallasActivas = fallasActivas;
    }

    /**
     * Obtiene las fallas críticas o altas activas.
     *
     * @return fallas críticas o altas.
     */
    public int getFallasCriticasOAltas() {
        return fallasCriticasOAltas;
    }

    /**
     * Establece las fallas críticas o altas activas.
     *
     * @param fallasCriticasOAltas fallas críticas o altas.
     */
    public void setFallasCriticasOAltas(int fallasCriticasOAltas) {
        this.fallasCriticasOAltas = fallasCriticasOAltas;
    }

    /**
     * Obtiene los mantenimientos pendientes.
     *
     * @return mantenimientos pendientes.
     */
    public int getMantenimientosPendientes() {
        return mantenimientosPendientes;
    }

    /**
     * Establece los mantenimientos pendientes.
     *
     * @param mantenimientosPendientes mantenimientos pendientes.
     */
    public void setMantenimientosPendientes(int mantenimientosPendientes) {
        this.mantenimientosPendientes = mantenimientosPendientes;
    }

    /**
     * Obtiene los mantenimientos finalizados.
     *
     * @return mantenimientos finalizados.
     */
    public int getMantenimientosFinalizados() {
        return mantenimientosFinalizados;
    }

    /**
     * Establece los mantenimientos finalizados.
     *
     * @param mantenimientosFinalizados mantenimientos finalizados.
     */
    public void setMantenimientosFinalizados(int mantenimientosFinalizados) {
        this.mantenimientosFinalizados = mantenimientosFinalizados;
    }

    /**
     * Obtiene la cantidad de repuestos con bajo stock.
     *
     * @return repuestos bajo stock.
     */
    public int getRepuestosBajoStock() {
        return repuestosBajoStock;
    }

    /**
     * Establece la cantidad de repuestos con bajo stock.
     *
     * @param repuestosBajoStock repuestos bajo stock.
     */
    public void setRepuestosBajoStock(int repuestosBajoStock) {
        this.repuestosBajoStock = repuestosBajoStock;
    }

    /**
     * Obtiene el costo total acumulado de mantenimiento.
     *
     * @return costo total en Soles.
     */
    public BigDecimal getCostoTotalMantenimiento() {
        return costoTotalMantenimiento;
    }

    /**
     * Establece el costo total acumulado de mantenimiento.
     *
     * @param costoTotalMantenimiento costo total en Soles.
     */
    public void setCostoTotalMantenimiento(BigDecimal costoTotalMantenimiento) {
        this.costoTotalMantenimiento = costoTotalMantenimiento;
    }
}
