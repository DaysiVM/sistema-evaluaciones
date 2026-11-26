package com.universidad.tests.application.evaluator;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Registro y fábrica de evaluadores de test psicométricos. */
public class TestEvaluatorRegistry {

    private final Map<String, TestEvaluator> evaluadores = new ConcurrentHashMap<>();

    public void registrar(TestEvaluator evaluador) {
        if (evaluador != null && evaluador.getCodigoTest() != null) {
            evaluadores.put(evaluador.getCodigoTest().toUpperCase(), evaluador);
        }
    }

    public Optional<TestEvaluator> obtener(String codigoTest) {
        if (codigoTest == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(evaluadores.get(codigoTest.toUpperCase()));
    }
}
