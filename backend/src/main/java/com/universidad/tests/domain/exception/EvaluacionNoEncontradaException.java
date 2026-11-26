package com.universidad.tests.domain.exception;

public class EvaluacionNoEncontradaException extends RuntimeException {

    public EvaluacionNoEncontradaException(Long idEvaluacion) {
        super("La evaluacion " + idEvaluacion + " no existe");
    }
}
