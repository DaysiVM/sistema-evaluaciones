package com.universidad.tests.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.universidad.tests.controller.SujetoController;
import com.universidad.tests.dto.request.SujetoRequest;
import com.universidad.tests.dto.response.SujetoResponse;
import java.sql.SQLException;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

class ConflictoSujetoTest {
    @Test
    void duplicadoConcurrenteDevuelve409() throws Exception {
        comprobar("23505", "uq_sujeto_codigo_institucional", 409, "SUJETO_DUPLICADO");
    }

    @Test
    void otraRestriccionUnicaSigueSiendo500() throws Exception {
        comprobar("23505", "otra_restriccion", 500, "ERROR_INTERNO");
    }

    @Test
    void otroEstadoSqlNoSeDisfrazaComoDuplicado() throws Exception {
        comprobar("23514", "uq_sujeto_codigo_institucional", 500, "ERROR_INTERNO");
    }

    private void comprobar(String estado, String restriccion, int http, String codigo)
            throws Exception {
        StaticListableBeanFactory fabrica = new StaticListableBeanFactory();
        fabrica.addBean("reloj", Clock.systemUTC());
        var manejador = new ManejadorErroresInicioEvaluacion(fabrica.getBeanProvider(Clock.class));
        MockMvc mvc =
                MockMvcBuilders.standaloneSetup(
                                new SujetoQueFalla(
                                        new DataIntegrityViolationException(
                                                "Prueba sintetica",
                                                new SQLException(restriccion, estado))))
                        .setControllerAdvice(manejador)
                        .build();
        mvc.perform(
                        post("/api/v1/sujetos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"identificadorInstitucional\":\"FICTICIO-ERROR\",\"nombres\":\"Persona"
                                                + " Ficticia\",\"apellidos\":\"Prueba\"}"))
                .andExpect(status().is(http))
                .andExpect(jsonPath("$.codigo").value(codigo));
    }

    @RestController
    @RequestMapping("/api/v1/sujetos")
    @Profile("solo-error-sujeto-test")
    public static class SujetoQueFalla extends SujetoController {
        private final DataIntegrityViolationException error;

        SujetoQueFalla(DataIntegrityViolationException error) {
            super(null, null, null, null);
            this.error = error;
        }

        @Override
        @PostMapping
        public ResponseEntity<SujetoResponse> registrar(@RequestBody SujetoRequest request) {
            throw error;
        }
    }
}
