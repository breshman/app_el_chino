package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.RepuestoDTO;
import com.elchino.mantenimiento.model.Repuesto;
import com.elchino.mantenimiento.service.IRepuestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador de inventario de repuestos y alertas de stock minimo (CUS08).
 */
@Controller
@Tag(name = "08. Repuestos", description = "Catalogo de almacen y alertas de stock minimo (CUS08)")
public class CtrlRepuestos {

    private final IRepuestoService servicio;

    /**
     * Constructor del controlador.
     *
     * @param servicio servicio de repuestos.
     */
    public CtrlRepuestos(IRepuestoService servicio) {
        this.servicio = servicio;
    }

    /**
     * Muestra la plantilla FrmRepuestos.
     *
     * @param model modelo MVC.
     * @return vista FrmRepuestos.
     */
    @GetMapping("/repuestos")
    public String vista(Model model) {
        model.addAttribute("repuestos", servicio.listarTodos());
        model.addAttribute("alertasBajoStock", servicio.listarAlertasBajoStock());
        model.addAttribute("repuestoDTO", new RepuestoDTO());
        return "FrmRepuestos";
    }

    /**
     * Da de alta un repuesto desde formulario web (CUS08).
     *
     * @param dto   datos del repuesto.
     * @param flash atributos flash.
     * @return redireccion a /repuestos.
     */
    @PostMapping("/repuestos/guardar")
    public String guardarWeb(@Valid @ModelAttribute("repuestoDTO") RepuestoDTO dto,
                             RedirectAttributes flash) {
        Repuesto r = servicio.registrarRepuesto(dto);
        flash.addFlashAttribute("exitoMensaje", "Repuesto " + r.getCodigoSku() + " agregado al inventario.");
        return "redirect:/repuestos";
    }

    /**
     * Endpoint REST para listar catalogo de repuestos (CUS08).
     *
     * @return lista JSON.
     */
    @GetMapping("/api/v1/repuestos")
    @ResponseBody
    @Operation(summary = "Listar repuestos (CUS08)", description = "Retorna el inventario completo.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Repuesto>>> listarRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Inventario consultado", servicio.listarTodos()));
    }

    /**
     * Endpoint REST para consultar alertas de stock minimo (CUS08).
     *
     * @return repuestos con stock menor o igual al minimo.
     */
    @GetMapping("/api/v1/repuestos/alertas")
    @ResponseBody
    @Operation(summary = "Alertas de bajo stock (CUS08)", description = "Lista items con stock <= stock_minimo.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Repuesto>>> alertasRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Alertas de stock", servicio.listarAlertasBajoStock()));
    }

    /**
     * Endpoint REST para registrar un repuesto (CUS08).
     *
     * @param dto datos del repuesto.
     * @return repuesto creado.
     */
    @PostMapping("/api/v1/repuestos")
    @ResponseBody
    @Operation(summary = "Crear repuesto (CUS08)", description = "Agrega item al almacen.")
    @ApiResponse(responseCode = "201", description = "Creado")
    public ResponseEntity<ApiResponseDTO<Repuesto>> crearRest(@Valid @RequestBody RepuestoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Repuesto creado", servicio.registrarRepuesto(dto)));
    }
}
