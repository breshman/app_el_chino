package com.elchino.mantenimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Entidad que mapea la tabla {@code mantenimiento} en PostgreSQL.
 * Representa una orden de trabajo de mantenimiento preventivo o correctivo
 * programada para una unidad y asignada a un técnico (Implementa CUS06 y CUS11).
 *
 * @author Ingeniero de Base de Datos (@DatabaseAgent)
 * @version 1.0.0
 */
@Schema(description = "Orden de mantenimiento preventivo o correctivo programada para una unidad")
public class Mantenimiento {

    @Schema(description = "Identificador único del mantenimiento", example = "1")
    private Integer idMantenimiento;

    @Schema(description = "ID de la unidad intervenida", example = "2")
    private Integer idUnidad;

    @Schema(description = "ID de la falla asociada (en caso de mantenimiento correctivo)", example = "1")
    private Integer idFalla;

    @Schema(description = "ID del técnico mecánico asignado", example = "3")
    private Integer idTecnico;

    @Schema(description = "ID del responsable de mantenimiento que autorizó la orden", example = "2")
    private Integer idResponsable;

    @Schema(description = "Tipo de mantenimiento (PREVENTIVO, CORRECTIVO)", example = "CORRECTIVO")
    private String tipoMantenimiento;

    @Schema(description = "Fecha y hora programada para la intervención")
    private LocalDateTime fechaProgramada;

    @Schema(description = "Fecha y hora real de inicio de trabajos")
    private LocalDateTime fechaInicio;

    @Schema(description = "Fecha y hora real de finalización de trabajos")
    private LocalDateTime fechaFin;

    @Schema(description = "Descripción técnica de las labores a ejecutar")
    private String descripcionTrabajo;

    @Schema(description = "Estado de la orden (PROGRAMADO, EN_PROCESO, FINALIZADO, CANCELADO)", example = "EN_PROCESO")
    private String estado;

    // Campos auxiliares de visualización (JOIN)
    @Schema(description = "Código y placa de la unidad", example = "CH-102 (B4C-825)")
    private String unidadDescripcion;

    @Schema(description = "Nombre completo del técnico asignado", example = "Miguel Ángel Paredes Soto")
    private String tecnicoNombre;

    @Schema(description = "Título de la falla vinculada, si aplica", example = "Fuga de aire y desgaste en sistema de frenos")
    private String fallaTitulo;

    /**
     * Constructor por defecto.
     */
    public Mantenimiento() {
    }

    /**
     * Obtiene el ID del mantenimiento.
     *
     * @return identificador único.
     */
    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    /**
     * Establece el ID del mantenimiento.
     *
     * @param idMantenimiento identificador único.
     */
    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    /**
     * Obtiene el ID de la unidad asignada.
     *
     * @return ID de unidad.
     */
    public Integer getIdUnidad() {
        return idUnidad;
    }

    /**
     * Establece el ID de la unidad asignada.
     *
     * @param idUnidad ID de unidad.
     */
    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    /**
     * Obtiene el ID de la falla asociada.
     *
     * @return ID de falla o {@code null}.
     */
    public Integer getIdFalla() {
        return idFalla;
    }

    /**
     * Establece el ID de la falla asociada.
     *
     * @param idFalla ID de falla o {@code null}.
     */
    public void setIdFalla(Integer idFalla) {
        this.idFalla = idFalla;
    }

    /**
     * Obtiene el ID del técnico asignado.
     *
     * @return ID de usuario técnico.
     */
    public Integer getIdTecnico() {
        return idTecnico;
    }

    /**
     * Establece el ID del técnico asignado.
     *
     * @param idTecnico ID de usuario técnico.
     */
    public void setIdTecnico(Integer idTecnico) {
        this.idTecnico = idTecnico;
    }

    /**
     * Obtiene el ID del responsable que programó el mantenimiento.
     *
     * @return ID de usuario responsable.
     */
    public Integer getIdResponsable() {
        return idResponsable;
    }

    /**
     * Establece el ID del responsable que programó el mantenimiento.
     *
     * @param idResponsable ID de usuario responsable.
     */
    public void setIdResponsable(Integer idResponsable) {
        this.idResponsable = idResponsable;
    }

    /**
     * Obtiene el tipo de mantenimiento ({@code PREVENTIVO} o {@code CORRECTIVO}).
     *
     * @return tipo de mantenimiento.
     */
    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    /**
     * Establece el tipo de mantenimiento.
     *
     * @param tipoMantenimiento {@code PREVENTIVO} o {@code CORRECTIVO}.
     */
    public void setTipoMantenimiento(String tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    /**
     * Obtiene la fecha y hora programada.
     *
     * @return timestamp programado.
     */
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    /**
     * Establece la fecha y hora programada.
     *
     * @param fechaProgramada timestamp programado.
     */
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    /**
     * Obtiene la fecha y hora de inicio real.
     *
     * @return timestamp de inicio o {@code null}.
     */
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece la fecha y hora de inicio real.
     *
     * @param fechaInicio timestamp de inicio.
     */
    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene la fecha y hora de finalización real.
     *
     * @return timestamp de fin o {@code null}.
     */
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    /**
     * Establece la fecha y hora de finalización real.
     *
     * @param fechaFin timestamp de fin.
     */
    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Obtiene la descripción del trabajo programado.
     *
     * @return descripción de labores.
     */
    public String getDescripcionTrabajo() {
        return descripcionTrabajo;
    }

    /**
     * Establece la descripción del trabajo programado.
     *
     * @param descripcionTrabajo descripción de labores.
     */
    public void setDescripcionTrabajo(String descripcionTrabajo) {
        this.descripcionTrabajo = descripcionTrabajo;
    }

    /**
     * Obtiene el estado del mantenimiento ({@code PROGRAMADO}, {@code EN_PROCESO}, {@code FINALIZADO}, {@code CANCELADO}).
     *
     * @return estado actual.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado del mantenimiento.
     *
     * @param estado nuevo estado.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la descripción de la unidad asociada.
     *
     * @return código interno y placa.
     */
    public String getUnidadDescripcion() {
        return unidadDescripcion;
    }

    /**
     * Establece la descripción de la unidad asociada.
     *
     * @param unidadDescripcion código interno y placa.
     */
    public void setUnidadDescripcion(String unidadDescripcion) {
        this.unidadDescripcion = unidadDescripcion;
    }

    /**
     * Obtiene el nombre completo del técnico asignado.
     *
     * @return nombre del técnico.
     */
    public String getTecnicoNombre() {
        return tecnicoNombre;
    }

    /**
     * Establece el nombre completo del técnico asignado.
     *
     * @param tecnicoNombre nombre del técnico.
     */
    public void setTecnicoNombre(String tecnicoNombre) {
        this.tecnicoNombre = tecnicoNombre;
    }

    /**
     * Obtiene el título de la falla asociada.
     *
     * @return título de la falla o {@code null}.
     */
    public String getFallaTitulo() {
        return fallaTitulo;
    }

    /**
     * Establece el título de la falla asociada.
     *
     * @param fallaTitulo título de la falla.
     */
    public void setFallaTitulo(String fallaTitulo) {
        this.fallaTitulo = fallaTitulo;
    }
}
