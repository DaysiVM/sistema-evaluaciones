package com.universidad.tests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.universidad.tests.application.service.ConfiguracionVersionadaService;
import com.universidad.tests.application.service.EdicionContenidoVersionadoService;
import com.universidad.tests.domain.model.*;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ConfiguracionVersionadaServiceTest {
    @Test
    void actualizaLaConfiguracionSinCambiarSuIdentidad() {
        VersionFormularioRepository versiones = mock(VersionFormularioRepository.class);
        SubpruebaRepository subpruebas = mock(SubpruebaRepository.class);
        ConfiguracionSubpruebaRepository configuraciones =
                mock(ConfiguracionSubpruebaRepository.class);
        EdicionContenidoVersionadoService edicion = mock(EdicionContenidoVersionadoService.class);
        VersionFormulario version = new VersionFormulario(11L, 1L, "v2", null, "DOC");
        Subprueba subprueba = mock(Subprueba.class);
        when(subprueba.getIdSubprueba()).thenReturn(3L);
        ConfiguracionSubprueba anterior =
                new ConfiguracionSubprueba(
                        20L, 11L, 3L, 1, 300, TipoRespuesta.SELECCION, "Anterior", null, null, 2);
        when(versiones.buscarPorId(11L)).thenReturn(Optional.of(version));
        when(subpruebas.buscarPorCodigo(1L, "R1")).thenReturn(Optional.of(subprueba));
        when(configuraciones.buscarPorVersionYSubprueba(11L, 3L)).thenReturn(Optional.of(anterior));
        when(configuraciones.listarPorVersion(11L)).thenReturn(List.of(anterior));
        when(edicion.guardarConfiguracionSubprueba(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ConfiguracionVersionadaService servicio =
                new ConfiguracionVersionadaService(
                        versiones,
                        subpruebas,
                        configuraciones,
                        mock(AutorizacionContenidoPort.class),
                        edicion);

        ConfiguracionSubprueba guardada =
                servicio.guardar(
                        11L, "R1", 1, 300, "SELECCION", 2, null, "Nueva instruccion", null);

        assertEquals(20L, guardada.getIdConfiguracionSubprueba());
        assertEquals(2, guardada.getOpcionesEsperadas());
        assertEquals("Nueva instruccion", guardada.getInstruccionesRef());
    }
}
