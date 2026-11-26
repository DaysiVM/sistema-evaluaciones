package com.universidad.tests.evaluacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.universidad.tests.application.service.AutorizacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.application.service.RegistroAuditoria;
import com.universidad.tests.application.usecase.IniciarEvaluacionComando;
import com.universidad.tests.application.usecase.IniciarEvaluacionUseCase;
import com.universidad.tests.application.usecase.bfa.ReglaInicioBfa;
import com.universidad.tests.application.usecase.vista.EvaluacionVista;
import com.universidad.tests.application.usecase.vista.SubpruebaEstadoVista;
import com.universidad.tests.domain.exception.ErrorNegocioException;
import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.model.Periodo;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.Sujeto;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.port.AutorizacionEvaluacionPort;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.domain.repository.PeriodoRepository;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.domain.repository.SujetoRepository;
import com.universidad.tests.domain.repository.TestRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import com.universidad.tests.domain.repository.bfa.AplicacionFluidezBfaRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Reglas de E3 con repositorios simulados (datos ficticios de DOC-05). */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IniciarEvaluacionUseCaseTest {

    private static final Instant AHORA = Instant.parse("2026-10-30T15:30:00Z");
    private static final long TEST = 1L;
    private static final long VERSION = 10L;
    private static final long SUJETO = 25L;
    private static final long PERIODO = 3L;

    @Mock private AutorizacionEvaluacionPort autorizacion;
    @Mock private AutorizacionService sesiones;
    @Mock private RegistroAuditoria auditoria;
    @Mock private TestRepository testRepository;
    @Mock private VersionFormularioRepository versionRepository;
    @Mock private ConfiguracionSubpruebaRepository configuracionRepository;
    @Mock private SubpruebaRepository subpruebaRepository;
    @Mock private SujetoRepository sujetoRepository;
    @Mock private PeriodoRepository periodoRepository;
    @Mock private EvaluacionRepository evaluacionRepository;
    @Mock private AplicacionFluidezBfaRepository fluidezRepository;

    private IniciarEvaluacionUseCase useCase;

    @BeforeEach
    void preparar() {
        when(sesiones.exigirSesion()).thenReturn(new IdentidadSesion(1L, 1L, "SUJETO", null));
        useCase =
                new IniciarEvaluacionUseCase(
                        autorizacion,
                        sesiones,
                        auditoria,
                        testRepository,
                        versionRepository,
                        configuracionRepository,
                        subpruebaRepository,
                        sujetoRepository,
                        periodoRepository,
                        evaluacionRepository,
                        List.of(new ReglaInicioBfa(fluidezRepository)),
                        Clock.fixed(AHORA, ZoneOffset.UTC));

        when(testRepository.buscarPorId(TEST))
                .thenReturn(
                        Optional.of(
                                new com.universidad.tests.domain.model.Test(
                                        TEST,
                                        "BFA",
                                        "Batería Factorial de Aptitudes",
                                        null,
                                        true)));
        when(versionRepository.buscarPorId(VERSION))
                .thenReturn(Optional.of(version(VERSION, EstadoVersionFormulario.PUBLICADA)));
        when(sujetoRepository.buscarPorId(SUJETO))
                .thenReturn(Optional.of(new Sujeto(SUJETO, "2026-0153", "Ana", "Ruiz", null)));
        when(periodoRepository.buscarPorId(PERIODO))
                .thenReturn(
                        Optional.of(
                                new Periodo(
                                        PERIODO,
                                        "2026-2",
                                        "Segundo semestre 2026",
                                        null,
                                        null,
                                        true)));
        when(subpruebaRepository.listarPorTest(TEST))
                .thenReturn(
                        List.of(
                                new Subprueba(101L, TEST, "VOC1", "Vocabulario A", null, null),
                                new Subprueba(102L, TEST, "VOC2", "Vocabulario B", null, null),
                                new Subprueba(103L, TEST, "CV", "Comprensión verbal", null, null),
                                new Subprueba(113L, TEST, "FV", "Fluidez verbal", null, null)));
        when(configuracionRepository.listarPorVersion(VERSION))
                .thenReturn(
                        List.of(
                                configuracion(213L, 113L, 13, TipoRespuesta.TEXTO_LIBRE),
                                configuracion(201L, 101L, 1, TipoRespuesta.SELECCION),
                                configuracion(203L, 103L, 3, TipoRespuesta.SELECCION),
                                configuracion(202L, 102L, 2, TipoRespuesta.SELECCION)));
        when(evaluacionRepository.guardarNueva(any())).thenAnswer(i -> conIds(i.getArgument(0)));
    }

    @Test
    void aplicacionParcialRespetaElOrdenOficialYNoLlevaLasDemas() {
        EvaluacionVista vista = useCase.iniciar(comando(List.of("CV", "VOC1"), null));

        assertThat(vista.estado()).isEqualTo("EN_CURSO");
        assertThat(vista.subpruebaActiva()).isNull();
        assertThat(vista.subpruebas())
                .extracting(SubpruebaEstadoVista::codigo)
                .containsExactly("VOC1", "CV");
        assertThat(vista.fechaInicio()).isEqualTo(AHORA);
        verify(fluidezRepository, never()).registrarLetra(anyLong(), anyString());
    }

    @Test
    void aplicacionCompletaConFluidezRegistraLaLetraEnMayuscula() {
        EvaluacionVista vista = useCase.iniciar(comando(null, "m"));

        assertThat(vista.subpruebas())
                .extracting(SubpruebaEstadoVista::codigo)
                .containsExactly("VOC1", "VOC2", "CV", "FV");
        verify(fluidezRepository).registrarLetra(anyLong(), eq("M"));
    }

    @Test
    void conFluidezYSinLetraNoCreaLaEvaluacion() {
        assertError(() -> useCase.iniciar(comando(null, null)), "LETRA_NO_ASIGNADA");
        verify(evaluacionRepository, never()).guardarNueva(any());
    }

    @Test
    void rechazaSubpruebasDesconocidasORepetidas() {
        assertError(
                () -> useCase.iniciar(comando(List.of("VOC1", "XX"), null)),
                "SUBPRUEBA_NO_INCLUIDA");
        assertError(
                () -> useCase.iniciar(comando(List.of("VOC1", "VOC1"), null)),
                "SOLICITUD_INVALIDA");
        verify(evaluacionRepository, never()).guardarNueva(any());
    }

    @Test
    void rechazaVersionNoPublicadaODeOtroTest() {
        when(versionRepository.buscarPorId(VERSION))
                .thenReturn(Optional.of(version(VERSION, EstadoVersionFormulario.BORRADOR)));
        assertError(() -> useCase.iniciar(comando(List.of("VOC1"), null)), "VERSION_NO_PUBLICADA");

        when(testRepository.buscarPorId(2L))
                .thenReturn(
                        Optional.of(
                                new com.universidad.tests.domain.model.Test(
                                        2L, "OTRO", "Otro test", null, true)));
        IniciarEvaluacionComando deOtroTest =
                new IniciarEvaluacionComando(2L, VERSION, SUJETO, PERIODO, List.of("VOC1"), null);
        assertError(() -> useCase.iniciar(deOtroTest), "VERSION_INCOMPATIBLE");
    }

    @Test
    void rechazaSujetoOPeriodoInexistentes() {
        when(sujetoRepository.buscarPorId(SUJETO)).thenReturn(Optional.empty());
        assertError(() -> useCase.iniciar(comando(List.of("VOC1"), null)), "SUJETO_NO_ENCONTRADO");

        when(sujetoRepository.buscarPorId(SUJETO))
                .thenReturn(Optional.of(new Sujeto(SUJETO, "2026-0153", "Ana", "Ruiz", null)));
        when(periodoRepository.buscarPorId(PERIODO)).thenReturn(Optional.empty());
        assertError(() -> useCase.iniciar(comando(List.of("VOC1"), null)), "PERIODO_NO_ENCONTRADO");
    }

    @Test
    void siYaHayUnaEvaluacionAbiertaNoCreaOtra() {
        when(evaluacionRepository.existeEnCurso(SUJETO, TEST, PERIODO)).thenReturn(true);

        assertError(() -> useCase.iniciar(comando(List.of("VOC1"), null)), "EVALUACION_EN_CURSO");
        verify(evaluacionRepository, never()).guardarNueva(any());
    }

    private static void assertError(Runnable accion, String codigo) {
        assertThatThrownBy(accion::run)
                .isInstanceOf(ErrorNegocioException.class)
                .extracting(e -> ((ErrorNegocioException) e).getCodigo())
                .isEqualTo(codigo);
    }

    private static IniciarEvaluacionComando comando(List<String> subpruebas, String letra) {
        return new IniciarEvaluacionComando(TEST, VERSION, SUJETO, PERIODO, subpruebas, letra);
    }

    private static VersionFormulario version(long id, EstadoVersionFormulario estado) {
        boolean borrador = estado == EstadoVersionFormulario.BORRADOR;
        return VersionFormulario.restaurar(
                id,
                TEST,
                "v-prueba",
                null,
                "Fuente ficticia",
                estado,
                borrador ? null : AHORA.minusSeconds(3600),
                borrador ? null : "Evidencia ficticia");
    }

    private static ConfiguracionSubprueba configuracion(
            long id, long idSubprueba, int orden, TipoRespuesta tipo) {
        return new ConfiguracionSubprueba(
                id,
                VERSION,
                idSubprueba,
                orden,
                300,
                tipo,
                "instrucciones-ficticias",
                null,
                tipo == TipoRespuesta.TEXTO_LIBRE ? 75 : null);
    }

    /** Simula el guardado: asigna IDs como lo haría la base de datos. */
    private static Evaluacion conIds(Evaluacion nueva) {
        AtomicLong secuencia = new AtomicLong(500);
        List<EvaluacionSubprueba> subpruebas = new ArrayList<>();
        for (EvaluacionSubprueba s : nueva.getSubpruebas()) {
            subpruebas.add(
                    EvaluacionSubprueba.restaurar(
                            secuencia.incrementAndGet(),
                            s.getIdConfiguracionSubprueba(),
                            s.getCodigo(),
                            s.getOrdenAplicacion(),
                            s.getEstado(),
                            null,
                            null,
                            null));
        }
        return Evaluacion.restaurar(
                100L,
                nueva.getIdSujeto(),
                nueva.getIdVersionFormulario(),
                nueva.getIdTest(),
                nueva.getIdPeriodo(),
                nueva.getEstado(),
                nueva.getIniciadaEn(),
                null,
                subpruebas);
    }
}
