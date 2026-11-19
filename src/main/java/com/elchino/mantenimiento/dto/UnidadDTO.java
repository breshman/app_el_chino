package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para el registro y actualización de unidades de transporte (CUS02 y CUS03).
 *
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Datos para registrar o actualizar una unidad de transporte de la flota El Chino")
public class UnidadDTO {

    @Schema(description = "Código interno único de la unidad", example = "CH-106")
    @NotBlank(message = "El código interno es obligatorio")
    private String codigoInterno;

    @Schema(description = "Placa de rodaje única", example = "F9K-732")
    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    @Schema(description = "Marca del autobús", example = "Mercedes-Benz")
    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @Schema(description = "Modelo del autobús", example = "OF-1721 Euro V")
    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @Schema(description = "Año de fabricación (1990 - 2100)", example = "2023")
    @NotNull(message = "El año de fabricación es obligatorio")
    @Min(value = 1990, message = "El año de fabricación no puede ser menor a 1990")
    @Max(value = 2100, message = "El año de fabricación no es válido")
    private Integer anioFabricacion;

    @Schema(description = "Kilometraje actual del odómetro", example = "45000")
    @NotNull(message = "El kilometraje actual es obligatorio")
    @Min(value = 0, message = "El kilometraje no puede ser negativo")
    private Integer kilometrajeActual;

    @Schema(description = "Estado operativo de la unidad", example = "OPERATIVA",
            allowableValues = {"OPERATIVA", "EN_MANTENIMIENTO", "FUERA_DE_SERVICIO"})
    @Pattern(regexp = "OPERATIVA|EN_MANTENIMIENTO|FUERA_DE_SERVICIO",
            message = "Estado inválido. Use OPERATIVA, EN_MANTENIMIENTO o FUERA_DE_SERVICIO")
    private String estado = "OPERATIVA";

    /**
     * Constructor por defecto.
     */
    public UnidadDTO() {
    }

    /**
     * Obtiene el código interno.
     *
     * @return código interno.
     */
    public String getCodigoInterno() {
        return codigoInterno;
    }

    /**
     * Establece el código interno.
     *
     * @param codigoInterno código interno.
     */
    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    /**
     * Obtiene la placa.
     *
     * @return placa vehicular.
     */
    public String getPlaca() {
        return placa;
    }

    /**
     * Establece la placa.
     *
     * @param placa placa vehicular.
     */
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    /**
     * Obtiene la marca.
     *
     * @return marca.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Establece la marca.
     *
     * @param marca marca.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Obtiene el modelo.
     *
     * @return modelo.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Establece el modelo.
     *
     * @param modelo modelo.
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Obtiene el año de fabricación.
     *
     * @return año de fabricación.
     */
    public Integer getAnioFabricacion() {
        return anioFabricacion;
    }

    /**
     * Establece el año de fabricación.
     *
     * @param anioFabricacion año de fabricación.
     */
    public void setAnioFabricacion(Integer anioFabricacion) {
        this.anioFabricacion = anioFabricacion;
    }

    /**
     * Obtiene el kilometraje actual.
     *
     * @return kilometraje.
     */
    public Integer getKilometrajeActual() {
        return kilometrajeActual;
    }

    /**
     * Establece el kilometraje actual.
     *
     * @param kilometrajeActual kilometraje.
     */
    public void setKilometrajeActual(Integer kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }

    /**
     * Obtiene el estado operativo.
     *
     * @return estado de la unidad.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado operativo.
     *
     * @param estado estado de la unidad.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
