package com.universidad.tests;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.application.service.VersionFormularioRestService;
import com.universidad.tests.application.service.VersionFormularioRestService.ResumenVersion;
import com.universidad.tests.application.service.VersionFormularioService.VersionDuplicadaException;
import com.universidad.tests.controller.VersionFormularioController;
import com.universidad.tests.controller.VersionadoErrorHandler;
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

@WebMvcTest(VersionFormularioController.class)
@Import(VersionadoErrorHandler.class)
class VersionFormularioRestControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private VersionFormularioRestService servicio;
    @MockitoBean private AutenticacionService autenticacion;

    private static final String RUTA = "/api/v1/tests/1/versiones";
    private static final String TOKEN = "token-prueba";
    private static final String CUERPO = "{\"codigoVersion\":\"v2\",\"fuenteRef\":\"DOC-BFA\"}";

    @BeforeEach
    void prepararAutenticacion() {
        when(autenticacion.validar(TOKEN))
                .thenReturn(
                        Optional.of(
                                new IdentidadSesion(1L, 1L, "ADMIN", Set.of("CONTENIDO_EDITAR"))));
    }

    @Test
    void creaBorrador() throws Exception {
        when(servicio.crear(1L, "v2", null, "DOC-BFA"))
                .thenReturn(new ResumenVersion(11L, 1L, "BORRADOR"));

        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(header().string("Location", "/api/v1/versiones/11"));
    }

    @Test
    void validaCuerpoYRecursos() throws Exception {
        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().isBadRequest());

        when(servicio.crear(anyLong(), anyString(), any(), anyString()))
                .thenThrow(new NoSuchElementException("test ausente"));

        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isNotFound());
    }

    @Test
    void rechazaVersionDuplicada() throws Exception {
        when(servicio.crear(anyLong(), anyString(), any(), anyString()))
                .thenThrow(new VersionDuplicadaException("duplicada"));

        mvc.perform(
                        post(RUTA)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("VERSION_DUPLICADA"));
    }

    @Test
    void rechazaSolicitudSinAutenticacion() throws Exception {
        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }
}
