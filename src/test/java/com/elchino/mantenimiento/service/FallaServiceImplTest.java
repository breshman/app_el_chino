package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dao.IFallaDAO;
import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.FallaDTO;
import com.elchino.mantenimiento.model.Falla;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.impl.FallaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para la lógica de reporte y priorización de fallas (CUS05 y QA-05).
 */
@ExtendWith(MockitoExtension.class)
class FallaServiceImplTest {

    @Mock
    private IFallaDAO fallaDAO;
    @Mock
    private IUnidadDAO unidadDAO;
    @Mock
    private IUsuarioDAO usuarioDAO;
    @Mock
    private IHistorialMantenimientoDAO historialDAO;

    @InjectMocks
    private FallaServiceImpl fallaService;

    @Test
    @DisplayName("CUS05/QA-05: Falla CRITICA de Responsable cambia unidad a EN_MANTENIMIENTO y registra historial")
    void testFallaCriticaDeResponsableCambiaEstadoUnidadYAudita() {
        Unidad unidad = new Unidad();
        unidad.setIdUnidad(1);
        unidad.setCodigoInterno("CH-101");
        unidad.setEstado("OPERATIVA");

        Usuario jefeTaller = new Usuario();
        jefeTaller.setIdUsuario(2);
        jefeTaller.setUsername("jefe.taller");
        jefeTaller.setRol("RESPONSABLE_MANTENIMIENTO");

        when(unidadDAO.buscarPorId(1)).thenReturn(Optional.of(unidad));
        when(usuarioDAO.buscarPorUsername("jefe.taller")).thenReturn(Optional.of(jefeTaller));
        when(fallaDAO.insertar(any(Falla.class))).thenAnswer(i -> {
            Falla f = i.getArgument(0);
            f.setIdFalla(99);
            return f;
        });

        FallaDTO dto = new FallaDTO();
        dto.setIdUnidad(1);
        dto.setTitulo("Rotura de manguera de frenos");
        dto.setDescripcion("Pérdida total de aire");
        dto.setSistemaAfectado("Frenos");
        dto.setPrioridad("CRITICA");

        Falla creada = fallaService.registrarFalla(dto, "jefe.taller");

        assertEquals("REPORTADA", creada.getEstado());
        assertEquals("CRITICA", creada.getPrioridad());
        verify(unidadDAO, times(1)).actualizarEstado(1, "EN_MANTENIMIENTO");
        verify(historialDAO, times(1)).insertar(any(HistorialMantenimiento.class));
    }

    @Test
    @DisplayName("CUS05: Falla reportada por CONDUCTOR limita prioridad inicial a MEDIA sin sacar unidad de ruta")
    void testFallaDeConductorLimitaPrioridadAMedia() {
        Unidad unidad = new Unidad();
        unidad.setIdUnidad(2);
        unidad.setCodigoInterno("CH-102");
        unidad.setEstado("OPERATIVA");

        Usuario conductor = new Usuario();
        conductor.setIdUsuario(6);
        conductor.setUsername("conductor1");
        conductor.setRol("CONDUCTOR");

        when(unidadDAO.buscarPorId(2)).thenReturn(Optional.of(unidad));
        when(usuarioDAO.buscarPorUsername("conductor1")).thenReturn(Optional.of(conductor));
        when(fallaDAO.insertar(any(Falla.class))).thenAnswer(i -> {
            Falla f = i.getArgument(0);
            f.setIdFalla(100);
            return f;
        });

        FallaDTO dto = new FallaDTO();
        dto.setIdUnidad(2);
        dto.setTitulo("Ruido en suspensión delantera");
        dto.setDescripcion("Golpeteo en baches");
        dto.setSistemaAfectado("Suspensión");
        dto.setPrioridad("CRITICA");

        Falla creada = fallaService.registrarFalla(dto, "conductor1");

        assertEquals("REPORTADA", creada.getEstado());
        assertEquals("MEDIA", creada.getPrioridad());
        verify(unidadDAO, never()).actualizarEstado(any(), any());
        verify(historialDAO, never()).insertar(any());
    }
}