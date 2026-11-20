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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación de la capa de servicio para la generación de reportes gerenciales
 * e indicadores operativos de la flota "El Chino" (Implementa CUS11 y CUS12).
 */
@Service
public class ReporteServiceImpl implements IReporteService {

    private final IUnidadDAO unidadDAO;
    private final IFallaDAO fallaDAO;
    private final IMantenimientoDAO mantenimientoDAO;
    private final IRepuestoDAO repuestoDAO;
    private final IReparacionDAO reparacionDAO;

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

    @Override
    @Transactional(readOnly = true)
    public ReporteResumenDTO obtenerResumenEjecutivo() {
        ReporteResumenDTO resumen = new ReporteResumenDTO();

        // 1. Conteo de flota en una sola pasada
        List<Unidad> unidades = unidadDAO.listarTodas();
        int operativas = 0;
        int enMantenimiento = 0;
        int fueraServicio = 0;
        for (Unidad u : unidades) {
            if ("OPERATIVA".equalsIgnoreCase(u.getEstado())) {
                operativas++;
            } else if ("EN_MANTENIMIENTO".equalsIgnoreCase(u.getEstado())) {
                enMantenimiento++;
            } else if ("FUERA_DE_SERVICIO".equalsIgnoreCase(u.getEstado())) {
                fueraServicio++;
            }
        }
        resumen.setTotalUnidades(unidades.size());
        resumen.setUnidadesOperativas(operativas);
        resumen.setUnidadesEnMantenimiento(enMantenimiento);
        resumen.setUnidadesFueraDeServicio(fueraServicio);

        // 2. Conteo de fallas activas en una sola pasada
        List<Falla> fallasActivas = fallaDAO.listarActivas();
        int criticasOAltas = 0;
        for (Falla f : fallasActivas) {
            if ("CRITICA".equalsIgnoreCase(f.getPrioridad()) || "ALTA".equalsIgnoreCase(f.getPrioridad())) {
                criticasOAltas++;
            }
        }
        resumen.setFallasActivas(fallasActivas.size());
        resumen.setFallasCriticasOAltas(criticasOAltas);

        // 3. Conteo de órdenes de mantenimiento en una sola pasada
        List<Mantenimiento> todosMant = mantenimientoDAO.listarTodos();
        int pendientes = 0;
        int finalizados = 0;
        for (Mantenimiento m : todosMant) {
            if ("PROGRAMADO".equalsIgnoreCase(m.getEstado()) || "EN_PROCESO".equalsIgnoreCase(m.getEstado())) {
                pendientes++;
            } else if ("FINALIZADO".equalsIgnoreCase(m.getEstado())) {
                finalizados++;
            }
        }
        resumen.setMantenimientosPendientes(pendientes);
        resumen.setMantenimientosFinalizados(finalizados);

        // 4. Alertas de almacén y costo acumulado
        resumen.setRepuestosBajoStock(repuestoDAO.listarBajoStock().size());
        BigDecimal costoTotal = reparacionDAO.calcularCostoTotalMantenimiento();
        resumen.setCostoTotalMantenimiento(costoTotal != null ? costoTotal : BigDecimal.ZERO);

        return resumen;
    }
}