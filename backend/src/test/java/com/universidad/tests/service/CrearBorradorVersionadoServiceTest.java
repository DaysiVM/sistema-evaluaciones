package com.universidad.tests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.universidad.tests.application.service.EdicionContenidoVersionadoService;
import com.universidad.tests.application.service.VersionFormularioService;
import com.universidad.tests.domain.model.*;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CrearBorradorVersionadoServiceTest {
    @Test
    void copiaLaUltimaPublicadaSinModificarElOrigen() {
        VersionFormularioRepository versiones = mock(VersionFormularioRepository.class);
        TestRepository tests = mock(TestRepository.class);
        ConfiguracionSubpruebaRepository configuraciones =
                mock(ConfiguracionSubpruebaRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);
        OpcionRespuestaRepository opciones = mock(OpcionRespuestaRepository.class);
        EdicionContenidoVersionadoService edicion = mock(EdicionContenidoVersionadoService.class);
        VersionFormulario origen = new VersionFormulario(10L, 1L, "v1", null, "DOC-1");
        origen.publicar("ACTA-1");
        VersionFormulario nuevo = new VersionFormulario(11L, 1L, "v2", null, "DOC-2");
        ConfiguracionSubprueba configuracion =
                new ConfiguracionSubprueba(
                        20L, 10L, 3L, 1, 300, TipoRespuesta.SELECCION, "Instrucciones", null, null);
        Pregunta pregunta = new Pregunta(30L, 20L, "1", 1, "Serie", null);
        when(tests.buscarPorId(1L))
                .thenReturn(Optional.of(mock(com.universidad.tests.domain.model.Test.class)));
        when(versiones.guardar(any())).thenReturn(nuevo);
        when(versiones.listarPorTest(1L)).thenReturn(List.of(origen, nuevo));
        when(configuraciones.listarPorVersion(10L)).thenReturn(List.of(configuracion));
        when(edicion.guardarConfiguracion(any()))
                .thenReturn(
                        new ConfiguracionSubprueba(
                                21L,
                                11L,
                                3L,
                                1,
                                300,
                                TipoRespuesta.SELECCION,
                                "Instrucciones",
                                null,
                                null));
        when(preguntas.listarPorConfiguracion(20L)).thenReturn(List.of(pregunta));
        when(edicion.guardarPregunta(any()))
                .thenReturn(new Pregunta(31L, 21L, "1", 1, "Serie", null));
        when(opciones.listarPorPregunta(30L))
                .thenReturn(List.of(new OpcionRespuesta(40L, 30L, "A", 1, "Uno", null)));
        VersionFormularioService servicio =
                new VersionFormularioService(
                        versiones,
                        tests,
                        mock(AutorizacionContenidoPort.class),
                        configuraciones,
                        preguntas,
                        opciones,
                        edicion);

        assertEquals(
                EstadoVersionFormulario.BORRADOR,
                servicio.crearBorradorDesdeUltimaPublicada(1L, "v2", null, "DOC-2").getEstado());
        assertEquals(EstadoVersionFormulario.PUBLICADA, origen.getEstado());
        verify(edicion).guardarOpcion(argThat(o -> o.getIdPregunta().equals(31L)));
    }
}
