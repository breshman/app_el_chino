package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
import com.elchino.mantenimiento.service.IHistorialService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de la capa de servicio para la consulta del historial
 * inmutable de mantenimiento y auditoría de unidades (Implementa CUS10 y QA-05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Service
public class HistorialServiceImpl implements IHistorialService {

    private final IHistorialMantenimientoDAO historialDAO;

    /**
     * Constructor con inyección de {@link IHistorialMantenimientoDAO}.
     *
     * @param historialDAO DAO de historial de mantenimiento.
     */
    public HistorialServiceImpl(IHistorialMantenimientoDAO historialDAO) {
        this.historialDAO = historialDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HistorialMantenimiento> listarTodo() {
        return historialDAO.listarTodo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HistorialMantenimiento> listarPorUnidad(Integer idUnidad) {
        return historialDAO.listarPorUnidad(idUnidad);
    }
}
