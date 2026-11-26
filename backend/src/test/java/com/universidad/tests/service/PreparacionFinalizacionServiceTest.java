package com.universidad.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.universidad.tests.application.service.CierreSubpruebasService;
import com.universidad.tests.application.service.PreparacionFinalizacionService;
import com.universidad.tests.domain.exception.EvaluacionNoEncontradaException;
import com.universidad.tests.domain.exception.SubpruebasPendientesException;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.EstadoSubprueba;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PreparacionFinalizacionServiceTest {
    private static final Instant INICIO = Instant.parse("2026-11-24T15:00:00Z");
    private final CierreSubpruebasService cierre = mock(CierreSubpruebasService.class);
    private final EvaluacionRepository evaluaciones = mock(EvaluacionRepository.class);
    private final PreparacionFinalizacionService servicio =
            new PreparacionFinalizacionService(cierre, evaluaciones);

    @Test
    void cierraVencidasAntesDeComprobarElEstadoPersistido() {
        var evaluacion = evaluacion(EstadoEvaluacion.EN_CURSO, EstadoSubprueba.CERRADA);
        when(evaluaciones.buscarPorId(15L)).thenReturn(Optional.of(evaluacion));

        assertThat(servicio.preparar(15L)).isSameAs(evaluacion);
        var orden = inOrder(cierre, evaluaciones);
        orden.verify(cierre).cerrarVencidas(15L);
        orden.verify(evaluaciones).buscarPorId(15L);
        assertThat(evaluacion.estado()).isEqualTo(EstadoEvaluacion.EN_CURSO);
        assertThat(evaluacion.fechaFin()).isNull();
    }

    @ParameterizedTest
    @EnumSource(
            value = EstadoSubprueba.class,
            names = {"PENDIENTE", "ACTIVA"})
    void rechazaSubpruebasQueSiguenPendientesOActivas(EstadoSubprueba estado) {
        when(evaluaciones.buscarPorId(15L))
                .thenReturn(Optional.of(evaluacion(EstadoEvaluacion.EN_CURSO, estado)));

        assertThatThrownBy(() -> servicio.preparar(15L))
                .isInstanceOf(SubpruebasPendientesException.class);
    }

    @Test
    void conservaUnaFinalizacionYaRegistrada() {
        var evaluacion = evaluacion(EstadoEvaluacion.FINALIZADA, EstadoSubprueba.CERRADA);
        when(evaluaciones.buscarPorId(15L)).thenReturn(Optional.of(evaluacion));

        assertThat(servicio.preparar(15L)).isSameAs(evaluacion);
        assertThat(evaluacion.fechaFin()).isEqualTo(INICIO.plusSeconds(300));
    }

    @Test
    void rechazaUnaEvaluacionInexistente() {
        when(evaluaciones.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.preparar(99L))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }

    private Evaluacion evaluacion(EstadoEvaluacion estado, EstadoSubprueba subestado) {
        boolean pendiente = subestado == EstadoSubprueba.PENDIENTE;
        var subprueba =
                new EvaluacionSubprueba(
                        1L,
                        15L,
                        1L,
                        "VOC1",
                        "Ficticia",
                        1,
                        300,
                        subestado,
                        "INSTR",
                        true,
                        pendiente ? null : INICIO,
                        pendiente ? null : INICIO.plusSeconds(300),
                        subestado == EstadoSubprueba.CERRADA ? INICIO.plusSeconds(300) : null,
                        null,
                        75);
        return new Evaluacion(
                15L,
                25L,
                3L,
                1L,
                1L,
                estado,
                INICIO,
                estado == EstadoEvaluacion.FINALIZADA ? INICIO.plusSeconds(300) : null,
                List.of(subprueba));
    }
}
