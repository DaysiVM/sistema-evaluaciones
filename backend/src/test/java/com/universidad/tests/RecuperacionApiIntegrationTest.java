package com.universidad.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.domain.model.*;
import com.universidad.tests.domain.port.*;
import com.universidad.tests.domain.repository.CuentaRepository;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** HTTP, hashing y PostgreSQL reales. Solo el transporte de correo es un receptor en memoria. */
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class RecuperacionApiIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private CuentaRepository cuentas;
    @Autowired private HashContrasenasPort hash;
    @Autowired private TokensSesionPort tokens;
    @Autowired private AutenticacionService autenticacion;
    @Autowired private JdbcTemplate jdbc;
    @MockitoBean private CorreoRecuperacionPort correo;
    private final Map<String, String> entregados = new ConcurrentHashMap<>();
    private static final String ANTERIOR = "clave-ficticia-anterior";
    private static final String NUEVA = "clave-ficticia-nueva";

    @BeforeEach
    void transporte() {
        entregados.clear();
        when(correo.disponible()).thenReturn(true);
        doAnswer(
                        inv -> {
                            entregados.put(inv.getArgument(0), inv.getArgument(1));
                            return null;
                        })
                .when(correo)
                .enviar(anyString(), anyString());
    }

    private String cuenta(EstadoCuenta estado) {
        String email = "rec-" + UUID.randomUUID() + "@ejemplo.test";
        cuentas.guardar(
                new Cuenta(
                        null,
                        email,
                        hash.crear(ANTERIOR.toCharArray()),
                        "Persona",
                        "Ficticia",
                        estado,
                        Set.of(Rol.SUJETO)));
        return email;
    }

    private MockHttpServletRequestBuilder pedir(String email) throws Exception {
        return post("/api/v1/auth/recuperacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("correo", email)));
    }

    private MockHttpServletRequestBuilder confirmar(String token, String clave) throws Exception {
        return post("/api/v1/auth/recuperacion/confirmar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("token", token, "nuevaContrasena", clave)));
    }

    private String solicitar(String email) throws Exception {
        mvc.perform(pedir(email)).andExpect(status().isAccepted()).andExpect(content().string(""));
        return entregados.get(email);
    }

    @Test
    void respuestasNoEnumeranCuentasYNuncaCreanBearer() throws Exception {
        String activa = cuenta(EstadoCuenta.ACTIVA), inactiva = cuenta(EstadoCuenta.INHABILITADA);
        String token = solicitar(activa);
        solicitar(inactiva);
        solicitar("ausente-" + UUID.randomUUID() + "@ejemplo.test");
        assertThat(entregados.size()).isEqualTo(1);
        assertThat(
                        jdbc.queryForObject(
                                "select count(*) from recuperacion_cuenta where resumen_token=?",
                                Long.class,
                                tokens.resumirSiValido(token)))
                .isEqualTo(1);
        assertThat(
                        jdbc.queryForObject(
                                "select extract(epoch from (expira_en-creada_en)) from recuperacion_cuenta where resumen_token=?",
                                Long.class,
                                tokens.resumirSiValido(token)))
                .isEqualTo(900);
        mvc.perform(get("/api/v1/tests").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cambiaClaveUnaVezYRevocaSesiones() throws Exception {
        String email = cuenta(EstadoCuenta.ACTIVA);
        String bearer = autenticacion.abrirSesion(email, ANTERIOR.toCharArray()).token();
        String token = solicitar(email);
        mvc.perform(confirmar(token, NUEVA))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mvc.perform(confirmar(token, NUEVA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("TOKEN_INVALIDO"));
        assertThat(autenticacion.validar(bearer).isPresent()).isFalse();
        assertThat(
                        hash.verificar(
                                NUEVA.toCharArray(),
                                cuentas.buscarPorCorreo(email)
                                        .orElseThrow()
                                        .getResumenContrasena()))
                .isTrue();
        assertThat(
                        hash.verificar(
                                ANTERIOR.toCharArray(),
                                cuentas.buscarPorCorreo(email)
                                        .orElseThrow()
                                        .getResumenContrasena()))
                .isFalse();
    }

    @Test
    void nuevaSolicitudInvalidaElTokenAnterior() throws Exception {
        String email = cuenta(EstadoCuenta.ACTIVA);
        String anterior = solicitar(email), siguiente = solicitar(email);
        mvc.perform(confirmar(anterior, NUEVA)).andExpect(status().isBadRequest());
        mvc.perform(confirmar(siguiente, NUEVA)).andExpect(status().isNoContent());
    }

    @Test
    void vencidoNoCambiaLaClave() throws Exception {
        String email = cuenta(EstadoCuenta.ACTIVA), token = solicitar(email);
        jdbc.update(
                "update recuperacion_cuenta set creada_en=now()-interval '20 minutes',expira_en=now()-interval '5 minutes' where resumen_token=?",
                tokens.resumirSiValido(token));
        mvc.perform(confirmar(token, NUEVA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("TOKEN_INVALIDO"));
        assertThat(
                        hash.verificar(
                                ANTERIOR.toCharArray(),
                                cuentas.buscarPorCorreo(email)
                                        .orElseThrow()
                                        .getResumenContrasena()))
                .isTrue();
    }

    @Test
    void claveCortaNoConsumeElToken() throws Exception {
        String token = solicitar(cuenta(EstadoCuenta.ACTIVA));
        mvc.perform(confirmar(token, "corta"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));
        mvc.perform(confirmar(token, NUEVA)).andExpect(status().isNoContent());
    }

    @Test
    void dosConfirmacionesConcurrentesSoloConsumenUnaVez() throws Exception {
        String token = solicitar(cuenta(EstadoCuenta.ACTIVA));
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Callable<Integer> intento =
                    () ->
                            mvc.perform(confirmar(token, NUEVA))
                                    .andReturn()
                                    .getResponse()
                                    .getStatus();
            var a = pool.submit(intento);
            var b = pool.submit(intento);
            assertThat(List.of(a.get(15, TimeUnit.SECONDS), b.get(15, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(204, 400);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void correoNoConfiguradoResponde503TambienParaCuentaAusente() throws Exception {
        when(correo.disponible()).thenReturn(false);
        mvc.perform(pedir(cuenta(EstadoCuenta.ACTIVA)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.codigo").value("CORREO_NO_DISPONIBLE"));
        mvc.perform(pedir("ausente-" + UUID.randomUUID() + "@ejemplo.test"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void falloDeEnvioRevierteLaSolicitud() throws Exception {
        String email = cuenta(EstadoCuenta.ACTIVA);
        doThrow(new IllegalStateException("Fallo simulado"))
                .when(correo)
                .enviar(anyString(), anyString());
        mvc.perform(pedir(email)).andExpect(status().isServiceUnavailable());
        Long id = cuentas.buscarPorCorreo(email).orElseThrow().getIdCuenta();
        assertThat(
                        jdbc.queryForObject(
                                "select count(*) from recuperacion_cuenta where id_cuenta=?",
                                Long.class,
                                id))
                .isZero();
    }
}
