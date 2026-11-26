package com.universidad.tests.application.evaluator.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.OpcionRespuesta;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.model.VersionFormulario;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

// BFA-BE-03: catálogo ficticio; no contiene ítems ni claves reales del BFA
class BloqueVerbalVersionadoTest {

    private static final long TEST = 1L;
    private static final long VERSION = 5L;
    private static final long SUBPRUEBA = 7L;
    private static final long CONFIGURACION = 9L;

    private final VersionFormulario version =
            new VersionFormulario(VERSION, TEST, "V-FICT", "Version ficticia", "FUENTE-FICTICIA");
    private final ConfiguracionSubprueba configuracion =
            configuracion(VERSION, SUBPRUEBA, TipoRespuesta.SELECCION);
    private final Subprueba cv = new Subprueba(SUBPRUEBA, TEST, "CV", "Proverbios", null, null);

    @Test
    void desde_bloqueValido_conservaVersionCodigoYPreguntas() {
        var bloque =
                BloqueVerbalVersionado.desde(
                        version, configuracion, cv, List.of(pregunta(101), pregunta(102)));

        assertEquals(VERSION, bloque.idVersionFormulario());
        assertEquals("CV", bloque.codigoSubprueba());
        assertEquals(Set.of(101L, 102L), bloque.idsPreguntas());
    }

    @Test
    void calificarVersionado_conClaveExacta_cuentaElBloque() {
        var bloque =
                BloqueVerbalVersionado.desde(
                        version, configuracion, cv, List.of(pregunta(101), pregunta(102)));
        var opciones =
                List.of(
                        opcion(1011, 101, "A"),
                        opcion(1012, 101, "B"),
                        opcion(1013, 101, "C"),
                        opcion(1021, 102, "A"),
                        opcion(1022, 102, "B"),
                        opcion(1023, 102, "C"));

        ConteoBloqueBfa conteo =
                new CalificadorVerbalBfa()
                        .calificarVersionado(
                                bloque,
                                List.of(new RespuestaSeleccionBfa(101L, 1013L)),
                                Map.of(101L, 1013L, 102L, 1021L),
                                opciones);

        assertEquals(new ConteoBloqueBfa(1, 0, 1), conteo);
    }

    @Test
    void calificarVersionado_claveQueNoCubreElBloque_seRechaza() {
        var bloque =
                BloqueVerbalVersionado.desde(
                        version, configuracion, cv, List.of(pregunta(101), pregunta(102)));

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new CalificadorVerbalBfa()
                                .calificarVersionado(
                                        bloque, List.of(), Map.of(101L, 1011L), List.of()));
    }

    @Test
    void desde_configuracionDeOtraVersion_seRechaza() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        BloqueVerbalVersionado.desde(
                                version,
                                configuracion(6L, SUBPRUEBA, TipoRespuesta.SELECCION),
                                cv,
                                List.of(pregunta(101))));
    }

    @Test
    void desde_subpruebaDeOtraFamilia_seRechaza() {
        var numerica = new Subprueba(SUBPRUEBA, TEST, "N1-A", "Numerico", null, null);
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        BloqueVerbalVersionado.desde(
                                version, configuracion, numerica, List.of(pregunta(101))));
    }

    @Test
    void desde_configuracionDeTextoLibre_seRechaza() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        BloqueVerbalVersionado.desde(
                                version,
                                configuracion(VERSION, SUBPRUEBA, TipoRespuesta.TEXTO_LIBRE),
                                cv,
                                List.of(pregunta(101))));
    }

    @Test
    void desde_preguntaDeOtraConfiguracionODuplicada_seRechaza() {
        var ajena = new Pregunta(103L, 99L, "3", 3, "Enunciado ficticio", null);
        assertThrows(
                IllegalArgumentException.class,
                () -> BloqueVerbalVersionado.desde(version, configuracion, cv, List.of(ajena)));
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        BloqueVerbalVersionado.desde(
                                version, configuracion, cv, List.of(pregunta(101), pregunta(101))));
    }

    @Test
    void desde_sinPreguntasPuntuables_seRechaza() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BloqueVerbalVersionado.desde(version, configuracion, cv, List.of()));
    }

    private static ConfiguracionSubprueba configuracion(
            long version, long subprueba, TipoRespuesta tipo) {
        Integer maximo = tipo == TipoRespuesta.SELECCION ? null : 10;
        return new ConfiguracionSubprueba(
                CONFIGURACION,
                version,
                subprueba,
                1,
                600,
                tipo,
                "INSTRUCCIONES-FICTICIAS",
                null,
                maximo);
    }

    private static Pregunta pregunta(long id) {
        return new Pregunta(id, CONFIGURACION, String.valueOf(id), 1, "Enunciado ficticio", null);
    }

    private static OpcionRespuesta opcion(long id, long pregunta, String letra) {
        return new OpcionRespuesta(id, pregunta, letra, 1, "Alternativa ficticia", null);
    }
}
