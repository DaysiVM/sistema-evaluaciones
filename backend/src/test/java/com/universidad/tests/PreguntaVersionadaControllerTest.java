package com.universidad.tests;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.universidad.tests.application.service.AgregarPreguntaVersionadaService;
import com.universidad.tests.application.service.AgregarPreguntaVersionadaService.PreguntaDuplicadaException;
import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.controller.ManejadorErroresSeguridad;
import com.universidad.tests.controller.PreguntaVersionadaController;
import com.universidad.tests.controller.VersionadoErrorHandler;
import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.dto.PreguntaCreada;
import java.util.NoSuchElementException;
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

@WebMvcTest(PreguntaVersionadaController.class)
@Import({VersionadoErrorHandler.class, ManejadorErroresSeguridad.class})
class PreguntaVersionadaControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private AgregarPreguntaVersionadaService servicio;
    @MockitoBean private AutenticacionService autenticacion;

    private static final String RUTA = "/api/v1/versiones/11/configuraciones/R1/preguntas";
    private static final String TOKEN = "token-prueba";
    private static final String CUERPO =
            "{\"numeroVisible\":1,\"ordenPresentacion\":1,\"enunciado\":\"Serie\"}";

    @BeforeEach
    void prepararAutenticacion() {
        when(autenticacion.validar(TOKEN))
                .thenReturn(
                        Optional.of(
                                new IdentidadSesion(1L, 1L, "ADMIN", Set.of("CONTENIDO_EDITAR"))));
    }

    @Test
    void creadaResponde201YDto() throws Exception {
        when(servicio.agregar(eq(11L), eq("R1"), eq("1"), eq(1), eq("Serie"), isNull()))
                .thenReturn(new PreguntaCreada(120L, 1, 1, "Serie", null));
        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(120))
                .andExpect(header().string("Location", "/api/v1/versiones/11/preguntas/120"));
    }

    @Test
    void solicitudInvalidaResponde400() throws Exception {
        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));
    }

    @Test
    void permisoDenegadoResponde403() throws Exception {
        when(servicio.agregar(anyLong(), anyString(), anyString(), anyInt(), anyString(), any()))
                .thenThrow(new SinPermisoException());
        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));
    }

    @Test
    void recursoAusenteResponde404() throws Exception {
        when(servicio.agregar(anyLong(), anyString(), anyString(), anyInt(), anyString(), any()))
                .thenThrow(new NoSuchElementException("sin configuracion"));
        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("RECURSO_NO_ENCONTRADO"));
    }

    @Test
    void versionPublicadaResponde409() throws Exception {
        when(servicio.agregar(anyLong(), anyString(), anyString(), anyInt(), anyString(), any()))
                .thenThrow(new IllegalStateException("VERSION_NO_EDITABLE"));
        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("VERSION_NO_EDITABLE"));
    }

    @Test
    void preguntaDuplicadaResponde422() throws Exception {
        when(servicio.agregar(anyLong(), anyString(), anyString(), anyInt(), anyString(), any()))
                .thenThrow(new PreguntaDuplicadaException());
        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("PREGUNTA_DUPLICADA"));
    }

    @Test
    void rechazaSolicitudSinAutenticacion() throws Exception {
        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }
}
