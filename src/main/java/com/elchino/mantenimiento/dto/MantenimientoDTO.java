package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * DTO para la programación de órdenes de mantenimiento preventivo o correctivo (Implementa CUS06).
 *
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Datos para programar una orden de mantenimiento y asignar un técnico (CUS06)")
public class MantenimientoDTO {

    @Schema(description = "ID de la unidad de transporte a intervenir", example = "2")
    @NotNull(message = "Debe seleccionar una unidad")
    private Integer idUnidad;

    @Schema(description = "ID de la falla vinculada (opcional para mantenimiento preventivo)", example = "1")
    private Integer idFalla;

    @Schema(description = "ID del técnico mecánico asignado", example = "3")
    @NotNull(message = "Debe asignar un técnico responsable")
    private Integer idTecnico;

    @Schema(description = "Tipo de mantenimiento a programar", example = "CORRECTIVO",
            allowableValues = {"PREVENTIVO", "CORRECTIVO"})
    @NotBlank(message = "El tipo de mantenimiento es obligatorio")
    @Pattern(regexp = "PREVENTIVO|CORRECTIVO",
            message = "Tipo inválido. Use PREVENTIVO o CORRECTIVO")
    private String tipoMantenimiento;

    @Schema(description = "Fecha y hora programada (no debe ser fecha pasada ni cruzar horario del técnico)",
            example = "2026-11-20T09:00:00")
    @NotNull(message = "La fecha programada es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaProgramada;

    @Schema(description = "Descripción detallada del trabajo a realizar",
            example = "Cambio de pastillas de freno y calibración de sistema neumático")
    @NotBlank(message = "La descripción del trabajo es obligatoria")
    private String descripcionTrabajo;

    /**
     * Constructor por defecto.
     */
    public MantenimientoDTO() {
    }

    /**
     * Obtiene el ID de la unidad.
     *
     * @return ID de unidad.
     */
    public Integer getIdUnidad() {
        return idUnidad;
    }

    /**
     * Establece el ID de la unidad.
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
     * @param idFalla ID de falla.
     */
    public void setIdFalla(Integer idFalla) {
        this.idFalla = idFalla;
    }

    /**
     * Obtiene el ID del técnico asignado.
     *
     * @return ID del técnico.
     */
    public Integer getIdTecnico() {
        return idTecnico;
    }

    /**
     * Establece el ID del técnico asignado.
     *
     * @param idTecnico ID del técnico.
     */
    public void setIdTecnico(Integer idTecnico) {
        this.idTecnico = idTecnico;
    }

    /**
     * Obtiene el tipo de mantenimiento.
     *
     * @return {@code PREVENTIVO} o {@code CORRECTIVO}.
     */
    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    /**
     * Establece el tipo de mantenimiento.
     *
     * @param tipoMantenimiento tipo de mantenimiento.
     */
    public void setTipoMantenimiento(String tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    /**
     * Obtiene la fecha y hora programada.
     *
     * @return fecha y hora programada.
     */
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    /**
     * Establece la fecha y hora programada.
     *
     * @param fechaProgramada fecha y hora programada.
     */
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    /**
     * Obtiene la descripción del trabajo.
     *
     * @return descripción del trabajo.
     */
    public String getDescripcionTrabajo() {
        return descripcionTrabajo;
    }

    /**
     * Establece la descripción del trabajo.
     *
     * @param descripcionTrabajo descripción del trabajo.
     */
    public void setDescripcionTrabajo(String descripcionTrabajo) {
        this.descripcionTrabajo = descripcionTrabajo;
    }
}
