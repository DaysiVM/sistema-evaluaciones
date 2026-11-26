package com.universidad.tests.application.evaluator.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.universidad.tests.application.evaluator.bfa.ConsolidadorCompuestosBfa.EstadoCalculo;
import com.universidad.tests.application.evaluator.bfa.ConsolidadorCompuestosBfa.PuntuacionCompuesta;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ConsolidadorCompuestosBfaTest {

    private final ConsolidadorCompuestosBfa consolidador = new ConsolidadorCompuestosBfa();

    @Test
    void consolidaLosOchoCompuestosConLasFormulasAprobadas() {
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

        Map<String, PuntuacionCompuesta> resultado = consolidador.consolidar(entradas);

        assertPuntuacion(resultado, "VOCT", 5);
        assertPuntuacion(resultado, "ST", 11);
        assertPuntuacion(resultado, "RT", 17);
        assertPuntuacion(resultado, "NT", 3);
        assertPuntuacion(resultado, "EIV", 27);
        assertPuntuacion(resultado, "EIC", 48);
        assertPuntuacion(resultado, "EIG", 75);
        assertPuntuacion(resultado, "AE", 45);
    }

    @Test
    void dejaPendientesLosCompuestosConInsumosFaltantesSinConvertirlosEnCero() {
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

        Map<String, PuntuacionCompuesta> resultado = consolidador.consolidar(entradas);

        assertEquals(EstadoCalculo.PENDIENTE, resultado.get("EIV").estadoCalculo());
        assertNull(resultado.get("EIV").puntuacionDirecta());
        assertEquals("FALTA_PUNTUACION_FV", resultado.get("EIV").motivo());
        assertEquals(EstadoCalculo.PENDIENTE, resultado.get("EIG").estadoCalculo());
        assertEquals(48, resultado.get("EIC").puntuacionDirecta());
    }

    @Test
    void aplicaElPisoDeN1DespuesDeCombinarLosDosBloques() {
        assertEquals(0, consolidador.calcularN1(2, 4, 3, 2));
        assertEquals(5, consolidador.calcularN1(7, 1, 2, 3));
    }

    @Test
    void rechazaConteosNegativosParaN1() {
        assertThrows(IllegalArgumentException.class, () -> consolidador.calcularN1(-1, 0, 0, 0));
    }

    private static void assertPuntuacion(
            Map<String, PuntuacionCompuesta> resultado, String codigo, int esperado) {
        assertEquals(EstadoCalculo.CALCULADA, resultado.get(codigo).estadoCalculo());
        assertEquals(esperado, resultado.get(codigo).puntuacionDirecta());
        assertNull(resultado.get(codigo).motivo());
    }
}
