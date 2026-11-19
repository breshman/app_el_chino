package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
import com.elchino.mantenimiento.service.IHistorialService;
import com.elchino.mantenimiento.service.IUnidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controlador de bitacora e historial de mantenimiento por unidad (CUS10, QA-05).
 */
@Controller
@Tag(name = "09. Historial", description = "Trazabilidad inmutable de eventos y cambios de estado (CUS10)")
public class CtrlHistorial {

    private final IHistorialService servicio;
    private final IUnidadService unidadServicio;

    /**
     * Constructor del controlador.
     *
     * @param servicio       servicio de historial.
     * @param unidadServicio servicio de unidades.
     */
    public CtrlHistorial(IHistorialService servicio, IUnidadService unidadServicio) {
        this.servicio = servicio;
        this.unidadServicio = unidadServicio;
    }

    /**
     * Muestra la plantilla FrmHistorial con filtro opcional por unidad.
     *
     * @param idUnidad ID de unidad opcional.
     * @param model    modelo MVC.
     * @return vista FrmHistorial.
     */
    @GetMapping("/historial")
    public String vista(@RequestParam(value = "idUnidad", required = false) Integer idUnidad, Model model) {
        List<HistorialMantenimiento> eventos = (idUnidad != null)
                ? servicio.listarPorUnidad(idUnidad)
                : servicio.listarTodo();
        model.addAttribute("eventos", eventos);
        model.addAttribute("idUnidadSeleccionada", idUnidad);
        model.addAttribute("unidades", unidadServicio.listarTodas());
        return "FrmHistorial";
    }

    /**
     * Endpoint REST para consultar historial (CUS10).
     *
     * @param idUnidad filtro opcional por unidad.
     * @return lista JSON de eventos.
     */
    @GetMapping("/api/v1/historial")
    @ResponseBody
    @Operation(summary = "Consultar historial (CUS10)", description = "Bitacora general o filtrada por idUnidad.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<HistorialMantenimiento>>> listarRest(
            @RequestParam(value = "idUnidad", required = false) Integer idUnidad) {
        List<HistorialMantenimiento> eventos = (idUnidad != null)
                ? servicio.listarPorUnidad(idUnidad)
                : servicio.listarTodo();
        return ResponseEntity.ok(ApiResponseDTO.ok("Historial consultado", eventos));
    }
}
