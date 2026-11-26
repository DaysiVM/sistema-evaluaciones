package com.universidad.tests.application.evaluator;

import java.util.Map;

/**
 * Contrato común TestEvaluator (Documento general §5.1.7). Cada test (BFA, Weill, Thurstone,
 * Allport, etc.) implementa este contrato. El motor común no incorpora fórmulas específicas de los
 * tests.
 */
public interface TestEvaluator {

    /** Identificador o código del test (ej. "BFA", "WEILL"). */
    String getCodigoTest();

    /**
     * Consolida los resultados de la evaluación a partir de las puntuaciones de las subpruebas.
     *
     * @param evaluacionId identificador de la evaluación
     * @param versionFormularioId versión del formulario utilizada
     * @param puntuacionesSubpruebas mapa con los códigos de subprueba y sus puntuaciones directas
     *     obtenidas
     * @return resultado consolidado con los factores calculados y el estado del resultado
     */
    ResultadoEvaluacion consolidar(
            long evaluacionId,
            long versionFormularioId,
            Map<String, Integer> puntuacionesSubpruebas);
}
