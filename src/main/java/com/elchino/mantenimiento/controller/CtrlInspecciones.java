package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.InspeccionDTO;
import com.elchino.mantenimiento.model.Inspeccion;
import com.elchino.mantenimiento.service.IInspeccionService;
import com.elchino.mantenimiento.service.IUnidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador de revisiones tecnicas y encadenamiento a fallas (CUS04).
 */
@Controller
@Tag(name = "04. Inspecciones", description = "Revisiones tecnicas de unidades (CUS04)")
public class CtrlInspecciones {

    private final IInspeccionService servicio;
    private final IUnidadService unidadServicio;

    /**
     * Constructor del controlador.
     *
     * @param servicio       servicio de inspecciones.
     * @param unidadServicio servicio de unidades.
     */
    public CtrlInspecciones(IInspeccionService servicio, IUnidadService unidadServicio) {
        this.servicio = servicio;
        this.unidadServicio = unidadServicio;
    }

    /**
     * Muestra la plantilla FrmInspeccion.
     *
     * @param model modelo MVC.
     * @return vista FrmInspeccion.
     */
    @GetMapping("/inspecciones")
    public String vista(Model model) {
        model.addAttribute("inspecciones", servicio.listarTodas());
        model.addAttribute("unidades", unidadServicio.listarTodas());
        model.addAttribute("inspeccionDTO", new InspeccionDTO());
        return "FrmInspeccion";
    }

    /**
     * Guarda una inspeccion y redirige a fallas si no es conforme (CUS04 -> CUS05).
     *
     * @param dto   datos de la revision.
     * @param auth  sesion activa.
     * @param flash atributos flash.
     * @return redireccion correspondiente.
     */
    @PostMapping("/inspecciones/guardar")
    public String guardarWeb(@Valid @ModelAttribute("inspeccionDTO") InspeccionDTO dto,
                             Authentication auth,
                             RedirectAttributes flash) {
        String autor = auth != null ? auth.getName() : "jefe.taller";
        Inspeccion reg = servicio.registrarInspeccion(dto, autor);
        if (servicio.requiereRegistroFalla(reg.getResultado())) {
            flash.addFlashAttribute("alertaMensaje",
                    "Resultado " + reg.getResultado() + ": Complete el reporte de falla detectada.");
            return "redirect:/fallas?idUnidad=" + reg.getIdUnidad() + "&idInspeccion=" + reg.getIdInspeccion();
        }
        flash.addFlashAttribute("exitoMensaje", "Inspeccion conforme registrada.");
        return "redirect:/inspecciones";
    }

    /**
     * Endpoint REST para listar inspecciones (CUS04).
     *
     * @return lista JSON.
     */
    @GetMapping("/api/v1/inspecciones")
    @ResponseBody
    @Operation(summary = "Listar inspecciones (CUS04)", description = "Retorna todas las revisiones.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Inspeccion>>> listarRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Inspecciones listadas", servicio.listarTodas()));
    }

    /**
     * Endpoint REST para registrar inspeccion (CUS04).
     *
     * @param dto  datos de entrada.
     * @param auth sesion activa.
     * @return inspeccion creada.
     */
    @PostMapping("/api/v1/inspecciones")
    @ResponseBody
    @Operation(summary = "Registrar inspeccion (CUS04)", description = "Guarda revision y verifica si requiere falla.")
    @ApiResponse(responseCode = "201", description = "Creada")
    public ResponseEntity<ApiResponseDTO<Inspeccion>> crearRest(@Valid @RequestBody InspeccionDTO dto,
                                                                Authentication auth) {
        String autor = auth != null ? auth.getName() : "jefe.taller";
        Inspeccion reg = servicio.registrarInspeccion(dto, autor);
        String msg = servicio.requiereRegistroFalla(reg.getResultado())
                ? "Inspeccion guardada. Requiere registrar falla (CUS05)."
                : "Inspeccion conforme.";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.ok(msg, reg));
    }
}
