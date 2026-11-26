package com.universidad.tests.domain.port;

public interface TokensSesionPort {

    // Token opaco que solo conoce el cliente
    String generar();

    // Resumen que se guarda en lugar del token; null si el token no tiene formato valido
    String resumirSiValido(String token);
}
