package com.elchino.mantenimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Entidad que mapea la tabla {@code falla} en PostgreSQL.
 * Representa una avería o incidencia mecánica/eléctrica reportada por un conductor,
 * originada en una inspección técnica o registrada por el taller (Implementa CUS05).
 *
 * @author Ingeniero de Base de Datos (@DatabaseAgent)
 * @version 1.0.0
 */
@Schema(description = "Avería o falla mecánica/eléctrica reportada en una unidad de transporte")
public class Falla {

    @Schema(description = "Identificador único de la falla", example = "1")
    private Integer idFalla;

    @Schema(description = "ID de la unidad afectada", example = "2")
    private Integer idUnidad;

    @Schema(description = "ID del usuario que reporta la falla (conductor o técnico)", example = "6")
    private Integer idUsuarioReporta;

    @Schema(description = "ID de la inspección origen (opcional si fue reporte directo en ruta)", example = "1")
    private Integer idInspeccion;

    @Schema(description = "Título resumido de la falla", example = "Fuga de aire y desgaste en sistema de frenos")
    private String titulo;

    @Schema(description = "Descripción detallada de los síntomas de la avería")
    private String descripcion;

    @Schema(description = "Sistema mecánico afectado (Frenos, Motor, Suspensión, Transmisión, Eléctrico)", example = "Frenos")
    private String sistemaAfectado;

    @Schema(description = "Nivel de prioridad (BAJA, MEDIA, ALTA, CRITICA)", example = "ALTA")
    private String prioridad;

    @Schema(description = "Estado de gestión de la falla (REPORTADA, EN_ATENCION, RESUELTA, CERRADA)", example = "EN_ATENCION")
    private String estado;

    @Schema(description = "Fecha y hora en que se reportó la falla")
    private LocalDateTime fechaReporte;

    // Campos auxiliares de consulta JOIN
    @Schema(description = "Código y placa de la unidad afectada", example = "CH-102 (B4C-825)")
    private String unidadDescripcion;

    @Schema(description = "Nombre del usuario que reportó la falla", example = "Pedro Pablo Cárdenas Silva")
    private String usuarioReportaNombre;

    /**
     * Constructor por defecto.
     */
    public Falla() {
    }

    /**
     * Obtiene el identificador de la falla.
     *
     * @return ID de la falla.
     */
    public Integer getIdFalla() {
        return idFalla;
    }

    /**
     * Establece el identificador de la falla.
     *
     * @param idFalla ID único.
     */
    public void setIdFalla(Integer idFalla) {
        this.idFalla = idFalla;
    }

    /**
     * Obtiene el ID de la unidad afectada.
     *
     * @return ID de la unidad.
     */
    public Integer getIdUnidad() {
        return idUnidad;
    }

    /**
     * Establece el ID de la unidad afectada.
     *
     * @param idUnidad ID de la unidad.
     */
    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    /**
     * Obtiene el ID del usuario que reportó la incidencia.
     *
     * @return ID de usuario.
     */
    public Integer getIdUsuarioReporta() {
        return idUsuarioReporta;
    }

    /**
     * Establece el ID del usuario que reportó la incidencia.
     *
     * @param idUsuarioReporta ID de usuario.
     */
    public void setIdUsuarioReporta(Integer idUsuarioReporta) {
        this.idUsuarioReporta = idUsuarioReporta;
    }

    /**
     * Obtiene el ID de la inspección de origen, si aplica.
     *
     * @return ID de inspección o {@code null}.
     */
    public Integer getIdInspeccion() {
        return idInspeccion;
    }

    /**
     * Establece el ID de la inspección de origen.
     *
     * @param idInspeccion ID de inspección o {@code null}.
     */
    public void setIdInspeccion(Integer idInspeccion) {
        this.idInspeccion = idInspeccion;
    }

    /**
     * Obtiene el título resumido de la falla.
     *
     * @return título de la falla.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título resumido de la falla.
     *
     * @param titulo título descriptivo.
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene la descripción detallada de la falla.
     *
     * @return descripción de la avería.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción detallada de la falla.
     *
     * @param descripcion detalle de síntomas.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el subsistema del vehículo afectado.
     *
     * @return nombre del sistema afectado.
     */
    public String getSistemaAfectado() {
        return sistemaAfectado;
    }

    /**
     * Establece el subsistema del vehículo afectado.
     *
     * @param sistemaAfectado nombre del sistema afectado.
     */
    public void setSistemaAfectado(String sistemaAfectado) {
        this.sistemaAfectado = sistemaAfectado;
    }

    /**
     * Obtiene el nivel de prioridad ({@code BAJA}, {@code MEDIA}, {@code ALTA}, {@code CRITICA}).
     *
     * @return prioridad de atención.
     */
    public String getPrioridad() {
        return prioridad;
    }

    /**
     * Establece el nivel de prioridad.
     *
     * @param prioridad nivel de prioridad.
     */
    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    /**
     * Obtiene el estado actual de la falla ({@code REPORTADA}, {@code EN_ATENCION}, {@code RESUELTA}, {@code CERRADA}).
     *
     * @return estado de la falla.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado actual de la falla.
     *
     * @param estado nuevo estado.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la fecha y hora del reporte.
     *
     * @return timestamp de reporte.
     */
    public LocalDateTime getFechaReporte() {
        return fechaReporte;
    }

    /**
     * Establece la fecha y hora del reporte.
     *
     * @param fechaReporte timestamp de reporte.
     */
    public void setFechaReporte(LocalDateTime fechaReporte) {
        this.fechaReporte = fechaReporte;
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
     * Obtiene el nombre del usuario que reportó la falla.
     *
     * @return nombre completo del reportante.
     */
    public String getUsuarioReportaNombre() {
        return usuarioReportaNombre;
    }

    /**
     * Establece el nombre del usuario que reportó la falla.
     *
     * @param usuarioReportaNombre nombre completo del reportante.
     */
    public void setUsuarioReportaNombre(String usuarioReportaNombre) {
        this.usuarioReportaNombre = usuarioReportaNombre;
    }
}
