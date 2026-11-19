package com.elchino.mantenimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que mapea la tabla {@code reparacion} en PostgreSQL.
 * Registra la ejecución técnica de un mantenimiento, el diagnóstico, acciones,
 * costos de mano de obra, repuestos utilizados y resultado de verificación
 * (Implementa CUS07, CUS08 y CUS09).
 *
 * @author Ingeniero de Base de Datos (@DatabaseAgent)
 * @version 1.0.0
 */
@Schema(description = "Ejecución técnica de una reparación vinculada a una orden de mantenimiento")
public class Reparacion {

    @Schema(description = "Identificador único de la reparación", example = "1")
    private Integer idReparacion;

    @Schema(description = "ID de la orden de mantenimiento asociada (relación 1:1)", example = "1")
    private Integer idMantenimiento;

    @Schema(description = "ID del técnico ejecutor", example = "3")
    private Integer idTecnico;

    @Schema(description = "Diagnóstico técnico detallado en taller")
    private String diagnosticoTecnico;

    @Schema(description = "Acciones correctivas o preventivas ejecutadas")
    private String accionesRealizadas;

    @Schema(description = "Horas-hombre invertidas en la reparación", example = "3.50")
    private BigDecimal horasHombre;

    @Schema(description = "Costo de mano de obra en Soles (PEN)", example = "150.00")
    private BigDecimal costoManoObra;

    @Schema(description = "Resultado de verificación de calidad (PENDIENTE, SATISFACTORIO, REQUIERE_REVISION)", example = "SATISFACTORIO")
    private String resultado;

    @Schema(description = "Observaciones de la verificación final del jefe de taller")
    private String observacionesVerificacion;

    @Schema(description = "Fecha y hora de registro de la reparación")
    private LocalDateTime fechaReparacion;

    @Schema(description = "Lista de repuestos consumidos en esta reparación")
    private List<DetalleRepuesto> detallesRepuestos = new ArrayList<>();

    // Campos auxiliares JOIN
    @Schema(description = "Descripción de la unidad intervenida", example = "CH-102 (B4C-825)")
    private String unidadDescripcion;

    @Schema(description = "Nombre del técnico ejecutor", example = "Miguel Ángel Paredes Soto")
    private String tecnicoNombre;

    /**
     * Constructor por defecto.
     */
    public Reparacion() {
    }

    /**
     * Obtiene el identificador de la reparación.
     *
     * @return ID de reparación.
     */
    public Integer getIdReparacion() {
        return idReparacion;
    }

    /**
     * Establece el identificador de la reparación.
     *
     * @param idReparacion ID único.
     */
    public void setIdReparacion(Integer idReparacion) {
        this.idReparacion = idReparacion;
    }

    /**
     * Obtiene el ID del mantenimiento asociado.
     *
     * @return ID de mantenimiento.
     */
    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    /**
     * Establece el ID del mantenimiento asociado.
     *
     * @param idMantenimiento ID de mantenimiento.
     */
    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    /**
     * Obtiene el ID del técnico ejecutor.
     *
     * @return ID de usuario técnico.
     */
    public Integer getIdTecnico() {
        return idTecnico;
    }

    /**
     * Establece el ID del técnico ejecutor.
     *
     * @param idTecnico ID de usuario técnico.
     */
    public void setIdTecnico(Integer idTecnico) {
        this.idTecnico = idTecnico;
    }

    /**
     * Obtiene el diagnóstico técnico.
     *
     * @return texto del diagnóstico.
     */
    public String getDiagnosticoTecnico() {
        return diagnosticoTecnico;
    }

    /**
     * Establece el diagnóstico técnico.
     *
     * @param diagnosticoTecnico texto del diagnóstico.
     */
    public void setDiagnosticoTecnico(String diagnosticoTecnico) {
        this.diagnosticoTecnico = diagnosticoTecnico;
    }

    /**
     * Obtiene las acciones realizadas durante la intervención.
     *
     * @return detalle de acciones.
     */
    public String getAccionesRealizadas() {
        return accionesRealizadas;
    }

    /**
     * Establece las acciones realizadas durante la intervención.
     *
     * @param accionesRealizadas detalle de acciones.
     */
    public void setAccionesRealizadas(String accionesRealizadas) {
        this.accionesRealizadas = accionesRealizadas;
    }

    /**
     * Obtiene las horas-hombre empleadas.
     *
     * @return cantidad de horas-hombre.
     */
    public BigDecimal getHorasHombre() {
        return horasHombre;
    }

    /**
     * Establece las horas-hombre empleadas.
     *
     * @param horasHombre cantidad mayor a cero.
     */
    public void setHorasHombre(BigDecimal horasHombre) {
        this.horasHombre = horasHombre;
    }

    /**
     * Obtiene el costo de mano de obra.
     *
     * @return importe en soles.
     */
    public BigDecimal getCostoManoObra() {
        return costoManoObra;
    }

    /**
     * Establece el costo de mano de obra.
     *
     * @param costoManoObra importe no negativo.
     */
    public void setCostoManoObra(BigDecimal costoManoObra) {
        this.costoManoObra = costoManoObra;
    }

    /**
     * Obtiene el resultado de la reparación ({@code PENDIENTE}, {@code SATISFACTORIO}, {@code REQUIERE_REVISION}).
     *
     * @return resultado de verificación.
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Establece el resultado de la reparación.
     *
     * @param resultado resultado de verificación.
     */
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    /**
     * Obtiene las observaciones de verificación de calidad.
     *
     * @return observaciones de verificación.
     */
    public String getObservacionesVerificacion() {
        return observacionesVerificacion;
    }

    /**
     * Establece las observaciones de verificación de calidad.
     *
     * @param observacionesVerificacion observaciones de verificación.
     */
    public void setObservacionesVerificacion(String observacionesVerificacion) {
        this.observacionesVerificacion = observacionesVerificacion;
    }

    /**
     * Obtiene la fecha y hora de la reparación.
     *
     * @return timestamp de reparación.
     */
    public LocalDateTime getFechaReparacion() {
        return fechaReparacion;
    }

    /**
     * Establece la fecha y hora de la reparación.
     *
     * @param fechaReparacion timestamp de reparación.
     */
    public void setFechaReparacion(LocalDateTime fechaReparacion) {
        this.fechaReparacion = fechaReparacion;
    }

    /**
     * Obtiene los ítems de repuestos utilizados en la reparación.
     *
     * @return lista de {@link DetalleRepuesto}.
     */
    public List<DetalleRepuesto> getDetallesRepuestos() {
        return detallesRepuestos;
    }

    /**
     * Establece los ítems de repuestos utilizados en la reparación.
     *
     * @param detallesRepuestos lista de detalles de repuestos.
     */
    public void setDetallesRepuestos(List<DetalleRepuesto> detallesRepuestos) {
        this.detallesRepuestos = detallesRepuestos;
    }

    /**
     * Obtiene la descripción de la unidad intervenida.
     *
     * @return código y placa de la unidad.
     */
    public String getUnidadDescripcion() {
        return unidadDescripcion;
    }

    /**
     * Establece la descripción de la unidad intervenida.
     *
     * @param unidadDescripcion código y placa de la unidad.
     */
    public void setUnidadDescripcion(String unidadDescripcion) {
        this.unidadDescripcion = unidadDescripcion;
    }

    /**
     * Obtiene el nombre del técnico ejecutor.
     *
     * @return nombre completo del técnico.
     */
    public String getTecnicoNombre() {
        return tecnicoNombre;
    }

    /**
     * Establece el nombre del técnico ejecutor.
     *
     * @param tecnicoNombre nombre completo del técnico.
     */
    public void setTecnicoNombre(String tecnicoNombre) {
        this.tecnicoNombre = tecnicoNombre;
    }
}
