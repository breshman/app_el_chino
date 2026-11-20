package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.MantenimientoDTO;
import com.elchino.mantenimiento.model.Mantenimiento;
import com.elchino.mantenimiento.service.IFallaService;
import com.elchino.mantenimiento.service.IMantenimientoService;
import com.elchino.mantenimiento.service.IUnidadService;
import com.elchino.mantenimiento.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de ordenes de mantenimiento y agenda de tecnicos (CUS06, CUS11).
 */
@Controller
@Tag(name = "06. Mantenimientos", description = "Agenda de mantenimientos y validacion de horarios (CUS06, CUS11)")
public class CtrlMantenimientos {

    private final IMantenimientoService servicio;
    private final IUnidadService unidadServicio;
    private final IUsuarioService usuarioServicio;
    private final IFallaService fallaServicio;

    public CtrlMantenimientos(IMantenimientoService servicio,
                              IUnidadService unidadServicio,
                              IUsuarioService usuarioServicio,
                              IFallaService fallaServicio) {
        this.servicio = servicio;
        this.unidadServicio = unidadServicio;
        this.usuarioServicio = usuarioServicio;
        this.fallaServicio = fallaServicio;
    }

    @GetMapping({"/mantenimientos", "/mantenimientos/programar"})
    public String vista(Model model) {
        if (!model.containsAttribute("mantenimientoDTO")) {
            model.addAttribute("mantenimientoDTO", new MantenimientoDTO());
        }
        cargarCatalogosVista(model);
        return "FrmProgramacion";
    }

    @PostMapping("/mantenimientos/programar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    public String programarWeb(@Valid @ModelAttribute("mantenimientoDTO") MantenimientoDTO dto,
                               BindingResult result,
                               Authentication auth,
                               RedirectAttributes flash,
                               Model model) {
        String autor = obtenerUsuarioAutenticado(auth);

        if (result.hasErrors()) {
            String errores = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            model.addAttribute("errorMensaje", "Revisa los datos de programación: " + errores);
            cargarCatalogosVista(model);
            return "FrmProgramacion";
        }

        Mantenimiento m = servicio.programarMantenimiento(dto, autor);
        flash.addFlashAttribute("exitoMensaje",
                "Mantenimiento #" + m.getIdMantenimiento() + " programado correctamente.");
        return "redirect:/mantenimientos";
    }

    @PostMapping("/mantenimientos/{id}/iniciar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO', 'TECNICO')")
    public String iniciarWeb(@PathVariable("id") Integer id,
                             Authentication auth,
                             RedirectAttributes flash) {
        String autor = obtenerUsuarioAutenticado(auth);
        servicio.iniciarMantenimiento(id, autor);
        flash.addFlashAttribute("exitoMensaje", "Mantenimiento #" + id + " iniciado y en proceso.");
        return "redirect:/mantenimientos";
    }

    @GetMapping("/api/v1/mantenimientos")
    @ResponseBody
    @Operation(summary = "Listar mantenimientos (CUS11)", description = "Retorna todas las ordenes de mantenimiento.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Mantenimiento>>> listarRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Ordenes consultadas", servicio.listarTodos()));
    }

    @PostMapping("/api/v1/mantenimientos/programar")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    @Operation(summary = "Programar mantenimiento (CUS06)", description = "Valida no cruce de horario del tecnico.")
    @ApiResponse(responseCode = "201", description = "Programado")
    public ResponseEntity<ApiResponseDTO<Mantenimiento>> programarRest(@Valid @RequestBody MantenimientoDTO dto,
                                                                       Authentication auth) {
        String autor = obtenerUsuarioAutenticado(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Mantenimiento programado", servicio.programarMantenimiento(dto, autor)));
    }

    private void cargarCatalogosVista(Model model) {
        model.addAttribute("mantenimientos", servicio.listarTodos());
        model.addAttribute("unidades", unidadServicio.listarTodas());
        model.addAttribute("tecnicos", usuarioServicio.listarTecnicosActivos());
        model.addAttribute("fallasActivas", fallaServicio.listarActivas());
    }

    private String obtenerUsuarioAutenticado(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new AccessDeniedException("Debe iniciar sesión para ejecutar esta operación.");
        }
        return auth.getName();
    }
}