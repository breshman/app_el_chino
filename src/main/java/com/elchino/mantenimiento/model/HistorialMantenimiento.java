package com.elchino.mantenimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Entidad que mapea la tabla {@code historial_mantenimiento} en PostgreSQL.
 * Constituye la bitácora de auditoría inmutable de todos los eventos técnicos y
 * cambios de estado de cada unidad de transporte (Implementa CUS10 y QA-05).
 *
 * @author Ingeniero de Base de Datos (@DatabaseAgent)
 * @version 1.0.0
 */
@Schema(description = "Registro inmutable de auditoría e historial técnico de una unidad")
public class HistorialMantenimiento {

    @Schema(description = "Identificador único del evento en el historial", example = "1")
    private Integer idHistorial;

    @Schema(description = "ID de la unidad de transporte auditada", example = "2")
    private Integer idUnidad;

    @Schema(description = "ID de la orden de mantenimiento vinculada (si aplica)", example = "1")
    private Integer idMantenimiento;

    @Schema(description = "ID del usuario que originó el evento o cambio de estado", example = "2")
    private Integer idUsuario;

    @Schema(description = "Categoría del evento (CAMBIO_ESTADO, FALLA_CRITICA, PROGRAMACION, REPARACION_FINALIZADA)", example = "REPARACION_FINALIZADA")
    private String tipoEvento;

    @Schema(description = "Estado operativo previo de la unidad", example = "EN_MANTENIMIENTO")
    private String estadoAnteriorUnidad;

    @Schema(description = "Nuevo estado operativo asignado a la unidad", example = "OPERATIVA")
    private String estadoNuevoUnidad;

    @Schema(description = "Detalle descriptivo del evento para trazabilidad técnica")
    private String descripcionEvento;

    @Schema(description = "Fecha y hora exacta del evento")
    private LocalDateTime fechaEvento;

    // Campos auxiliares JOIN
    @Schema(description = "Código interno y placa de la unidad", example = "CH-102 (B4C-825)")
    private String unidadDescripcion;

    @Schema(description = "Nombre completo del usuario responsable del evento", example = "Roberto Huamán Rojas")
    private String usuarioNombre;

    /**
     * Constructor por defecto.
     */
    public HistorialMantenimiento() {
    }

    /**
     * Obtiene el identificador del registro de historial.
     *
     * @return ID del historial.
     */
    public Integer getIdHistorial() {
        return idHistorial;
    }

    /**
     * Establece el identificador del registro de historial.
     *
     * @param idHistorial ID único.
     */
    public void setIdHistorial(Integer idHistorial) {
        this.idHistorial = idHistorial;
    }

    /**
     * Obtiene el ID de la unidad asociada.
     *
     * @return ID de la unidad.
     */
    public Integer getIdUnidad() {
        return idUnidad;
    }

    /**
     * Establece el ID de la unidad asociada.
     *
     * @param idUnidad ID de la unidad.
     */
    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    /**
     * Obtiene el ID del mantenimiento asociado, si aplica.
     *
     * @return ID de mantenimiento o {@code null}.
     */
    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    /**
     * Establece el ID del mantenimiento asociado.
     *
     * @param idMantenimiento ID de mantenimiento o {@code null}.
     */
    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    /**
     * Obtiene el ID del usuario que ejecutó la acción.
     *
     * @return ID de usuario.
     */
    public Integer getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el ID del usuario que ejecutó la acción.
     *
     * @param idUsuario ID de usuario.
     */
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el tipo de evento registrado.
     *
     * @return tipo de evento.
     */
    public String getTipoEvento() {
        return tipoEvento;
    }

    /**
     * Establece el tipo de evento registrado.
     *
     * @param tipoEvento tipo de evento.
     */
    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    /**
     * Obtiene el estado anterior de la unidad.
     *
     * @return estado anterior.
     */
    public String getEstadoAnteriorUnidad() {
        return estadoAnteriorUnidad;
    }

    /**
     * Establece el estado anterior de la unidad.
     *
     * @param estadoAnteriorUnidad estado anterior.
     */
    public void setEstadoAnteriorUnidad(String estadoAnteriorUnidad) {
        this.estadoAnteriorUnidad = estadoAnteriorUnidad;
    }

    /**
     * Obtiene el nuevo estado de la unidad.
     *
     * @return estado nuevo.
     */
    public String getEstadoNuevoUnidad() {
        return estadoNuevoUnidad;
    }

    /**
     * Establece el nuevo estado de la unidad.
     *
     * @param estadoNuevoUnidad estado nuevo.
     */
    public void setEstadoNuevoUnidad(String estadoNuevoUnidad) {
        this.estadoNuevoUnidad = estadoNuevoUnidad;
    }

    /**
     * Obtiene la descripción detallada del evento.
     *
     * @return descripción del evento.
     */
    public String getDescripcionEvento() {
        return descripcionEvento;
    }

    /**
     * Establece la descripción detallada del evento.
     *
     * @param descripcionEvento descripción del evento.
     */
    public void setDescripcionEvento(String descripcionEvento) {
        this.descripcionEvento = descripcionEvento;
    }

    /**
     * Obtiene la fecha y hora del evento.
     *
     * @return timestamp del evento.
     */
    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    /**
     * Establece la fecha y hora del evento.
     *
     * @param fechaEvento timestamp del evento.
     */
    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
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
     * Obtiene el nombre del usuario responsable.
     *
     * @return nombre completo del usuario.
     */
    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    /**
     * Establece el nombre del usuario responsable.
     *
     * @param usuarioNombre nombre completo del usuario.
     */
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
}
