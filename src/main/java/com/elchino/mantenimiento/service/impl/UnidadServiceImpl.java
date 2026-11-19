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

/**
 * Implementación de la capa de servicio para la gestión de unidades de transporte
 * de la flota "El Chino" (Implementa CUS02, CUS03, CUS10 y QA-05).
 *
 * <p>Garantiza que todo cambio de estado de una unidad genere automáticamente un
 * registro inmutable en {@code historial_mantenimiento}.</p>
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Service
public class UnidadServiceImpl implements IUnidadService {

    private final IUnidadDAO unidadDAO;
    private final IUsuarioDAO usuarioDAO;
    private final IHistorialMantenimientoDAO historialDAO;

    /**
     * Constructor con inyección de DAOs requeridos.
     *
     * @param unidadDAO    DAO de unidades.
     * @param usuarioDAO   DAO de usuarios.
     * @param historialDAO DAO de historial de mantenimiento.
     */
    public UnidadServiceImpl(IUnidadDAO unidadDAO,
                             IUsuarioDAO usuarioDAO,
                             IHistorialMantenimientoDAO historialDAO) {
        this.unidadDAO = unidadDAO;
        this.usuarioDAO = usuarioDAO;
        this.historialDAO = historialDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Unidad> listarTodas() {
        return unidadDAO.listarTodas();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Unidad> listarPorEstado(String estado) {
        return unidadDAO.listarPorEstado(estado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Unidad obtenerPorId(Integer idUnidad) {
        return unidadDAO.buscarPorId(idUnidad)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la unidad con ID: " + idUnidad));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Unidad registrarUnidad(UnidadDTO dto, String usernameEjecutor) {
        Optional<Unidad> existente = unidadDAO.buscarPorCodigoOPlaca(dto.getCodigoInterno(), dto.getPlaca());
        if (existente.isPresent()) {
            throw new BusinessException("Ya existe una unidad registrada con el código '" +
                    dto.getCodigoInterno() + "' o placa '" + dto.getPlaca() + "'.");
        }

        Unidad unidad = new Unidad();
        unidad.setCodigoInterno(dto.getCodigoInterno().trim().toUpperCase());
        unidad.setPlaca(dto.getPlaca().trim().toUpperCase());
        unidad.setMarca(dto.getMarca().trim());
        unidad.setModelo(dto.getModelo().trim());
        unidad.setAnioFabricacion(dto.getAnioFabricacion());
        unidad.setKilometrajeActual(dto.getKilometrajeActual());
        String estadoInicial = dto.getEstado() != null ? dto.getEstado() : "OPERATIVA";
        unidad.setEstado(estadoInicial);

        Unidad guardada = unidadDAO.insertar(unidad);

        // QA-05: Registrar alta inicial de la unidad en historial_mantenimiento
        registrarEventoHistorial(guardada.getIdUnidad(), null, usernameEjecutor,
                "ALTA_UNIDAD", null, estadoInicial,
                "Alta de unidad " + guardada.getCodigoInterno() + " (" + guardada.getPlaca() + ") en estado " + estadoInicial);

        return guardada;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Unidad actualizarUnidad(Integer idUnidad, UnidadDTO dto, String usernameEjecutor) {
        Unidad actual = obtenerPorId(idUnidad);
        String estadoAnterior = actual.getEstado();
        String estadoNuevo = dto.getEstado() != null ? dto.getEstado() : estadoAnterior;

        actual.setCodigoInterno(dto.getCodigoInterno().trim().toUpperCase());
        actual.setPlaca(dto.getPlaca().trim().toUpperCase());
        actual.setMarca(dto.getMarca().trim());
        actual.setModelo(dto.getModelo().trim());
        actual.setAnioFabricacion(dto.getAnioFabricacion());
        actual.setKilometrajeActual(dto.getKilometrajeActual());
        actual.setEstado(estadoNuevo);

        unidadDAO.actualizar(actual);

        // QA-05: Si hubo cambio de estado operativo, insertar automáticamente en historial_mantenimiento
        if (!estadoAnterior.equalsIgnoreCase(estadoNuevo)) {
            registrarEventoHistorial(idUnidad, null, usernameEjecutor,
                    "CAMBIO_ESTADO_MANUAL", estadoAnterior, estadoNuevo,
                    "Cambio de estado operativo de unidad " + actual.getCodigoInterno() +
                            " de " + estadoAnterior + " a " + estadoNuevo);
        }

        return actual;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cambiarEstadoConAuditoria(Integer idUnidad,
                                          String nuevoEstado,
                                          String motivo,
                                          Integer idMantenimiento,
                                          String usernameEjecutor) {
        Unidad actual = obtenerPorId(idUnidad);
        String estadoAnterior = actual.getEstado();
        unidadDAO.actualizarEstado(idUnidad, nuevoEstado);

        // QA-05: Trazabilidad obligatoria en historial_mantenimiento
        registrarEventoHistorial(idUnidad, idMantenimiento, usernameEjecutor,
                "CAMBIO_ESTADO", estadoAnterior, nuevoEstado, motivo);
    }

    /**
     * Método auxiliar interno para persistir un evento en {@code historial_mantenimiento}.
     */
    private void registrarEventoHistorial(Integer idUnidad,
                                          Integer idMantenimiento,
                                          String usernameEjecutor,
                                          String tipoEvento,
                                          String estadoAnterior,
                                          String estadoNuevo,
                                          String descripcion) {
        Integer idUsuario = usuarioDAO.buscarPorUsername(usernameEjecutor != null ? usernameEjecutor : "admin")
                .map(Usuario::getIdUsuario)
                .orElse(1);

        HistorialMantenimiento h = new HistorialMantenimiento();
        h.setIdUnidad(idUnidad);
        h.setIdMantenimiento(idMantenimiento);
        h.setIdUsuario(idUsuario);
        h.setTipoEvento(tipoEvento);
        h.setEstadoAnteriorUnidad(estadoAnterior);
        h.setEstadoNuevoUnidad(estadoNuevo);
        h.setDescripcionEvento(descripcion);
        historialDAO.insertar(h);
    }
}
