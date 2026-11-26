// Verificación independiente de Testing (BFA-TEST-02) sobre el PR #64 (BFA-BE-08).
// Solo se ejecuta en una copia aparte del código; no forma parte del PR.
// Copiar en backend/src/test/java/com/universidad/tests/application/evaluator/bfa/ de la copia.
// Datos ficticios: puntuaciones agregadas inventadas, sin reactivos, claves ni baremos reales.
// Los valores esperados se calcularon a mano con las fórmulas de DOC-02 §2.4-2.5
// (ver docs/pruebas/BFA-TEST-02-casos-prueba-v2.md), no con el código del PR.
package com.universidad.tests.application.evaluator.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.universidad.tests.application.evaluator.FactorCalculado;
import com.universidad.tests.application.evaluator.ResultadoEvaluacion;
import com.universidad.tests.application.evaluator.bfa.ConsolidadorCompuestosBfa.EstadoCalculo;
import com.universidad.tests.application.evaluator.bfa.ConsolidadorCompuestosBfa.PuntuacionCompuesta;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PR64VerificacionTestingTest {

    private final ConsolidadorCompuestosBfa consolidador = new ConsolidadorCompuestosBfa();
    private final BfaTestEvaluator evaluador = new BfaTestEvaluator();

    /** Fixture de TC-003 (v1): CV=10, VOC1=7, VOC2=5, FV=4, S1=6+4, S2=8-2, R1=5, R2=7, N1=10, N2=8+9. */
    private static Map<String, Integer> fixtureTc003() {
        Map<String, Integer> m = new HashMap<>();
        m.put("CV", 10);
        m.put("VOC1", 7);
        m.put("VOC2", 5);
        m.put("FV", 4);
        m.put("S1", 10);
        m.put("S2", 6);
        m.put("R1", 5);
        m.put("R2", 7);
        m.put("N1", 10);
        m.put("N2", 17);
        return m;
    }

    private static void calculada(Map<String, PuntuacionCompuesta> r, String codigo, int valor) {
        assertEquals(EstadoCalculo.CALCULADA, r.get(codigo).estadoCalculo(), codigo);
        assertEquals(valor, r.get(codigo).puntuacionDirecta(), codigo);
        assertNull(r.get(codigo).motivo(), codigo);
    }

    private static void pendiente(Map<String, PuntuacionCompuesta> r, String codigo, String motivo) {
        assertEquals(EstadoCalculo.PENDIENTE, r.get(codigo).estadoCalculo(), codigo);
        assertNull(r.get(codigo).puntuacionDirecta(), codigo + " no debe tener valor (ni 0)");
        assertEquals(motivo, r.get(codigo).motivo(), codigo);
    }

    // ---------- TC-BFA-TEST-02-001: N1 ----------

    @Test
    @DisplayName("TC-001 A: N1 = max(0, (9+6) - (2+3)) = 10")
    void tc001CasoA() {
        assertEquals(10, consolidador.calcularN1(9, 2, 6, 3));
    }

    @Test
    @DisplayName("TC-001 B: N1 = max(0, (2+1) - (5+2)) = 0")
    void tc001CasoB() {
        assertEquals(0, consolidador.calcularN1(2, 5, 1, 2));
    }

    @Test
    @DisplayName("TC-001 C (DOC-04 §3.4): N1 = max(0, (6+9) - (9+2)) = 4")
    void tc001CasoCDoc04() {
        assertEquals(4, consolidador.calcularN1(6, 9, 9, 2));
    }

    @Test
    @DisplayName("TC-001 D: el piso se aplica una sola vez al combinar (5), no por bloque (8)")
    void tc001PisoDespuesDeCombinar() {
        // Bloque A: 2-5 = -3; bloque B: 9-1 = 8. Combinado: 11-6 = 5. Por bloque daría 0+8 = 8.
        assertEquals(5, consolidador.calcularN1(2, 5, 9, 1));
    }

    @Test
    @DisplayName("TC-001 E: rechaza un conteo negativo en cualquiera de las 4 posiciones")
    void tc001RechazaNegativos() {
        assertThrows(IllegalArgumentException.class, () -> consolidador.calcularN1(-1, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> consolidador.calcularN1(0, -1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> consolidador.calcularN1(0, 0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> consolidador.calcularN1(0, 0, 0, -1));
    }

    // ---------- TC-BFA-TEST-02-003: compuestos ----------

    @Test
    @DisplayName("TC-003 A: los 8 compuestos con el fixture de v1")
    void tc003Fixture() {
        Map<String, PuntuacionCompuesta> r = consolidador.consolidar(fixtureTc003());
        assertEquals(8, r.size());
        calculada(r, "VOCT", 12); // 7+5
        calculada(r, "ST", 16); // 10+6
        calculada(r, "RT", 12); // 5+7
        calculada(r, "NT", 27); // 10+17
        calculada(r, "EIV", 36); // 2·10 + 12 + 4
        calculada(r, "EIC", 67); // 2·12 + 16 + 27
        calculada(r, "EIG", 103); // 36 + 67
        calculada(r, "AE", 46); // 2·12 + 10 + 12
    }

    @Test
    @DisplayName("TC-003 B: FV entra solo en EIV (y por EIV en EIG), no en EIC ni AE")
    void tc003FvSoloEnEiv() {
        Map<String, Integer> m = fixtureTc003();
        m.put("FV", 5);
        Map<String, PuntuacionCompuesta> r = consolidador.consolidar(m);
        calculada(r, "EIV", 37);
        calculada(r, "EIC", 67);
        calculada(r, "EIG", 104);
        calculada(r, "AE", 46);
    }

    @Test
    @DisplayName("TC-003 C: pesos — CV pesa 2 en EIV y 1 en AE; RT pesa 2 en EIC y en AE")
    void tc003Pesos() {
        Map<String, Integer> m = fixtureTc003();
        m.put("CV", 11); // +1
        m.put("R1", 6); // RT +1
        Map<String, PuntuacionCompuesta> r = consolidador.consolidar(m);
        calculada(r, "RT", 13);
        calculada(r, "EIV", 38); // +2 por CV
        calculada(r, "EIC", 69); // +2 por RT
        calculada(r, "EIG", 107);
        calculada(r, "AE", 49); // +2 por RT, +1 por CV
    }

    @Test
    @DisplayName("TC-003 D: el evaluador BFA devuelve PENDIENTE_VALIDACION, sin resultadoId ni percentiles")
    void tc003Evaluador() {
        ResultadoEvaluacion res = evaluador.consolidar(1L, 1L, fixtureTc003());
        assertEquals(ResultadoEvaluacion.PENDIENTE_VALIDACION, res.estadoResultado());
        assertNull(res.resultadoId());
        assertEquals(18, res.factores().size()); // 10 de entrada + 8 compuestos
        for (FactorCalculado f : res.factores().values()) {
            assertEquals(FactorCalculado.EstadoCalculo.CALCULADO, f.estadoCalculo(), f.codigo());
            assertNull(f.percentil(), f.codigo() + ": sin baremos no hay percentil");
        }
        assertEquals(103, res.factores().get("EIG").puntuacionDirecta());
        assertEquals(10, res.factores().get("CV").puntuacionDirecta());
    }

    // ---------- TC-BFA-TEST-02-004: parciales y faltantes ----------

    @Test
    @DisplayName("TC-004 A: un 0 recibido es un valor válido (FV=0 calcula EIV=32)")
    void tc004CeroLegitimo() {
        Map<String, Integer> m = fixtureTc003();
        m.put("FV", 0);
        Map<String, PuntuacionCompuesta> r = consolidador.consolidar(m);
        calculada(r, "EIV", 32);
        calculada(r, "EIG", 99);
    }

    @Test
    @DisplayName("TC-004 B: sin R2 quedan pendientes RT, EIC, EIG y AE; los demás se calculan")
    void tc004FaltaR2() {
        Map<String, Integer> m = fixtureTc003();
        m.remove("R2");
        Map<String, PuntuacionCompuesta> r = consolidador.consolidar(m);
        pendiente(r, "RT", "FALTA_PUNTUACION_R2");
        pendiente(r, "EIC", "FALTA_PUNTUACION_RT");
        pendiente(r, "EIG", "FALTA_PUNTUACION_EIC");
        pendiente(r, "AE", "FALTA_PUNTUACION_RT");
        calculada(r, "VOCT", 12);
        calculada(r, "ST", 16);
        calculada(r, "NT", 27);
        calculada(r, "EIV", 36);
    }

    @Test
    @DisplayName("TC-004 C: sin CV quedan pendientes EIV, EIG y AE; EIC se calcula")
    void tc004FaltaCv() {
        Map<String, Integer> m = fixtureTc003();
        m.remove("CV");
        Map<String, PuntuacionCompuesta> r = consolidador.consolidar(m);
        pendiente(r, "EIV", "FALTA_PUNTUACION_CV");
        pendiente(r, "EIG", "FALTA_PUNTUACION_EIV");
        pendiente(r, "AE", "FALTA_PUNTUACION_CV");
        calculada(r, "EIC", 67);
    }

    @Test
    @DisplayName("TC-004 D: FV recibido como nulo queda pendiente en el evaluador, no como 0")
    void tc004FvNulo() {
        Map<String, Integer> m = fixtureTc003();
        m.put("FV", null);
        ResultadoEvaluacion res = evaluador.consolidar(2L, 1L, m);
        FactorCalculado fv = res.factores().get("FV");
        assertEquals(FactorCalculado.EstadoCalculo.PENDIENTE, fv.estadoCalculo());
        assertNull(fv.puntuacionDirecta());
        assertEquals("FALTA_PUNTUACION_FV", fv.motivo());
        FactorCalculado eiv = res.factores().get("EIV");
        assertEquals(FactorCalculado.EstadoCalculo.PENDIENTE, eiv.estadoCalculo());
        assertNull(eiv.puntuacionDirecta());
        assertEquals(67, res.factores().get("EIC").puntuacionDirecta());
    }

    @Test
    @DisplayName("TC-004 E: sin ninguna puntuación, los 8 compuestos quedan pendientes y ninguno vale 0")
    void tc004SinDatos() {
        Map<String, PuntuacionCompuesta> r = consolidador.consolidar(new HashMap<>());
        assertEquals(8, r.size());
        for (Map.Entry<String, PuntuacionCompuesta> e : r.entrySet()) {
            assertEquals(EstadoCalculo.PENDIENTE, e.getValue().estadoCalculo(), e.getKey());
            assertNull(e.getValue().puntuacionDirecta(), e.getKey());
        }
    }
}
