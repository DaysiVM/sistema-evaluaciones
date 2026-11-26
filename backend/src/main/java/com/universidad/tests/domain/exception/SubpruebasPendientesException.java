package com.universidad.tests.domain.exception;

public class SubpruebasPendientesException extends RuntimeException {
    public SubpruebasPendientesException() {
        super("La evaluación tiene subpruebas pendientes o con tiempo disponible");
    }
}
