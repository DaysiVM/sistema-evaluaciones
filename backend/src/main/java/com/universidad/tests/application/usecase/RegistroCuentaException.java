package com.universidad.tests.application.usecase;

public class RegistroCuentaException extends RuntimeException {

    public enum Codigo {
        CUENTA_EXISTENTE,
        ROL_NO_AUTORIZADO,
        SOLICITUD_INVALIDA
    }

    private final Codigo codigo;

    public RegistroCuentaException(Codigo codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
