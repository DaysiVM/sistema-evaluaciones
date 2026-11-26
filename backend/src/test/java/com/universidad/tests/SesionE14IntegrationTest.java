package com.universidad.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.tests.domain.model.*;
import com.universidad.tests.domain.port.*;
import com.universidad.tests.domain.repository.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** E14 real con cuenta y sesión persistidas, sin dobles y sin imprimir secretos. */
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
class SesionE14IntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private CuentaRepository cuentas;
    @Autowired private SesionRepository sesiones;
    @Autowired private HashContrasenasPort hash;
    @Autowired private TokensSesionPort tokens;
    private static final String CLAVE = "solo-fixture-sintetico-123";

    private String cuenta() {
        String correo = "e14-" + UUID.randomUUID() + "@ejemplo.test";
        cuentas.guardar(
                new Cuenta(
                        null,
                        correo,
                        hash.crear(CLAVE.toCharArray()),
                        "Persona",
                        "Ficticia",
                        EstadoCuenta.ACTIVA,
                        Set.of(Rol.SUJETO)));
        return correo;
    }

    @Test
    void loginDevuelveVencimientoRealYLogoutRevoca() throws Exception {
        String correo = cuenta();
        var respuesta =
                mvc.perform(
                                post("/api/v1/auth/sesion")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                json.writeValueAsString(
                                                        Map.of(
                                                                "correo",
                                                                correo,
                                                                "contrasena",
                                                                CLAVE))))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.rolActivo").value("SUJETO"))
                        .andExpect(jsonPath("$.permisos").isArray())
                        .andReturn();
        var cuerpo = json.readTree(respuesta.getResponse().getContentAsString());
        String token = cuerpo.path("token").asText();
        assertThat(token.isBlank()).isFalse();
        Instant expira = Instant.parse(cuerpo.path("expiraEn").asText());
        var guardada = sesiones.buscarPorResumenToken(tokens.resumirSiValido(token)).orElseThrow();
        assertThat(expira).isEqualTo(guardada.getExpiraEn());
        assertThat(Duration.between(guardada.getAbiertaEn(), expira))
                .isEqualTo(Duration.ofHours(2));
        mvc.perform(get("/api/v1/tests").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/v1/auth/sesion").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mvc.perform(get("/api/v1/tests").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void credencialesIncorrectasNoDevuelvenSesion() throws Exception {
        String correo = cuenta();
        mvc.perform(
                        post("/api/v1/auth/sesion")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json.writeValueAsString(
                                                Map.of(
                                                        "correo",
                                                        correo,
                                                        "contrasena",
                                                        "invalida"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").doesNotExist());
    }
}
