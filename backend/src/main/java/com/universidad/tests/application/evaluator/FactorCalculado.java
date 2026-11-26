package com.universidad.tests.application.evaluator;

import java.util.Objects;

/** Representa la puntuación y estado de cálculo de un factor individual o resultado compuesto. */
public record FactorCalculado(
        String codigo,
        Integer puntuacionDirecta,
        Integer percentil,
        EstadoCalculo estadoCalculo,
        String motivo) {

    public enum EstadoCalculo {
        CALCULADO,
        PENDIENTE
    }

    public FactorCalculado {
        Objects.requireNonNull(codigo, "codigo es obligatorio");
        Objects.requireNonNull(estadoCalculo, "estadoCalculo es obligatorio");
    }

    public static FactorCalculado calculada(
            String codigo, int puntuacionDirecta, Integer percentil) {
        return new FactorCalculado(
                codigo, puntuacionDirecta, percentil, EstadoCalculo.CALCULADO, null);
    }

    public static FactorCalculado pendiente(String codigo, String motivo) {
        return new FactorCalculado(codigo, null, null, EstadoCalculo.PENDIENTE, motivo);
    }
}
