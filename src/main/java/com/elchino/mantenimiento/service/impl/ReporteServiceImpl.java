package com.elchino.mantenimiento.service.impl;

import com.elchino.mantenimiento.dao.IFallaDAO;
import com.elchino.mantenimiento.dao.IMantenimientoDAO;
import com.elchino.mantenimiento.dao.IReparacionDAO;
import com.elchino.mantenimiento.dao.IRepuestoDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dto.ReporteResumenDTO;
import com.elchino.mantenimiento.model.Falla;
import com.elchino.mantenimiento.model.Mantenimiento;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.service.IReporteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de la capa de servicio para la generación de reportes gerenciales
 * e indicadores operativos de la flota "El Chino" (Implementa CUS11 y CUS12).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Service
public class ReporteServiceImpl implements IReporteService {

    private final IUnidadDAO unidadDAO;
    private final IFallaDAO fallaDAO;
    private final IMantenimientoDAO mantenimientoDAO;
    private final IRepuestoDAO repuestoDAO;
    private final IReparacionDAO reparacionDAO;

    /**
     * Constructor con inyección de los DAOs de cada módulo operativo.
     *
     * @param unidadDAO        DAO de unidades.
     * @param fallaDAO         DAO de fallas.
     * @param mantenimientoDAO DAO de mantenimientos.
     * @param repuestoDAO      DAO de repuestos.
     * @param reparacionDAO    DAO de reparaciones.
     */
    public ReporteServiceImpl(IUnidadDAO unidadDAO,
                              IFallaDAO fallaDAO,
                              IMantenimientoDAO mantenimientoDAO,
                              IRepuestoDAO repuestoDAO,
                              IReparacionDAO reparacionDAO) {
        this.unidadDAO = unidadDAO;
        this.fallaDAO = fallaDAO;
        this.mantenimientoDAO = mantenimientoDAO;
        this.repuestoDAO = repuestoDAO;
        this.reparacionDAO = reparacionDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReporteResumenDTO obtenerResumenEjecutivo() {
        ReporteResumenDTO resumen = new ReporteResumenDTO();

        List<Unidad> unidades = unidadDAO.listarTodas();
        resumen.setTotalUnidades(unidades.size());
        resumen.setUnidadesOperativas((int) unidades.stream()
                .filter(u -> "OPERATIVA".equalsIgnoreCase(u.getEstado())).count());
        resumen.setUnidadesEnMantenimiento((int) unidades.stream()
                .filter(u -> "EN_MANTENIMIENTO".equalsIgnoreCase(u.getEstado())).count());
        resumen.setUnidadesFueraDeServicio((int) unidades.stream()
                .filter(u -> "FUERA_DE_SERVICIO".equalsIgnoreCase(u.getEstado())).count());

        List<Falla> fallasActivas = fallaDAO.listarActivas();
        resumen.setFallasActivas(fallasActivas.size());
        resumen.setFallasCriticasOAltas((int) fallasActivas.stream()
                .filter(f -> "CRITICA".equalsIgnoreCase(f.getPrioridad()) || "ALTA".equalsIgnoreCase(f.getPrioridad()))
                .count());

        List<Mantenimiento> todosMant = mantenimientoDAO.listarTodos();
        resumen.setMantenimientosPendientes((int) todosMant.stream()
                .filter(m -> "PROGRAMADO".equalsIgnoreCase(m.getEstado()) || "EN_PROCESO".equalsIgnoreCase(m.getEstado()))
                .count());
        resumen.setMantenimientosFinalizados((int) todosMant.stream()
                .filter(m -> "FINALIZADO".equalsIgnoreCase(m.getEstado()))
                .count());

        resumen.setRepuestosBajoStock(repuestoDAO.listarBajoStock().size());
        resumen.setCostoTotalMantenimiento(reparacionDAO.calcularCostoTotalMantenimiento());

        return resumen;
    }
}
