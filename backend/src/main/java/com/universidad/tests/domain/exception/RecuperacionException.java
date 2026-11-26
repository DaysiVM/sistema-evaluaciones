package com.universidad.tests.domain.exception;

public class RecuperacionException extends RuntimeException {
    public enum Codigo {
        CORREO_NO_DISPONIBLE,
        TOKEN_INVALIDO,
        SOLICITUD_INVALIDA
    }

    private final Codigo codigo;

    public RecuperacionException(Codigo codigo) {
        super("No se pudo completar la recuperacion");
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
