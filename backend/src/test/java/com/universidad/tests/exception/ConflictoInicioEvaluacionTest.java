package com.universidad.tests.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.controller.InicioEvaluacionController;
import com.universidad.tests.dto.request.IniciarEvaluacionRequest;
import com.universidad.tests.dto.response.EvaluacionResponse;
import java.sql.SQLException;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class ConflictoInicioEvaluacionTest {

    private static final String MENSAJE_GENERICO =
            "Ocurrió un error inesperado. Intente de nuevo más tarde.";

    @Test
    void carreraPorEvaluacionAbiertaDevuelve409ConElErrorDocumentado() throws Exception {
        mvc("23505", "uq_evaluacion_en_curso")
                .perform(solicitud())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("EVALUACION_EN_CURSO"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.ruta").value("/api/v1/evaluaciones"));
    }

    @Test
    void otraRestriccionUnicaNoSeDisfrazaComoEvaluacionEnCurso() throws Exception {
        mvc("23505", "otra_restriccion")
                .perform(solicitud())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_GENERICO));
    }

    @Test
    void unaClaveForaneaNoSeDisfrazaComoEvaluacionEnCurso() throws Exception {
        mvc("23503", "uq_evaluacion_en_curso")
                .perform(solicitud())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"));
    }

    private static MockHttpServletRequestBuilder solicitud() {
        return post("/api/v1/evaluaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sujetoId\":1,\"periodoId\":1,\"versionFormularioId\":1}");
    }

    private static MockMvc mvc(String estadoSql, String restriccion) {
        StaticListableBeanFactory fabrica = new StaticListableBeanFactory();
        fabrica.addBean("reloj", Clock.systemUTC());
        ManejadorErroresInicioEvaluacion manejador =
                new ManejadorErroresInicioEvaluacion(fabrica.getBeanProvider(Clock.class));
        DataIntegrityViolationException error =
                new DataIntegrityViolationException(
                        "Restriccion ficticia", new SQLException(restriccion, estadoSql));
        return MockMvcBuilders.standaloneSetup(new InicioQueFalla(error))
                .setControllerAdvice(manejador)
                .build();
    }

    @RestController
    @Profile("prueba-conflicto-http")
    @RequestMapping("/api/v1/evaluaciones")
    public static class InicioQueFalla extends InicioEvaluacionController {
        private final DataIntegrityViolationException error;

        public InicioQueFalla(DataIntegrityViolationException error) {
            super(null, null);
            this.error = error;
        }

        @Override
        @PostMapping
        public ResponseEntity<EvaluacionResponse> iniciar(
                @RequestBody IniciarEvaluacionRequest request) {
            throw error;
        }
    }
}
