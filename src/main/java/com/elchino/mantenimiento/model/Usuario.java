package com.elchino.mantenimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Entidad que representa la tabla {@code usuario} en PostgreSQL.
 * Almacena las credenciales, rol operativo, estado y control de bloqueo por intentos
 * fallidos de autenticación (Implementa CUS01 y RNF01).
 *
 * @author Ingeniero de Base de Datos (@DatabaseAgent)
 * @version 1.0.0
 */
@Schema(description = "Entidad de usuario del sistema con rol operativo y control de seguridad")
public class Usuario {

    @Schema(description = "Identificador único del usuario", example = "1")
    private Integer idUsuario;

    @Schema(description = "Nombre de usuario único para inicio de sesión", example = "jefe.taller")
    private String username;

    @Schema(description = "Contraseña cifrada con algoritmo BCrypt", example = "$2a$10$...")
    private String passwordHash;

    @Schema(description = "Nombres completos del colaborador", example = "Roberto")
    private String nombres;

    @Schema(description = "Apellidos completos del colaborador", example = "Huamán Rojas")
    private String apellidos;

    @Schema(description = "Correo electrónico institucional", example = "rhuaman@elchino.pe")
    private String email;

    @Schema(description = "Rol asignado (ADMINISTRADOR, RESPONSABLE_MANTENIMIENTO, TECNICO, OPERACIONES, CONDUCTOR)", example = "RESPONSABLE_MANTENIMIENTO")
    private String rol;

    @Schema(description = "Estado de la cuenta (ACTIVO, INACTIVO)", example = "ACTIVO")
    private String estado;

    @Schema(description = "Contador de intentos fallidos consecutivos de inicio de sesión", example = "0")
    private Integer intentosFallidos;

    @Schema(description = "Fecha y hora límite del bloqueo temporal tras 3 intentos fallidos")
    private LocalDateTime bloqueadoHasta;

    @Schema(description = "Fecha y hora de alta del usuario en el sistema")
    private LocalDateTime fechaCreacion;

    /**
     * Constructor vacío requerido para instanciación por RowMapper y frameworks.
     */
    public Usuario() {
    }

    /**
     * Verifica si la cuenta del usuario se encuentra temporalmente bloqueada por seguridad (CUS01).
     *
     * @return {@code true} si {@code bloqueadoHasta} es posterior a la fecha/hora actual.
     */
    public boolean isCuentaBloqueada() {
        return bloqueadoHasta != null && bloqueadoHasta.isAfter(LocalDateTime.now());
    }

    /**
     * Obtiene el nombre completo concatenado del usuario.
     *
     * @return cadena con nombres y apellidos.
     */
    public String getNombreCompleto() {
        return (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
    }

    /**
     * Obtiene el identificador del usuario.
     *
     * @return ID del usuario.
     */
    public Integer getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param idUsuario ID único.
     */
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el nombre de usuario (login).
     *
     * @return username único.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario.
     *
     * @param username nombre de usuario único.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene el hash BCrypt de la contraseña.
     *
     * @return hash de contraseña.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Establece el hash BCrypt de la contraseña.
     *
     * @param passwordHash contraseña cifrada.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Obtiene los nombres del usuario.
     *
     * @return nombres del colaborador.
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * Establece los nombres del usuario.
     *
     * @param nombres nombres del colaborador.
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * Obtiene los apellidos del usuario.
     *
     * @return apellidos del colaborador.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del usuario.
     *
     * @param apellidos apellidos del colaborador.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return email corporativo.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email email corporativo único.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene el rol operativo del usuario.
     *
     * @return rol del sistema.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol operativo del usuario.
     *
     * @param rol rol permitido en el CHECK de base de datos.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Obtiene el estado de la cuenta ({@code ACTIVO} o {@code INACTIVO}).
     *
     * @return estado de cuenta.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la cuenta.
     *
     * @param estado {@code ACTIVO} o {@code INACTIVO}.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el número de intentos fallidos de inicio de sesión.
     *
     * @return cantidad de intentos fallidos consecutivos.
     */
    public Integer getIntentosFallidos() {
        return intentosFallidos;
    }

    /**
     * Establece el número de intentos fallidos de inicio de sesión.
     *
     * @param intentosFallidos contador de intentos.
     */
    public void setIntentosFallidos(Integer intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    /**
     * Obtiene la fecha/hora hasta la cual la cuenta permanece bloqueada.
     *
     * @return timestamp de fin de bloqueo o {@code null}.
     */
    public LocalDateTime getBloqueadoHasta() {
        return bloqueadoHasta;
    }

    /**
     * Establece la fecha/hora límite de bloqueo temporal.
     *
     * @param bloqueadoHasta timestamp de desbloqueo.
     */
    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }

    /**
     * Obtiene la fecha de creación del registro.
     *
     * @return timestamp de creación.
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Establece la fecha de creación del registro.
     *
     * @param fechaCreacion timestamp de creación.
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
