package com.universidad.tests.application.evaluator.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Datos ficticios: ids de pregunta y de opción inventados, sin ítems reales del BFA. */
class CalificadorSeleccionBfaTest {

    private final CalificadorSeleccionBfa calificador = new CalificadorSeleccionBfa();

    /** Clave ficticia de cinco preguntas: pregunta 101..105, opción correcta 1..4. */
    private static Map<Long, Long> claveFicticia() {
        Map<Long, Long> clave = new LinkedHashMap<>();
        clave.put(101L, 1L);
        clave.put(102L, 2L);
        clave.put(103L, 3L);
        clave.put(104L, 4L);
        clave.put(105L, 1L);
        return clave;
    }

    private static RespuestaSeleccionBfa resp(long pregunta, Long opcion) {
        return new RespuestaSeleccionBfa(pregunta, opcion);
    }

    @Test
    void todasCorrectas() {
        List<RespuestaSeleccionBfa> respuestas =
                List.of(resp(101, 1L), resp(102, 2L), resp(103, 3L), resp(104, 4L), resp(105, 1L));

        assertEquals(
                new ConteoBloqueBfa(5, 0, 0), calificador.calificar(respuestas, claveFicticia()));
    }

    @Test
    void cuentaAciertosErroresYOmisionesPorSeparado() {
        List<RespuestaSeleccionBfa> respuestas =
                List.of(resp(101, 1L), resp(102, 3L), resp(103, 3L), resp(104, 1L));

        assertEquals(
                new ConteoBloqueBfa(2, 2, 1), calificador.calificar(respuestas, claveFicticia()));
    }

    @Test
    void opcionNulaCuentaComoOmisionNoComoError() {
        List<RespuestaSeleccionBfa> respuestas =
                Arrays.asList(resp(101, null), resp(102, 2L), resp(103, null));

        assertEquals(
                new ConteoBloqueBfa(1, 0, 4), calificador.calificar(respuestas, claveFicticia()));
    }

    @Test
    void bloqueSinRespuestasQuedaTodoComoOmisiones() {
        assertEquals(
                new ConteoBloqueBfa(0, 0, 5), calificador.calificar(List.of(), claveFicticia()));
    }

    @Test
    void elOrdenDeLasRespuestasNoCambiaElResultado() {
        List<RespuestaSeleccionBfa> respuestas =
                List.of(resp(105, 2L), resp(103, 3L), resp(101, 1L));

        assertEquals(
                new ConteoBloqueBfa(2, 1, 2), calificador.calificar(respuestas, claveFicticia()));
    }

    @Test
    void rechazaRespuestaAPreguntaFueraDeLaClave() {
        List<RespuestaSeleccionBfa> respuestas = List.of(resp(101, 1L), resp(999, 1L));

        assertThrows(
                IllegalArgumentException.class,
                () -> calificador.calificar(respuestas, claveFicticia()));
    }

    @Test
    void rechazaDosRespuestasALaMismaPregunta() {
        List<RespuestaSeleccionBfa> respuestas = List.of(resp(101, 1L), resp(101, 2L));

        assertThrows(
                IllegalArgumentException.class,
                () -> calificador.calificar(respuestas, claveFicticia()));
    }

    @Test
    void rechazaClaveVacia() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calificador.calificar(List.of(resp(101, 1L)), Map.of()));
    }

    @Test
    void rechazaClaveConOpcionCorrectaNula() {
        Map<Long, Long> clave = new LinkedHashMap<>();
        clave.put(101L, null);

        assertThrows(NullPointerException.class, () -> calificador.calificar(List.of(), clave));
    }

    @Test
    void rechazaArgumentosNulos() {
        assertThrows(
                NullPointerException.class, () -> calificador.calificar(null, claveFicticia()));
        assertThrows(NullPointerException.class, () -> calificador.calificar(List.of(), null));
        assertThrows(NullPointerException.class, () -> new RespuestaSeleccionBfa(null, 1L));
    }

    @Test
    void conteoNoAdmiteValoresNegativos() {
        assertThrows(IllegalArgumentException.class, () -> new ConteoBloqueBfa(-1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new ConteoBloqueBfa(0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ConteoBloqueBfa(0, 0, -1));
    }
}
