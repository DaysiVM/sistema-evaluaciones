package com.universidad.tests.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.CatalogoTestsService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.domain.exception.TestNoEncontradoException;
import com.universidad.tests.dto.response.TestResumenResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CatalogoTestsController.class)
class CatalogoTestsControllerTest {

    // Token ficticio; la validacion real se prueba en AutenticacionServiceTest
    private static final String BEARER = "Bearer token-ficticio";

    @Autowired private MockMvc mvc;

    @MockitoBean private CatalogoTestsService catalogoService;

    @MockitoBean private AutenticacionService autenticacion;

    @BeforeEach
    void sesionValida() {
        when(autenticacion.validar(anyString())).thenReturn(Optional.empty());
        when(autenticacion.validar("token-ficticio"))
                .thenReturn(Optional.of(new IdentidadSesion(1L, 1L, "SUJETO", Set.of())));
    }

    @Test
    void e1DevuelveLosTestsActivos() throws Exception {

        when(catalogoService.listarTests())
                .thenReturn(List.of(new TestResumenResponse(1L, "FICT", "Test ficticio", 2)));

        mvc.perform(get("/api/v1/tests").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("FICT"))
                .andExpect(jsonPath("$[0].numeroSubpruebas").value(2));
    }

    @Test
    void e2Responde404ConElFormatoComunDeError() throws Exception {

        when(catalogoService.obtenerTest(99L)).thenThrow(new TestNoEncontradoException(99L));

        mvc.perform(get("/api/v1/tests/99").header("Authorization", BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.codigo").value("TEST_NO_ENCONTRADO"))
                .andExpect(jsonPath("$.ruta").value("/api/v1/tests/99"))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    // GEN-06-BE: el catalogo exige sesion (openapi.yaml: toda operacion salvo E13, E14, E16, E17)
    @Test
    void e1SinTokenResponde401NoAutenticado() throws Exception {

        mvc.perform(get("/api/v1/tests"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"))
                .andExpect(jsonPath("$.ruta").value("/api/v1/tests"))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void e1ConTokenInvalidoResponde401() throws Exception {

        mvc.perform(get("/api/v1/tests").header("Authorization", "Bearer otro-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }

    @Test
    void e1ConEsquemaDistintoDeBearerResponde401() throws Exception {

        mvc.perform(get("/api/v1/tests").header("Authorization", "Basic token-ficticio"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }
}
