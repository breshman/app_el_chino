package com.elchino.mantenimiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación y actualización de usuarios del sistema (CUS01 / RF01).
 *
 * @author Desarrollador Frontend y Controladores MVC (@FullstackMVCAgent)
 * @version 1.0.0
 */
@Schema(description = "Datos de entrada para registrar o actualizar un usuario del sistema")
public class UsuarioDTO {

    @Schema(description = "Nombre de usuario único", example = "tecnico3")
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El username debe tener entre 4 y 50 caracteres")
    private String username;

    @Schema(description = "Contraseña en texto plano que será cifrada con BCrypt", example = "Taller2026!")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 60, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @Schema(description = "Nombres completos del usuario", example = "Luis Alberto")
    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @Schema(description = "Apellidos completos del usuario", example = "Ramos Gutiérrez")
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @Schema(description = "Correo electrónico institucional", example = "lramos@elchino.pe")
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;

    @Schema(description = "Rol operativo asignado", example = "TECNICO",
            allowableValues = {"ADMINISTRADOR", "RESPONSABLE_MANTENIMIENTO", "TECNICO", "OPERACIONES", "CONDUCTOR"})
    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "ADMINISTRADOR|RESPONSABLE_MANTENIMIENTO|TECNICO|OPERACIONES|CONDUCTOR",
            message = "Rol inválido. Valores permitidos: ADMINISTRADOR, RESPONSABLE_MANTENIMIENTO, TECNICO, OPERACIONES, CONDUCTOR")
    private String rol;

    /**
     * Constructor por defecto.
     */
    public UsuarioDTO() {
    }

    /**
     * Obtiene el username.
     *
     * @return nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el username.
     *
     * @param username nombre de usuario.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene la contraseña en texto plano antes del cifrado BCrypt.
     *
     * @return contraseña sin cifrar.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña en texto plano.
     *
     * @param password contraseña sin cifrar.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene los nombres del usuario.
     *
     * @return nombres.
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * Establece los nombres del usuario.
     *
     * @param nombres nombres.
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * Obtiene los apellidos del usuario.
     *
     * @return apellidos.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del usuario.
     *
     * @param apellidos apellidos.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el correo electrónico.
     *
     * @return email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico.
     *
     * @param email email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene el rol operativo.
     *
     * @return rol del usuario.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol operativo.
     *
     * @param rol rol del usuario.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }
}
