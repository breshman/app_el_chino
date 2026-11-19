package com.elchino.mantenimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * Entidad que mapea la tabla {@code repuesto} en PostgreSQL.
 * Administra el inventario de autopartes, lubricantes y suministros del almacén
 * del taller con control de stock mínimo y alertas de reposición (Implementa CUS08).
 *
 * @author Ingeniero de Base de Datos (@DatabaseAgent)
 * @version 1.0.0
 */
@Schema(description = "Repuesto o insumo del almacén de mantenimiento con control de stock")
public class Repuesto {

    @Schema(description = "Identificador único del repuesto", example = "1")
    private Integer idRepuesto;

    @Schema(description = "Código SKU único de almacén", example = "REP-FR-001")
    private String codigoSku;

    @Schema(description = "Nombre comercial y técnico del repuesto", example = "Juego de Pastillas de Freno Delantero MB")
    private String nombre;

    @Schema(description = "Categoría técnica (Frenos, Motor, Lubricantes, Suspensión, Sistema Eléctrico, Transmisión)", example = "Frenos")
    private String categoria;

    @Schema(description = "Cantidad actual disponible en almacén", example = "18")
    private Integer stock;

    @Schema(description = "Umbral mínimo de stock para disparar alerta de reposición", example = "6")
    private Integer stockMinimo;

    @Schema(description = "Precio unitario referencial en Soles (PEN)", example = "380.00")
    private BigDecimal precioUnitario;

    @Schema(description = "Unidad de medida de despacho (UNIDAD, JUEGO, KIT, BALDE)", example = "JUEGO")
    private String unidadMedida;

    /**
     * Constructor por defecto.
     */
    public Repuesto() {
    }

    /**
     * Evalúa si el repuesto se encuentra en nivel crítico de inventario ({@code stock <= stockMinimo}).
     *
     * @return {@code true} si requiere alerta de reposición inmediata (CUS08).
     */
    public boolean isBajoStock() {
        return stock != null && stockMinimo != null && stock <= stockMinimo;
    }

    /**
     * Obtiene el identificador del repuesto.
     *
     * @return ID del repuesto.
     */
    public Integer getIdRepuesto() {
        return idRepuesto;
    }

    /**
     * Establece el identificador del repuesto.
     *
     * @param idRepuesto ID único.
     */
    public void setIdRepuesto(Integer idRepuesto) {
        this.idRepuesto = idRepuesto;
    }

    /**
     * Obtiene el código SKU de almacén.
     *
     * @return código SKU único.
     */
    public String getCodigoSku() {
        return codigoSku;
    }

    /**
     * Establece el código SKU de almacén.
     *
     * @param codigoSku código SKU único.
     */
    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
    }

    /**
     * Obtiene el nombre del repuesto.
     *
     * @return nombre descriptivo.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del repuesto.
     *
     * @param nombre nombre descriptivo.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la categoría del repuesto.
     *
     * @return categoría técnica.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría del repuesto.
     *
     * @param categoria categoría técnica.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el stock disponible actual.
     *
     * @return cantidad en inventario.
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * Establece el stock disponible actual.
     *
     * @param stock cantidad no negativa.
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * Obtiene el umbral de stock mínimo.
     *
     * @return stock mínimo de seguridad.
     */
    public Integer getStockMinimo() {
        return stockMinimo;
    }

    /**
     * Establece el umbral de stock mínimo.
     *
     * @param stockMinimo stock mínimo no negativo.
     */
    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    /**
     * Obtiene el precio unitario en Soles.
     *
     * @return precio unitario.
     */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario en Soles.
     *
     * @param precioUnitario precio no negativo.
     */
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Obtiene la unidad de medida.
     *
     * @return unidad de medida.
     */
    public String getUnidadMedida() {
        return unidadMedida;
    }

    /**
     * Establece la unidad de medida.
     *
     * @param unidadMedida unidad de medida.
     */
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}
