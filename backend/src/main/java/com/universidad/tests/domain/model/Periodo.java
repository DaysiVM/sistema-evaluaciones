package com.universidad.tests.domain.model;

import java.time.LocalDate;

/** Contexto administrativo de la aplicación (ENT-COM-04). No es el cronómetro. */
public record Periodo(
        Long idPeriodo,
        String codigo,
        String nombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean activo) {

    public Periodo {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del período es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del período es obligatorio");
        }
        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "El fin del período no puede ser anterior al inicio");
        }
    }

    /** Solo un período activo admite nuevas evaluaciones (E21 disponible=true). */
    public boolean admiteNuevasEvaluaciones() {
        return activo;
    }
}
