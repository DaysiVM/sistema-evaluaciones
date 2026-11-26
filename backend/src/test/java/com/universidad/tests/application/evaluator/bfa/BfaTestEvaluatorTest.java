package com.universidad.tests.application.evaluator.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.universidad.tests.application.evaluator.FactorCalculado;
import com.universidad.tests.application.evaluator.ResultadoEvaluacion;
import java.util.Map;
import org.junit.jupiter.api.Test;

class BfaTestEvaluatorTest {

    private final BfaTestEvaluator evaluador = new BfaTestEvaluator();

    @Test
    void codigoTestEsBfa() {
        assertEquals("BFA", evaluador.getCodigoTest());
    }

    @Test
    void consolidaCorrectamenteLosResultadosBfaConPuntuacionesCompletas() {
        Map<String, Integer> entradas =
                Map.of(
                        "VOC1", 2,
                        "VOC2", 3,
                        "S1", 7,
                        "S2", 4,
                        "R1", 8,
                        "R2", 9,
                        "N1", 1,
                        "N2", 2,
                        "CV", 6,
                        "FV", 10);

        ResultadoEvaluacion resultado = evaluador.consolidar(100L, 1L, entradas);

        assertNotNull(resultado);
        assertEquals(ResultadoEvaluacion.PENDIENTE_VALIDACION, resultado.estadoResultado());
        assertNull(resultado.resultadoId());

        Map<String, FactorCalculado> factores = resultado.factores();
        assertEquals(5, factores.get("VOCT").puntuacionDirecta());
        assertEquals(11, factores.get("ST").puntuacionDirecta());
        assertEquals(17, factores.get("RT").puntuacionDirecta());
        assertEquals(3, factores.get("NT").puntuacionDirecta());
        assertEquals(27, factores.get("EIV").puntuacionDirecta());
        assertEquals(48, factores.get("EIC").puntuacionDirecta());
        assertEquals(75, factores.get("EIG").puntuacionDirecta());
        assertEquals(45, factores.get("AE").puntuacionDirecta());
    }

    @Test
    void conservaPuntuacionNulaYMotivoCuandoFaltaFluidezVerbalSinAsignarCero() {
        Map<String, Integer> entradas =
                Map.of(
                        "VOC1", 2,
                        "VOC2", 3,
                        "S1", 7,
                        "S2", 4,
                        "R1", 8,
                        "R2", 9,
                        "N1", 1,
                        "N2", 2,
                        "CV", 6);

        ResultadoEvaluacion resultado = evaluador.consolidar(200L, 1L, entradas);

        assertEquals(ResultadoEvaluacion.PENDIENTE_VALIDACION, resultado.estadoResultado());
        FactorCalculado eiv = resultado.factores().get("EIV");
        assertEquals(FactorCalculado.EstadoCalculo.PENDIENTE, eiv.estadoCalculo());
        assertNull(eiv.puntuacionDirecta());
        assertEquals("FALTA_PUNTUACION_FV", eiv.motivo());

        FactorCalculado eig = resultado.factores().get("EIG");
        assertEquals(FactorCalculado.EstadoCalculo.PENDIENTE, eig.estadoCalculo());
        assertNull(eig.puntuacionDirecta());

        // Compuestos no afectados siguen calculados
        assertEquals(48, resultado.factores().get("EIC").puntuacionDirecta());
    }
}
