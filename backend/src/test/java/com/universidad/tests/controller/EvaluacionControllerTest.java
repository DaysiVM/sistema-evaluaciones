package com.universidad.tests.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.ConsultaEvaluacionService;
import com.universidad.tests.application.service.MotorSubpruebasService;
import com.universidad.tests.domain.exception.EvaluacionNoEncontradaException;
import com.universidad.tests.domain.exception.MotorSubpruebasException;
import com.universidad.tests.domain.exception.MotorSubpruebasException.Codigo;
import com.universidad.tests.domain.exception.NoAutenticadoException;
import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.dto.response.AvanceSubpruebaResponse;
import com.universidad.tests.dto.response.EvaluacionResponse;
import com.universidad.tests.dto.response.PreguntasSubpruebaResponse;
import com.universidad.tests.dto.response.SubpruebaActivaResponse;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EvaluacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class EvaluacionControllerTest {

    @Autowired private MockMvc mvc;

    @MockitoBean private ConsultaEvaluacionService consultaService;

    @MockitoBean private MotorSubpruebasService motor;

    @MockitoBean
    private com.universidad.tests.application.service.FinalizacionEvaluacionService finalizacion;

    @MockitoBean private AutenticacionService autenticacion;

    private static final String SUBPRUEBA = "/api/v1/evaluaciones/15/subpruebas/SUB";

    @Test
    void e5SerializaElRelojOficial() throws Exception {
        Instant inicio = Instant.parse("2026-11-24T19:00:00Z");
        when(motor.iniciar(15L, "SUB"))
                .thenReturn(
                        new SubpruebaActivaResponse(
                                "SUB", "ACTIVA", inicio, inicio.plusSeconds(60), 60, null));
        mvc.perform(post(SUBPRUEBA + "/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inicio").value(inicio.toString()))
                .andExpect(jsonPath("$.segundosRestantes").value(60))
                .andExpect(jsonPath("$.evaluacionId").doesNotHaveJsonPath());
    }

    @Test
    void e6ExponeRecursosYRespuestaSinClaves() throws Exception {
        when(motor.consultarPreguntas(15L, "SUB"))
                .thenReturn(
                        new PreguntasSubpruebaResponse(
                                "SUB",
                                12,
                                List.of(
                                        new PreguntasSubpruebaResponse.Pregunta(
                                                120L,
                                                1,
                                                "Ejemplo",
                                                "/v1/p.png",
                                                List.of(
                                                        new PreguntasSubpruebaResponse.Opcion(
                                                                "A", "Ejemplo", "/v1/a.png")),
                                                "A")),
                                null));
        mvc.perform(get(SUBPRUEBA + "/preguntas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preguntas[0].recursoUrl").value("/v1/p.png"))
                .andExpect(jsonPath("$.preguntas[0].opciones[0].recursoUrl").value("/v1/a.png"))
                .andExpect(jsonPath("$.preguntas[0].respuestaActual").value("A"))
                .andExpect(jsonPath("$..esCorrecta").isEmpty())
                .andExpect(jsonPath("$..clave").isEmpty())
                .andExpect(jsonPath("$..puntuacion").isEmpty())
                .andExpect(jsonPath("$.palabras").doesNotHaveJsonPath());
    }

    @Test
    void e7Responde400ConElFormatoComunParaJsonMalFormado() throws Exception {
        mvc.perform(
                        put(SUBPRUEBA + "/respuestas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
                .andExpect(jsonPath("$.detalles[0].campo").value("cuerpo"));
    }

    @Test
    void e7RechazaTiposQueJacksonConvertiriaAutomaticamente() throws Exception {
        for (String json :
                List.of(
                        """
                {"respuestas":[{"preguntaId":120.5,"opcion":"A"}]}
                """,
                        """
                {"respuestas":[{"preguntaId":120,"opcion":1}]}
                """,
                        """
                {"palabras":[{"ordenEntrada":"1","texto":"ficticia"}]}
                """)) {
            mvc.perform(
                            put(SUBPRUEBA + "/respuestas")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));
        }
    }

    @Test
    void e7SerializaAvanceYUsaLosCodigosDeConflictoYValidacion() throws Exception {
        when(motor.registrarRespuestas(eq(15L), eq("SUB"), any()))
                .thenReturn(new AvanceSubpruebaResponse("SUB", 1, 2, 25))
                .thenThrow(new MotorSubpruebasException(Codigo.TIEMPO_AGOTADO, "Respuesta tardía"))
                .thenThrow(new MotorSubpruebasException(Codigo.RESPUESTA_INVALIDA, "Opción ajena"));
        var solicitud =
                put(SUBPRUEBA + "/respuestas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                        {"respuestas":[{"preguntaId":120,"opcion":"A"}]}
                        """);
        mvc.perform(solicitud)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("SUB"))
                .andExpect(jsonPath("$.registradas").value(1))
                .andExpect(jsonPath("$.totalRespondidas").value(2))
                .andExpect(jsonPath("$.segundosRestantes").value(25))
                .andExpect(jsonPath("$.evaluacionId").doesNotHaveJsonPath());
        mvc.perform(solicitud)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("TIEMPO_AGOTADO"));
        mvc.perform(solicitud)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("RESPUESTA_INVALIDA"));
    }

    @Test
    void e6Responde401CuandoFaltaUnaSesionVerificada() throws Exception {
        when(motor.consultarPreguntas(15L, "SUB")).thenThrow(new NoAutenticadoException());
        mvc.perform(get(SUBPRUEBA + "/preguntas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }

    @Test
    void e5Responde403CuandoElPuertoDeniegaElAcceso() throws Exception {
        when(motor.iniciar(15L, "SUB")).thenThrow(new SinPermisoException());
        mvc.perform(post(SUBPRUEBA + "/iniciar"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));
    }

    @Test
    void e4DevuelveElEstadoComoPideElContrato() throws Exception {
        Instant fin = Instant.parse("2026-10-30T15:42:10Z");
        when(consultaService.consultar(15L))
                .thenReturn(
                        new EvaluacionResponse(
                                15L,
                                1L,
                                25L,
                                3L,
                                10L,
                                "EN_CURSO",
                                Instant.parse("2026-10-30T15:30:00Z"),
                                null,
                                new EvaluacionResponse.SubpruebaActiva("VOC2", fin, 214),
                                List.of(
                                        new EvaluacionResponse.SubpruebaEstado(
                                                "VOC1", "CERRADA", fin),
                                        new EvaluacionResponse.SubpruebaEstado(
                                                "VOC2", "ACTIVA", null))));

        mvc.perform(get("/api/v1/evaluaciones/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.testId").value(1))
                .andExpect(jsonPath("$.sujetoId").value(25))
                .andExpect(jsonPath("$.periodoId").value(3))
                .andExpect(jsonPath("$.versionFormularioId").value(10))
                .andExpect(jsonPath("$.estado").value("EN_CURSO"))
                .andExpect(jsonPath("$.fechaFin").isEmpty())
                .andExpect(jsonPath("$.subpruebaActiva.codigo").value("VOC2"))
                .andExpect(jsonPath("$.subpruebaActiva.segundosRestantes").value(214))
                .andExpect(jsonPath("$.subpruebas[0].estado").value("CERRADA"))
                .andExpect(jsonPath("$.subpruebas[1].cerradaEn").isEmpty());
    }

    @Test
    void e4Responde404ConElFormatoComunDeError() throws Exception {
        when(consultaService.consultar(99L)).thenThrow(new EvaluacionNoEncontradaException(99L));

        mvc.perform(get("/api/v1/evaluaciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.codigo").value("EVALUACION_NO_ENCONTRADA"))
                .andExpect(jsonPath("$.ruta").value("/api/v1/evaluaciones/99"));
    }
}
