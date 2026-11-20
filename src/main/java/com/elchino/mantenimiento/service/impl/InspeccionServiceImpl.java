package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IInspeccionDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.InspeccionDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Inspeccion;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.IInspeccionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Implementación de la capa de servicio para inspecciones técnicas de unidades.
 * Implementa CUS04 y la regla de encadenamiento automático CUS04 -> CUS05.
 */
@Service
public class InspeccionServiceImpl implements IInspeccionService {

    private static final Set<String> TIPOS_VALIDOS = Set.of("PREVENTIVA", "RUTINARIA", "EXTRAORDINARIA");
    private static final Set<String> RESULTADOS_VALIDOS = Set.of("CONFORME", "CON_OBSERVACIONES", "NO_CONFORME");

    private final IInspeccionDAO inspeccionDAO;
    private final IUnidadDAO unidadDAO;
    private final IUsuarioDAO usuarioDAO;

    public InspeccionServiceImpl(IInspeccionDAO inspeccionDAO,
                                 IUnidadDAO unidadDAO,
                                 IUsuarioDAO usuarioDAO) {
        this.inspeccionDAO = inspeccionDAO;
        this.unidadDAO = unidadDAO;
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public List<Inspeccion> listarTodas() {
        return inspeccionDAO.listarTodas();
    }

    @Override
    public List<Inspeccion> listarPorUnidad(Integer idUnidad) {
        return inspeccionDAO.listarPorUnidad(idUnidad);
    }

    @Override
    public Inspeccion obtenerPorId(Integer idInspeccion) {
        return inspeccionDAO.buscarPorId(idInspeccion)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la inspección con ID: " + idInspeccion));
    }

    @Override
    @Transactional
    public Inspeccion registrarInspeccion(InspeccionDTO dto, String usernameInspector) {
        Unidad unidad = unidadDAO.buscarPorId(dto.getIdUnidad())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la unidad con ID: " + dto.getIdUnidad()));

        Usuario inspector = usuarioDAO.buscarPorUsername(usernameInspector)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario inspector no encontrado: " + usernameInspector));

        // Regla de integridad de odómetro: el kilometraje no puede retroceder
        Integer kmActual = unidad.getKilometrajeActual() != null ? unidad.getKilometrajeActual() : 0;
        if (dto.getKilometrajeRegistrado() == null || dto.getKilometrajeRegistrado() < kmActual) {
            throw new BusinessException("El kilometraje registrado (" + dto.getKilometrajeRegistrado() +
                    " km) no puede ser menor al odómetro actual de la unidad " +
                    unidad.getCodigoInterno() + " (" + kmActual + " km).");
        }

        String tipoNormalizado = dto.getTipoInspeccion() != null
                ? dto.getTipoInspeccion().trim().toUpperCase()
                : "RUTINARIA";
        if (!TIPOS_VALIDOS.contains(tipoNormalizado)) {
            throw new BusinessException("Tipo de inspección no válido: " + tipoNormalizado);
        }

        String resultadoNormalizado = dto.getResultado() != null
                ? dto.getResultado().trim().toUpperCase()
                : "";
        if (!RESULTADOS_VALIDOS.contains(resultadoNormalizado)) {
            throw new BusinessException("Resultado de inspección no válido: " + resultadoNormalizado);
        }

        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setIdUnidad(unidad.getIdUnidad());
        inspeccion.setIdInspector(inspector.getIdUsuario());
        inspeccion.setTipoInspeccion(tipoNormalizado);
        inspeccion.setKilometrajeRegistrado(dto.getKilometrajeRegistrado());
        inspeccion.setObservaciones(dto.getObservaciones() != null ? dto.getObservaciones().trim() : null);
        inspeccion.setResultado(resultadoNormalizado);

        Inspeccion guardada = inspeccionDAO.insertar(inspeccion);

        // Actualiza el odómetro de la unidad si la inspección reporta un kilometraje superior
        if (dto.getKilometrajeRegistrado() > kmActual) {
            unidadDAO.actualizarKilometraje(unidad.getIdUnidad(), dto.getKilometrajeRegistrado());
        }

        return guardada;
    }

    @Override
    public boolean requiereRegistroFalla(String resultado) {
        return "NO_CONFORME".equalsIgnoreCase(resultado) || "CON_OBSERVACIONES".equalsIgnoreCase(resultado);
    }
}