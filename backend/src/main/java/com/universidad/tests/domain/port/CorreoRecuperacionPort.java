package com.universidad.tests.domain.port;

public interface CorreoRecuperacionPort {
    boolean disponible();

    void enviar(String correo, String token);
}
