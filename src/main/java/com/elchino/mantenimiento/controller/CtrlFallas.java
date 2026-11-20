package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.FallaDTO;
import com.elchino.mantenimiento.model.Falla;
import com.elchino.mantenimiento.service.IFallaService;
import com.elchino.mantenimiento.service.IUnidadService;
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
 * Controlador de averias e incidencias mecanicas (CUS05).
 */
@Controller
@Tag(name = "05. Fallas", description = "Reporte y priorizacion de averias (CUS05)")
public class CtrlFallas {

    private final IFallaService servicio;
    private final IUnidadService unidadServicio;

    public CtrlFallas(IFallaService servicio, IUnidadService unidadServicio) {
        this.servicio = servicio;
        this.unidadServicio = unidadServicio;
    }

    @GetMapping("/fallas")
    public String vista(@RequestParam(value = "idUnidad", required = false) Integer idUnidad,
                        @RequestParam(value = "idInspeccion", required = false) Integer idInspeccion,
                        Model model) {
        if (!model.containsAttribute("fallaDTO")) {
            FallaDTO dto = new FallaDTO();
            dto.setIdUnidad(idUnidad);
            dto.setIdInspeccion(idInspeccion);
            model.addAttribute("fallaDTO", dto);
        }
        model.addAttribute("fallas", servicio.listarTodas());
        model.addAttribute("unidades", unidadServicio.listarTodas());
        return "FrmFalla";
    }

    @PostMapping("/fallas/guardar")
    public String guardarWeb(@Valid @ModelAttribute("fallaDTO") FallaDTO dto,
                             BindingResult result,
                             Authentication auth,
                             RedirectAttributes flash,
                             Model model) {
        String autor = obtenerUsuarioAutenticado(auth);

        if (result.hasErrors()) {
            String errores = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            model.addAttribute("errorMensaje", "Revisa los datos de la falla: " + errores);
            model.addAttribute("fallas", servicio.listarTodas());
            model.addAttribute("unidades", unidadServicio.listarTodas());
            return "FrmFalla";
        }

        Falla f = servicio.registrarFalla(dto, autor);
        flash.addFlashAttribute("exitoMensaje",
                "Falla #" + f.getIdFalla() + " reportada con prioridad " + f.getPrioridad());
        return "redirect:/fallas";
    }

    @PostMapping("/fallas/{id}/priorizar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    public String priorizarWeb(@PathVariable("id") Integer id,
                               @RequestParam("prioridad") String prioridad,
                               @RequestParam("estado") String estado,
                               Authentication auth,
                               RedirectAttributes flash) {
        String autor = obtenerUsuarioAutenticado(auth);
        Falla actualizada = servicio.priorizarFalla(id, prioridad, estado, autor);
        flash.addFlashAttribute("exitoMensaje",
                "Falla #" + id + " actualizada a prioridad " + actualizada.getPrioridad() + " (" + actualizada.getEstado() + ")");
        return "redirect:/fallas";
    }

    @GetMapping("/api/v1/fallas")
    @ResponseBody
    @Operation(summary = "Listar fallas (CUS05)", description = "Retorna todas las averias.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Falla>>> listarRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Fallas consultadas", servicio.listarTodas()));
    }

    @PostMapping("/api/v1/fallas")
    @ResponseBody
    @Operation(summary = "Reportar falla (CUS05)", description = "Registra averia y evalua prioridad alta/critica.")
    @ApiResponse(responseCode = "201", description = "Creada")
    public ResponseEntity<ApiResponseDTO<Falla>> crearRest(@Valid @RequestBody FallaDTO dto,
                                                           Authentication auth) {
        String autor = obtenerUsuarioAutenticado(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Falla registrada", servicio.registrarFalla(dto, autor)));
    }

    private String obtenerUsuarioAutenticado(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new AccessDeniedException("Debe iniciar sesión para ejecutar esta operación.");
        }
        return auth.getName();
    }
}