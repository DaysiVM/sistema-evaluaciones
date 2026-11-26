package com.universidad.tests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.universidad.tests.application.service.AgregarPreguntaVersionadaService;
import com.universidad.tests.application.service.AgregarPreguntaVersionadaService.PreguntaDuplicadaException;
import com.universidad.tests.application.service.EdicionContenidoVersionadoService;
import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.PreguntaRepository;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import com.universidad.tests.dto.PreguntaCreada;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgregarPreguntaVersionadaServiceTest {
    private final VersionFormularioRepository versiones = mock(VersionFormularioRepository.class);
    private final SubpruebaRepository subpruebas = mock(SubpruebaRepository.class);
    private final ConfiguracionSubpruebaRepository configuraciones =
            mock(ConfiguracionSubpruebaRepository.class);
    private final PreguntaRepository preguntas = mock(PreguntaRepository.class);
    private final AutorizacionContenidoPort autorizacion = mock(AutorizacionContenidoPort.class);
    private final EdicionContenidoVersionadoService edicion =
            mock(EdicionContenidoVersionadoService.class);
    private AgregarPreguntaVersionadaService servicio;

    @BeforeEach
    void preparar() {
        servicio =
                new AgregarPreguntaVersionadaService(
                        versiones, subpruebas, configuraciones, preguntas, autorizacion, edicion);
    }

    @Test
    void agregaPreguntaEnBorradorConLaConfiguracionCorrecta() {
        VersionFormulario version = mock(VersionFormulario.class);
        when(version.getEstado()).thenReturn(EstadoVersionFormulario.BORRADOR);
        when(version.getIdTest()).thenReturn(1L);
        when(versiones.buscarPorId(11L)).thenReturn(Optional.of(version));
        Subprueba subprueba = mock(Subprueba.class);
        when(subprueba.getIdSubprueba()).thenReturn(2L);
        when(subpruebas.buscarPorCodigo(1L, "R1")).thenReturn(Optional.of(subprueba));
        ConfiguracionSubprueba configuracion = mock(ConfiguracionSubprueba.class);
        when(configuracion.getIdConfiguracionSubprueba()).thenReturn(3L);
        when(configuraciones.buscarPorVersionYSubprueba(11L, 2L))
                .thenReturn(Optional.of(configuracion));
        when(preguntas.listarPorConfiguracion(3L)).thenReturn(List.of());
        when(edicion.guardarPregunta(any(Pregunta.class)))
                .thenReturn(new Pregunta(120L, 3L, "1", 1, "Serie", null));

        PreguntaCreada resultado = servicio.agregar(11L, "R1", "1", 1, "Serie", null);

        assertEquals(120L, resultado.id());
        assertEquals(1, resultado.numeroVisible());
        verify(edicion).guardarPregunta(any(Pregunta.class));
    }

    @Test
    void permisoDenegadoNoConsultaNiGuarda() {
        doThrow(new SinPermisoException()).when(autorizacion).exigirPermisoEditarContenido(11L);
        assertThrows(
                SinPermisoException.class,
                () -> servicio.agregar(11L, "R1", "1", 1, "Serie", null));
        verifyNoInteractions(versiones, subpruebas, configuraciones, preguntas, edicion);
    }

    @Test
    void versionPublicadaEsInmutable() {
        VersionFormulario version = mock(VersionFormulario.class);
        when(version.getEstado()).thenReturn(EstadoVersionFormulario.PUBLICADA);
        when(versiones.buscarPorId(11L)).thenReturn(Optional.of(version));
        assertThrows(
                IllegalStateException.class,
                () -> servicio.agregar(11L, "R1", "1", 1, "Serie", null));
        verifyNoInteractions(edicion);
    }

    @Test
    void numeroDuplicadoNoSeGuarda() {
        VersionFormulario version = mock(VersionFormulario.class);
        when(version.getEstado()).thenReturn(EstadoVersionFormulario.BORRADOR);
        when(version.getIdTest()).thenReturn(1L);
        when(versiones.buscarPorId(11L)).thenReturn(Optional.of(version));
        Subprueba subprueba = mock(Subprueba.class);
        when(subprueba.getIdSubprueba()).thenReturn(2L);
        when(subpruebas.buscarPorCodigo(1L, "R1")).thenReturn(Optional.of(subprueba));
        ConfiguracionSubprueba configuracion = mock(ConfiguracionSubprueba.class);
        when(configuracion.getIdConfiguracionSubprueba()).thenReturn(3L);
        when(configuraciones.buscarPorVersionYSubprueba(11L, 2L))
                .thenReturn(Optional.of(configuracion));
        when(preguntas.existePorNumeroVisible(3L, "1")).thenReturn(true);

        assertThrows(
                PreguntaDuplicadaException.class,
                () -> servicio.agregar(11L, "R1", "1", 1, "Serie", null));
        verifyNoInteractions(edicion);
    }
}
