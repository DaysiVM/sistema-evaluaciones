package com.universidad.tests.domain.port;

public interface HashContrasenasPort {

    // Genera el resumen cifrado; nunca se guarda la contrasena en texto plano
    String crear(char[] contrasena);

    boolean verificar(char[] contrasena, String resumen);
}
