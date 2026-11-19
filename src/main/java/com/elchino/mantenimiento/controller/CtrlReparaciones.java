package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.ItemRepuestoDTO;
import com.elchino.mantenimiento.dto.ReparacionDTO;
import com.elchino.mantenimiento.model.Reparacion;
import com.elchino.mantenimiento.service.IMantenimientoService;
import com.elchino.mantenimiento.service.IReparacionService;
import com.elchino.mantenimiento.service.IRepuestoService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de reparaciones, descuento transaccional de stock y verificacion (CUS07, CUS08, CUS09).
 */
@Controller
@Tag(name = "07. Reparaciones", description = "Ejecucion transaccional de reparaciones y consumo de repuestos (CUS07, CUS08, CUS09)")
public class CtrlReparaciones {

    private final IReparacionService servicio;
    private final IMantenimientoService mantenimientoServicio;
    private final IRepuestoService repuestoServicio;

    /**
     * Constructor del controlador.
     *
     * @param servicio              servicio de reparaciones.
     * @param mantenimientoServicio servicio de mantenimientos.
     * @param repuestoServicio      servicio de repuestos.
     */
    public CtrlReparaciones(IReparacionService servicio,
                            IMantenimientoService mantenimientoServicio,
                            IRepuestoService repuestoServicio) {
        this.servicio = servicio;
        this.mantenimientoServicio = mantenimientoServicio;
        this.repuestoServicio = repuestoServicio;
    }

    /**
     * Muestra la vista FrmReparacion con selector dinamico de repuestos.
     *
     * @param idMantenimiento orden preseleccionada opcional.
     * @param model           modelo MVC.
     * @return vista FrmReparacion.
     */
    @GetMapping("/reparaciones")
    public String vista(@RequestParam(value = "idMantenimiento", required = false) Integer idMantenimiento,
                        Model model) {
        ReparacionDTO dto = new ReparacionDTO();
        dto.setIdMantenimiento(idMantenimiento);
        model.addAttribute("reparacionDTO", dto);
        model.addAttribute("reparaciones", servicio.listarTodas());
        model.addAttribute("mantenimientosActivos", mantenimientoServicio.listarPendientesOEnProceso());
        model.addAttribute("catalogoRepuestos", repuestoServicio.listarTodos());
        return "FrmReparacion";
    }

    /**
     * Procesa la reparacion con listas paralelas de repuestos y cantidades desde la vista web.
     *
     * @param idMantenimiento           ID del mantenimiento.
     * @param diagnosticoTecnico        diagnostico.
     * @param accionesRealizadas        acciones.
     * @param horasHombre               horas invertidas.
     * @param costoManoObra             costo en soles.
     * @param resultado                 dictamen de verificacion.
     * @param observacionesVerificacion notas de calidad.
     * @param idsRepuestos              IDs de repuestos seleccionados.
     * @param cantidades                cantidades de cada repuesto.
     * @param auth                      sesion activa.
     * @param flash                     atributos flash.
     * @return redireccion a /reparaciones.
     */
    @PostMapping("/reparaciones/guardar")
    public String guardarWeb(@RequestParam("idMantenimiento") Integer idMantenimiento,
                             @RequestParam("diagnosticoTecnico") String diagnosticoTecnico,
                             @RequestParam("accionesRealizadas") String accionesRealizadas,
                             @RequestParam("horasHombre") BigDecimal horasHombre,
                             @RequestParam("costoManoObra") BigDecimal costoManoObra,
                             @RequestParam("resultado") String resultado,
                             @RequestParam(value = "observacionesVerificacion", required = false) String observacionesVerificacion,
                             @RequestParam(value = "idRepuesto", required = false) List<Integer> idsRepuestos,
                             @RequestParam(value = "cantidadRepuesto", required = false) List<Integer> cantidades,
                             Authentication auth,
                             RedirectAttributes flash) {
        ReparacionDTO dto = new ReparacionDTO();
        dto.setIdMantenimiento(idMantenimiento);
        dto.setDiagnosticoTecnico(diagnosticoTecnico);
        dto.setAccionesRealizadas(accionesRealizadas);
        dto.setHorasHombre(horasHombre);
        dto.setCostoManoObra(costoManoObra);
        dto.setResultado(resultado);
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

        String autor = auth != null ? auth.getName() : "tecnico1";
        Reparacion r = servicio.registrarReparacionTransaccional(dto, autor);
        flash.addFlashAttribute("exitoMensaje",
                "Reparacion #" + r.getIdReparacion() + " registrada con resultado " + r.getResultado());
        return "redirect:/reparaciones";
    }

    /**
     * Actualiza la verificacion de calidad desde la tabla web (CUS09).
     *
     * @param id            ID de reparacion.
     * @param resultado     nuevo dictamen.
     * @param observaciones notas.
     * @param auth          sesion activa.
     * @param flash         atributos flash.
     * @return redireccion a /reparaciones.
     */
    @PostMapping("/reparaciones/{id}/verificar")
    public String verificarWeb(@PathVariable("id") Integer id,
                               @RequestParam("resultado") String resultado,
                               @RequestParam(value = "observaciones", required = false) String observaciones,
                               Authentication auth,
                               RedirectAttributes flash) {
        String autor = auth != null ? auth.getName() : "jefe.taller";
        servicio.verificarReparacion(id, resultado, observaciones, autor);
        flash.addFlashAttribute("exitoMensaje", "Verificacion actualizada a " + resultado);
        return "redirect:/reparaciones";
    }

    /**
     * Endpoint REST para listar reparaciones (CUS07).
     *
     * @return lista JSON.
     */
    @GetMapping("/api/v1/reparaciones")
    @ResponseBody
    @Operation(summary = "Listar reparaciones (CUS07)", description = "Incluye detalle de repuestos consumidos.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Reparacion>>> listarRest() {
        return ResponseEntity.ok(ApiResponseDTO.ok("Reparaciones listadas", servicio.listarTodas()));
    }

    /**
     * Endpoint REST para ejecutar transaccion maestra de reparacion (CUS07, CUS08, CUS09).
     *
     * @param dto  datos de la reparacion y repuestos.
     * @param auth sesion activa.
     * @return reparacion creada.
     */
    @PostMapping("/api/v1/reparaciones")
    @ResponseBody
    @Operation(summary = "Registrar reparacion transaccional (CUS07, CUS08, CUS09)",
               description = "Descuenta stock atomicamente y actualiza estados de unidad, falla y mantenimiento.")
    @ApiResponse(responseCode = "201", description = "Registrada")
    public ResponseEntity<ApiResponseDTO<Reparacion>> crearRest(@Valid @RequestBody ReparacionDTO dto,
                                                                Authentication auth) {
        String autor = auth != null ? auth.getName() : "tecnico1";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Reparacion registrada", servicio.registrarReparacionTransaccional(dto, autor)));
    }
}
