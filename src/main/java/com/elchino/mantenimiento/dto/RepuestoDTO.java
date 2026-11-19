package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para el registro y abastecimiento de repuestos en el almacén (Implementa CUS08).
 *
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Datos para crear o actualizar un repuesto del inventario del taller")
public class RepuestoDTO {

    @Schema(description = "Código SKU único", example = "REP-FR-009")
    @NotBlank(message = "El código SKU es obligatorio")
    private String codigoSku;

    @Schema(description = "Nombre descriptivo del repuesto", example = "Tambor de Freno Posterior MB OF-1721")
    @NotBlank(message = "El nombre del repuesto es obligatorio")
    private String nombre;

    @Schema(description = "Categoría técnica", example = "Frenos")
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @Schema(description = "Stock físico inicial o actualizado", example = "15")
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Stock mínimo para alerta de reposición", example = "4")
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @Schema(description = "Precio unitario en Soles (PEN)", example = "520.00")
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio unitario no puede ser negativo")
    private BigDecimal precioUnitario;

    @Schema(description = "Unidad de medida", example = "UNIDAD")
    @NotBlank(message = "La unidad de medida es obligatoria")
    private String unidadMedida = "UNIDAD";

    /**
     * Constructor por defecto.
     */
    public RepuestoDTO() {
    }

    /**
     * Obtiene el código SKU.
     *
     * @return SKU.
     */
    public String getCodigoSku() {
        return codigoSku;
    }

    /**
     * Establece el código SKU.
     *
     * @param codigoSku SKU.
     */
    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
    }

    /**
     * Obtiene el nombre del repuesto.
     *
     * @return nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del repuesto.
     *
     * @param nombre nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la categoría.
     *
     * @return categoría.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría.
     *
     * @param categoria categoría.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el stock.
     *
     * @return stock disponible.
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * Establece el stock.
     *
     * @param stock stock disponible.
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * Obtiene el stock mínimo.
     *
     * @return stock mínimo.
     */
    public Integer getStockMinimo() {
        return stockMinimo;
    }

    /**
     * Establece el stock mínimo.
     *
     * @param stockMinimo stock mínimo.
     */
    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    /**
     * Obtiene el precio unitario.
     *
     * @return precio unitario.
     */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario.
     *
     * @param precioUnitario precio unitario.
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
