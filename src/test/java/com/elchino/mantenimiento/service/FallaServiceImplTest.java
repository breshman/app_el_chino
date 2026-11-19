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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para la lógica de reporte y priorización de fallas (CUS05 y QA-05).
 *
 * @author Ingeniero de QA y Seguridad (@QAAgent)
 * @version 1.0.0
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

    /**
     * Verifica que una falla reportada con prioridad CRITICA o ALTA nace con estado REPORTADA,
     * pasa la unidad a EN_MANTENIMIENTO y genera trazabilidad en historial_mantenimiento (CUS05 / QA-05).
     */
    @Test
    @DisplayName("CUS05/QA-05: Falla CRITICA cambia unidad a EN_MANTENIMIENTO y registra historial")
    void testFallaCriticaCambiaEstadoUnidadYAudita() {
        Unidad unidad = new Unidad();
        unidad.setIdUnidad(1);
        unidad.setCodigoInterno("CH-101");
        unidad.setEstado("OPERATIVA");

        Usuario conductor = new Usuario();
        conductor.setIdUsuario(6);
        conductor.setUsername("conductor1");
        conductor.setRol("CONDUCTOR");

        when(unidadDAO.buscarPorId(1)).thenReturn(Optional.of(unidad));
        when(usuarioDAO.buscarPorUsername("conductor1")).thenReturn(Optional.of(conductor));
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

        Falla creada = fallaService.registrarFalla(dto, "conductor1");

        assertEquals("REPORTADA", creada.getEstado());
        verify(unidadDAO, times(1)).actualizarEstado(1, "EN_MANTENIMIENTO");
        verify(historialDAO, times(1)).insertar(any(HistorialMantenimiento.class));
    }
}
