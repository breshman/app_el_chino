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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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

    /**
     * Constructor del controlador.
     *
     * @param servicio        servicio de mantenimientos.
     * @param unidadServicio  servicio de unidades.
     * @param usuarioServicio servicio de usuarios.
     * @param fallaServicio   servicio de fallas.
     */
    public CtrlMantenimientos(IMantenimientoService servicio,
                              IUnidadService unidadServicio,
                              IUsuarioService usuarioServicio,
                              IFallaService fallaServicio) {
        this.servicio = servicio;
        this.unidadServicio = unidadServicio;
        this.usuarioServicio = usuarioServicio;
        this.fallaServicio = fallaServicio;
    }

    /**
     * Muestra la plantilla FrmProgramacion.
     *
     * @param model modelo MVC.
     * @return vista FrmProgramacion.
     */
    @GetMapping({"/mantenimientos", "/mantenimientos/programar"})
    public String vista(Model model) {
        model.addAttribute("mantenimientos", servicio.listarTodos());
        model.addAttribute("unidades", unidadServicio.listarTodas());
        model.addAttribute("tecnicos", usuarioServicio.listarTecnicosActivos());
        model.addAttribute("fallasActivas", fallaServicio.listarActivas());
        model.addAttribute("mantenimientoDTO", new MantenimientoDTO());
        return "FrmProgramacion";
    }

    /**
     * Programa una orden desde la vista web validando horarios (CUS06).
     *
     * @param dto   datos de la orden.
     * @param auth  sesion activa.
     * @param flash atributos flash.
     * @return redireccion a /mantenimientos.
     */
    @PostMapping("/mantenimientos/programar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    public String programarWeb(@Valid @ModelAttribute("mantenimientoDTO") MantenimientoDTO dto,
                               Authentication auth,
                               RedirectAttributes flash) {
        String autor = auth != null ? auth.getName() : "jefe.taller";
        Mantenimiento m = servicio.programarMantenimiento(dto, autor);
        flash.addFlashAttribute("exitoMensaje",
                "Mantenimiento #" + m.getIdMantenimiento() + " programado correctamente.");
        return "redirect:/mantenimientos";
    }

    /**
     * Marca el inicio de un mantenimiento desde la tabla web.
     *
     * @param id    ID del mantenimiento.
     * @param auth  sesion activa.
     * @param flash atributos flash.
     * @return redireccion a /mantenimientos.
     */
    @PostMapping("/mantenimientos/{id}/iniciar")
    public String iniciarWeb(@PathVariable("id") Integer id,
                             Authentication auth,
                             RedirectAttributes flash) {
        String autor = auth != null ? auth.getName() : "tecnico1";
        servicio.iniciarMantenimiento(id, autor);
        flash.addFlashAttribute("exitoMensaje", "Mantenimiento #" + id + " en proceso.");
        return "redirect:/mantenimientos";
    }

    /**
     * Endpoint REST para listar todos los mantenimientos (CUS06, CUS11).
     *
     * @return lista JSON.
     */
    @GetMapping("/api/v1/mantenimientos")
    @ResponseBody
    @Operation(summary = "Listar mantenimientos (CUS11)", description = "Retorna todas las ordenes de mantenimiento.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Mantenimiento>>> listarRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Ordenes consultadas", servicio.listarTodos()));
    }

    /**
     * Endpoint REST para programar mantenimiento (CUS06).
     *
     * @param dto  datos de la orden.
     * @param auth sesion activa.
     * @return mantenimiento creado.
     */
    @PostMapping("/api/v1/mantenimientos/programar")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    @Operation(summary = "Programar mantenimiento (CUS06)", description = "Valida no cruce de horario del tecnico.")
    @ApiResponse(responseCode = "201", description = "Programado")
    public ResponseEntity<ApiResponseDTO<Mantenimiento>> programarRest(@Valid @RequestBody MantenimientoDTO dto,
                                                                       Authentication auth) {
        String autor = auth != null ? auth.getName() : "jefe.taller";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Mantenimiento programado", servicio.programarMantenimiento(dto, autor)));
    }
}
