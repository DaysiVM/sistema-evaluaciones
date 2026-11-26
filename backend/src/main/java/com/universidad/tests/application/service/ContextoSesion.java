package com.universidad.tests.application.service;

import java.util.Optional;

// Identidad de la solicitud en curso. La fija el filtro HTTP al validar el token
// y la limpia al terminar la solicitud.
public final class ContextoSesion {

    private static final ThreadLocal<IdentidadSesion> ACTUAL = new ThreadLocal<>();

    private ContextoSesion() {}

    public static void establecer(IdentidadSesion identidad) {
        ACTUAL.set(identidad);
    }

    public static Optional<IdentidadSesion> actual() {
        return Optional.ofNullable(ACTUAL.get());
    }

    public static void limpiar() {
        ACTUAL.remove();
    }
}
