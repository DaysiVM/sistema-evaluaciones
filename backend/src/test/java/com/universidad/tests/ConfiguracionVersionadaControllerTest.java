package com.universidad.tests;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.ConfiguracionVersionadaService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.controller.ConfiguracionVersionadaController;
import com.universidad.tests.controller.VersionadoErrorHandler;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConfiguracionVersionadaController.class)
@Import(VersionadoErrorHandler.class)
class ConfiguracionVersionadaControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private ConfiguracionVersionadaService servicio;
    @MockitoBean private AutenticacionService autenticacion;

    private static final String RUTA = "/api/v1/versiones/11/configuraciones/R1";
    private static final String TOKEN = "token-prueba";

    @BeforeEach
    void prepararAutenticacion() {
        when(autenticacion.validar(TOKEN))
                .thenReturn(
                        Optional.of(
                                new IdentidadSesion(
                                        1L,
                                        1L,
                                        "ADMIN",
                                        Set.of("CONFIGURAR_BFA", "INSTRUCCIONES_EDITAR"))));
    }

    @Test
    void guardaConfiguracion() throws Exception {
        mvc.perform(
                        put(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"orden\":1,\"tipoRespuesta\":\"SELECCION\",\"opciones\":2,"
                                                + "\"instrucciones\":\"Lea las instrucciones\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opciones").value(2));
    }

    @Test
    void rechazaDatosInvalidos() throws Exception {
        mvc.perform(
                        put(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().isBadRequest());

        doThrow(new IllegalArgumentException("Tipo de respuesta desconocido"))
                .when(servicio)
                .guardar(
                        eq(11L),
                        eq("R1"),
                        eq(1),
                        isNull(),
                        eq("OTRO"),
                        isNull(),
                        isNull(),
                        eq("Lea"),
                        isNull());

        mvc.perform(
                        put(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"orden\":1,\"tipoRespuesta\":\"OTRO\",\"instrucciones\":\"Lea\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaSolicitudSinAutenticacion() throws Exception {
        mvc.perform(
                        put(RUTA)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"orden\":1,\"tipoRespuesta\":\"SELECCION\","
                                                + "\"instrucciones\":\"Lea las instrucciones\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }
}
