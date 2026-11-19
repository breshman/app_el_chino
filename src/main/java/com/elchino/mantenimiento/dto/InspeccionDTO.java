package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para registrar una inspección técnica sobre una unidad (Implementa CUS04).
 * Si el resultado es {@code NO_CONFORME} o {@code CON_OBSERVACIONES}, el flujo
 * encadena automáticamente al registro de falla (CUS04 -> CUS05).
 *
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Datos para registrar una inspección técnica de unidad (CUS04)")
public class InspeccionDTO {

    @Schema(description = "ID de la unidad inspeccionada", example = "1")
    @NotNull(message = "Debe seleccionar una unidad para la inspección")
    private Integer idUnidad;

    @Schema(description = "Tipo de inspección realizada", example = "RUTINARIA",
            allowableValues = {"PREVENTIVA", "RUTINARIA", "EXTRAORDINARIA"})
    @NotBlank(message = "El tipo de inspección es obligatorio")
    @Pattern(regexp = "PREVENTIVA|RUTINARIA|EXTRAORDINARIA",
            message = "Tipo de inspección inválido")
    private String tipoInspeccion;

    @Schema(description = "Kilometraje registrado durante la inspección", example = "149200")
    @NotNull(message = "El kilometraje registrado es obligatorio")
    @Min(value = 0, message = "El kilometraje no puede ser negativo")
    private Integer kilometrajeRegistrado;

    @Schema(description = "Observaciones y hallazgos técnicos", example = "Ligera vibración en tren delantero")
    @NotBlank(message = "Las observaciones técnicas son obligatorias")
    private String observaciones;

    @Schema(description = "Resultado técnico dictaminado", example = "CON_OBSERVACIONES",
            allowableValues = {"CONFORME", "CON_OBSERVACIONES", "NO_CONFORME"})
    @NotBlank(message = "El resultado de la inspección es obligatorio")
    @Pattern(regexp = "CONFORME|CON_OBSERVACIONES|NO_CONFORME",
            message = "Resultado inválido. Use CONFORME, CON_OBSERVACIONES o NO_CONFORME")
    private String resultado;

    /**
     * Constructor por defecto.
     */
    public InspeccionDTO() {
    }

    /**
     * Obtiene el ID de la unidad.
     *
     * @return ID de la unidad.
     */
    public Integer getIdUnidad() {
        return idUnidad;
    }

    /**
     * Establece el ID de la unidad.
     *
     * @param idUnidad ID de la unidad.
     */
    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    /**
     * Obtiene el tipo de inspección.
     *
     * @return tipo de inspección.
     */
    public String getTipoInspeccion() {
        return tipoInspeccion;
    }

    /**
     * Establece el tipo de inspección.
     *
     * @param tipoInspeccion tipo de inspección.
     */
    public void setTipoInspeccion(String tipoInspeccion) {
        this.tipoInspeccion = tipoInspeccion;
    }

    /**
     * Obtiene el kilometraje registrado.
     *
     * @return kilometraje en km.
     */
    public Integer getKilometrajeRegistrado() {
        return kilometrajeRegistrado;
    }

    /**
     * Establece el kilometraje registrado.
     *
     * @param kilometrajeRegistrado kilometraje en km.
     */
    public void setKilometrajeRegistrado(Integer kilometrajeRegistrado) {
        this.kilometrajeRegistrado = kilometrajeRegistrado;
    }

    /**
     * Obtiene las observaciones.
     *
     * @return texto de observaciones.
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones.
     *
     * @param observaciones texto de observaciones.
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Obtiene el resultado dictaminado.
     *
     * @return resultado de la inspección.
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Establece el resultado dictaminado.
     *
     * @param resultado resultado de la inspección.
     */
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
