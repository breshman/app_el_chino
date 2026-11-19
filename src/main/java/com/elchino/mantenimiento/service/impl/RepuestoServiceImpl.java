package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IRepuestoDAO;
import com.elchino.mantenimiento.dto.RepuestoDTO;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Repuesto;
import com.elchino.mantenimiento.service.IRepuestoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicio para la administración del catálogo
 * e inventario de repuestos (Implementa CUS08).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Service
public class RepuestoServiceImpl implements IRepuestoService {

    private final IRepuestoDAO repuestoDAO;

    /**
     * Constructor con inyección de {@link IRepuestoDAO}.
     *
     * @param repuestoDAO DAO de repuestos.
     */
    public RepuestoServiceImpl(IRepuestoDAO repuestoDAO) {
        this.repuestoDAO = repuestoDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Repuesto> listarTodos() {
        return repuestoDAO.listarTodos();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Repuesto> listarAlertasBajoStock() {
        return repuestoDAO.listarBajoStock();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Repuesto obtenerPorId(Integer idRepuesto) {
        return repuestoDAO.buscarPorId(idRepuesto)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el repuesto con ID: " + idRepuesto));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Repuesto registrarRepuesto(RepuestoDTO dto) {
        Repuesto r = new Repuesto();
        r.setCodigoSku(dto.getCodigoSku().trim().toUpperCase());
        r.setNombre(dto.getNombre().trim());
        r.setCategoria(dto.getCategoria().trim());
        r.setStock(dto.getStock());
        r.setStockMinimo(dto.getStockMinimo());
        r.setPrecioUnitario(dto.getPrecioUnitario());
        r.setUnidadMedida(dto.getUnidadMedida().trim().toUpperCase());
        return repuestoDAO.insertar(r);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Repuesto actualizarRepuesto(Integer idRepuesto, RepuestoDTO dto) {
        Repuesto actual = obtenerPorId(idRepuesto);
        actual.setCodigoSku(dto.getCodigoSku().trim().toUpperCase());
        actual.setNombre(dto.getNombre().trim());
        actual.setCategoria(dto.getCategoria().trim());
        actual.setStock(dto.getStock());
        actual.setStockMinimo(dto.getStockMinimo());
        actual.setPrecioUnitario(dto.getPrecioUnitario());
        actual.setUnidadMedida(dto.getUnidadMedida().trim().toUpperCase());
        repuestoDAO.actualizar(actual);
        return actual;
    }
}
