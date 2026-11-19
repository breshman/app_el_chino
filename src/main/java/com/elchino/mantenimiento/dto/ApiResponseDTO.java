package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Objeto de Transferencia de Datos (DTO) estándar para envolver las respuestas
 * de los endpoints REST documentados con Swagger/OpenAPI 3.
 *
 * @param <T> tipo del payload de datos devuelto en la respuesta.
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Estructura estándar de respuesta para la API REST del sistema El Chino")
public class ApiResponseDTO<T> {

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean exito;

    @Schema(description = "Mensaje descriptivo del resultado o alerta de negocio", example = "Operación registrada correctamente")
    private String mensaje;

    @Schema(description = "Datos resultantes de la operación")
    private T datos;

    @Schema(description = "Marca de tiempo de la respuesta del servidor", example = "2026-11-18T20:30:00")
    private LocalDateTime timestamp;

    /**
     * Constructor por defecto que inicializa la marca de tiempo actual.
     */
    public ApiResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor parametrizado para construir respuestas completas.
     *
     * @param exito   indicador booleano de éxito.
     * @param mensaje mensaje funcional para el usuario o cliente API.
     * @param datos   carga útil de la respuesta.
     */
    public ApiResponseDTO(boolean exito, String mensaje, T datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Crea una respuesta exitosa rápida con mensaje y datos.
     *
     * @param mensaje mensaje de éxito.
     * @param datos   objeto devuelto.
     * @param <T>     tipo de dato.
     * @return instancia de {@link ApiResponseDTO}.
     */
    public static <T> ApiResponseDTO<T> ok(String mensaje, T datos) {
        return new ApiResponseDTO<>(true, mensaje, datos);
    }

    /**
     * Crea una respuesta de error sin carga útil de datos.
     *
     * @param mensaje detalle del error ocurrido.
     * @param <T>     tipo de dato esperado.
     * @return instancia de {@link ApiResponseDTO} marcada con {@code exito = false}.
     */
    public static <T> ApiResponseDTO<T> error(String mensaje) {
        return new ApiResponseDTO<>(false, mensaje, null);
    }

    /**
     * Obtiene el indicador de éxito.
     *
     * @return {@code true} si la operación concluyó sin errores.
     */
    public boolean isExito() {
        return exito;
    }

    /**
     * Establece el indicador de éxito.
     *
     * @param exito estado booleano de la operación.
     */
    public void setExito(boolean exito) {
        this.exito = exito;
    }

    /**
     * Obtiene el mensaje descriptivo.
     *
     * @return mensaje de la operación.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el mensaje descriptivo.
     *
     * @param mensaje texto descriptivo.
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Obtiene el payload de datos de la respuesta.
     *
     * @return objeto de datos.
     */
    public T getDatos() {
        return datos;
    }

    /**
     * Establece el payload de datos de la respuesta.
     *
     * @param datos objeto de datos.
     */
    public void setDatos(T datos) {
        this.datos = datos;
    }

    /**
     * Obtiene la fecha y hora de generación de la respuesta.
     *
     * @return timestamp de la respuesta.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Establece la fecha y hora de la respuesta.
     *
     * @param timestamp marca de tiempo.
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
