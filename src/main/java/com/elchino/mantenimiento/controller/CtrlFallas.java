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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador de averias e incidencias mecanicas (CUS05).
 */
@Controller
@Tag(name = "05. Fallas", description = "Reporte y priorizacion de averias (CUS05)")
public class CtrlFallas {

    private final IFallaService servicio;
    private final IUnidadService unidadServicio;

    /**
     * Constructor del controlador.
     *
     * @param servicio       servicio de fallas.
     * @param unidadServicio servicio de unidades.
     */
    public CtrlFallas(IFallaService servicio, IUnidadService unidadServicio) {
        this.servicio = servicio;
        this.unidadServicio = unidadServicio;
    }

    /**
     * Muestra la plantilla FrmFalla con preseleccion de unidad si viene de CUS04.
     *
     * @param idUnidad     unidad preseleccionada.
     * @param idInspeccion inspeccion origen.
     * @param model        modelo MVC.
     * @return vista FrmFalla.
     */
    @GetMapping("/fallas")
    public String vista(@RequestParam(value = "idUnidad", required = false) Integer idUnidad,
                        @RequestParam(value = "idInspeccion", required = false) Integer idInspeccion,
                        Model model) {
        FallaDTO dto = new FallaDTO();
        dto.setIdUnidad(idUnidad);
        dto.setIdInspeccion(idInspeccion);
        model.addAttribute("fallaDTO", dto);
        model.addAttribute("fallas", servicio.listarTodas());
        model.addAttribute("unidades", unidadServicio.listarTodas());
        return "FrmFalla";
    }

    /**
     * Registra una falla desde el formulario web (CUS05).
     *
     * @param dto   datos de la averia.
     * @param auth  sesion activa.
     * @param flash atributos flash.
     * @return redireccion a /fallas.
     */
    @PostMapping("/fallas/guardar")
    public String guardarWeb(@Valid @ModelAttribute("fallaDTO") FallaDTO dto,
                             Authentication auth,
                             RedirectAttributes flash) {
        String autor = auth != null ? auth.getName() : "conductor1";
        Falla f = servicio.registrarFalla(dto, autor);
        flash.addFlashAttribute("exitoMensaje",
                "Falla #" + f.getIdFalla() + " reportada con prioridad " + f.getPrioridad());
        return "redirect:/fallas";
    }

    /**
     * Cambia prioridad y estado de una falla desde la vista (CUS05).
     *
     * @param id        ID de falla.
     * @param prioridad nueva prioridad.
     * @param estado    nuevo estado.
     * @param auth      sesion activa.
     * @param flash     atributos flash.
     * @return redireccion a /fallas.
     */
    @PostMapping("/fallas/{id}/priorizar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE_MANTENIMIENTO')")
    public String priorizarWeb(@PathVariable("id") Integer id,
                               @RequestParam("prioridad") String prioridad,
                               @RequestParam("estado") String estado,
                               Authentication auth,
                               RedirectAttributes flash) {
        String autor = auth != null ? auth.getName() : "jefe.taller";
        servicio.priorizarFalla(id, prioridad, estado, autor);
        flash.addFlashAttribute("exitoMensaje", "Falla actualizada a prioridad " + prioridad);
        return "redirect:/fallas";
    }

    /**
     * Endpoint REST para listar fallas (CUS05).
     *
     * @return lista JSON.
     */
    @GetMapping("/api/v1/fallas")
    @ResponseBody
    @Operation(summary = "Listar fallas (CUS05)", description = "Retorna todas las averias.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Falla>>> listarRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Fallas consultadas", servicio.listarTodas()));
    }

    /**
     * Endpoint REST para reportar falla (CUS05).
     *
     * @param dto  datos de la falla.
     * @param auth sesion activa.
     * @return falla creada.
     */
    @PostMapping("/api/v1/fallas")
    @ResponseBody
    @Operation(summary = "Reportar falla (CUS05)", description = "Registra averia y evalua prioridad alta/critica.")
    @ApiResponse(responseCode = "201", description = "Creada")
    public ResponseEntity<ApiResponseDTO<Falla>> crearRest(@Valid @RequestBody FallaDTO dto,
                                                           Authentication auth) {
        String autor = auth != null ? auth.getName() : "conductor1";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Falla registrada", servicio.registrarFalla(dto, autor)));
    }
}
