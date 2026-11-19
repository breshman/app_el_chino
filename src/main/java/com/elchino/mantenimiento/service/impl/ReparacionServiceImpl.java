package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IDetalleRepuestoDAO;
import com.elchino.mantenimiento.dao.IFallaDAO;
import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.dao.IMantenimientoDAO;
import com.elchino.mantenimiento.dao.IReparacionDAO;
import com.elchino.mantenimiento.dao.IRepuestoDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.ItemRepuestoDTO;
import com.elchino.mantenimiento.dto.ReparacionDTO;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.exception.StockInsuficienteException;
import com.elchino.mantenimiento.model.DetalleRepuesto;
import com.elchino.mantenimiento.model.Falla;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
import com.elchino.mantenimiento.model.Mantenimiento;
import com.elchino.mantenimiento.model.Reparacion;
import com.elchino.mantenimiento.model.Repuesto;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IReparacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la capa de servicio para la **Transacción Maestra de Reparación**,
 * consumo de repuestos con control de stock y verificación de calidad
 * (Implementa CUS07, CUS08, CUS09, QA-04 y QA-05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Service
public class ReparacionServiceImpl implements IReparacionService {

    private static final Logger log = LoggerFactory.getLogger(ReparacionServiceImpl.class);

    private final IReparacionDAO reparacionDAO;
    private final IMantenimientoDAO mantenimientoDAO;
    private final IRepuestoDAO repuestoDAO;
    private final IDetalleRepuestoDAO detalleRepuestoDAO;
    private final IFallaDAO fallaDAO;
    private final IUnidadDAO unidadDAO;
    private final IUsuarioDAO usuarioDAO;
    private final IHistorialMantenimientoDAO historialDAO;

    /**
     * Constructor con inyección de todos los DAOs participantes en la transacción maestra.
     *
     * @param reparacionDAO      DAO de reparaciones.
     * @param mantenimientoDAO   DAO de mantenimientos.
     * @param repuestoDAO        DAO de repuestos.
     * @param detalleRepuestoDAO DAO de detalle de repuestos.
     * @param fallaDAO           DAO de fallas.
     * @param unidadDAO          DAO de unidades.
     * @param usuarioDAO         DAO de usuarios.
     * @param historialDAO       DAO de historial de mantenimiento.
     */
    public ReparacionServiceImpl(IReparacionDAO reparacionDAO,
                                 IMantenimientoDAO mantenimientoDAO,
                                 IRepuestoDAO repuestoDAO,
                                 IDetalleRepuestoDAO detalleRepuestoDAO,
                                 IFallaDAO fallaDAO,
                                 IUnidadDAO unidadDAO,
                                 IUsuarioDAO usuarioDAO,
                                 IHistorialMantenimientoDAO historialDAO) {
        this.reparacionDAO = reparacionDAO;
        this.mantenimientoDAO = mantenimientoDAO;
        this.repuestoDAO = repuestoDAO;
        this.detalleRepuestoDAO = detalleRepuestoDAO;
        this.fallaDAO = fallaDAO;
        this.unidadDAO = unidadDAO;
        this.usuarioDAO = usuarioDAO;
        this.historialDAO = historialDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Reparacion> listarTodas() {
        List<Reparacion> lista = reparacionDAO.listarTodas();
        for (Reparacion r : lista) {
            r.setDetallesRepuestos(detalleRepuestoDAO.listarPorReparacion(r.getIdReparacion()));
        }
        return lista;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reparacion obtenerPorId(Integer idReparacion) {
        Reparacion r = reparacionDAO.buscarPorId(idReparacion)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la reparación con ID: " + idReparacion));
        r.setDetallesRepuestos(detalleRepuestoDAO.listarPorReparacion(idReparacion));
        return r;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reparacion registrarReparacionTransaccional(ReparacionDTO dto, String usernameTecnico) {
        Mantenimiento mantenimiento = mantenimientoDAO.buscarPorId(dto.getIdMantenimiento())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el mantenimiento con ID: " + dto.getIdMantenimiento()));

        Unidad unidad = unidadDAO.buscarPorId(mantenimiento.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la unidad asociada al mantenimiento"));

        Usuario tecnico = usuarioDAO.buscarPorUsername(usernameTecnico)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario técnico no encontrado: " + usernameTecnico));

        // 1. Insertar registro principal de reparación (CUS07)
        Reparacion reparacion = new Reparacion();
        reparacion.setIdMantenimiento(mantenimiento.getIdMantenimiento());
        reparacion.setIdTecnico(tecnico.getIdUsuario());
        reparacion.setDiagnosticoTecnico(dto.getDiagnosticoTecnico().trim());
        reparacion.setAccionesRealizadas(dto.getAccionesRealizadas().trim());
        reparacion.setHorasHombre(dto.getHorasHombre());
        reparacion.setCostoManoObra(dto.getCostoManoObra());
        reparacion.setResultado(dto.getResultado());
        reparacion.setObservacionesVerificacion(dto.getObservacionesVerificacion());

        Reparacion guardada = reparacionDAO.insertar(reparacion);
        List<DetalleRepuesto> detallesGuardados = new ArrayList<>();

        // 2. Procesar y descontar transaccionalmente cada repuesto utilizado (CUS08 y QA-04)
        if (dto.getRepuestos() != null) {
            for (ItemRepuestoDTO item : dto.getRepuestos()) {
                if (item.getIdRepuesto() == null || item.getCantidad() == null || item.getCantidad() <= 0) {
                    continue;
                }
                Repuesto repuesto = repuestoDAO.buscarPorId(item.getIdRepuesto())
                        .orElseThrow(() -> new ResourceNotFoundException("No existe el repuesto con ID: " + item.getIdRepuesto()));

                // Validación de stock antes y durante actualización atómica
                if (repuesto.getStock() < item.getCantidad()) {
                    throw new StockInsuficienteException("Stock insuficiente para el repuesto '" + repuesto.getNombre() +
                            "' (SKU: " + repuesto.getCodigoSku() + "). Disponible: " + repuesto.getStock() +
                            ", Solicitado: " + item.getCantidad());
                }

                int filasAfectadas = repuestoDAO.descontarStock(repuesto.getIdRepuesto(), item.getCantidad());
                if (filasAfectadas == 0) {
                    throw new StockInsuficienteException("No se pudo descontar stock del repuesto '" + repuesto.getNombre() +
                            "'. Operación revertida por concurrencia o falta de stock.");
                }

                int nuevoStock = repuesto.getStock() - item.getCantidad();
                // Regla CUS08: Alertar si stock <= stock_minimo
                if (nuevoStock <= repuesto.getStockMinimo()) {
                    log.warn("ALERTA DE STOCK MÍNIMO (CUS08): El repuesto {} ({}) quedó con {} unidades (Mínimo: {}).",
                            repuesto.getCodigoSku(), repuesto.getNombre(), nuevoStock, repuesto.getStockMinimo());
                }

                DetalleRepuesto detalle = new DetalleRepuesto();
                detalle.setIdReparacion(guardada.getIdReparacion());
                detalle.setIdRepuesto(repuesto.getIdRepuesto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(repuesto.getPrecioUnitario());
                BigDecimal subtotal = repuesto.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
                detalle.setSubtotal(subtotal);
                detalle.setRepuestoNombre(repuesto.getNombre());
                detalle.setRepuestoSku(repuesto.getCodigoSku());

                detallesGuardados.add(detalleRepuestoDAO.insertar(detalle));
            }
        }
        guardada.setDetallesRepuestos(detallesGuardados);

        // 3. Aplicar reglas de negocio según resultado de verificación (CUS09 y QA-05)
        aplicarEfectosVerificacion(mantenimiento, unidad, guardada.getResultado(),
                guardada.getAccionesRealizadas(), tecnico.getIdUsuario());

        return guardada;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reparacion verificarReparacion(Integer idReparacion,
                                          String resultado,
                                          String observaciones,
                                          String usernameVerificador) {
        Reparacion reparacion = obtenerPorId(idReparacion);
        Mantenimiento mantenimiento = mantenimientoDAO.buscarPorId(reparacion.getIdMantenimiento())
                .orElseThrow(() -> new ResourceNotFoundException("Mantenimiento asociado no encontrado"));
        Unidad unidad = unidadDAO.buscarPorId(mantenimiento.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("Unidad asociada no encontrada"));
        Usuario verificador = usuarioDAO.buscarPorUsername(usernameVerificador)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario verificador no encontrado"));

        reparacionDAO.actualizarVerificacion(idReparacion, resultado, observaciones);
        reparacion.setResultado(resultado);
        reparacion.setObservacionesVerificacion(observaciones);

        aplicarEfectosVerificacion(mantenimiento, unidad, resultado,
                "Verificación de calidad: " + (observaciones != null ? observaciones : resultado),
                verificador.getIdUsuario());

        return reparacion;
    }

    /**
     * Ejecuta la actualización de estados de mantenimiento, falla, unidad e historial
     * según el dictamen de la reparación ({@code SATISFACTORIO} vs {@code REQUIERE_REVISION})
     * (Regla crítica CUS07 + CUS09 + QA-05).
     */
    private void aplicarEfectosVerificacion(Mantenimiento mantenimiento,
                                            Unidad unidad,
                                            String resultado,
                                            String detalleAccion,
                                            Integer idUsuario) {
        String estadoAnteriorUnidad = unidad.getEstado();

        if ("SATISFACTORIO".equalsIgnoreCase(resultado)) {
            // Finalizar mantenimiento (FINALIZADO)
            mantenimientoDAO.actualizarEstadoYFechas(
                    mantenimiento.getIdMantenimiento(), "FINALIZADO",
                    mantenimiento.getFechaInicio() != null ? mantenimiento.getFechaInicio() : LocalDateTime.now(),
                    LocalDateTime.now());

            // Si tiene falla asociada, marcar falla como RESUELTA
            if (mantenimiento.getIdFalla() != null) {
                fallaDAO.buscarPorId(mantenimiento.getIdFalla()).ifPresent((Falla f) ->
                        fallaDAO.actualizarPrioridadYEstado(f.getIdFalla(), f.getPrioridad(), "RESUELTA")
                );
            }

            // Cambiar estado de unidad a OPERATIVA
            unidadDAO.actualizarEstado(unidad.getIdUnidad(), "OPERATIVA");

            // Insertar registro automático en historial_mantenimiento (QA-05)
            HistorialMantenimiento h = new HistorialMantenimiento();
            h.setIdUnidad(unidad.getIdUnidad());
            h.setIdMantenimiento(mantenimiento.getIdMantenimiento());
            h.setIdUsuario(idUsuario);
            h.setTipoEvento("REPARACION_SATISFACTORIA");
            h.setEstadoAnteriorUnidad(estadoAnteriorUnidad);
            h.setEstadoNuevoUnidad("OPERATIVA");
            h.setDescripcionEvento("Reparación verificada como SATISFACTORIO. Mantenimiento FINALIZADO y unidad " +
                    unidad.getCodigoInterno() + " restituida a estado OPERATIVA. Detalle: " + detalleAccion);
            historialDAO.insertar(h);

        } else if ("REQUIERE_REVISION".equalsIgnoreCase(resultado)) {
            // Regla CUS09: Si es REQUIERE_REVISION, mantener mantenimiento EN_PROCESO y unidad EN_MANTENIMIENTO
            mantenimientoDAO.actualizarEstadoYFechas(
                    mantenimiento.getIdMantenimiento(), "EN_PROCESO",
                    mantenimiento.getFechaInicio() != null ? mantenimiento.getFechaInicio() : LocalDateTime.now(),
                    null);
            unidadDAO.actualizarEstado(unidad.getIdUnidad(), "EN_MANTENIMIENTO");

            HistorialMantenimiento h = new HistorialMantenimiento();
            h.setIdUnidad(unidad.getIdUnidad());
            h.setIdMantenimiento(mantenimiento.getIdMantenimiento());
            h.setIdUsuario(idUsuario);
            h.setTipoEvento("REPARACION_OBSERVADA");
            h.setEstadoAnteriorUnidad(estadoAnteriorUnidad);
            h.setEstadoNuevoUnidad("EN_MANTENIMIENTO");
            h.setDescripcionEvento("Reparación marcada como REQUIERE_REVISION. Mantenimiento continúa EN_PROCESO. Detalle: " + detalleAccion);
            historialDAO.insertar(h);
        }
    }
}
