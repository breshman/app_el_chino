package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.ItemRepuestoDTO;
import com.elchino.mantenimiento.dto.ReparacionDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.model.Reparacion;
import com.elchino.mantenimiento.service.IMantenimientoService;
import com.elchino.mantenimiento.service.IReparacionService;
import com.elchino.mantenimiento.service.IRepuestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador de reparaciones, descuento transaccional de stock y verificacion (CUS07, CUS08, CUS09).
 */
@Controller
@Tag(name = "07. Reparaciones", description = "Ejecucion transaccional de reparaciones y consumo de repuestos (CUS07, CUS08, CUS09)")
public class CtrlReparaciones {

    private static final Set<String> RESULTADOS_VALIDOS = Set.of("PENDIENTE", "SATISFACTORIO", "REQUIERE_REVISION");

    private final IReparacionService servicio;
    private final IMantenimientoService mantenimientoServicio;
    private final IRepuestoService repuestoServicio;
    private final Validator validator;

    public CtrlReparaciones(IReparacionService servicio,
                            IMantenimientoService mantenimientoServicio,
                            IRepuestoService repuestoServicio,
                            Validator validator) {
        this.servicio = servicio;
        this.mantenimientoServicio = mantenimientoServicio;
        this.repuestoServicio = repuestoServicio;
        this.validator = validator;
    }

    @GetMapping("/reparaciones")
    public String vista(@RequestParam(value = "idMantenimiento", required = false) Integer idMantenimiento,
                        Model model) {
        if (!model.containsAttribute("reparacionDTO")) {
            ReparacionDTO dto = new ReparacionDTO();
            dto.setIdMantenimiento(idMantenimiento);
            model.addAttribute("reparacionDTO", dto);
        }
        cargarCatalogosVista(model);
        return "FrmReparacion";
    }

    @PostMapping("/reparaciones/guardar")
    public String guardarWeb(@RequestParam(value = "idMantenimiento", required = false) Integer idMantenimiento,
                             @RequestParam(value = "diagnosticoTecnico", required = false) String diagnosticoTecnico,
                             @RequestParam(value = "accionesRealizadas", required = false) String accionesRealizadas,
                             @RequestParam(value = "horasHombre", required = false) BigDecimal horasHombre,
                             @RequestParam(value = "costoManoObra", required = false) BigDecimal costoManoObra,
                             @RequestParam(value = "resultado", defaultValue = "PENDIENTE") String resultado,
                             @RequestParam(value = "observacionesVerificacion", required = false) String observacionesVerificacion,
                             @RequestParam(value = "idRepuesto", required = false) List<Integer> idsRepuestos,
                             @RequestParam(value = "cantidadRepuesto", required = false) List<Integer> cantidades,
                             Authentication auth,
                             RedirectAttributes flash,
                             Model model) {
        String autor = obtenerUsuarioAutenticado(auth);

        // Si el usuario tiene rol TECNICO, no puede auto-aprobarse: el resultado inicia en PENDIENTE
        boolean esTecnico = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_TECNICO".equals(a.getAuthority()));
        String resultadoNormalizado = (esTecnico || resultado == null || resultado.isBlank())
                ? "PENDIENTE"
                : resultado.trim().toUpperCase();

        if (!RESULTADOS_VALIDOS.contains(resultadoNormalizado)) {
            throw new BusinessException("Resultado de reparación no válido: " + resultadoNormalizado);
        }

        ReparacionDTO dto = new ReparacionDTO();
        dto.setIdMantenimiento(idMantenimiento);
        dto.setDiagnosticoTecnico(diagnosticoTecnico != null ? diagnosticoTecnico.trim() : null);
        dto.setAccionesRealizadas(accionesRealizadas != null ? accionesRealizadas.trim() : null);
        dto.setHorasHombre(horasHombre);
        dto.setCostoManoObra(costoManoObra);
        dto.setResultado(resultadoNormalizado);
        dto.setObservacionesVerificacion(observacionesVerificacion);

        List<ItemRepuestoDTO> items = new ArrayList<>();
        if (idsRepuestos != null && cantidades != null) {
            int max = Math.min(idsRepuestos.size(), cantidades.size());
            for (int i = 0; i < max; i++) {
                if (idsRepuestos.get(i) != null && cantidades.get(i) != null && cantidades.get(i) > 0) {
                    items.add(new ItemRepuestoDTO(idsRepuestos.get(i), cantidades.get(i)));
                }
            }
        }
        dto.setRepuestos(items);

        // Validar reglas del DTO (@NotNull, @NotBlank, @DecimalMin, etc.) antes de ejecutar
        Set<ConstraintViolation<ReparacionDTO>> violaciones = validator.validate(dto);
        if (!violaciones.isEmpty()) {
            String errores = violaciones.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            model.addAttribute("errorMensaje", "Revisa los datos ingresados: " + errores);
            model.addAttribute("reparacionDTO", dto);
            cargarCatalogosVista(model);
            return "FrmReparacion";
        }

        Reparacion r = servicio.registrarReparacionTransaccional(dto, autor);
        flash.addFlashAttribute("exitoMensaje",
                "Reparacion #" + r.getIdReparacion() + " registrada con resultado " + r.getResultado());
        return "redirect:/reparaciones";
    }

    @PostMapping("/reparaciones/{id}/verificar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    public String verificarWeb(@PathVariable("id") Integer id,
                               @RequestParam("resultado") String resultado,
                               @RequestParam(value = "observaciones", required = false) String observaciones,
                               Authentication auth,
                               RedirectAttributes flash) {
        String autor = obtenerUsuarioAutenticado(auth);
        String resultadoNormalizado = resultado != null ? resultado.trim().toUpperCase() : "";

        if (!RESULTADOS_VALIDOS.contains(resultadoNormalizado)) {
            throw new BusinessException("El dictamen de verificación '" + resultado + "' no es válido.");
        }

        servicio.verificarReparacion(id, resultadoNormalizado, observaciones, autor);
        flash.addFlashAttribute("exitoMensaje", "Verificacion actualizada a " + resultadoNormalizado);
        return "redirect:/reparaciones";
    }

    @GetMapping("/api/v1/reparaciones")
    @ResponseBody
    @Operation(summary = "Listar reparaciones (CUS07)", description = "Incluye detalle de repuestos consumidos.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Reparacion>>> listarRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Reparaciones listadas", servicio.listarTodas()));
    }

    @PostMapping("/api/v1/reparaciones")
    @ResponseBody
    @Operation(summary = "Registrar reparacion transaccional (CUS07, CUS08, CUS09)",
               description = "Descuenta stock atomicamente y actualiza estados de unidad, falla y mantenimiento.")
    @ApiResponse(responseCode = "201", description = "Registrada")
    public ResponseEntity<ApiResponseDTO<Reparacion>> crearRest(@Valid @RequestBody ReparacionDTO dto,
                                                                Authentication auth) {
        String autor = obtenerUsuarioAutenticado(auth);
        boolean esTecnico = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_TECNICO".equals(a.getAuthority()));
        if (esTecnico) {
            dto.setResultado("PENDIENTE");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Reparacion registrada", servicio.registrarReparacionTransaccional(dto, autor)));
    }

    private void cargarCatalogosVista(Model model) {
        model.addAttribute("reparaciones", servicio.listarTodas());
        model.addAttribute("mantenimientosActivos", mantenimientoServicio.listarPendientesOEnProceso());
        model.addAttribute("catalogoRepuestos", repuestoServicio.listarTodos());
    }

    private String obtenerUsuarioAutenticado(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new AccessDeniedException("Debe iniciar sesión para ejecutar esta operación.");
        }
        return auth.getName();
    }
}