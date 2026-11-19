package com.elchino.mantenimiento.controller;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import com.elchino.mantenimiento.dto.ReporteResumenDTO;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IFallaService;
import com.elchino.mantenimiento.service.IMantenimientoService;
import com.elchino.mantenimiento.service.IReporteService;
import com.elchino.mantenimiento.service.IRepuestoService;
import com.elchino.mantenimiento.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador para inicio de sesion y panel principal por rol (CUS01).
 */
@Controller
@Tag(name = "01. Autenticacion", description = "Acceso al sistema y resumen del panel operativo (CUS01)")
public class CtrlAutenticacion {

    private final IReporteService reporteService;
    private final IMantenimientoService mantenimientoService;
    private final IFallaService fallaService;
    private final IRepuestoService repuestoService;
    private final IUsuarioService usuarioService;

    /**
     * Construye el controlador de autenticacion inyectando los servicios requeridos.
     *
     * @param reporteService       servicio de metricas ejecutivas.
     * @param mantenimientoService servicio de ordenes de mantenimiento.
     * @param fallaService         servicio de averias.
     * @param repuestoService      servicio de inventario de repuestos.
     * @param usuarioService       servicio de cuentas de usuario.
     */
    public CtrlAutenticacion(IReporteService reporteService,
                             IMantenimientoService mantenimientoService,
                             IFallaService fallaService,
                             IRepuestoService repuestoService,
                             IUsuarioService usuarioService) {
        this.reporteService = reporteService;
        this.mantenimientoService = mantenimientoService;
        this.fallaService = fallaService;
        this.repuestoService = repuestoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Redirige la ruta base al panel principal.
     *
     * @return ruta de redireccion al dashboard.
     */
    @GetMapping("/")
    public String irInicio() {
        return "redirect:/dashboard";
    }

    /**
     * Renderiza la plantilla de inicio de sesion FrmLogin (CUS01).
     *
     * @return nombre de la vista FrmLogin.
     */
    @GetMapping("/login")
    public String verFormularioLogin() {
        return "FrmLogin";
    }

    /**
     * Carga el panel principal con indicadores y alertas segun el rol activo (CUS01, CUS11).
     *
     * @param model          modelo de datos para Thymeleaf.
     * @param authentication contexto de sesion activa.
     * @return nombre de la vista FrmDashboard.
     */
    @GetMapping("/dashboard")
    public String verPanelPrincipal(Model model, Authentication authentication) {
        ReporteResumenDTO resumen = reporteService.obtenerResumenEjecutivo();
        model.addAttribute("resumen", resumen);
        model.addAttribute("mantenimientosPendientes", mantenimientoService.listarPendientesOEnProceso());
        model.addAttribute("fallasActivas", fallaService.listarActivas());
        model.addAttribute("alertasStock", repuestoService.listarAlertasBajoStock());
        if (authentication != null) {
            model.addAttribute("usuarioActual", authentication.getName());
        }
        return "FrmDashboard";
    }

    /**
     * Devuelve en formato JSON la informacion del usuario en sesion (CUS01).
     *
     * @param authentication sesion activa.
     * @return respuesta HTTP con el perfil del usuario.
     */
    @GetMapping("/api/v1/auth/perfil")
    @ResponseBody
    @Operation(summary = "Obtener perfil de sesion (CUS01)",
               description = "Retorna el usuario autenticado y su rol asignado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil consultado con exito"),
        @ApiResponse(responseCode = "403", description = "Sesion no iniciada")
    })
    public ResponseEntity<ApiResponseDTO<Usuario>> consultarPerfilSesion(Authentication authentication) {
        Usuario u = usuarioService.obtenerPorUsername(authentication.getName());
        u.setPasswordHash("[HASH-OCULTO]");
        return ResponseEntity.ok(ApiResponseDTO.ok("Perfil activo", u));
    }
}
