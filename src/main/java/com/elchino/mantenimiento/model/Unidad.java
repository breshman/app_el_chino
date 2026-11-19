package com.elchino.mantenimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Entidad que mapea la tabla {@code unidad} en PostgreSQL.
 * Representa cada autobús de la flota de transporte urbano "El Chino" (Implementa CUS02 y CUS03).
 *
 * @author Ingeniero de Base de Datos (@DatabaseAgent)
 * @version 1.0.0
 */
@Schema(description = "Unidad de transporte urbano (autobús) de la flota El Chino")
public class Unidad {

    @Schema(description = "Identificador único de la unidad", example = "1")
    private Integer idUnidad;

    @Schema(description = "Código interno único de flota", example = "CH-101")
    private String codigoInterno;

    @Schema(description = "Placa de rodaje nacional única", example = "A1B-710")
    private String placa;

    @Schema(description = "Marca del fabricante del chasis/carrocería", example = "Mercedes-Benz")
    private String marca;

    @Schema(description = "Modelo técnico del vehículo", example = "OF-1721 Euro V")
    private String modelo;

    @Schema(description = "Año de fabricación de la unidad", example = "2021")
    private Integer anioFabricacion;

    @Schema(description = "Kilometraje acumulado actual del odómetro", example = "148500")
    private Integer kilometrajeActual;

    @Schema(description = "Estado operativo actual (OPERATIVA, EN_MANTENIMIENTO, FUERA_DE_SERVICIO)", example = "OPERATIVA")
    private String estado;

    @Schema(description = "Fecha y hora de registro en el sistema")
    private LocalDateTime fechaRegistro;

    /**
     * Constructor por defecto.
     */
    public Unidad() {
    }

    /**
     * Obtiene el identificador de la unidad.
     *
     * @return ID de la unidad.
     */
    public Integer getIdUnidad() {
        return idUnidad;
    }

    /**
     * Establece el identificador de la unidad.
     *
     * @param idUnidad ID único.
     */
    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    /**
     * Obtiene el código interno de la unidad (ej. {@code CH-101}).
     *
     * @return código interno único.
     */
    public String getCodigoInterno() {
        return codigoInterno;
    }

    /**
     * Establece el código interno de la unidad.
     *
     * @param codigoInterno código interno único.
     */
    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    /**
     * Obtiene la placa vehicular.
     *
     * @return número de placa único.
     */
    public String getPlaca() {
        return placa;
    }

    /**
     * Establece la placa vehicular.
     *
     * @param placa número de placa único.
     */
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    /**
     * Obtiene la marca del vehículo.
     *
     * @return marca de la unidad.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Establece la marca del vehículo.
     *
     * @param marca marca del fabricante.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Obtiene el modelo del vehículo.
     *
     * @return modelo técnico.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Establece el modelo del vehículo.
     *
     * @param modelo modelo técnico.
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
     * @param anioFabricacion año entre 1990 y 2100.
     */
    public void setAnioFabricacion(Integer anioFabricacion) {
        this.anioFabricacion = anioFabricacion;
    }

    /**
     * Obtiene el kilometraje actual del odómetro.
     *
     * @return kilometraje en kilómetros.
     */
    public Integer getKilometrajeActual() {
        return kilometrajeActual;
    }

    /**
     * Establece el kilometraje actual del odómetro.
     *
     * @param kilometrajeActual kilometraje no negativo.
     */
    public void setKilometrajeActual(Integer kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }

    /**
     * Obtiene el estado operativo de la unidad.
     *
     * @return {@code OPERATIVA}, {@code EN_MANTENIMIENTO} o {@code FUERA_DE_SERVICIO}.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado operativo de la unidad.
     *
     * @param estado nuevo estado operativo.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la fecha de registro de la unidad.
     *
     * @return timestamp de alta.
     */
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha de registro de la unidad.
     *
     * @param fechaRegistro timestamp de alta.
     */
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
