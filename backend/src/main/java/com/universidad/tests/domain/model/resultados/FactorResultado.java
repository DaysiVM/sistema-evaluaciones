package com.universidad.tests.domain.model.resultados;

import java.util.Objects;

/**
 * Resultado de un factor dentro de la revisión vigente (ENT-BFA-10 en BFA; equivalente en otros
 * tests).
 *
 * <p>Los estados son los que guarda el evaluador del test. Esta clase no calcula puntuaciones ni
 * percentiles: solo decide qué valores se pueden presentar como datos verificados.
 */
public record FactorResultado(
        String codigo,
        Integer puntuacionDirecta,
        String estadoCalculo,
        Integer percentil,
        Long idRango,
        String estadoNormativo,
        String motivo) {

    /** Único estado de cálculo que permite presentar una puntuación directa. */
    public static final String CALCULADO = "CALCULADO";

    /** Único estado normativo que permite presentar un percentil. */
    public static final String CONVERTIDO = "CONVERTIDO";

    public FactorResultado {
        Objects.requireNonNull(codigo, "codigo es obligatorio");
        Objects.requireNonNull(estadoCalculo, "estadoCalculo es obligatorio");
        Objects.requireNonNull(estadoNormativo, "estadoNormativo es obligatorio");
    }

    /**
     * Devuelve el factor con solo lo que se puede presentar como definitivo (E9, Documento BFA
     * §5.3):
     *
     * <ul>
     *   <li>La puntuación directa solo se entrega si el factor está {@code CALCULADO}. Un factor no
     *       calculable o pendiente de validación la entrega nula.
     *   <li>El percentil y el rango solo se entregan si el factor está calculado, la conversión es
     *       {@code CONVERTIDO} y hay rango exacto del baremo. En otro caso se entregan nulos.
     *   <li>Un 0 calculado se conserva: no equivale a ausencia (CA-03).
     *   <li>Estados y motivo se entregan tal como están guardados.
     * </ul>
     *
     * <p>Los estados desconocidos nunca habilitan valores: se aplica una lista de permitidos.
     */
    public FactorResultado paraPresentar() {
        boolean calculado = CALCULADO.equals(estadoCalculo) && puntuacionDirecta != null;
        Integer directa = calculado ? puntuacionDirecta : null;
        boolean convertido =
                calculado
                        && CONVERTIDO.equals(estadoNormativo)
                        && idRango != null
                        && percentil != null;
        return new FactorResultado(
                codigo,
                directa,
                estadoCalculo,
                convertido ? percentil : null,
                convertido ? idRango : null,
                estadoNormativo,
                motivo);
    }
}
