package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO maestro para registrar una reparación, sus repuestos consumidos y
 * la verificación de calidad final en una sola operación transaccional
 * (Implementa CUS07, CUS08 y CUS09).
 *
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Datos para registrar una reparación con consumo transaccional de repuestos y verificación (CUS07, CUS08, CUS09)")
public class ReparacionDTO {

    @Schema(description = "ID de la orden de mantenimiento que se está ejecutando", example = "1")
    @NotNull(message = "El ID de mantenimiento es obligatorio")
    private Integer idMantenimiento;

    @Schema(description = "Diagnóstico técnico determinado en taller",
            example = "Pastillas delanteras cristalizadas y válvula relé descalibrada")
    @NotBlank(message = "El diagnóstico técnico es obligatorio")
    private String diagnosticoTecnico;

    @Schema(description = "Acciones técnicas realizadas sobre la unidad",
            example = "Sustitución de pastillas MB y regulación de presión a 8.5 bar")
    @NotBlank(message = "Las acciones realizadas son obligatorias")
    private String accionesRealizadas;

    @Schema(description = "Horas-hombre invertidas", example = "3.50")
    @NotNull(message = "Las horas-hombre son obligatorias")
    @DecimalMin(value = "0.1", message = "Las horas-hombre deben ser mayores a 0")
    private BigDecimal horasHombre = new BigDecimal("1.00");

    @Schema(description = "Costo de mano de obra en Soles (PEN)", example = "180.00")
    @NotNull(message = "El costo de mano de obra es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo de mano de obra no puede ser negativo")
    private BigDecimal costoManoObra = BigDecimal.ZERO;

    @Schema(description = "Resultado de verificación técnica (PENDIENTE, SATISFACTORIO, REQUIERE_REVISION)",
            example = "SATISFACTORIO",
            allowableValues = {"PENDIENTE", "SATISFACTORIO", "REQUIERE_REVISION"})
    @NotBlank(message = "El resultado de verificación es obligatorio")
    @Pattern(regexp = "PENDIENTE|SATISFACTORIO|REQUIERE_REVISION",
            message = "Resultado inválido. Use PENDIENTE, SATISFACTORIO o REQUIERE_REVISION")
    private String resultado = "SATISFACTORIO";

    @Schema(description = "Observaciones de la prueba de ruta o control de calidad",
            example = "Frenado uniforme en prueba de patio, unidad apta para servicio")
    private String observacionesVerificacion;

    @Schema(description = "Lista de repuestos utilizados para descuento transaccional de stock")
    @Valid
    private List<ItemRepuestoDTO> repuestos = new ArrayList<>();

    /**
     * Constructor por defecto.
     */
    public ReparacionDTO() {
    }

    /**
     * Obtiene el ID del mantenimiento.
     *
     * @return ID de mantenimiento.
     */
    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    /**
     * Establece el ID del mantenimiento.
     *
     * @param idMantenimiento ID de mantenimiento.
     */
    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    /**
     * Obtiene el diagnóstico técnico.
     *
     * @return diagnóstico técnico.
     */
    public String getDiagnosticoTecnico() {
        return diagnosticoTecnico;
    }

    /**
     * Establece el diagnóstico técnico.
     *
     * @param diagnosticoTecnico diagnóstico técnico.
     */
    public void setDiagnosticoTecnico(String diagnosticoTecnico) {
        this.diagnosticoTecnico = diagnosticoTecnico;
    }

    /**
     * Obtiene las acciones realizadas.
     *
     * @return acciones realizadas.
     */
    public String getAccionesRealizadas() {
        return accionesRealizadas;
    }

    /**
     * Establece las acciones realizadas.
     *
     * @param accionesRealizadas acciones realizadas.
     */
    public void setAccionesRealizadas(String accionesRealizadas) {
        this.accionesRealizadas = accionesRealizadas;
    }

    /**
     * Obtiene las horas-hombre empleadas.
     *
     * @return horas-hombre.
     */
    public BigDecimal getHorasHombre() {
        return horasHombre;
    }

    /**
     * Establece las horas-hombre empleadas.
     *
     * @param horasHombre horas-hombre.
     */
    public void setHorasHombre(BigDecimal horasHombre) {
        this.horasHombre = horasHombre;
    }

    /**
     * Obtiene el costo de mano de obra.
     *
     * @return costo de mano de obra.
     */
    public BigDecimal getCostoManoObra() {
        return costoManoObra;
    }

    /**
     * Establece el costo de mano de obra.
     *
     * @param costoManoObra costo de mano de obra.
     */
    public void setCostoManoObra(BigDecimal costoManoObra) {
        this.costoManoObra = costoManoObra;
    }

    /**
     * Obtiene el resultado de verificación.
     *
     * @return resultado.
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Establece el resultado de verificación.
     *
     * @param resultado resultado.
     */
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    /**
     * Obtiene las observaciones de verificación.
     *
     * @return observaciones.
     */
    public String getObservacionesVerificacion() {
        return observacionesVerificacion;
    }

    /**
     * Establece las observaciones de verificación.
     *
     * @param observacionesVerificacion observaciones.
     */
    public void setObservacionesVerificacion(String observacionesVerificacion) {
        this.observacionesVerificacion = observacionesVerificacion;
    }

    /**
     * Obtiene la lista de repuestos a descontar.
     *
     * @return lista de {@link ItemRepuestoDTO}.
     */
    public List<ItemRepuestoDTO> getRepuestos() {
        return repuestos;
    }

    /**
     * Establece la lista de repuestos a descontar.
     *
     * @param repuestos lista de {@link ItemRepuestoDTO}.
     */
    public void setRepuestos(List<ItemRepuestoDTO> repuestos) {
        this.repuestos = repuestos;
    }
}
