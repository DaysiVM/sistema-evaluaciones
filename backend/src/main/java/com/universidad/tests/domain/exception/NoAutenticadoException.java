package com.universidad.tests.domain.exception;

// NO_AUTENTICADO: no hay sesion valida
public class NoAutenticadoException extends RuntimeException {

    public NoAutenticadoException() {
        super("No hay una sesion valida");
    }
}
