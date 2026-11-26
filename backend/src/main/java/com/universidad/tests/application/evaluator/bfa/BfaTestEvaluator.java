package com.universidad.tests.application.evaluator.bfa;

import com.universidad.tests.application.evaluator.FactorCalculado;
import com.universidad.tests.application.evaluator.ResultadoEvaluacion;
import com.universidad.tests.application.evaluator.TestEvaluator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Evaluador del módulo Batería Factorial de Aptitudes (BFA). Integra el consolidador psicométrico
 * para calcular factores compuestos y baremos.
 */
public final class BfaTestEvaluator implements TestEvaluator {

    public static final String CODIGO_TEST = "BFA";
    private final ConsolidadorCompuestosBfa consolidador;

    public BfaTestEvaluator(ConsolidadorCompuestosBfa consolidador) {
        this.consolidador = Objects.requireNonNull(consolidador, "consolidador es obligatorio");
    }

    public BfaTestEvaluator() {
        this(new ConsolidadorCompuestosBfa());
    }

    @Override
    public String getCodigoTest() {
        return CODIGO_TEST;
    }

    @Override
    public ResultadoEvaluacion consolidar(
            long evaluacionId,
            long versionFormularioId,
            Map<String, Integer> puntuacionesSubpruebas) {
        Objects.requireNonNull(puntuacionesSubpruebas, "puntuacionesSubpruebas es obligatorio");

        Map<String, ConsolidadorCompuestosBfa.PuntuacionCompuesta> compuestos =
                consolidador.consolidar(puntuacionesSubpruebas);

        Map<String, FactorCalculado> factores = new LinkedHashMap<>();

        // Registrar las puntuaciones directas de entrada
        for (Map.Entry<String, Integer> entrada : puntuacionesSubpruebas.entrySet()) {
            if (entrada.getValue() != null) {
                factores.put(
                        entrada.getKey(),
                        FactorCalculado.calculada(entrada.getKey(), entrada.getValue(), null));
            } else {
                factores.put(
                        entrada.getKey(),
                        FactorCalculado.pendiente(
                                entrada.getKey(), "FALTA_PUNTUACION_" + entrada.getKey()));
            }
        }

        // Registrar los 8 compuestos calculados de BFA
        for (Map.Entry<String, ConsolidadorCompuestosBfa.PuntuacionCompuesta> comp :
                compuestos.entrySet()) {
            ConsolidadorCompuestosBfa.PuntuacionCompuesta valor = comp.getValue();
            if (valor.estadoCalculo() == ConsolidadorCompuestosBfa.EstadoCalculo.CALCULADA) {
                factores.put(
                        comp.getKey(),
                        FactorCalculado.calculada(comp.getKey(), valor.puntuacionDirecta(), null));
            } else {
                factores.put(
                        comp.getKey(), FactorCalculado.pendiente(comp.getKey(), valor.motivo()));
            }
        }

        // Al finalizar E8, el resultado queda PENDIENTE_VALIDACION sin convertir faltantes en cero
        return ResultadoEvaluacion.pendienteValidacion(
                null, Map.copyOf(factores), "Consolidación BFA completada");
    }
}
