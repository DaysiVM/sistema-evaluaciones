package com.universidad.tests.resultados;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.application.usecase.resultados.ConsultarResultadosUseCase;
import com.universidad.tests.application.usecase.resultados.ResultadoVista;
import com.universidad.tests.application.usecase.resultados.ResultadoVista.FactorVista;
import com.universidad.tests.controller.resultados.ResultadosController;
import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException;
import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException.Motivo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** GEN-05: contrato HTTP de E9 (forma de la respuesta y de los errores). */
@WebMvcTest(ResultadosController.class)
class ResultadosControllerTest {

    private static final String RUTA = "/api/v1/evaluaciones/{id}/resultados";

    @Autowired private MockMvc mvc;
    @MockitoBean private ConsultarResultadosUseCase consultarResultados;

    @Test
    void devuelveElResultadoConTodosLosCamposDelContrato() throws Exception {
        when(consultarResultados.consultar(16L))
                .thenReturn(
                        new ResultadoVista(
                                16L,
                                101L,
                                "DISPONIBLE",
                                3L,
                                501L,
                                1,
                                "BFA-REGLAS-v1",
                                11L,
                                21L,
                                List.of(
                                        new FactorVista(
                                                "VOC1",
                                                23,
                                                "CALCULADO",
                                                60,
                                                7L,
                                                "CONVERTIDO",
                                                null),
                                        new FactorVista(
                                                "FV",
                                                null,
                                                "NO_CALCULABLE",
                                                null,
                                                null,
                                                "NO_APLICA",
                                                "Aplicación parcial"))));

        mvc.perform(get(RUTA, 16))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.evaluacionId").value(16))
                .andExpect(jsonPath("$.resultadoId").value(101))
                .andExpect(jsonPath("$.estadoResultado").value("DISPONIBLE"))
                .andExpect(jsonPath("$.versionFormularioId").value(3))
                .andExpect(jsonPath("$.calificacionId").value(501))
                .andExpect(jsonPath("$.revision").value(1))
                .andExpect(jsonPath("$.versionReglas").value("BFA-REGLAS-v1"))
                .andExpect(jsonPath("$.idClave").value(11))
                .andExpect(jsonPath("$.idBaremo").value(21))
                .andExpect(jsonPath("$.factores", hasSize(2)))
                .andExpect(jsonPath("$.factores[0].codigo").value("VOC1"))
                .andExpect(jsonPath("$.factores[0].puntuacionDirecta").value(23))
                .andExpect(jsonPath("$.factores[0].percentil").value(60))
                .andExpect(jsonPath("$.factores[0].idRango").value(7))
                .andExpect(jsonPath("$.factores[0].estadoNormativo").value("CONVERTIDO"))
                .andExpect(jsonPath("$.factores[1].puntuacionDirecta").isEmpty())
                .andExpect(jsonPath("$.factores[1].percentil").isEmpty())
                .andExpect(jsonPath("$.factores[1].estadoCalculo").value("NO_CALCULABLE"))
                .andExpect(jsonPath("$.factores[1].motivo").value("Aplicación parcial"));
    }

    @Test
    void sinRevisionPublicadaEnviaNulosYFactoresVacio() throws Exception {
        when(consultarResultados.consultar(17L))
                .thenReturn(
                        new ResultadoVista(
                                17L,
                                102L,
                                "PENDIENTE_VALIDACION",
                                3L,
                                null,
                                null,
                                null,
                                null,
                                null,
                                List.of()));

        mvc.perform(get(RUTA, 17))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoResultado").value("PENDIENTE_VALIDACION"))
                .andExpect(jsonPath("$.calificacionId").isEmpty())
                .andExpect(jsonPath("$.revision").isEmpty())
                .andExpect(jsonPath("$.factores", hasSize(0)));
    }

    @Test
    void laRespuestaNoIncluyeContenidoDeClave() throws Exception {
        when(consultarResultados.consultar(16L))
                .thenReturn(
                        new ResultadoVista(
                                16L, 101L, "DISPONIBLE", 3L, 501L, 1, "R", 11L, 21L, List.of()));

        mvc.perform(get(RUTA, 16))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clave").doesNotExist())
                .andExpect(jsonPath("$.respuestasCorrectas").doesNotExist())
                .andExpect(content().string(not(matchesPattern("(?s).*\"opcionCorrecta\".*"))));
    }

    @Test
    void sinPermisoResponde403ConElFormatoComun() throws Exception {
        when(consultarResultados.consultar(16L)).thenThrow(ConsultaResultadoException.sinPermiso());

        mvc.perform(get(RUTA, 16))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"))
                .andExpect(jsonPath("$.mensaje").isNotEmpty())
                .andExpect(jsonPath("$.ruta").value("/api/v1/evaluaciones/16/resultados"))
                .andExpect(
                        jsonPath("$.timestamp")
                                .value(
                                        matchesPattern(
                                                "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z")))
                .andExpect(jsonPath("$.detalles").doesNotExist());
    }

    @Test
    void noAutenticadoResponde401() throws Exception {
        when(consultarResultados.consultar(16L))
                .thenThrow(new ConsultaResultadoException(Motivo.NO_AUTENTICADO, "Sin sesión."));

        mvc.perform(get(RUTA, 16))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }

    @Test
    void securityExceptionDeLosAdaptadoresProvisionalesResponde403() throws Exception {
        when(consultarResultados.consultar(16L)).thenThrow(new SecurityException("interno"));

        mvc.perform(get(RUTA, 16))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"))
                .andExpect(jsonPath("$.mensaje").value(not("interno")));
    }

    @Test
    void evaluacionInexistenteResponde404() throws Exception {
        when(consultarResultados.consultar(999L))
                .thenThrow(
                        new ConsultaResultadoException(
                                Motivo.EVALUACION_NO_ENCONTRADA, "No existe la evaluación 999."));

        mvc.perform(get(RUTA, 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("EVALUACION_NO_ENCONTRADA"));
    }

    @Test
    void evaluacionNoFinalizadaResponde409() throws Exception {
        when(consultarResultados.consultar(15L))
                .thenThrow(
                        new ConsultaResultadoException(
                                Motivo.EVALUACION_NO_FINALIZADA, "Aún no ha finalizado."));

        mvc.perform(get(RUTA, 15))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("EVALUACION_NO_FINALIZADA"));
    }

    @Test
    void idNoNumericoResponde400ConDetalle() throws Exception {
        mvc.perform(get("/api/v1/evaluaciones/abc/resultados"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
                .andExpect(jsonPath("$.detalles[0].campo").value("evaluacionId"));
        verifyNoInteractions(consultarResultados);
    }

    @Test
    void resultadoInconsistenteResponde500SinDetallesInternos() throws Exception {
        when(consultarResultados.consultar(16L))
                .thenThrow(
                        new ConsultaResultadoException(
                                Motivo.RESULTADO_INCONSISTENTE,
                                "Resultado inconsistente: resultado DISPONIBLE sin revisión"));

        mvc.perform(get(RUTA, 16))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(not(matchesPattern("(?s).*(DISPONIBLE|revisión).*"))));
    }

    @Test
    void errorNoPrevistoResponde500SinDetallesInternos() throws Exception {
        when(consultarResultados.consultar(16L))
                .thenThrow(new IllegalStateException("tabla resultado no existe"));

        mvc.perform(get(RUTA, 16))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
                .andExpect(jsonPath("$.mensaje").value(not(matchesPattern("(?s).*tabla.*"))));
    }
}
