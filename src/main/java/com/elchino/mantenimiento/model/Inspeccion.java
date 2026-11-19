package com.elchino.mantenimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Entidad que mapea la tabla {@code inspeccion} en PostgreSQL.
 * Registra las evaluaciones técnicas preventivas, rutinarias o extraordinarias
 * realizadas sobre las unidades de transporte (Implementa CUS04).
 *
 * @author Ingeniero de Base de Datos (@DatabaseAgent)
 * @version 1.0.0
 */
@Schema(description = "Registro de inspección técnica realizada a una unidad de transporte")
public class Inspeccion {

    @Schema(description = "Identificador único de la inspección", example = "1")
    private Integer idInspeccion;

    @Schema(description = "ID de la unidad inspeccionada", example = "2")
    private Integer idUnidad;

    @Schema(description = "ID del usuario inspector o responsable técnico", example = "2")
    private Integer idInspector;

    @Schema(description = "Tipo de inspección (PREVENTIVA, RUTINARIA, EXTRAORDINARIA)", example = "RUTINARIA")
    private String tipoInspeccion;

    @Schema(description = "Fecha y hora de ejecución de la inspección")
    private LocalDateTime fechaInspeccion;

    @Schema(description = "Lectura del odómetro al momento de la inspección", example = "182300")
    private Integer kilometrajeRegistrado;

    @Schema(description = "Hallazgos y observaciones técnicas detalladas")
    private String observaciones;

    @Schema(description = "Resultado dictaminado (CONFORME, CON_OBSERVACIONES, NO_CONFORME)", example = "NO_CONFORME")
    private String resultado;

    // Campos auxiliares de visualización (JOIN)
    @Schema(description = "Código interno y placa de la unidad asociada", example = "CH-102 (B4C-825)")
    private String unidadDescripcion;

    @Schema(description = "Nombre completo del inspector responsable", example = "Roberto Huamán Rojas")
    private String inspectorNombre;

    /**
     * Constructor por defecto.
     */
    public Inspeccion() {
    }

    /**
     * Obtiene el ID de la inspección.
     *
     * @return identificador único.
     */
    public Integer getIdInspeccion() {
        return idInspeccion;
    }

    /**
     * Establece el ID de la inspección.
     *
     * @param idInspeccion identificador único.
     */
    public void setIdInspeccion(Integer idInspeccion) {
        this.idInspeccion = idInspeccion;
    }

    /**
     * Obtiene el ID de la unidad inspeccionada.
     *
     * @return ID de unidad.
     */
    public Integer getIdUnidad() {
        return idUnidad;
    }

    /**
     * Establece el ID de la unidad inspeccionada.
     *
     * @param idUnidad ID de unidad.
     */
    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    /**
     * Obtiene el ID del inspector que realizó la revisión.
     *
     * @return ID de usuario inspector.
     */
    public Integer getIdInspector() {
        return idInspector;
    }

    /**
     * Establece el ID del inspector.
     *
     * @param idInspector ID de usuario inspector.
     */
    public void setIdInspector(Integer idInspector) {
        this.idInspector = idInspector;
    }

    /**
     * Obtiene el tipo de inspección ({@code PREVENTIVA}, {@code RUTINARIA}, {@code EXTRAORDINARIA}).
     *
     * @return tipo de inspección.
     */
    public String getTipoInspeccion() {
        return tipoInspeccion;
    }

    /**
     * Establece el tipo de inspección.
     *
     * @param tipoInspeccion tipo válido según restricción CHECK.
     */
    public void setTipoInspeccion(String tipoInspeccion) {
        this.tipoInspeccion = tipoInspeccion;
    }

    /**
     * Obtiene la fecha y hora de la inspección.
     *
     * @return timestamp de la inspección.
     */
    public LocalDateTime getFechaInspeccion() {
        return fechaInspeccion;
    }

    /**
     * Establece la fecha y hora de la inspección.
     *
     * @param fechaInspeccion timestamp de la inspección.
     */
    public void setFechaInspeccion(LocalDateTime fechaInspeccion) {
        this.fechaInspeccion = fechaInspeccion;
    }

    /**
     * Obtiene el kilometraje registrado durante la inspección.
     *
     * @return kilometraje en km.
     */
    public Integer getKilometrajeRegistrado() {
        return kilometrajeRegistrado;
    }

    /**
     * Establece el kilometraje registrado durante la inspección.
     *
     * @param kilometrajeRegistrado lectura del odómetro.
     */
    public void setKilometrajeRegistrado(Integer kilometrajeRegistrado) {
        this.kilometrajeRegistrado = kilometrajeRegistrado;
    }

    /**
     * Obtiene las observaciones técnicas de la inspección.
     *
     * @return texto de observaciones.
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones técnicas de la inspección.
     *
     * @param observaciones detalle de hallazgos.
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Obtiene el resultado de la inspección ({@code CONFORME}, {@code CON_OBSERVACIONES}, {@code NO_CONFORME}).
     *
     * @return resultado dictaminado.
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Establece el resultado de la inspección.
     *
     * @param resultado resultado dictaminado.
     */
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    /**
     * Obtiene la descripción legible de la unidad asociada (código y placa).
     *
     * @return descripción de la unidad.
     */
    public String getUnidadDescripcion() {
        return unidadDescripcion;
    }

    /**
     * Establece la descripción legible de la unidad asociada.
     *
     * @param unidadDescripcion código interno y placa.
     */
    public void setUnidadDescripcion(String unidadDescripcion) {
        this.unidadDescripcion = unidadDescripcion;
    }

    /**
     * Obtiene el nombre completo del inspector.
     *
     * @return nombre del inspector.
     */
    public String getInspectorNombre() {
        return inspectorNombre;
    }

    /**
     * Establece el nombre completo del inspector.
     *
     * @param inspectorNombre nombre del inspector.
     */
    public void setInspectorNombre(String inspectorNombre) {
        this.inspectorNombre = inspectorNombre;
    }
}
