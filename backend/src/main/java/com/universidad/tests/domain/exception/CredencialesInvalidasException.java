package com.universidad.tests.domain.exception;

// CREDENCIALES_INVALIDAS (E14): correo o contrasena incorrectos, o cuenta inhabilitada.
// El mensaje es el mismo en todos los casos y no muestra datos protegidos.
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Correo o contrasena incorrectos");
    }
}
