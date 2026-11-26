package com.universidad.tests.application.evaluator.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.universidad.tests.domain.model.OpcionRespuesta;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// BFA-BE-03: preguntas, opciones y claves ficticias; no son ítems ni claves reales del BFA
class CalificadorVerbalBfaTest {

    private final CalificadorVerbalBfa calificador = new CalificadorVerbalBfa();

    @ParameterizedTest
    @ValueSource(strings = {"VOC1", "VOC2", "CV"})
    void calificar_cadaSubpruebaVerbal_separaAciertosErroresYOmisiones(String subprueba) {
        int alternativas = alternativasDe(subprueba);
        var opciones = opcionesPara(3, alternativas);
        var clave = Map.of(1L, 12L, 2L, 21L, 3L, 30L + alternativas);
        var respuestas =
                List.of(
                        new RespuestaSeleccionBfa(1L, 12L),
                        new RespuestaSeleccionBfa(2L, 22L),
                        new RespuestaSeleccionBfa(3L, null));

        assertEquals(
                new ConteoBloqueBfa(1, 1, 1),
                calificador.calificar(subprueba, respuestas, clave, opciones));
    }

    @Test
    void calificar_laPuntuacionDirectaEsElNumeroDeAciertosSinPenalizarErrores() {
        var opciones = opcionesPara(4, 5);
        var clave = Map.of(1L, 11L, 2L, 21L, 3L, 31L, 4L, 41L);
        var respuestas =
                List.of(
                        new RespuestaSeleccionBfa(1L, 11L),
                        new RespuestaSeleccionBfa(2L, 21L),
                        new RespuestaSeleccionBfa(3L, 35L),
                        new RespuestaSeleccionBfa(4L, 45L));

        ConteoBloqueBfa conteo = calificador.calificar("VOC1", respuestas, clave, opciones);

        assertEquals(2, conteo.aciertos());
        assertEquals(2, conteo.errores());
    }

    @Test
    void calificar_bloqueSinRespuestas_cuentaTodasComoOmisiones() {
        assertEquals(
                new ConteoBloqueBfa(0, 0, 2),
                calificador.calificar(
                        "CV", List.of(), Map.of(1L, 11L, 2L, 23L), opcionesPara(2, 3)));
    }

    @Test
    void calificar_todasCorrectas() {
        var respuestas =
                List.of(new RespuestaSeleccionBfa(1L, 13L), new RespuestaSeleccionBfa(2L, 25L));
        assertEquals(
                new ConteoBloqueBfa(2, 0, 0),
                calificador.calificar(
                        "VOC2", respuestas, Map.of(1L, 13L, 2L, 25L), opcionesPara(2, 5)));
    }

    @Test
    void calificar_cvConAlternativaDYE_rechazaElCatalogo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calificador.calificar("CV", List.of(), Map.of(1L, 11L), opcionesPara(1, 5)));
    }

    @Test
    void calificar_vocabularioConSoloTresAlternativas_rechazaElCatalogo() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        calificador.calificar(
                                "VOC1", List.of(), Map.of(1L, 11L), opcionesPara(1, 3)));
    }

    @Test
    void calificar_subpruebaDeOtraFamilia_seRechaza() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        calificador.calificar(
                                "N1-A", List.of(), Map.of(1L, 11L), opcionesPara(1, 5)));
        assertThrows(
                IllegalArgumentException.class,
                () -> calificador.calificar(null, List.of(), Map.of(1L, 11L), opcionesPara(1, 5)));
    }

    @Test
    void calificar_respuestaDuplicada_rechazaElBloque() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        calificador.calificar(
                                "VOC1",
                                List.of(
                                        new RespuestaSeleccionBfa(1L, null),
                                        new RespuestaSeleccionBfa(1L, 11L)),
                                Map.of(1L, 11L),
                                opcionesPara(1, 5)));
    }

    @Test
    void calificar_respuestaConOpcionDeOtraPregunta_rechazaElBloque() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        calificador.calificar(
                                "VOC1",
                                List.of(new RespuestaSeleccionBfa(1L, 21L)),
                                Map.of(1L, 11L, 2L, 21L),
                                opcionesPara(2, 5)));
    }

    @Test
    void calificar_respuestaAPreguntaAjena_rechazaElBloque() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        calificador.calificar(
                                "CV",
                                List.of(new RespuestaSeleccionBfa(9L, 91L)),
                                Map.of(1L, 11L),
                                opcionesPara(1, 3)));
    }

    @Test
    void calificar_claveVacia_noProduceUnPuntajeCero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calificador.calificar("VOC2", List.of(), Map.of(), List.of()));
    }

    @Test
    void calificar_claveConOpcionAjena_seRechaza() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calificador.calificar("CV", List.of(), Map.of(1L, 99L), opcionesPara(1, 3)));
    }

    @Test
    void calificar_opcionesDuplicadas_seRechazan() {
        var opciones = new ArrayList<>(opcionesPara(1, 3));
        opciones.add(new OpcionRespuesta(19L, 1L, "A", 4, "Alternativa ficticia", null));
        assertThrows(
                IllegalArgumentException.class,
                () -> calificador.calificar("CV", List.of(), Map.of(1L, 11L), opciones));
    }

    private static int alternativasDe(String subprueba) {
        return "CV".equals(subprueba) ? 3 : 5;
    }

    // Pregunta p tiene opciones p1..pN con letras A.. (id = p*10 + indice)
    private static List<OpcionRespuesta> opcionesPara(int preguntas, int alternativas) {
        var opciones = new ArrayList<OpcionRespuesta>();
        for (long pregunta = 1; pregunta <= preguntas; pregunta++) {
            for (int indice = 1; indice <= alternativas; indice++) {
                opciones.add(
                        new OpcionRespuesta(
                                pregunta * 10 + indice,
                                pregunta,
                                String.valueOf((char) ('A' + indice - 1)),
                                indice,
                                "Alternativa ficticia",
                                null));
            }
        }
        return opciones;
    }
}
