package com.universidad.tests.application.service;

import java.util.Set;

// Quien hace la solicitud: cuenta, sesion, rol activo y permisos concedidos a ese rol
public record IdentidadSesion(
        Long idCuenta, Long idSesion, String rolActivo, Set<String> permisos) {

    public IdentidadSesion {
        permisos = permisos == null ? Set.of() : Set.copyOf(permisos);
    }

    public boolean tienePermiso(String permiso) {
        return permisos.contains(permiso);
    }
}
