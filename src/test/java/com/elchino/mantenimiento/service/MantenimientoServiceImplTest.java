package com.elchino.mantenimiento.service;

import com.elchino.mantenimiento.dao.IFallaDAO;
import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.dao.IMantenimientoDAO;
import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.dto.MantenimientoDTO;
import com.elchino.mantenimiento.exception.HorarioConflictoException;
import com.elchino.mantenimiento.model.Unidad;
import com.elchino.mantenimiento.model.Usuario;
import com.elchino.mantenimiento.service.impl.MantenimientoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias con JUnit 5 y Mockito para validar las reglas de programación
 * de mantenimiento y no cruce de horarios de técnicos (CUS06 y QA-06).
 *
 * @author Ingeniero de QA y Seguridad (@QAAgent)
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class MantenimientoServiceImplTest {

    @Mock
    private IMantenimientoDAO mantenimientoDAO;
    @Mock
    private IUnidadDAO unidadDAO;
    @Mock
    private IUsuarioDAO usuarioDAO;
    @Mock
    private IFallaDAO fallaDAO;
    @Mock
    private IHistorialMantenimientoDAO historialDAO;

    @InjectMocks
    private MantenimientoServiceImpl mantenimientoService;

    /**
     * Verifica que no se permita programar un mantenimiento en fecha pasada (CUS06).
     */
    @Test
    @DisplayName("CUS06: Rechaza programación de mantenimiento con fecha pasada")
    void testRechazaFechaPasada() {
        MantenimientoDTO dto = new MantenimientoDTO();
        dto.setIdUnidad(1);
        dto.setIdTecnico(3);
        dto.setTipoMantenimiento("PREVENTIVO");
        dto.setFechaProgramada(LocalDateTime.now().minusDays(1));
        dto.setDescripcionTrabajo("Cambio de aceite");

        assertThrows(HorarioConflictoException.class,
                () -> mantenimientoService.programarMantenimiento(dto, "jefe.taller"));
    }

    /**
     * Verifica que si el técnico ya tiene otro mantenimiento PROGRAMADO o EN_PROCESO
     * en la misma fecha/hora, se lance {@link HorarioConflictoException} (CUS06).
     */
    @Test
    @DisplayName("CUS06: Rechaza programación cuando el técnico tiene cruce de horario")
    void testRechazaCruceHorarioTecnico() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(2);

        Unidad unidad = new Unidad();
        unidad.setIdUnidad(1);
        unidad.setCodigoInterno("CH-101");

        Usuario tecnico = new Usuario();
        tecnico.setIdUsuario(3);
        tecnico.setNombres("Miguel");
        tecnico.setApellidos("Paredes");

        when(unidadDAO.buscarPorId(1)).thenReturn(Optional.of(unidad));
        when(usuarioDAO.buscarPorId(3)).thenReturn(Optional.of(tecnico));
        when(mantenimientoDAO.existeCruceHorarioTecnico(3, fechaFutura)).thenReturn(true);

        MantenimientoDTO dto = new MantenimientoDTO();
        dto.setIdUnidad(1);
        dto.setIdTecnico(3);
        dto.setTipoMantenimiento("CORRECTIVO");
        dto.setFechaProgramada(fechaFutura);
        dto.setDescripcionTrabajo("Revisión de caja");

        assertThrows(HorarioConflictoException.class,
                () -> mantenimientoService.programarMantenimiento(dto, "jefe.taller"));

        verify(mantenimientoDAO, never()).insertar(any());
    }
}
