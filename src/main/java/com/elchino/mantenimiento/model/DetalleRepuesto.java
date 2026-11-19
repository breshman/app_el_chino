package com.elchino.mantenimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * Entidad que mapea la tabla intermedia {@code detalle_repuesto} en PostgreSQL.
 * Representa la relación N:M entre {@link Reparacion} y {@link Repuesto} con
 * restricción {@code UNIQUE(id_reparacion, id_repuesto)} (Implementa CUS08).
 *
 * @author Ingeniero de Base de Datos (@DatabaseAgent)
 * @version 1.0.0
 */
@Schema(description = "Detalle de repuesto consumido en una reparación específica")
public class DetalleRepuesto {

    @Schema(description = "Identificador único del detalle", example = "1")
    private Integer idDetalle;

    @Schema(description = "ID de la reparación asociada", example = "1")
    private Integer idReparacion;

    @Schema(description = "ID del repuesto utilizado", example = "1")
    private Integer idRepuesto;

    @Schema(description = "Cantidad de unidades consumidas", example = "2")
    private Integer cantidad;

    @Schema(description = "Precio unitario aplicado al momento de la reparación", example = "380.00")
    private BigDecimal precioUnitario;

    @Schema(description = "Subtotal calculado (cantidad * precioUnitario)", example = "760.00")
    private BigDecimal subtotal;

    // Campos auxiliares de consulta JOIN
    @Schema(description = "Nombre del repuesto utilizado", example = "Juego de Pastillas de Freno Delantero MB")
    private String repuestoNombre;

    @Schema(description = "Código SKU del repuesto", example = "REP-FR-001")
    private String repuestoSku;

    /**
     * Constructor por defecto.
     */
    public DetalleRepuesto() {
    }

    /**
     * Obtiene el identificador del detalle.
     *
     * @return ID del detalle.
     */
    public Integer getIdDetalle() {
        return idDetalle;
    }

    /**
     * Establece el identificador del detalle.
     *
     * @param idDetalle ID único.
     */
    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    /**
     * Obtiene el ID de la reparación asociada.
     *
     * @return ID de reparación.
     */
    public Integer getIdReparacion() {
        return idReparacion;
    }

    /**
     * Establece el ID de la reparación asociada.
     *
     * @param idReparacion ID de reparación.
     */
    public void setIdReparacion(Integer idReparacion) {
        this.idReparacion = idReparacion;
    }

    /**
     * Obtiene el ID del repuesto utilizado.
     *
     * @return ID de repuesto.
     */
    public Integer getIdRepuesto() {
        return idRepuesto;
    }

    /**
     * Establece el ID del repuesto utilizado.
     *
     * @param idRepuesto ID de repuesto.
     */
    public void setIdRepuesto(Integer idRepuesto) {
        this.idRepuesto = idRepuesto;
    }

    /**
     * Obtiene la cantidad utilizada del repuesto.
     *
     * @return cantidad mayor a cero.
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad utilizada del repuesto.
     *
     * @param cantidad cantidad mayor a cero.
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio unitario registrado.
     *
     * @return precio unitario.
     */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario registrado.
     *
     * @param precioUnitario precio unitario.
     */
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Obtiene el subtotal de la línea.
     *
     * @return subtotal calculado.
     */
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    /**
     * Establece el subtotal de la línea.
     *
     * @param subtotal subtotal calculado.
     */
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Obtiene el nombre del repuesto.
     *
     * @return nombre del repuesto.
     */
    public String getRepuestoNombre() {
        return repuestoNombre;
    }

    /**
     * Establece el nombre del repuesto.
     *
     * @param repuestoNombre nombre del repuesto.
     */
    public void setRepuestoNombre(String repuestoNombre) {
        this.repuestoNombre = repuestoNombre;
    }

    /**
     * Obtiene el código SKU del repuesto.
     *
     * @return código SKU.
     */
    public String getRepuestoSku() {
        return repuestoSku;
    }

    /**
     * Establece el código SKU del repuesto.
     *
     * @param repuestoSku código SKU.
     */
    public void setRepuestoSku(String repuestoSku) {
        this.repuestoSku = repuestoSku;
    }
}
