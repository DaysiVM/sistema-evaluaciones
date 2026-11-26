package com.universidad.tests.domain.exception;

// PENDIENTE GEN-06-BE: la documentacion aun no define como se elige el rol activo
// de una cuenta con varios roles; no se combina ni se elige uno por suposicion.
public class RolActivoNoDeterminadoException extends RuntimeException {

    public RolActivoNoDeterminadoException() {
        super("No se pudo determinar el rol activo de la cuenta");
    }
}
