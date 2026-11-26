package com.universidad.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.universidad.tests.application.service.CierreSubpruebasService;
import com.universidad.tests.application.service.ConsultaEvaluacionService;
import com.universidad.tests.domain.exception.EvaluacionNoEncontradaException;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.EstadoSubprueba;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.dto.response.EvaluacionResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ConsultaEvaluacionServiceTest {

    private static final Instant AHORA = Instant.parse("2026-10-30T15:40:00Z");

    private final EvaluacionRepository repositorio = mock(EvaluacionRepository.class);
    private final ConsultaEvaluacionService servicio =
            new ConsultaEvaluacionService(
                    repositorio,
                    mock(com.universidad.tests.domain.port.AutorizacionEvaluacionPort.class),
                    mock(CierreSubpruebasService.class),
                    Clock.fixed(AHORA, ZoneOffset.UTC));

    private EvaluacionSubprueba subprueba(
            String codigo, int orden, EstadoSubprueba estado, Instant inicio, Instant fin) {
        Instant cierre = estado == EstadoSubprueba.CERRADA ? fin : null;
        return new EvaluacionSubprueba(
                orden * 10L,
                15L,
                1L,
                codigo,
                "Subprueba " + codigo,
                orden,
                600,
                estado,
                null,
                true,
                inicio,
                fin,
                cierre,
                null,
                75);
    }

    @Test
    void devuelveLaSubpruebaActivaConSegundosRestantesCalculadosPorElServidor() {
        Instant fin = Instant.parse("2026-10-30T15:42:10Z");
        Evaluacion evaluacion =
                new Evaluacion(
                        15L,
                        25L,
                        3L,
                        1L,
                        10L,
                        EstadoEvaluacion.EN_CURSO,
                        Instant.parse("2026-10-30T15:30:00Z"),
                        null,
                        List.of(
                                subprueba(
                                        "VOC1",
                                        1,
                                        EstadoSubprueba.CERRADA,
                                        Instant.parse("2026-10-30T15:30:00Z"),
                                        Instant.parse("2026-10-30T15:35:00Z")),
                                subprueba(
                                        "VOC2",
                                        2,
                                        EstadoSubprueba.ACTIVA,
                                        Instant.parse("2026-10-30T15:32:10Z"),
                                        fin),
                                subprueba("VOC3", 3, EstadoSubprueba.PENDIENTE, null, null)));
        when(repositorio.buscarPorId(15L)).thenReturn(Optional.of(evaluacion));

        EvaluacionResponse respuesta = servicio.consultar(15L);

        assertThat(respuesta.id()).isEqualTo(15L);
        assertThat(respuesta.estado()).isEqualTo("EN_CURSO");
        assertThat(respuesta.subpruebaActiva().codigo()).isEqualTo("VOC2");
        assertThat(respuesta.subpruebaActiva().finLimite()).isEqualTo(fin);
        assertThat(respuesta.subpruebaActiva().segundosRestantes()).isEqualTo(130);
        assertThat(respuesta.subpruebas())
                .extracting(EvaluacionResponse.SubpruebaEstado::estado)
                .containsExactly("CERRADA", "ACTIVA", "PENDIENTE");
        assertThat(respuesta.subpruebas().get(0).cerradaEn())
                .isEqualTo(Instant.parse("2026-10-30T15:35:00Z"));
        assertThat(respuesta.subpruebas().get(1).cerradaEn()).isNull();
    }

    @Test
    void sinSubpruebaActivaDevuelveNulo() {
        Evaluacion evaluacion =
                new Evaluacion(
                        15L,
                        25L,
                        3L,
                        1L,
                        10L,
                        EstadoEvaluacion.EN_CURSO,
                        AHORA,
                        null,
                        List.of(subprueba("VOC1", 1, EstadoSubprueba.PENDIENTE, null, null)));
        when(repositorio.buscarPorId(15L)).thenReturn(Optional.of(evaluacion));

        assertThat(servicio.consultar(15L).subpruebaActiva()).isNull();
    }

    @Test
    void segundosRestantesNuncaSonNegativos() {
        Evaluacion evaluacion =
                new Evaluacion(
                        15L,
                        25L,
                        3L,
                        1L,
                        10L,
                        EstadoEvaluacion.EN_CURSO,
                        AHORA,
                        null,
                        List.of(
                                subprueba(
                                        "VOC1",
                                        1,
                                        EstadoSubprueba.ACTIVA,
                                        Instant.parse("2026-10-30T15:20:00Z"),
                                        Instant.parse("2026-10-30T15:30:00Z"))));
        when(repositorio.buscarPorId(15L)).thenReturn(Optional.of(evaluacion));

        assertThat(servicio.consultar(15L).subpruebaActiva().segundosRestantes()).isZero();
    }

    @Test
    void evaluacionInexistenteLanzaNoEncontrada() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.consultar(99L))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }

    @Test
    void unaConsultaDenegadaNoCierraSubpruebasVencidas() {
        var auth = mock(com.universidad.tests.domain.port.AutorizacionEvaluacionPort.class);
        var cierre = mock(CierreSubpruebasService.class);
        var e =
                new Evaluacion(
                        15L, 25L, 3L, 1L, 10L, EstadoEvaluacion.EN_CURSO, AHORA, null, List.of());
        when(repositorio.buscarPorId(15L)).thenReturn(Optional.of(e));
        org.mockito.Mockito.doThrow(
                        new com.universidad.tests.domain.exception.SinPermisoException())
                .when(auth)
                .exigirPermisoConsultarEvaluacion(15L, 25L);
        var consulta =
                new ConsultaEvaluacionService(
                        repositorio, auth, cierre, Clock.fixed(AHORA, ZoneOffset.UTC));
        assertThatThrownBy(() -> consulta.consultar(15L))
                .isInstanceOf(com.universidad.tests.domain.exception.SinPermisoException.class);
        org.mockito.Mockito.verifyNoInteractions(cierre);
    }
}
