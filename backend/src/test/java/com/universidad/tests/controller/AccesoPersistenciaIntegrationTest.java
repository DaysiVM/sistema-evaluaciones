package com.universidad.tests.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.tests.domain.model.Cuenta;
import com.universidad.tests.domain.model.EstadoCuenta;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.model.Sesion;
import com.universidad.tests.domain.port.HashContrasenasPort;
import com.universidad.tests.domain.port.TokensSesionPort;
import com.universidad.tests.domain.repository.CuentaRepository;
import com.universidad.tests.domain.repository.SesionRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccesoPersistenciaIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private CuentaRepository cuentas;
    @Autowired private SesionRepository sesiones;
    @Autowired private HashContrasenasPort contrasenas;
    @Autowired private TokensSesionPort tokens;
    @Autowired private JdbcTemplate jdbc;

    @Test
    void registrar_persisteHashYRechazaCorreoDuplicadoSinCambiarLaCuenta() throws Exception {
        String correo = correoFicticio();
        String cuerpo = registro(correo, "SUJETO");

        mvc.perform(
                        post("/api/v1/auth/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("SUJETO"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        Cuenta creada = cuentas.buscarPorCorreo(correo).orElseThrow();
        assertThat(creada.getResumenContrasena()).isNotEqualTo("credencial ficticia");
        char[] entrada = "credencial ficticia".toCharArray();
        try {
            assertThat(contrasenas.verificar(entrada, creada.getResumenContrasena())).isTrue();
        } finally {
            Arrays.fill(entrada, '\0');
        }

        mvc.perform(
                        post("/api/v1/auth/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registro(correo.toUpperCase(Locale.ROOT), "SUJETO")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("CUENTA_EXISTENTE"));

        Cuenta conservada = cuentas.buscarPorCorreo(correo).orElseThrow();
        assertThat(conservada.getIdCuenta()).isEqualTo(creada.getIdCuenta());
        assertThat(conservada.getResumenContrasena()).isEqualTo(creada.getResumenContrasena());
    }

    @Test
    void registrar_sinPermisoNoCreaCuentaPrivilegiada() throws Exception {
        String correo = correoFicticio();

        mvc.perform(
                        post("/api/v1/auth/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registro(correo, "COORDINADOR")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("ROL_NO_AUTORIZADO"));

        assertThat(cuentas.buscarPorCorreo(correo)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"contrasena", "nombres", "apellidos", "rol"})
    void registrar_rechazaCamposJsonQueNoSonTexto(String campo) throws Exception {
        String correo = correoFicticio();
        Map<String, Object> solicitud =
                new HashMap<>(
                        Map.of(
                                "correo",
                                correo,
                                "contrasena",
                                "credencial ficticia",
                                "nombres",
                                "Persona",
                                "apellidos",
                                "Ficticia",
                                "rol",
                                "SUJETO"));
        solicitud.put(campo, 42);

        mvc.perform(
                        post("/api/v1/auth/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(solicitud)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));

        assertThat(cuentas.buscarPorCorreo(correo)).isEmpty();
    }

    @Test
    void cerrar_revocaSesionPersistidaYRechazaReutilizarSuBearer() throws Exception {
        Cuenta cuenta =
                cuentas.guardar(
                        new Cuenta(
                                null,
                                correoFicticio(),
                                "resumen ficticio",
                                "Persona",
                                "Ficticia",
                                EstadoCuenta.ACTIVA,
                                Set.of(Rol.SUJETO)));
        String token = tokens.generar();
        String resumen = tokens.resumirSiValido(token);
        Instant ahora = Instant.now();
        // Fechas de un fixture: no establecen una politica de vigencia del producto.
        sesiones.guardar(
                new Sesion(
                        null,
                        cuenta.getIdCuenta(),
                        resumen,
                        Rol.SUJETO,
                        ahora.minusSeconds(1),
                        ahora.plusSeconds(60),
                        null));

        mvc.perform(
                        delete("/api/v1/auth/sesion")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertThat(
                        jdbc.queryForObject(
                                "SELECT cerrada_en IS NOT NULL FROM sesion WHERE resumen_token = ?",
                                Boolean.class,
                                resumen))
                .isTrue();
        mvc.perform(
                        delete("/api/v1/auth/sesion")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }

    private String correoFicticio() {
        return "prueba-" + UUID.randomUUID() + "@example.invalid";
    }

    private String registro(String correo, String rol) throws Exception {
        return json.writeValueAsString(
                Map.of(
                        "correo",
                        correo,
                        "contrasena",
                        "credencial ficticia",
                        "nombres",
                        "Persona",
                        "apellidos",
                        "Ficticia",
                        "rol",
                        rol));
    }
}
