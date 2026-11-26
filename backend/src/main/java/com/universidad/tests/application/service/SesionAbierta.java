package com.universidad.tests.application.service;

import java.time.Instant;
import java.util.List;

// Resultado de abrir una sesion (datos de SesionResponse, E14).
// expiraEn es nulo mientras la documentacion no defina la vigencia (PENDIENTE GEN-06-BE).
public record SesionAbierta(
        String token, Instant expiraEn, String rolActivo, List<String> permisos) {

    public SesionAbierta {
        permisos = permisos == null ? List.of() : List.copyOf(permisos);
    }

    @Override
    public String toString() {
        return "SesionAbierta[token protegido, rolActivo=" + rolActivo + "]";
    }
}
