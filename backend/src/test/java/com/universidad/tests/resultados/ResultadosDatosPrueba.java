package com.universidad.tests.resultados;

import com.universidad.tests.domain.model.resultados.ContenedorResultado;
import com.universidad.tests.domain.model.resultados.EvaluacionConsultada;
import com.universidad.tests.domain.model.resultados.FactorResultado;
import com.universidad.tests.domain.model.resultados.RevisionResultado;
import java.util.List;

/**
 * Datos sintéticos para las pruebas de GEN-05. Los valores numéricos se crearon para probar
 * escenarios: no son puntuaciones ni conversiones verificadas del BFA.
 */
final class ResultadosDatosPrueba {

    static final long EV_DISPONIBLE = 16L;
    static final long EV_PENDIENTE = 17L;
    static final long EV_CALCULANDO = 18L;
    static final long EV_EN_CURSO = 15L;
    static final long EV_SIN_RESULTADO = 19L;
    static final long SUJETO = 25L;
    static final long VERSION_FORMULARIO = 3L;

    private ResultadosDatosPrueba() {}

    static EvaluacionConsultada finalizada(long evaluacionId) {
        return new EvaluacionConsultada(evaluacionId, SUJETO, VERSION_FORMULARIO, "FINALIZADA");
    }

    static EvaluacionConsultada enCurso(long evaluacionId) {
        return new EvaluacionConsultada(evaluacionId, SUJETO, VERSION_FORMULARIO, "EN_CURSO");
    }

    static List<FactorResultado> factores() {
        return List.of(
                new FactorResultado("VOC1", 23, "CALCULADO", 60, 7L, "CONVERTIDO", null),
                new FactorResultado(
                        "R1", 31, "CALCULADO", 55, 9L, "SIN_COINCIDENCIA", "Fuera de rango"),
                new FactorResultado(
                        "CV", 0, "CALCULADO", null, null, "SIN_BAREMO_VALIDADO", "Sin baremo"),
                new FactorResultado(
                        "FV", 12, "NO_CALCULABLE", 40, 3L, "NO_APLICA", "Aplicación parcial"));
    }

    static ContenedorResultado disponible(long evaluacionId) {
        return new ContenedorResultado(
                101L,
                evaluacionId,
                "DISPONIBLE",
                new RevisionResultado(501L, 1, "BFA-REGLAS-v1", 11L, 21L, factores()));
    }

    static ContenedorResultado sinRevision(long evaluacionId, String estado) {
        return new ContenedorResultado(102L, evaluacionId, estado, null);
    }
}
