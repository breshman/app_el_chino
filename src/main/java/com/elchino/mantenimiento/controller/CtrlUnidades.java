package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.UnidadDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.model.Unidad;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador de flota vehicular (CUS02, CUS03).
 */
@Controller
@Tag(name = "03. Unidades", description = "Flota de buses El Chino (CUS02, CUS03)")
public class CtrlUnidades {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("OPERATIVA", "EN_MANTENIMIENTO", "FUERA_DE_SERVICIO");

    private final IUnidadService servicio;

    public CtrlUnidades(IUnidadService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/unidades")
    public String vista(@RequestParam(value = "estado", required = false) String estado, Model model) {
        String estadoNormalizado = (estado != null && !estado.isBlank()) ? estado.trim().toUpperCase() : null;
        if (estadoNormalizado != null && !ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            estadoNormalizado = null;
        }

        List<Unidad> items = estadoNormalizado != null
                ? servicio.listarPorEstado(estadoNormalizado)
                : servicio.listarTodas();

        model.addAttribute("unidades", items);
        model.addAttribute("estadoFiltro", estadoNormalizado);
        if (!model.containsAttribute("unidadDTO")) {
            model.addAttribute("unidadDTO", new UnidadDTO());
        }
        return "FrmUnidades";
    }

    @PostMapping("/unidades/guardar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    public String guardarWeb(@Valid @ModelAttribute("unidadDTO") UnidadDTO dto,
                             BindingResult result,
                             Authentication auth,
                             RedirectAttributes flash,
                             Model model) {
        String autor = obtenerUsuarioAutenticado(auth);

        if (result.hasErrors()) {
            String errores = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            model.addAttribute("errorMensaje", "Revisa los datos de la unidad: " + errores);
            model.addAttribute("unidades", servicio.listarTodas());
            model.addAttribute("estadoFiltro", null);
            return "FrmUnidades";
        }

        Unidad u = servicio.registrarUnidad(dto, autor);
        flash.addFlashAttribute("exitoMensaje", "Unidad registrada: " + u.getCodigoInterno());
        return "redirect:/unidades";
    }

    @PostMapping("/unidades/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    public String estadoWeb(@PathVariable("id") Integer id,
                            @RequestParam("nuevoEstado") String nuevoEstado,
                            @RequestParam(value = "motivo", defaultValue = "Actualizacion manual") String motivo,
                            Authentication auth,
                            RedirectAttributes flash) {
        String autor = obtenerUsuarioAutenticado(auth);
        String estadoNormalizado = nuevoEstado != null ? nuevoEstado.trim().toUpperCase() : "";

        if (!ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            throw new BusinessException("Estado operativo no válido: '" + nuevoEstado + "'.");
        }

        String motivoLimpio = (motivo == null || motivo.isBlank()) ? "Actualización manual de estado" : motivo.trim();
        servicio.cambiarEstadoConAuditoria(id, estadoNormalizado, motivoLimpio, null, autor);
        flash.addFlashAttribute("exitoMensaje", "Estado cambiado a " + estadoNormalizado);
        return "redirect:/unidades";
    }

    @GetMapping("/api/v1/unidades")
    @ResponseBody
    @Operation(summary = "Listar buses (CUS02)", description = "Consulta general o por estado.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Unidad>>> listarRest(
            @RequestParam(value = "estado", required = false) String estado) {
        String estadoNormalizado = (estado != null && !estado.isBlank()) ? estado.trim().toUpperCase() : null;
        if (estadoNormalizado != null && !ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            throw new BusinessException("Estado de filtro no válido: " + estado);
        }
        List<Unidad> items = estadoNormalizado != null
                ? servicio.listarPorEstado(estadoNormalizado)
                : servicio.listarTodas();
        return ResponseEntity.ok(ApiResponseDTO.ok("Flota consultada", items));
    }

    @GetMapping("/api/v1/unidades/{id}")
    @ResponseBody
    @Operation(summary = "Buscar bus por ID (CUS02)", description = "Detalle de un bus.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<Unidad>> obtenerRest(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ApiResponseDTO.ok("Unidad localizada", servicio.obtenerPorId(id)));
    }

    @PostMapping("/api/v1/unidades")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    @Operation(summary = "Dar de alta bus (CUS02)", description = "Crea unidad y audita en historial.")
    @ApiResponse(responseCode = "201", description = "Creado")
    public ResponseEntity<ApiResponseDTO<Unidad>> crearRest(@Valid @RequestBody UnidadDTO dto, Authentication auth) {
        String autor = obtenerUsuarioAutenticado(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Unidad creada", servicio.registrarUnidad(dto, autor)));
    }

    @PutMapping("/api/v1/unidades/{id}")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    @Operation(summary = "Actualizar bus (CUS03)", description = "Edita datos y audita cambios de estado.")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    public ResponseEntity<ApiResponseDTO<Unidad>> editarRest(@PathVariable("id") Integer id,
                                                             @Valid @RequestBody UnidadDTO dto,
                                                             Authentication auth) {
        String autor = obtenerUsuarioAutenticado(auth);
        return ResponseEntity.ok(ApiResponseDTO.ok("Unidad actualizada", servicio.actualizarUnidad(id, dto, autor)));
    }

    private String obtenerUsuarioAutenticado(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new AccessDeniedException("Debe iniciar sesión para ejecutar esta operación.");
        }
        return auth.getName();
    }
}