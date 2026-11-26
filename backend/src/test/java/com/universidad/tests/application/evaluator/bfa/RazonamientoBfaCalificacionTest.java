package com.universidad.tests.application.evaluator.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Casos sintéticos de R1 y R2. Documento BFA §5.6 fija 20 preguntas para R1; §5.6 y §5.7 establecen
 * puntuación directa igual al número de aciertos; §2.4 indica que una omisión no resta. No se
 * emplean preguntas ni claves oficiales.
 */
class RazonamientoBfaCalificacionTest {

    private final CalificadorSeleccionBfa calificador = new CalificadorSeleccionBfa();

    private static Map<Long, Long> claveR1() {
        Map<Long, Long> clave = new LinkedHashMap<>();
        for (long numero = 1; numero <= 20; numero++) {
            clave.put(1000L + numero, 1L + (numero - 1) % 4);
        }
        return clave;
    }

    private static RespuestaSeleccionBfa respuesta(long pregunta, Long opcion) {
        return new RespuestaSeleccionBfa(pregunta, opcion);
    }

    @Test
    void r1ConVeinteAciertosTienePuntuacionDirectaVeinte() {
        Map<Long, Long> clave = claveR1();
        List<RespuestaSeleccionBfa> respuestas = new ArrayList<>();
        clave.forEach((pregunta, opcion) -> respuestas.add(respuesta(pregunta, opcion)));

        ConteoBloqueBfa conteo = calificador.calificar(respuestas, clave);

        assertEquals(20, conteo.aciertos());
        assertEquals(0, conteo.errores());
        assertEquals(0, conteo.omisiones());
    }

    @Test
    void r1SeparaAciertosErroresYOmisionesEnVeinteReactivos() {
        Map<Long, Long> clave = claveR1();
        List<RespuestaSeleccionBfa> respuestas = new ArrayList<>();
        for (long numero = 1; numero <= 9; numero++) {
            long pregunta = 1000L + numero;
            respuestas.add(respuesta(pregunta, clave.get(pregunta)));
        }
        for (long numero = 10; numero <= 13; numero++) {
            long pregunta = 1000L + numero;
            respuestas.add(respuesta(pregunta, 1L + clave.get(pregunta) % 4));
        }

        ConteoBloqueBfa conteo = calificador.calificar(respuestas, clave);

        assertEquals(9, conteo.aciertos());
        assertEquals(4, conteo.errores());
        assertEquals(7, conteo.omisiones());
        assertEquals(20, conteo.aciertos() + conteo.errores() + conteo.omisiones());
    }

    @Test
    void r1SinRespuestasConservaVeinteOmisionesYSinErrores() {
        ConteoBloqueBfa conteo = calificador.calificar(List.of(), claveR1());

        assertEquals(new ConteoBloqueBfa(0, 0, 20), conteo);
    }

    @Test
    void r2CuentaOmisionesSinRestarlasDeLosAciertos() {
        // Identificadores sintéticos para serie de letras, analogía y relación conceptual.
        Map<Long, Long> claveR2 = Map.of(2001L, 1L, 2002L, 2L, 2003L, 3L);
        List<RespuestaSeleccionBfa> respuestas =
                List.of(respuesta(2001L, 1L), respuesta(2002L, 4L));

        ConteoBloqueBfa conteo = calificador.calificar(respuestas, claveR2);

        assertEquals(new ConteoBloqueBfa(1, 1, 1), conteo);
    }

    @Test
    void noAceptaEnR1UnaRespuestaDeOtroBloque() {
        List<RespuestaSeleccionBfa> respuestas = List.of(respuesta(2001L, 1L));

        assertThrows(
                IllegalArgumentException.class, () -> calificador.calificar(respuestas, claveR1()));
    }
}
