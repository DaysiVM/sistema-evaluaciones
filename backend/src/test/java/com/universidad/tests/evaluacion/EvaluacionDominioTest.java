package com.universidad.tests.evaluacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.EstadoSubprueba;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Reglas de ENT-COM-05/06 sin base de datos. */
class EvaluacionDominioTest {

    private static final Instant AHORA = Instant.parse("2026-10-30T15:38:36Z");

    @Test
    void unaEvaluacionNuevaQuedaEnCursoConSubpruebasPendientesEnOrdenOficial() {
        Evaluacion evaluacion =
                Evaluacion.iniciar(
                        25L,
                        10L,
                        1L,
                        3L,
                        List.of(
                                EvaluacionSubprueba.pendiente(203L, "CV", 3),
                                EvaluacionSubprueba.pendiente(201L, "VOC1", 1)),
                        AHORA);

        assertThat(evaluacion.getEstado()).isEqualTo(EstadoEvaluacion.EN_CURSO);
        assertThat(evaluacion.getIniciadaEn()).isEqualTo(AHORA);
        assertThat(evaluacion.getFinalizadaEn()).isNull();
        assertThat(evaluacion.getSubpruebas())
                .extracting(EvaluacionSubprueba::getCodigo)
                .containsExactly("VOC1", "CV");
        assertThat(evaluacion.getSubpruebas())
                .allMatch(s -> s.getEstado() == EstadoSubprueba.PENDIENTE);
        assertThat(evaluacion.subpruebaActiva()).isEmpty();
    }

    @Test
    void noAdmiteEvaluacionSinSubpruebasNiConfiguracionesRepetidas() {
        assertThatThrownBy(() -> Evaluacion.iniciar(25L, 10L, 1L, 3L, List.of(), AHORA))
                .isInstanceOf(IllegalArgumentException.class);

        List<EvaluacionSubprueba> repetidas =
                List.of(
                        EvaluacionSubprueba.pendiente(201L, "VOC1", 1),
                        EvaluacionSubprueba.pendiente(201L, "VOC1", 1));
        assertThatThrownBy(() -> Evaluacion.iniciar(25L, 10L, 1L, 3L, repetidas, AHORA))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void elTiempoRestanteSeDerivaDelFinLimiteSinModificarlo() {
        Instant inicio = AHORA.minusSeconds(86);
        Instant finLimite = inicio.plusSeconds(300);
        EvaluacionSubprueba activa =
                EvaluacionSubprueba.restaurar(
                        8L, 202L, "VOC2", 2, EstadoSubprueba.ACTIVA, inicio, finLimite, null);

        assertThat(activa.segundosRestantes(AHORA)).isEqualTo(214L);
        assertThat(activa.segundosRestantes(AHORA)).isEqualTo(214L);
        assertThat(activa.getFinLimite()).isEqualTo(finLimite);
        assertThat(activa.segundosRestantes(finLimite.plusSeconds(30))).isZero();
    }

    @Test
    void unaSubpruebaPendienteOCerradaNoTieneTiempoRestante() {
        EvaluacionSubprueba pendiente = EvaluacionSubprueba.pendiente(201L, "VOC1", 1);
        EvaluacionSubprueba cerrada =
                EvaluacionSubprueba.restaurar(
                        7L,
                        201L,
                        "VOC1",
                        1,
                        EstadoSubprueba.CERRADA,
                        AHORA.minusSeconds(600),
                        AHORA.minusSeconds(300),
                        AHORA.minusSeconds(300));

        assertThat(pendiente.segundosRestantes(AHORA)).isNull();
        assertThat(cerrada.segundosRestantes(AHORA)).isNull();
    }

    @Test
    void finalizadaExigeFechaDeFin() {
        List<EvaluacionSubprueba> subpruebas = List.of(EvaluacionSubprueba.pendiente(1L, "A", 1));
        assertThatThrownBy(
                        () ->
                                Evaluacion.restaurar(
                                        1L,
                                        25L,
                                        10L,
                                        1L,
                                        3L,
                                        EstadoEvaluacion.FINALIZADA,
                                        AHORA,
                                        null,
                                        subpruebas))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
