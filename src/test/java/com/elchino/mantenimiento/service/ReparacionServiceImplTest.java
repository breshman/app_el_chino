package com.elchino.mantenimiento.service;

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
import com.elchino.mantenimiento.exception.StockInsuficienteException;
import com.elchino.mantenimiento.model.DetalleRepuesto;
import com.elchino.mantenimiento.model.Falla;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
import com.elchino.mantenimiento.model.Mantenimiento;
import com.elchino.mantenimiento.model.Reparacion;
import com.elchino.mantenimiento.model.Repuesto;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.impl.ReparacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias con JUnit 5 y Mockito para validar la integridad transaccional
 * de la reparación, descuento de stock y trazabilidad en historial (QA-04, QA-05, QA-06).
 *
 * @author Ingeniero de QA y Seguridad (@QAAgent)
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ReparacionServiceImplTest {

    @Mock
    private IReparacionDAO reparacionDAO;
    @Mock
    private IMantenimientoDAO mantenimientoDAO;
    @Mock
    private IRepuestoDAO repuestoDAO;
    @Mock
    private IDetalleRepuestoDAO detalleRepuestoDAO;
    @Mock
    private IFallaDAO fallaDAO;
    @Mock
    private IUnidadDAO unidadDAO;
    @Mock
    private IUsuarioDAO usuarioDAO;
    @Mock
    private IHistorialMantenimientoDAO historialDAO;

    @InjectMocks
    private ReparacionServiceImpl reparacionService;

    private Mantenimiento mantenimiento;
    private Unidad unidad;
    private Usuario tecnico;

    /**
     * Inicializa las entidades simuladas antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        mantenimiento = new Mantenimiento();
        mantenimiento.setIdMantenimiento(10);
        mantenimiento.setIdUnidad(2);
        mantenimiento.setIdFalla(5);
        mantenimiento.setEstado("EN_PROCESO");

        unidad = new Unidad();
        unidad.setIdUnidad(2);
        unidad.setCodigoInterno("CH-102");
        unidad.setEstado("EN_MANTENIMIENTO");

        tecnico = new Usuario();
        tecnico.setIdUsuario(3);
        tecnico.setUsername("tecnico1");
    }

    /**
     * Verifica QA-04: Si se registra una reparación con 2 repuestos y el segundo falla
     * por falta de stock, se lanza {@link StockInsuficienteException} para revertir toda la transacción
     * y no finalizar el mantenimiento ni alterar el estado de la unidad.
     */
    @Test
    @DisplayName("QA-04: Falla el segundo repuesto por falta de stock y lanza StockInsuficienteException")
    void testRollbackCuandoSegundoRepuestoNoTieneStock() {
        when(mantenimientoDAO.buscarPorId(10)).thenReturn(Optional.of(mantenimiento));
        when(unidadDAO.buscarPorId(2)).thenReturn(Optional.of(unidad));
        when(usuarioDAO.buscarPorUsername("tecnico1")).thenReturn(Optional.of(tecnico));

        Reparacion guardada = new Reparacion();
        guardada.setIdReparacion(100);
        guardada.setResultado("SATISFACTORIO");
        when(reparacionDAO.insertar(any(Reparacion.class))).thenReturn(guardada);

        Repuesto rep1 = new Repuesto();
        rep1.setIdRepuesto(1);
        rep1.setCodigoSku("REP-01");
        rep1.setNombre("Pastillas de Freno");
        rep1.setStock(10);
        rep1.setStockMinimo(3);
        rep1.setPrecioUnitario(new BigDecimal("300.00"));

        Repuesto rep2 = new Repuesto();
        rep2.setIdRepuesto(2);
        rep2.setCodigoSku("REP-02");
        rep2.setNombre("Amortiguador Neumático");
        rep2.setStock(1); // Stock insuficiente para cantidad solicitada = 4
        rep2.setStockMinimo(2);
        rep2.setPrecioUnitario(new BigDecimal("650.00"));

        when(repuestoDAO.buscarPorId(1)).thenReturn(Optional.of(rep1));
        when(repuestoDAO.descontarStock(1, 2)).thenReturn(1);
        when(detalleRepuestoDAO.insertar(any(DetalleRepuesto.class))).thenAnswer(i -> i.getArgument(0));
        when(repuestoDAO.buscarPorId(2)).thenReturn(Optional.of(rep2));

        ReparacionDTO dto = new ReparacionDTO();
        dto.setIdMantenimiento(10);
        dto.setDiagnosticoTecnico("Falla en frenos y suspensión");
        dto.setAccionesRealizadas("Cambio de componentes");
        dto.setResultado("SATISFACTORIO");
        dto.setRepuestos(Arrays.asList(
                new ItemRepuestoDTO(1, 2),
                new ItemRepuestoDTO(2, 4)
        ));

        assertThrows(StockInsuficienteException.class,
                () -> reparacionService.registrarReparacionTransaccional(dto, "tecnico1"));

        // Verifica que nunca se finalizó el mantenimiento ni se cambió la unidad a OPERATIVA
        verify(unidadDAO, never()).actualizarEstado(eq(2), eq("OPERATIVA"));
        verify(historialDAO, never()).insertar(any(HistorialMantenimiento.class));
    }

    /**
     * Verifica CUS07 + CUS08 + CUS09 + QA-05: Cuando la reparación es SATISFACTORIO y hay stock,
     * se descuenta stock, finaliza mantenimiento, resuelve falla, pasa unidad a OPERATIVA
     * y registra en historial_mantenimiento.
     */
    @Test
    @DisplayName("CUS07/CUS09/QA-05: Reparación SATISFACTORIO actualiza estados y registra en historial")
    void testReparacionSatisfactoriaActualizaUnidadFallaYHistorial() {
        when(mantenimientoDAO.buscarPorId(10)).thenReturn(Optional.of(mantenimiento));
        when(unidadDAO.buscarPorId(2)).thenReturn(Optional.of(unidad));
        when(usuarioDAO.buscarPorUsername("tecnico1")).thenReturn(Optional.of(tecnico));

        Falla falla = new Falla();
        falla.setIdFalla(5);
        falla.setPrioridad("ALTA");
        when(fallaDAO.buscarPorId(5)).thenReturn(Optional.of(falla));

        Repuesto rep1 = new Repuesto();
        rep1.setIdRepuesto(1);
        rep1.setCodigoSku("REP-01");
        rep1.setNombre("Filtro Aceite");
        rep1.setStock(8);
        rep1.setStockMinimo(5);
        rep1.setPrecioUnitario(new BigDecimal("85.00"));

        when(repuestoDAO.buscarPorId(1)).thenReturn(Optional.of(rep1));
        when(repuestoDAO.descontarStock(1, 2)).thenReturn(1);
        when(detalleRepuestoDAO.insertar(any(DetalleRepuesto.class))).thenAnswer(i -> i.getArgument(0));

        when(reparacionDAO.insertar(any(Reparacion.class))).thenAnswer(inv -> {
            Reparacion r = inv.getArgument(0);
            r.setIdReparacion(55);
            return r;
        });

        ReparacionDTO dto = new ReparacionDTO();
        dto.setIdMantenimiento(10);
        dto.setDiagnosticoTecnico("Filtro obstruido");
        dto.setAccionesRealizadas("Reemplazo de filtro");
        dto.setResultado("SATISFACTORIO");
        dto.setRepuestos(Collections.singletonList(new ItemRepuestoDTO(1, 2)));

        Reparacion resultado = reparacionService.registrarReparacionTransaccional(dto, "tecnico1");

        assertNotNull(resultado);
        assertEquals(1, resultado.getDetallesRepuestos().size());
        verify(mantenimientoDAO, times(1)).actualizarEstadoYFechas(eq(10), eq("FINALIZADO"), any(), any());
        verify(fallaDAO, times(1)).actualizarPrioridadYEstado(5, "ALTA", "RESUELTA");
        verify(unidadDAO, times(1)).actualizarEstado(2, "OPERATIVA");
        verify(historialDAO, times(1)).insertar(any(HistorialMantenimiento.class));
    }
}
