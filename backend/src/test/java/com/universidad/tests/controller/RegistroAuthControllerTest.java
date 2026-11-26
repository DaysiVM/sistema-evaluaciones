package com.universidad.tests.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.usecase.RegistrarCuentaService;
import com.universidad.tests.application.usecase.RegistroCuentaException;
import com.universidad.tests.dto.response.CuentaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RegistroAuthController.class)
class RegistroAuthControllerTest {

    private static final String RUTA = "/api/v1/auth/registro";
    private static final String CUERPO =
            """
            {"correo":"persona@example.test","contrasena":"clave ficticia",
             "nombres":"Persona","apellidos":"Ficticia","rol":"SUJETO"}
            """;

    @Autowired private MockMvc mvc;
    @MockitoBean private RegistrarCuentaService registro;
    @MockitoBean private AutenticacionService autenticacion;

    @Test
    void registrar_conCuentaBasica_devuelve201SinCredenciales() throws Exception {
        when(registro.registrar(any()))
                .thenReturn(new CuentaResponse(7L, "persona@example.test", "SUJETO", "ACTIVA"));

        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.correo").value("persona@example.test"))
                .andExpect(jsonPath("$.rol").value("SUJETO"))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.resumenContrasena").doesNotExist());
        verifyNoInteractions(autenticacion);
    }

    @Test
    void registrar_conCorreoDuplicado_devuelve409ConFormatoComun() throws Exception {
        when(registro.registrar(any()))
                .thenThrow(
                        new RegistroCuentaException(
                                RegistroCuentaException.Codigo.CUENTA_EXISTENTE,
                                "El correo ya tiene cuenta"));

        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.codigo").value("CUENTA_EXISTENTE"))
                .andExpect(jsonPath("$.detalles").doesNotExist())
                .andExpect(jsonPath("$.ruta").value(RUTA))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void registrar_sinPermisoParaRol_devuelve403ConCodigoExacto() throws Exception {
        when(registro.registrar(any()))
                .thenThrow(
                        new RegistroCuentaException(
                                RegistroCuentaException.Codigo.ROL_NO_AUTORIZADO,
                                "No tiene permiso para asignar el rol solicitado"));

        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.codigo").value("ROL_NO_AUTORIZADO"))
                .andExpect(jsonPath("$.ruta").value(RUTA));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{}",
                "{",
                "null",
                "{\"correo\":\"invalido\",\"contrasena\":\"x\",\"nombres\":\"Ficticio\",\"apellidos\":\"Prueba\",\"rol\":\"SUJETO\"}",
                "{\"correo\":\"persona@example.test\",\"contrasena\":null,\"nombres\":\"Ficticio\",\"apellidos\":\"Prueba\",\"rol\":\"SUJETO\"}",
                "{\"correo\":\"persona@example.test\",\"contrasena\":42,\"nombres\":\"Ficticio\",\"apellidos\":\"Prueba\",\"rol\":\"SUJETO\"}"
            })
    void registrar_conEntradaInvalida_devuelve400SinCrearCuenta(String cuerpo) throws Exception {
        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
                .andExpect(jsonPath("$.detalles").isNotEmpty())
                .andExpect(jsonPath("$.ruta").value(RUTA));
        verifyNoInteractions(registro, autenticacion);
    }

    @Test
    void registrar_conCorreoInvalido_identificaCampoSinExponerDatos() throws Exception {
        String correoInvalido = "correo-ficticio-sin-dominio";
        String contrasenaFicticia = "secreto-ficticio-no-publicar";
        String cuerpo =
                CUERPO.replace("persona@example.test", correoInvalido)
                        .replace("clave ficticia", contrasenaFicticia);

        String respuesta =
                mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
                        .andExpect(jsonPath("$.detalles[0].campo").value("correo"))
                        .andExpect(jsonPath("$.detalles[0].mensaje").isString())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThat(respuesta).doesNotContain(correoInvalido, contrasenaFicticia);
        verifyNoInteractions(registro, autenticacion);
    }
}
