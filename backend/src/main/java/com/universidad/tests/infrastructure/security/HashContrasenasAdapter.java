package com.universidad.tests.infrastructure.security;

import com.universidad.tests.domain.port.HashContrasenasPort;
import org.springframework.stereotype.Component;

// Expone al dominio el hash PBKDF2 de HashContrasenasPbkdf2 sin modificarlo
@Component
public class HashContrasenasAdapter implements HashContrasenasPort {

    private final HashContrasenasPbkdf2 hash = new HashContrasenasPbkdf2();

    @Override
    public String crear(char[] contrasena) {
        return hash.crear(contrasena);
    }

    @Override
    public boolean verificar(char[] contrasena, String resumen) {
        return hash.verificar(contrasena, resumen);
    }
}
