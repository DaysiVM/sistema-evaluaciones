package com.universidad.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.universidad.tests.application.service.AutorizacionMotorSubpruebasService;
import com.universidad.tests.application.service.AutorizacionService;
import com.universidad.tests.application.service.ContextoSesion;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.application.service.MotorSubpruebasService;
import com.universidad.tests.application.service.RegistroAuditoria;
import com.universidad.tests.domain.exception.EvaluacionNoEncontradaException;
import com.universidad.tests.domain.exception.MotorSubpruebasException;
import com.universidad.tests.domain.exception.MotorSubpruebasException.Codigo;
import com.universidad.tests.domain.exception.NoAutenticadoException;
import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.domain.model.*;
import com.universidad.tests.domain.port.AlcanceMotorSubpruebasPort;
import com.universidad.tests.domain.port.AutorizacionMotorSubpruebasPort;
import com.universidad.tests.domain.port.AutorizacionMotorSubpruebasPort.Permiso;
import com.universidad.tests.domain.repository.*;
import com.universidad.tests.dto.request.RegistrarRespuestasRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MotorSubpruebasServiceTest {
    private static final Instant INICIO = Instant.parse("2026-11-24T19:00:00Z");
    private static final Instant LIMITE = INICIO.plusSeconds(60);
    private final EvaluacionRepository evaluaciones = mock(EvaluacionRepository.class);
    private final ConfiguracionSubpruebaRepository configuraciones =
            mock(ConfiguracionSubpruebaRepository.class);
    private final PreguntaRepository preguntas = mock(PreguntaRepository.class);
    private final OpcionRespuestaRepository opciones = mock(OpcionRespuestaRepository.class);
    private final RespuestaEvaluacionRepository respuestas =
            mock(RespuestaEvaluacionRepository.class);
    private final AutorizacionMotorSubpruebasPort autorizacion =
            mock(AutorizacionMotorSubpruebasPort.class);
    private final AutorizacionService sesiones = mock(AutorizacionService.class);
    private final RegistroAuditoria auditoria = mock(RegistroAuditoria.class);

    private MotorSubpruebasService motor(Instant ahora) {
        when(sesiones.exigirSesion()).thenReturn(new IdentidadSesion(7L, 1L, "SUJETO", Set.of()));
        return new MotorSubpruebasService(
                evaluaciones,
                configuraciones,
                preguntas,
                opciones,
                respuestas,
                autorizacion,
                sesiones,
                auditoria,
                Clock.fixed(ahora, ZoneOffset.UTC));
    }

    @Test
    void configuracionPorDefectoDeniegaAntesDeConsultarLaEvaluacion() {
        var motor =
                new MotorSubpruebasService(
                        evaluaciones,
                        configuraciones,
                        preguntas,
                        opciones,
                        respuestas,
                        new AutorizacionMotorSubpruebasService(
                                new AutorizacionService(), Optional.empty()),
                        new AutorizacionService(),
                        auditoria,
                        Clock.fixed(INICIO, ZoneOffset.UTC));
        assertThatThrownBy(() -> motor.iniciar(1L, "SUB"))
                .isInstanceOf(NoAutenticadoException.class);
        assertThatThrownBy(() -> motor.consultarPreguntas(1L, "SUB"))
                .isInstanceOf(NoAutenticadoException.class);
        assertThatThrownBy(() -> motor.registrarRespuestas(1L, "SUB", null))
                .isInstanceOf(NoAutenticadoException.class);
        verifyNoInteractions(evaluaciones, configuraciones, preguntas, opciones, respuestas);
    }

    @Test
    void permisoSinVerificadorDeAlcanceSeDeniega() {
        var politica =
                new AutorizacionMotorSubpruebasService(new AutorizacionService(), Optional.empty());
        ContextoSesion.establecer(
                new IdentidadSesion(7L, 1L, "SUJETO", Set.of(Permiso.ITEMS_LEER.name())));
        try {
            assertThatThrownBy(() -> politica.exigirPermiso(Permiso.ITEMS_LEER))
                    .isInstanceOf(SinPermisoException.class);
        } finally {
            ContextoSesion.limpiar();
        }
    }

    @Test
    void escrituraExigeRolSujetoYAlcanceSobreLaEvaluacion() {
        var alcance = mock(AlcanceMotorSubpruebasPort.class);
        var politica =
                new AutorizacionMotorSubpruebasService(
                        new AutorizacionService(), Optional.of(alcance));
        var permiso = Permiso.RESPUESTA_ESCRIBIR;
        when(alcance.permite(7L, "SUJETO", permiso, 1L, 5L)).thenReturn(true);
        try {
            ContextoSesion.establecer(
                    new IdentidadSesion(7L, 1L, "SUJETO", Set.of(permiso.name())));
            politica.exigirPermiso(permiso);
            politica.exigirAcceso(permiso, 1L, 5L);
            assertThatThrownBy(() -> politica.exigirAcceso(permiso, 2L, 9L))
                    .isInstanceOf(SinPermisoException.class);

            ContextoSesion.establecer(
                    new IdentidadSesion(8L, 2L, "ACADEMICO", Set.of(permiso.name())));
            assertThatThrownBy(() -> politica.exigirPermiso(permiso))
                    .isInstanceOf(SinPermisoException.class);
        } finally {
            ContextoSesion.limpiar();
        }
    }

    @Test
    void permisoDeInicioNoOtorgaPermisoParaResponder() {
        preparar(subprueba("SUB", 1, EstadoSubprueba.PENDIENTE, null));
        assertThat(motor(INICIO).iniciar(1L, "SUB").estado()).isEqualTo("ACTIVA");
        verify(autorizacion).exigirAcceso(Permiso.SUBPRUEBA_INICIAR, 1L, 5L);
        doThrow(new SinPermisoException())
                .when(autorizacion)
                .exigirPermiso(Permiso.RESPUESTA_ESCRIBIR);
        assertThatThrownBy(() -> motor(INICIO).registrarRespuestas(1L, "SUB", null))
                .isInstanceOf(SinPermisoException.class);
        verifyNoInteractions(respuestas);
    }

    @Test
    void evaluacionInexistente() {
        assertThatThrownBy(() -> motor(INICIO).iniciar(1L, "SUB"))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }

    @Test
    void subpruebaInexistente() {
        preparar();
        error(Codigo.SUBPRUEBA_NO_ENCONTRADA, () -> motor(INICIO).iniciar(1L, "SUB"));
    }

    @Test
    void siguienteSubpruebaPendienteYOtraActiva() {
        var segunda = subprueba("SUB2", 2, EstadoSubprueba.PENDIENTE, null);
        preparar(subprueba("SUB", 1, EstadoSubprueba.PENDIENTE, null), segunda);
        error(Codigo.SUBPRUEBA_FUERA_DE_ORDEN, () -> motor(INICIO).iniciar(1L, "SUB2"));

        preparar(subprueba("SUB", 1, EstadoSubprueba.ACTIVA, null), segunda);
        error(Codigo.HAY_SUBPRUEBA_ACTIVA, () -> motor(INICIO).iniciar(1L, "SUB2"));
    }

    @Test
    void rechazaReaperturaYLetraAusente() {
        preparar(subprueba("SUB", 1, EstadoSubprueba.CERRADA, LIMITE));
        error(Codigo.SUBPRUEBA_CERRADA, () -> motor(INICIO).iniciar(1L, "SUB"));

        preparar(subprueba("FV", 1, EstadoSubprueba.PENDIENTE, null));
        error(Codigo.LETRA_NO_CONFIGURADA, () -> motor(INICIO).iniciar(1L, "FV"));
    }

    @Test
    void e6RechazaPendienteYRechazaEvaluacionAjenaAntesDeLeerContenido() {
        preparar(subprueba("SUB", 1, EstadoSubprueba.PENDIENTE, null));
        error(Codigo.SUBPRUEBA_NO_ACTIVA, () -> motor(INICIO).consultarPreguntas(1L, "SUB"));

        doThrow(new SinPermisoException())
                .when(autorizacion)
                .exigirAcceso(Permiso.ITEMS_LEER, 1L, 5L);
        assertThatThrownBy(() -> motor(INICIO).consultarPreguntas(1L, "SUB"))
                .isInstanceOf(SinPermisoException.class);
        verifyNoInteractions(preguntas, opciones, respuestas);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0})
    void e7AceptaAntesYEnElLimite(long nanos) {
        preparar(subprueba("SUB", 1, EstadoSubprueba.ACTIVA, null));
        when(preguntas.buscarPorId(4L))
                .thenReturn(Optional.of(new Pregunta(4L, 3L, "1", 1, "Ejemplo", null)));
        when(opciones.buscarPorCodigo(4L, "A"))
                .thenReturn(Optional.of(new OpcionRespuesta(6L, 4L, "A", 1, "Ejemplo", null)));
        var solicitud =
                new RegistrarRespuestasRequest(
                        List.of(new RegistrarRespuestasRequest.Seleccion(4L, "A")), null);
        assertThat(
                        motor(LIMITE.plusNanos(nanos))
                                .registrarRespuestas(1L, "SUB", solicitud)
                                .registradas())
                .isEqualTo(1);
        verify(respuestas).registrar(any());
    }

    @Test
    void e7DistingueVencimientoDeCierreAnticipado() {
        Instant tarde = LIMITE.plusNanos(1);
        preparar(subprueba("SUB", 1, EstadoSubprueba.ACTIVA, null));
        error(Codigo.TIEMPO_AGOTADO, () -> motor(tarde).registrarRespuestas(1L, "SUB", null));
        preparar(subprueba("SUB", 1, EstadoSubprueba.CERRADA, LIMITE));
        error(Codigo.TIEMPO_AGOTADO, () -> motor(tarde).registrarRespuestas(1L, "SUB", null));
        preparar(subprueba("SUB", 1, EstadoSubprueba.CERRADA, LIMITE.minusSeconds(1)));
        error(Codigo.SUBPRUEBA_NO_ACTIVA, () -> motor(tarde).registrarRespuestas(1L, "SUB", null));
        verifyNoInteractions(respuestas);
    }

    @Test
    void e7RechazaPalabrasVaciasOrdenDuplicadoYExceso() {
        preparar(subprueba("FV", 1, EstadoSubprueba.ACTIVA, null));
        var primera = new RegistrarRespuestasRequest.Palabra(1, "ficticia");
        for (var palabras :
                List.of(
                        List.of(new RegistrarRespuestasRequest.Palabra(1, " ")),
                        List.of(primera, primera),
                        List.of(primera, new RegistrarRespuestasRequest.Palabra(3, "ejemplo")))) {
            error(
                    Codigo.SOLICITUD_INVALIDA,
                    () ->
                            motor(INICIO)
                                    .registrarRespuestas(
                                            1L,
                                            "FV",
                                            new RegistrarRespuestasRequest(null, palabras)));
        }
        verifyNoInteractions(respuestas);
    }

    private void preparar(EvaluacionSubprueba... subpruebas) {
        var evaluacion =
                new Evaluacion(
                        1L,
                        5L,
                        1L,
                        1L,
                        2L,
                        EstadoEvaluacion.EN_CURSO,
                        INICIO,
                        null,
                        List.of(subpruebas));
        when(evaluaciones.buscarPorIdParaActualizar(1L)).thenReturn(Optional.of(evaluacion));
        when(evaluaciones.buscarPorId(1L)).thenReturn(Optional.of(evaluacion));
        when(configuraciones.buscarPorId(3L))
                .thenReturn(
                        Optional.of(
                                new ConfiguracionSubprueba(
                                        3L,
                                        2L,
                                        1L,
                                        1,
                                        60,
                                        TipoRespuesta.SELECCION,
                                        "INSTR",
                                        null,
                                        null)));
    }

    private EvaluacionSubprueba subprueba(
            String codigo, int orden, EstadoSubprueba estado, Instant cierre) {
        boolean pendiente = estado == EstadoSubprueba.PENDIENTE;
        return new EvaluacionSubprueba(
                (long) orden,
                1L,
                3L,
                codigo,
                "Ficticia",
                orden,
                60,
                estado,
                null,
                true,
                pendiente ? null : INICIO,
                pendiente ? null : LIMITE,
                cierre,
                null,
                2);
    }

    private void error(Codigo codigo, Runnable operacion) {
        assertThatThrownBy(operacion::run)
                .isInstanceOfSatisfying(
                        MotorSubpruebasException.class,
                        ex -> assertThat(ex.codigo()).isEqualTo(codigo));
    }
}
