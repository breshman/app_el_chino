package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.UnidadDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IUnidadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementación de la capa de servicio para la gestión de unidades de transporte
 * de la flota "El Chino" (Implementa CUS02, CUS03, CUS10 y QA-05).
 */
@Service
public class UnidadServiceImpl implements IUnidadService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("OPERATIVA", "EN_MANTENIMIENTO", "FUERA_DE_SERVICIO");

    private final IUnidadDAO unidadDAO;
    private final IUsuarioDAO usuarioDAO;
    private final IHistorialMantenimientoDAO historialDAO;

    public UnidadServiceImpl(IUnidadDAO unidadDAO,
                             IUsuarioDAO usuarioDAO,
                             IHistorialMantenimientoDAO historialDAO) {
        this.unidadDAO = unidadDAO;
        this.usuarioDAO = usuarioDAO;
        this.historialDAO = historialDAO;
    }

    @Override
    public List<Unidad> listarTodas() {
        return unidadDAO.listarTodas();
    }

    @Override
    public List<Unidad> listarPorEstado(String estado) {
        return unidadDAO.listarPorEstado(estado);
    }

    @Override
    public Unidad obtenerPorId(Integer idUnidad) {
        return unidadDAO.buscarPorId(idUnidad)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la unidad con ID: " + idUnidad));
    }

    @Override
    @Transactional
    public Unidad registrarUnidad(UnidadDTO dto, String usernameEjecutor) {
        String codigo = dto.getCodigoInterno().trim().toUpperCase();
        String placa = dto.getPlaca().trim().toUpperCase();

        Optional<Unidad> existente = unidadDAO.buscarPorCodigoOPlaca(codigo, placa);
        if (existente.isPresent()) {
            throw new BusinessException("Ya existe una unidad registrada con el código '" +
                    codigo + "' o placa '" + placa + "'.");
        }

        String estadoInicial = (dto.getEstado() != null && !dto.getEstado().isBlank())
                ? dto.getEstado().trim().toUpperCase()
                : "OPERATIVA";
        validarEstadoUnidad(estadoInicial);

        Unidad unidad = new Unidad();
        unidad.setCodigoInterno(codigo);
        unidad.setPlaca(placa);
        unidad.setMarca(dto.getMarca().trim());
        unidad.setModelo(dto.getModelo().trim());
        unidad.setAnioFabricacion(dto.getAnioFabricacion());
        unidad.setKilometrajeActual(dto.getKilometrajeActual());
        unidad.setEstado(estadoInicial);

        Unidad guardada = unidadDAO.insertar(unidad);

        registrarEventoHistorial(guardada.getIdUnidad(), null, usernameEjecutor,
                "ALTA_UNIDAD", null, estadoInicial,
                "Alta de unidad " + guardada.getCodigoInterno() + " (" + guardada.getPlaca() + ") en estado " + estadoInicial);

        return guardada;
    }

    @Override
    @Transactional
    public Unidad actualizarUnidad(Integer idUnidad, UnidadDTO dto, String usernameEjecutor) {
        Unidad actual = obtenerPorId(idUnidad);
        String estadoAnterior = actual.getEstado();

        String nuevoCodigo = dto.getCodigoInterno().trim().toUpperCase();
        String nuevaPlaca = dto.getPlaca().trim().toUpperCase();

        // Validar duplicados excluyendo la misma unidad que se está editando
        Optional<Unidad> duplicada = unidadDAO.buscarPorCodigoOPlaca(nuevoCodigo, nuevaPlaca);
        if (duplicada.isPresent() && !duplicada.get().getIdUnidad().equals(idUnidad)) {
            throw new BusinessException("Ya existe otra unidad registrada con el código '" +
                    nuevoCodigo + "' o placa '" + nuevaPlaca + "'.");
        }

        String estadoNuevo = (dto.getEstado() != null && !dto.getEstado().isBlank())
                ? dto.getEstado().trim().toUpperCase()
                : estadoAnterior;
        validarEstadoUnidad(estadoNuevo);

        if (dto.getKilometrajeActual() != null && dto.getKilometrajeActual() < actual.getKilometrajeActual()) {
            throw new BusinessException("El kilometraje (" + dto.getKilometrajeActual() +
                    " km) no puede ser menor al actual registrado (" + actual.getKilometrajeActual() + " km).");
        }

        actual.setCodigoInterno(nuevoCodigo);
        actual.setPlaca(nuevaPlaca);
        actual.setMarca(dto.getMarca().trim());
        actual.setModelo(dto.getModelo().trim());
        actual.setAnioFabricacion(dto.getAnioFabricacion());
        actual.setKilometrajeActual(dto.getKilometrajeActual());
        actual.setEstado(estadoNuevo);

        unidadDAO.actualizar(actual);

        if (!estadoAnterior.equalsIgnoreCase(estadoNuevo)) {
            registrarEventoHistorial(idUnidad, null, usernameEjecutor,
                    "CAMBIO_ESTADO_MANUAL", estadoAnterior, estadoNuevo,
                    "Cambio de estado operativo de unidad " + actual.getCodigoInterno() +
                            " de " + estadoAnterior + " a " + estadoNuevo);
        }

        return actual;
    }

    @Override
    @Transactional
    public void cambiarEstadoConAuditoria(Integer idUnidad,
                                          String nuevoEstado,
                                          String motivo,
                                          Integer idMantenimiento,
                                          String usernameEjecutor) {
        String estadoNormalizado = nuevoEstado != null ? nuevoEstado.trim().toUpperCase() : "";
        validarEstadoUnidad(estadoNormalizado);

        Unidad actual = obtenerPorId(idUnidad);
        String estadoAnterior = actual.getEstado();
        unidadDAO.actualizarEstado(idUnidad, estadoNormalizado);

        registrarEventoHistorial(idUnidad, idMantenimiento, usernameEjecutor,
                "CAMBIO_ESTADO", estadoAnterior, estadoNormalizado, motivo);
    }

    private void validarEstadoUnidad(String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new BusinessException("Estado de unidad no válido: '" + estado +
                    "'. Valores permitidos: OPERATIVA, EN_MANTENIMIENTO, FUERA_DE_SERVICIO.");
        }
    }

    private void registrarEventoHistorial(Integer idUnidad,
                                          Integer idMantenimiento,
                                          String usernameEjecutor,
                                          String tipoEvento,
                                          String estadoAnterior,
                                          String estadoNuevo,
                                          String descripcion) {
        if (usernameEjecutor == null || usernameEjecutor.isBlank()) {
            throw new BusinessException("No se pudo identificar al usuario ejecutor para registrar la auditoría.");
        }

        Usuario ejecutor = usuarioDAO.buscarPorUsername(usernameEjecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado para auditoría: " + usernameEjecutor));

        HistorialMantenimiento h = new HistorialMantenimiento();
        h.setIdUnidad(idUnidad);
        h.setIdMantenimiento(idMantenimiento);
        h.setIdUsuario(ejecutor.getIdUsuario());
        h.setTipoEvento(tipoEvento);
        h.setEstadoAnteriorUnidad(estadoAnterior);
        h.setEstadoNuevoUnidad(estadoNuevo);
        h.setDescripcionEvento(descripcion);
        historialDAO.insertar(h);
    }
}