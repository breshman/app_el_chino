package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para reportar y priorizar una falla o incidencia mecánica (Implementa CUS05).
 *
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Datos para registrar una falla mecánica o eléctrica en una unidad (CUS05)")
public class FallaDTO {

    @Schema(description = "ID de la unidad afectada", example = "2")
    @NotNull(message = "La unidad afectada es obligatoria")
    private Integer idUnidad;

    @Schema(description = "ID de la inspección origen (opcional si fue reporte directo de conductor)", example = "1")
    private Integer idInspeccion;

    @Schema(description = "Título descriptivo de la falla", example = "Pérdida de presión en sistema de frenos")
    @NotBlank(message = "El título de la falla es obligatorio")
    private String titulo;

    @Schema(description = "Descripción detallada de síntomas", example = "Fuga de aire detectada al accionar el pedal de freno")
    @NotBlank(message = "La descripción de la falla es obligatoria")
    private String descripcion;

    @Schema(description = "Sistema mecánico o eléctrico afectado", example = "Frenos")
    @NotBlank(message = "El sistema afectado es obligatorio")
    private String sistemaAfectado;

    @Schema(description = "Nivel de prioridad (BAJA, MEDIA, ALTA, CRITICA)", example = "ALTA",
            allowableValues = {"BAJA", "MEDIA", "ALTA", "CRITICA"})
    @Pattern(regexp = "BAJA|MEDIA|ALTA|CRITICA",
            message = "Prioridad inválida. Use BAJA, MEDIA, ALTA o CRITICA")
    private String prioridad = "MEDIA";

    /**
     * Constructor por defecto.
     */
    public FallaDTO() {
    }

    /**
     * Obtiene el ID de la unidad afectada.
     *
     * @return ID de unidad.
     */
    public Integer getIdUnidad() {
        return idUnidad;
    }

    /**
     * Establece el ID de la unidad afectada.
     *
     * @param idUnidad ID de unidad.
     */
    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    /**
     * Obtiene el ID de la inspección origen.
     *
     * @return ID de inspección o {@code null}.
     */
    public Integer getIdInspeccion() {
        return idInspeccion;
    }

    /**
     * Establece el ID de la inspección origen.
     *
     * @param idInspeccion ID de inspección.
     */
    public void setIdInspeccion(Integer idInspeccion) {
        this.idInspeccion = idInspeccion;
    }

    /**
     * Obtiene el título de la falla.
     *
     * @return título de la falla.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título de la falla.
     *
     * @param titulo título de la falla.
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene la descripción detallada.
     *
     * @return descripción de la falla.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción detallada.
     *
     * @param descripcion descripción de la falla.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el sistema afectado.
     *
     * @return sistema afectado.
     */
    public String getSistemaAfectado() {
        return sistemaAfectado;
    }

    /**
     * Establece el sistema afectado.
     *
     * @param sistemaAfectado sistema afectado.
     */
    public void setSistemaAfectado(String sistemaAfectado) {
        this.sistemaAfectado = sistemaAfectado;
    }

    /**
     * Obtiene la prioridad de la falla.
     *
     * @return prioridad.
     */
    public String getPrioridad() {
        return prioridad;
    }

    /**
     * Establece la prioridad de la falla.
     *
     * @param prioridad prioridad.
     */
    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
}
