package com.universidad.tests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.universidad.tests.application.service.PublicacionVersionFormularioService;
import com.universidad.tests.domain.model.*;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicacionVersionFormularioServiceTest {

    @Mock private VersionFormularioRepository versionRepository;

    @Mock private ConfiguracionSubpruebaRepository configuracionRepository;

    @Mock private SubpruebaRepository subpruebaRepository;

    @Mock private PreguntaRepository preguntaRepository;

    @Mock private OpcionRespuestaRepository opcionRepository;

    @Mock private AutorizacionContenidoPort autorizacion;

    @InjectMocks private PublicacionVersionFormularioService servicio;

    private VersionFormulario crearVersionBorrador() {
        return new VersionFormulario(5L, 2L, "v1", "Version inicial", "documento-autorizado");
    }

    @Test
    void debeRechazarPublicacionSinPermiso() {

        doThrow(new SecurityException("Acceso denegado"))
                .when(autorizacion)
                .exigirPermisoPublicar(5L);

        assertThrows(SecurityException.class, () -> servicio.publicar(5L, "ACTA-001"));

        verifyNoInteractions(
                versionRepository,
                configuracionRepository,
                subpruebaRepository,
                preguntaRepository,
                opcionRepository);
    }

    @Test
    void debeRechazarVersionSinConfiguraciones() {

        VersionFormulario version = crearVersionBorrador();

        when(versionRepository.buscarPorId(5L)).thenReturn(Optional.of(version));

        when(configuracionRepository.listarPorVersion(5L)).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> servicio.publicar(5L, "ACTA-001"));

        assertEquals(EstadoVersionFormulario.BORRADOR, version.getEstado());

        verify(versionRepository, never()).guardar(any());
    }

    @Test
    void debeRechazarSubpruebaDeOtroTest() {

        VersionFormulario version = crearVersionBorrador();

        ConfiguracionSubprueba configuracion = mock(ConfiguracionSubprueba.class);

        Subprueba subprueba = mock(Subprueba.class);

        when(versionRepository.buscarPorId(5L)).thenReturn(Optional.of(version));

        when(configuracionRepository.listarPorVersion(5L)).thenReturn(List.of(configuracion));

        when(configuracion.getIdSubprueba()).thenReturn(3L);

        when(subpruebaRepository.buscarPorId(3L)).thenReturn(Optional.of(subprueba));

        // La version pertenece al test 2,
        // pero la subprueba pertenece al test 99.
        when(subprueba.getIdTest()).thenReturn(99L);

        assertThrows(IllegalStateException.class, () -> servicio.publicar(5L, "ACTA-001"));

        verify(versionRepository, never()).guardar(any());
    }

    @Test
    void debeRechazarPreguntaDeSeleccionSinOpciones() {

        VersionFormulario version = crearVersionBorrador();

        ConfiguracionSubprueba configuracion = mock(ConfiguracionSubprueba.class);

        Subprueba subprueba = mock(Subprueba.class);
        Pregunta pregunta = mock(Pregunta.class);

        when(versionRepository.buscarPorId(5L)).thenReturn(Optional.of(version));

        when(configuracionRepository.listarPorVersion(5L)).thenReturn(List.of(configuracion));

        when(configuracion.getIdSubprueba()).thenReturn(3L);

        when(subpruebaRepository.buscarPorId(3L)).thenReturn(Optional.of(subprueba));

        when(subprueba.getIdTest()).thenReturn(2L);

        when(configuracion.getIdConfiguracionSubprueba()).thenReturn(4L);

        when(preguntaRepository.listarPorConfiguracion(4L)).thenReturn(List.of(pregunta));

        when(configuracion.getTipoRespuesta()).thenReturn(TipoRespuesta.SELECCION);

        when(pregunta.getIdPregunta()).thenReturn(6L);

        when(opcionRepository.listarPorPregunta(6L)).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> servicio.publicar(5L, "ACTA-001"));

        verify(versionRepository, never()).guardar(any());
    }

    @Test
    void debePublicarVersionConContenidoValido() {

        VersionFormulario version = crearVersionBorrador();

        ConfiguracionSubprueba configuracion = mock(ConfiguracionSubprueba.class);

        Subprueba subprueba = mock(Subprueba.class);
        Pregunta pregunta = mock(Pregunta.class);

        OpcionRespuesta opcion = mock(OpcionRespuesta.class);

        when(versionRepository.buscarPorId(5L)).thenReturn(Optional.of(version));

        when(configuracionRepository.listarPorVersion(5L)).thenReturn(List.of(configuracion));

        when(configuracion.getIdSubprueba()).thenReturn(3L);

        when(subpruebaRepository.buscarPorId(3L)).thenReturn(Optional.of(subprueba));

        when(subprueba.getIdTest()).thenReturn(2L);

        when(configuracion.getIdConfiguracionSubprueba()).thenReturn(4L);

        when(preguntaRepository.listarPorConfiguracion(4L)).thenReturn(List.of(pregunta));

        when(configuracion.getTipoRespuesta()).thenReturn(TipoRespuesta.SELECCION);

        when(pregunta.getIdPregunta()).thenReturn(6L);

        when(opcionRepository.listarPorPregunta(6L)).thenReturn(List.of(opcion));

        when(versionRepository.guardar(version)).thenReturn(version);

        VersionFormulario resultado = servicio.publicar(5L, "ACTA-001");

        assertEquals(EstadoVersionFormulario.PUBLICADA, resultado.getEstado());

        assertEquals("ACTA-001", resultado.getEvidenciaValidacion());

        assertNotNull(resultado.getPublicadaEn());

        verify(autorizacion).exigirPermisoPublicar(5L);
        verify(versionRepository).guardar(version);
    }

    @Test
    void debeRetirarVersionPublicada() {

        VersionFormulario version = crearVersionBorrador();

        version.publicar("ACTA-001");

        when(versionRepository.buscarPorId(5L)).thenReturn(Optional.of(version));

        when(versionRepository.guardar(version)).thenReturn(version);

        VersionFormulario resultado = servicio.retirar(5L);

        assertEquals(EstadoVersionFormulario.RETIRADA, resultado.getEstado());

        verify(autorizacion).exigirPermisoRetirar(5L);
        verify(versionRepository).guardar(version);
    }
}
