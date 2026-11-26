package com.universidad.tests.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.ContextoSesion;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.application.service.SesionAbierta;
import com.universidad.tests.application.usecase.AbrirSesionService;
import com.universidad.tests.domain.exception.CredencialesInvalidasException;
import com.universidad.tests.domain.exception.NoAutenticadoException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SesionAuthController.class)
@Import(AbrirSesionService.class)
class SesionAuthControllerTest {

    @Test
    void abrir_sinContrasena_incluyeDetalleDeCampo() throws Exception {
        mvc.perform(
                        post("/api/v1/auth/sesion")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"correo\":\"persona@example.test\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
                .andExpect(jsonPath("$.detalles[0].campo").value("contrasena"))
                .andExpect(jsonPath("$.detalles[0].mensaje").isString());
        verifyNoInteractions(autenticacion);
    }

    private static final String RUTA = "/api/v1/auth/sesion";
    private static final String CUERPO =
            "{\"correo\":\"persona@example.test\",\"contrasena\":\" clave ficticia \"}";

    @Autowired private MockMvc mvc;
    @MockitoBean private AutenticacionService autenticacion;

    @Test
    void abrir_conCredencialesValidas_devuelveContratoSinExponerContrasena() throws Exception {
        AtomicReference<char[]> credencial = new AtomicReference<>();
        when(autenticacion.abrirSesion(eq("persona@example.test"), any(char[].class)))
                .thenAnswer(
                        invocacion -> {
                            char[] recibida = invocacion.getArgument(1);
                            assertThat(recibida).containsExactly(" clave ficticia ".toCharArray());
                            credencial.set(recibida);
                            return new SesionAbierta(
                                    "token-ficticio",
                                    Instant.parse("2026-11-25T01:00:00Z"),
                                    "SUJETO",
                                    List.of("PRACTICA_REALIZAR"));
                        });

        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("token-ficticio"))
                .andExpect(jsonPath("$.expiraEn").value("2026-11-25T01:00:00Z"))
                .andExpect(jsonPath("$.rolActivo").value("SUJETO"))
                .andExpect(jsonPath("$.permisos[0]").value("PRACTICA_REALIZAR"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.correo").doesNotExist());
        assertThat(credencial.get()).containsOnly('\0');
        verify(autenticacion).abrirSesion(eq("persona@example.test"), any(char[].class));
        verifyNoMoreInteractions(autenticacion);
    }

    @Test
    void abrir_conCredencialesRechazadas_devuelveErrorUniformeYLimpiaArreglo() throws Exception {
        AtomicReference<char[]> credencial = new AtomicReference<>();
        when(autenticacion.abrirSesion(eq("persona@example.test"), any(char[].class)))
                .thenAnswer(
                        invocacion -> {
                            credencial.set(invocacion.getArgument(1));
                            throw new CredencialesInvalidasException();
                        });

        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.codigo").value("CREDENCIALES_INVALIDAS"))
                .andExpect(jsonPath("$.mensaje").value("Correo o contrasena incorrectos"))
                .andExpect(jsonPath("$.ruta").value(RUTA))
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.contrasena").doesNotExist());
        assertThat(credencial.get()).containsOnly('\0');
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{}",
                "{\"correo\":\"persona@example.test\"}",
                "{\"correo\":\"invalido\",\"contrasena\":\"ficticia\"}",
                "{\"correo\":\"\",\"contrasena\":\"ficticia\"}",
                "{\"correo\":\"persona@example.test\",\"contrasena\":null}",
                "{",
                "null"
            })
    void abrir_conEntradaInvalida_devuelve400SinAutenticar(String cuerpo) throws Exception {
        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
                .andExpect(jsonPath("$.ruta").value(RUTA))
                .andExpect(jsonPath("$.timestamp").isString());
        verifyNoInteractions(autenticacion);
    }

    @Test
    void abrir_sinVigenciaEnDependencia_revocaTokenYDevuelve500SinExponerlo() throws Exception {
        when(autenticacion.abrirSesion(eq("persona@example.test"), any(char[].class)))
                .thenReturn(new SesionAbierta("token-ficticio", null, "SUJETO", List.of()));

        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
                .andExpect(jsonPath("$.mensaje").value("No se pudo completar la solicitud"))
                .andExpect(jsonPath("$.token").doesNotExist());
        verify(autenticacion).cerrarSesion("token-ficticio");
    }

    @Test
    void cerrar_conBearerValido_invalidaTokenSinCuerpoYLimpiaContexto() throws Exception {
        when(autenticacion.validar("token-ficticio"))
                .thenReturn(Optional.of(new IdentidadSesion(7L, 9L, "SUJETO", Set.of())));

        mvc.perform(delete(RUTA).header("Authorization", "bEaReR token-ficticio"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(autenticacion).validar("token-ficticio");
        verify(autenticacion).cerrarSesion("token-ficticio");
        verifyNoMoreInteractions(autenticacion);
        assertThat(ContextoSesion.actual()).isEmpty();
    }

    @Test
    void cerrar_sinBearer_devuelve401SinCerrar() throws Exception {
        mvc.perform(delete(RUTA))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
        verifyNoInteractions(autenticacion);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Basic ficticio", "Bearer ", "Bearer token-invalido"})
    void cerrar_conAutorizacionInvalida_devuelve401(String autorizacion) throws Exception {
        mvc.perform(delete(RUTA).header("Authorization", autorizacion))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"))
                .andExpect(jsonPath("$.ruta").value(RUTA))
                .andExpect(jsonPath("$.timestamp").isString());
        if (autorizacion.equals("Bearer token-invalido")) {
            verify(autenticacion).validar("token-invalido");
        }
        verifyNoMoreInteractions(autenticacion);
    }

    @Test
    void cerrar_conSesionRevocadaTrasValidacion_devuelve401YLimpiaContexto() throws Exception {
        when(autenticacion.validar("token-ficticio"))
                .thenReturn(Optional.of(new IdentidadSesion(7L, 9L, "SUJETO", Set.of())));
        doThrow(new NoAutenticadoException()).when(autenticacion).cerrarSesion("token-ficticio");

        mvc.perform(delete(RUTA).header("Authorization", "Bearer token-ficticio"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
        assertThat(ContextoSesion.actual()).isEmpty();
    }
}
