package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO que representa un ítem de repuesto y su cantidad solicitada dentro
 * de una transacción de reparación (CUS07 y CUS08).
 *
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Ítem de repuesto y cantidad solicitada para una reparación (CUS08)")
public class ItemRepuestoDTO {

    @Schema(description = "ID del repuesto en almacén", example = "1")
    @NotNull(message = "El ID del repuesto es obligatorio")
    private Integer idRepuesto;

    @Schema(description = "Cantidad de unidades a emplear y descontar de stock", example = "2")
    @NotNull(message = "La cantidad del repuesto es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    /**
     * Constructor por defecto.
     */
    public ItemRepuestoDTO() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param idRepuesto ID del repuesto.
     * @param cantidad   cantidad requerida.
     */
    public ItemRepuestoDTO(Integer idRepuesto, Integer cantidad) {
        this.idRepuesto = idRepuesto;
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el ID del repuesto.
     *
     * @return ID del repuesto.
     */
    public Integer getIdRepuesto() {
        return idRepuesto;
    }

    /**
     * Establece el ID del repuesto.
     *
     * @param idRepuesto ID del repuesto.
     */
    public void setIdRepuesto(Integer idRepuesto) {
        this.idRepuesto = idRepuesto;
    }

    /**
     * Obtiene la cantidad solicitada.
     *
     * @return cantidad.
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad solicitada.
     *
     * @param cantidad cantidad mayor a cero.
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
