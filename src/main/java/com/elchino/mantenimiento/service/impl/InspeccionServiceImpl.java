package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IInspeccionDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.InspeccionDTO;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Inspeccion;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IInspeccionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicio para inspecciones técnicas de unidades.
 * Implementa CUS04 y la regla de encadenamiento automático CUS04 -> CUS05.
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Service
public class InspeccionServiceImpl implements IInspeccionService {

    private final IInspeccionDAO inspeccionDAO;
    private final IUnidadDAO unidadDAO;
    private final IUsuarioDAO usuarioDAO;

    /**
     * Constructor con inyección de DAOs.
     *
     * @param inspeccionDAO DAO de inspecciones.
     * @param unidadDAO     DAO de unidades.
     * @param usuarioDAO    DAO de usuarios.
     */
    public InspeccionServiceImpl(IInspeccionDAO inspeccionDAO,
                                 IUnidadDAO unidadDAO,
                                 IUsuarioDAO usuarioDAO) {
        this.inspeccionDAO = inspeccionDAO;
        this.unidadDAO = unidadDAO;
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Inspeccion> listarTodas() {
        return inspeccionDAO.listarTodas();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Inspeccion> listarPorUnidad(Integer idUnidad) {
        return inspeccionDAO.listarPorUnidad(idUnidad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Inspeccion obtenerPorId(Integer idInspeccion) {
        return inspeccionDAO.buscarPorId(idInspeccion)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la inspección con ID: " + idInspeccion));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Inspeccion registrarInspeccion(InspeccionDTO dto, String usernameInspector) {
        Unidad unidad = unidadDAO.buscarPorId(dto.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la unidad con ID: " + dto.getIdUnidad()));

        Usuario inspector = usuarioDAO.buscarPorUsername(usernameInspector)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario inspector no encontrado: " + usernameInspector));

        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setIdUnidad(unidad.getIdUnidad());
        inspeccion.setIdInspector(inspector.getIdUsuario());
        inspeccion.setTipoInspeccion(dto.getTipoInspeccion());
        inspeccion.setKilometrajeRegistrado(dto.getKilometrajeRegistrado());
        inspeccion.setObservaciones(dto.getObservaciones());
        inspeccion.setResultado(dto.getResultado());

        Inspeccion guardada = inspeccionDAO.insertar(inspeccion);

        // Actualiza el odómetro de la unidad si la inspección reporta un kilometraje superior
        if (dto.getKilometrajeRegistrado() > unidad.getKilometrajeActual()) {
            unidadDAO.actualizarKilometraje(unidad.getIdUnidad(), dto.getKilometrajeRegistrado());
        }

        return guardada;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiereRegistroFalla(String resultado) {
        // Regla CUS04 -> CUS05: Si el resultado es NO_CONFORME o CON_OBSERVACIONES, encadenar al registro de falla
        return "NO_CONFORME".equalsIgnoreCase(resultado) || "CON_OBSERVACIONES".equalsIgnoreCase(resultado);
    }
}
