package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.ReporteResumenDTO;
import com.elchino.mantenimiento.service.IFallaService;
import com.elchino.mantenimiento.service.IMantenimientoService;
import com.elchino.mantenimiento.service.IReparacionService;
import com.elchino.mantenimiento.service.IReporteService;
import com.elchino.mantenimiento.service.IRepuestoService;
import com.elchino.mantenimiento.service.IUnidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador de indicadores gerenciales y reportes imprimibles (CUS11, CUS12).
 */
@Controller
@Tag(name = "10. Reportes", description = "Metricas gerenciales y costos de mantenimiento (CUS11, CUS12)")
public class CtrlReportes {

    private final IReporteService servicio;
    private final IUnidadService unidadServicio;
    private final IMantenimientoService mantenimientoServicio;
    private final IReparacionService reparacionServicio;
    private final IFallaService fallaServicio;
    private final IRepuestoService repuestoServicio;

    /**
     * Constructor del controlador.
     *
     * @param servicio              servicio de reportes.
     * @param unidadServicio        servicio de unidades.
     * @param mantenimientoServicio servicio de mantenimientos.
     * @param reparacionServicio    servicio de reparaciones.
     * @param fallaServicio         servicio de fallas.
     * @param repuestoServicio      servicio de repuestos.
     */
    public CtrlReportes(IReporteService servicio,
                        IUnidadService unidadServicio,
                        IMantenimientoService mantenimientoServicio,
                        IReparacionService reparacionServicio,
                        IFallaService fallaServicio,
                        IRepuestoService repuestoServicio) {
        this.servicio = servicio;
        this.unidadServicio = unidadServicio;
        this.mantenimientoServicio = mantenimientoServicio;
        this.reparacionServicio = reparacionServicio;
        this.fallaServicio = fallaServicio;
        this.repuestoServicio = repuestoServicio;
    }

    /**
     * Muestra la plantilla FrmReportes lista para impresion/exportacion PDF.
     *
     * @param model modelo MVC.
     * @return vista FrmReportes.
     */
    @GetMapping("/reportes")
    public String vista(Model model) {
        model.addAttribute("resumen", servicio.obtenerResumenEjecutivo());
        model.addAttribute("unidades", unidadServicio.listarTodas());
        model.addAttribute("mantenimientosPendientes", mantenimientoServicio.listarPendientesOEnProceso());
        model.addAttribute("reparaciones", reparacionServicio.listarTodas());
        model.addAttribute("fallasActivas", fallaServicio.listarActivas());
        model.addAttribute("alertasStock", repuestoServicio.listarAlertasBajoStock());
        return "FrmReportes";
    }

    /**
     * Endpoint REST que devuelve el resumen ejecutivo (CUS11, CUS12).
     *
     * @return indicadores consolidados en JSON.
     */
    @GetMapping("/api/v1/reportes/resumen")
    @ResponseBody
    @Operation(summary = "Resumen ejecutivo de flota (CUS11, CUS12)",
               description = "Retorna disponibilidad de flota, fallas criticas, alertas de stock y costos.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<ReporteResumenDTO>> resumenRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Resumen generado", servicio.obtenerResumenEjecutivo()));
    }
}
