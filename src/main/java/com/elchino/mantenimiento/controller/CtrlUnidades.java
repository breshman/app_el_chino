package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.UnidadDTO;
import com.elchino.mantenimiento.model.Unidad;
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
 * Controlador de flota vehicular (CUS02, CUS03).
 */
@Controller
@Tag(name = "03. Unidades", description = "Flota de buses El Chino (CUS02, CUS03)")
public class CtrlUnidades {

    private final IUnidadService servicio;

    /**
     * Constructor del controlador.
     *
     * @param servicio servicio de unidades.
     */
    public CtrlUnidades(IUnidadService servicio) {
        this.servicio = servicio;
    }

    /**
     * Muestra la plantilla FrmUnidades.
     *
     * @param estado filtro opcional.
     * @param model  modelo MVC.
     * @return vista FrmUnidades.
     */
    @GetMapping("/unidades")
    public String vista(@RequestParam(value = "estado", required = false) String estado, Model model) {
        List<Unidad> items = (estado != null && !estado.isBlank())
                ? servicio.listarPorEstado(estado)
                : servicio.listarTodas();
        model.addAttribute("unidades", items);
        model.addAttribute("estadoFiltro", estado);
        model.addAttribute("unidadDTO", new UnidadDTO());
        return "FrmUnidades";
    }

    /**
     * Registra un bus desde formulario web.
     *
     * @param dto   datos del bus.
     * @param auth  sesion activa.
     * @param flash atributos flash.
     * @return redireccion a /unidades.
     */
    @PostMapping("/unidades/guardar")
    public String guardarWeb(@Valid @ModelAttribute("unidadDTO") UnidadDTO dto,
                             Authentication auth,
                             RedirectAttributes flash) {
        String autor = auth != null ? auth.getName() : "admin";
        Unidad u = servicio.registrarUnidad(dto, autor);
        flash.addFlashAttribute("exitoMensaje", "Unidad registrada: " + u.getCodigoInterno());
        return "redirect:/unidades";
    }

    /**
     * Cambia el estado operativo desde la tabla web.
     *
     * @param id          ID de unidad.
     * @param nuevoEstado estado destino.
     * @param motivo      justificacion.
     * @param auth        sesion activa.
     * @param flash       atributos flash.
     * @return redireccion a /unidades.
     */
    @PostMapping("/unidades/{id}/estado")
    public String estadoWeb(@PathVariable("id") Integer id,
                            @RequestParam("nuevoEstado") String nuevoEstado,
                            @RequestParam(value = "motivo", defaultValue = "Actualizacion manual") String motivo,
                            Authentication auth,
                            RedirectAttributes flash) {
        String autor = auth != null ? auth.getName() : "admin";
        servicio.cambiarEstadoConAuditoria(id, nuevoEstado, motivo, null, autor);
        flash.addFlashAttribute("exitoMensaje", "Estado cambiado a " + nuevoEstado);
        return "redirect:/unidades";
    }

    /**
     * Endpoint REST para consultar flota (CUS02).
     *
     * @param estado filtro opcional.
     * @return lista JSON de buses.
     */
    @GetMapping("/api/v1/unidades")
    @ResponseBody
    @Operation(summary = "Listar buses (CUS02)", description = "Consulta general o por estado.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<List<Unidad>>> listarRest(
            @RequestParam(value = "estado", required = false) String estado) {
        List<Unidad> items = (estado != null && !estado.isBlank())
                ? servicio.listarPorEstado(estado)
                : servicio.listarTodas();
        return ResponseEntity.ok(ApiResponseDTO.ok("Flota consultada", items));
    }

    /**
     * Endpoint REST para obtener unidad por ID (CUS02).
     *
     * @param id ID de unidad.
     * @return unidad encontrada.
     */
    @GetMapping("/api/v1/unidades/{id}")
    @ResponseBody
    @Operation(summary = "Buscar bus por ID (CUS02)", description = "Detalle de un bus.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ApiResponseDTO<Unidad>> obtenerRest(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ApiResponseDTO.ok("Unidad localizada", servicio.obtenerPorId(id)));
    }

    /**
     * Endpoint REST para registrar bus (CUS02).
     *
     * @param dto  datos de alta.
     * @param auth sesion activa.
     * @return bus creado.
     */
    @PostMapping("/api/v1/unidades")
    @ResponseBody
    @Operation(summary = "Dar de alta bus (CUS02)", description = "Crea unidad y audita en historial.")
    @ApiResponse(responseCode = "201", description = "Creado")
    public ResponseEntity<ApiResponseDTO<Unidad>> crearRest(@Valid @RequestBody UnidadDTO dto, Authentication auth) {
        String autor = auth != null ? auth.getName() : "admin";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Unidad creada", servicio.registrarUnidad(dto, autor)));
    }

    /**
     * Endpoint REST para editar bus (CUS03).
     *
     * @param id   ID de unidad.
     * @param dto  nuevos datos.
     * @param auth sesion activa.
     * @return bus editado.
     */
    @PutMapping("/api/v1/unidades/{id}")
    @ResponseBody
    @Operation(summary = "Actualizar bus (CUS03)", description = "Edita datos y audita cambios de estado.")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    public ResponseEntity<ApiResponseDTO<Unidad>> editarRest(@PathVariable("id") Integer id,
                                                             @Valid @RequestBody UnidadDTO dto,
                                                             Authentication auth) {
        String autor = auth != null ? auth.getName() : "admin";
        return ResponseEntity.ok(ApiResponseDTO.ok("Unidad actualizada", servicio.actualizarUnidad(id, dto, autor)));
    }
}
