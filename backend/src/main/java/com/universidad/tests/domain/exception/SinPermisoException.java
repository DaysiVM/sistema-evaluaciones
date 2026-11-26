package com.universidad.tests.domain.exception;

// SIN_PERMISO: sin rol activo, permiso o alcance sobre el recurso
public class SinPermisoException extends RuntimeException {

    public SinPermisoException() {
        super("No tiene permiso para realizar esta operacion");
    }
}
