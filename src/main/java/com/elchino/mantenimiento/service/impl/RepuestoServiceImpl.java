package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IRepuestoDAO;
import com.elchino.mantenimiento.dto.RepuestoDTO;
import com.elchino.mantenimiento.exception.BusinessException;
import com.elchino.mantenimiento.exception.ResourceNotFoundException;
import com.elchino.mantenimiento.model.Repuesto;
import com.elchino.mantenimiento.service.IRepuestoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicio para la administración del catálogo
 * e inventario de repuestos (Implementa CUS08).
 */
@Service
public class RepuestoServiceImpl implements IRepuestoService {

    private final IRepuestoDAO repuestoDAO;

    public RepuestoServiceImpl(IRepuestoDAO repuestoDAO) {
        this.repuestoDAO = repuestoDAO;
    }

    @Override
    public List<Repuesto> listarTodos() {
        return repuestoDAO.listarTodos();
    }

    @Override
    public List<Repuesto> listarAlertasBajoStock() {
        return repuestoDAO.listarBajoStock();
    }

    @Override
    public Repuesto obtenerPorId(Integer idRepuesto) {
        return repuestoDAO.buscarPorId(idRepuesto)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el repuesto con ID: " + idRepuesto));
    }

    @Override
    @Transactional
    public Repuesto registrarRepuesto(RepuestoDTO dto) {
        String skuNormalizado = dto.getCodigoSku().trim().toUpperCase();

        boolean skuExiste = repuestoDAO.listarTodos().stream()
                .anyMatch(r -> r.getCodigoSku() != null && r.getCodigoSku().trim().equalsIgnoreCase(skuNormalizado));
        if (skuExiste) {
            throw new BusinessException("Ya existe un repuesto registrado con el código SKU '" + skuNormalizado + "'.");
        }

        Repuesto r = new Repuesto();
        r.setCodigoSku(skuNormalizado);
        r.setNombre(dto.getNombre().trim());
        r.setCategoria(dto.getCategoria().trim());
        r.setStock(dto.getStock());
        r.setStockMinimo(dto.getStockMinimo());
        r.setPrecioUnitario(dto.getPrecioUnitario());
        r.setUnidadMedida(dto.getUnidadMedida().trim().toUpperCase());
        return repuestoDAO.insertar(r);
    }

    @Override
    @Transactional
    public Repuesto actualizarRepuesto(Integer idRepuesto, RepuestoDTO dto) {
        Repuesto actual = obtenerPorId(idRepuesto);
        String skuNormalizado = dto.getCodigoSku().trim().toUpperCase();

        boolean skuDuplicado = repuestoDAO.listarTodos().stream()
                .anyMatch(r -> !r.getIdRepuesto().equals(idRepuesto)
                        && r.getCodigoSku() != null
                        && r.getCodigoSku().trim().equalsIgnoreCase(skuNormalizado));
        if (skuDuplicado) {
            throw new BusinessException("Otro repuesto ya utiliza el código SKU '" + skuNormalizado + "'.");
        }

        actual.setCodigoSku(skuNormalizado);
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