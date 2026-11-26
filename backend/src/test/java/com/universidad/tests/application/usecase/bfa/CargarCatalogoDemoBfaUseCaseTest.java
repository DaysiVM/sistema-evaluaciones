package com.universidad.tests.application.usecase.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.universidad.tests.domain.exception.bfa.ConfiguracionBfaInvalidaException;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.OpcionRespuesta;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.model.bfa.SubpruebaBfa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CargarCatalogoDemoBfaUseCaseTest {

    private ReposEnMemoria repos;
    private CargarCatalogoDemoBfaUseCase cargador;

    @BeforeEach
    void setUp() {
        repos = new ReposEnMemoria();
        ConsultarConfiguracionBfaUseCase consultar =
                new ConsultarConfiguracionBfaUseCase(
                        repos.tests, repos.versiones, repos.subpruebas, repos.configuraciones);
        cargador =
                new CargarCatalogoDemoBfaUseCase(
                        repos.tests,
                        repos.subpruebas,
                        repos.versiones,
                        repos.configuraciones,
                        repos.preguntas,
                        repos.opciones,
                        consultar);
    }

    @Test
    void creaTestBfaConLas13SubpruebasEnOrdenOficial() {
        VersionFormulario version = cargador.cargarContenido();

        assertEquals(1, repos.tests.datos.size());
        assertEquals("BFA", repos.tests.datos.values().iterator().next().getCodigo());
        assertEquals(13, repos.subpruebas.datos.size());
        assertEquals(
                13,
                repos.configuraciones.listarPorVersion(version.getIdVersionFormulario()).size());
        assertEquals(EstadoVersionFormulario.BORRADOR, version.getEstado());
        repos.configuraciones
                .datos
                .values()
                .forEach(
                        c -> {
                            SubpruebaBfa oficial =
                                    SubpruebaBfa.values()[c.getOrdenAplicacion() - 1];
                            assertEquals(
                                    oficial.getTiempoLimiteSegundos(), c.getTiempoLimiteSegundos());
                            assertEquals(oficial.getTipoRespuesta(), c.getTipoRespuesta());
                            assertEquals(oficial.getMaximoEntradas(), c.getMaximoEntradas());
                        });
    }

    @Test
    void cadaSubpruebaTieneTresPreguntasFicticiasYSeleccionTieneOpciones() {
        cargador.cargarContenido();

        assertEquals(13 * 3, repos.preguntas.datos.size());
        for (Pregunta p : repos.preguntas.datos.values()) {
            assertTrue(p.getEnunciado().startsWith("[DEMO]"), "el contenido debe ser ficticio");
            var config = repos.configuraciones.buscarPorId(p.getIdConfiguracionSubprueba()).get();
            int opciones = repos.opciones.listarPorPregunta(p.getIdPregunta()).size();
            if (config.getTipoRespuesta() == TipoRespuesta.SELECCION) {
                assertTrue(opciones >= 2, "la seleccion necesita opciones");
            } else {
                assertEquals(0, opciones);
            }
        }
        for (OpcionRespuesta o : repos.opciones.datos.values()) {
            assertTrue(o.getTexto().contains("ficticia"));
        }
        assertEquals(48 * 3, repos.opciones.datos.size());
    }

    @Test
    void cargarDosVecesNoDuplicaNada() {
        cargador.cargarContenido();
        int subpruebas = repos.subpruebas.datos.size();
        int configuraciones = repos.configuraciones.datos.size();
        int preguntas = repos.preguntas.datos.size();
        int opciones = repos.opciones.datos.size();

        cargador.cargarContenido();

        assertEquals(1, repos.tests.datos.size());
        assertEquals(1, repos.versiones.datos.size());
        assertEquals(subpruebas, repos.subpruebas.datos.size());
        assertEquals(configuraciones, repos.configuraciones.datos.size());
        assertEquals(preguntas, repos.preguntas.datos.size());
        assertEquals(opciones, repos.opciones.datos.size());
    }

    @Test
    void publicaLaVersionCuandoLaConfiguracionEsOficial() {
        VersionFormulario version = cargador.cargarContenido();

        VersionFormulario publicada = cargador.publicar(version.getIdVersionFormulario());

        assertEquals(EstadoVersionFormulario.PUBLICADA, publicada.getEstado());
        assertEquals(
                EstadoVersionFormulario.PUBLICADA,
                repos.versiones.buscarPorId(version.getIdVersionFormulario()).get().getEstado());
    }

    @Test
    void siLaVersionYaEstaPublicadaNoSeVuelveATocar() {
        VersionFormulario version = cargador.cargarContenido();
        cargador.publicar(version.getIdVersionFormulario());
        int preguntas = repos.preguntas.datos.size();

        VersionFormulario otra = cargador.cargarContenido();
        VersionFormulario denuevo = cargador.publicar(otra.getIdVersionFormulario());

        assertEquals(EstadoVersionFormulario.PUBLICADA, denuevo.getEstado());
        assertEquals(preguntas, repos.preguntas.datos.size());
    }

    @Test
    void siFaltaUnaConfiguracionNoSePublicaYQuedaEnBorrador() {
        VersionFormulario version = cargador.cargarContenido();
        Long idCv =
                repos.subpruebas.buscarPorCodigo(version.getIdTest(), "CV").get().getIdSubprueba();
        repos.configuraciones.datos.values().removeIf(c -> c.getIdSubprueba().equals(idCv));

        assertThrows(
                ConfiguracionBfaInvalidaException.class,
                () -> cargador.publicar(version.getIdVersionFormulario()));
        assertEquals(
                EstadoVersionFormulario.BORRADOR,
                repos.versiones.buscarPorId(version.getIdVersionFormulario()).get().getEstado());
    }
}
