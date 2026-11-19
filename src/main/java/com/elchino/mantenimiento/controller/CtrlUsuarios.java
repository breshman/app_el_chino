package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.UsuarioDTO;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para gestion de cuentas de usuario y roles (CUS01, RNF01).
 */
@Controller
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "02. Usuarios", description = "Administracion de usuarios y desbloqueo de cuentas (CUS01)")
public class CtrlUsuarios {

    private final IUsuarioService usuarioService;

    /**
     * Inicializa el controlador con el servicio de usuarios.
     *
     * @param usuarioService servicio de negocio de usuarios.
     */
    public CtrlUsuarios(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Carga la vista de administracion de usuarios FrmUsuarios.
     *
     * @param model modelo de vista.
     * @return plantilla FrmUsuarios.
     */
    @GetMapping("/usuarios")
    public String verUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        return "FrmUsuarios";
    }

    /**
     * Registra un usuario desde el formulario web cifrando su clave con BCrypt.
     *
     * @param dto                datos del formulario.
     * @param redirectAttributes mensajes flash para la vista.
     * @return redireccion al listado de usuarios.
     */
    @PostMapping("/usuarios/guardar")
    public String registrarDesdeVista(@Valid @ModelAttribute("usuarioDTO") UsuarioDTO dto,
                                      RedirectAttributes redirectAttributes) {
        Usuario nuevo = usuarioService.registrarUsuario(dto);
        redirectAttributes.addFlashAttribute("exitoMensaje",
                "Usuario " + nuevo.getUsername() + " creado con rol " + nuevo.getRol());
        return "redirect:/usuarios";
    }

    /**
     * Modifica el estado de un usuario y limpia el bloqueo temporal desde la vista.
     *
     * @param idUsuario          ID del usuario.
     * @param estado             estado destino (ACTIVO o INACTIVO).
     * @param redirectAttributes mensajes flash.
     * @return redireccion a /usuarios.
     */
    @PostMapping("/usuarios/{id}/estado")
    public String actualizarEstadoVista(@PathVariable("id") Integer idUsuario,
                                        @RequestParam("estado") String estado,
                                        RedirectAttributes redirectAttributes) {
        usuarioService.cambiarEstadoYDesbloquear(idUsuario, estado);
        redirectAttributes.addFlashAttribute("exitoMensaje", "Estado actualizado a " + estado);
        return "redirect:/usuarios";
    }

    /**
     * Endpoint REST para listar todos los usuarios (CUS01).
     *
     * @return respuesta JSON con la lista de usuarios.
     */
    @GetMapping("/api/v1/usuarios")
    @ResponseBody
    @Operation(summary = "Listar usuarios (CUS01)", description = "Retorna todos los usuarios registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMINISTRADOR")
    })
    public ResponseEntity<ApiResponseDTO<List<Usuario>>> listarApi() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Lista de usuarios", usuarioService.listarTodos()));
    }

    /**
     * Endpoint REST para dar de alta un usuario con BCrypt (CUS01).
     *
     * @param dto payload con los datos del usuario.
     * @return respuesta HTTP 201 con el usuario creado.
     */
    @PostMapping("/api/v1/usuarios")
    @ResponseBody
    @Operation(summary = "Crear usuario (CUS01)", description = "Crea un usuario con hash BCrypt.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o duplicados")
    })
    public ResponseEntity<ApiResponseDTO<Usuario>> crearApi(@Valid @RequestBody UsuarioDTO dto) {
        Usuario creado = usuarioService.registrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Usuario registrado", creado));
    }

    /**
     * Endpoint REST para cambiar estado y desbloquear cuenta (CUS01).
     *
     * @param idUsuario identificador del usuario.
     * @param estado    ACTIVO o INACTIVO.
     * @return confirmacion JSON.
     */
    @PatchMapping("/api/v1/usuarios/{id}/estado")
    @ResponseBody
    @Operation(summary = "Cambiar estado y desbloquear usuario (CUS01)",
               description = "Actualiza el estado del usuario y reinicia intentos fallidos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado con exito"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> cambiarEstadoApi(
            @Parameter(description = "ID de usuario") @PathVariable("id") Integer idUsuario,
            @Parameter(description = "Estado: ACTIVO o INACTIVO") @RequestParam("estado") String estado) {
        usuarioService.cambiarEstadoYDesbloquear(idUsuario, estado);
        return ResponseEntity.ok(ApiResponseDTO.ok("Estado actualizado", null));
    }
}
