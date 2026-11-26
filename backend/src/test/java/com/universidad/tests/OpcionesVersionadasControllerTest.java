package com.universidad.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.repository.OpcionRespuestaRepository;
import com.universidad.tests.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OpcionesVersionadasControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private EntityManager entidades;
    @Autowired private OpcionRespuestaRepository opciones;
    @MockitoBean private AutenticacionService autenticacion;
    private VersionFormularioJpaEntity version;
    private Long idPregunta;
    private String ruta;
    private static final String INICIAL =
            """
            {"opciones":[{"letra":"A","texto":"Uno"},{"letra":"B","texto":"Dos"},{"letra":"C","texto":"Tres"}]}
            """;

    @BeforeEach
    void prepararBorrador() {
        TestJpaEntity test =
                new TestJpaEntity("E28-" + UUID.randomUUID(), "Test ficticio", null, true);
        entidades.persist(test);
        SubpruebaJpaEntity subprueba = new SubpruebaJpaEntity(test, "SUB", "Serie", null);
        entidades.persist(subprueba);
        version = new VersionFormularioJpaEntity(test, "v1", null, "FUENTE");
        entidades.persist(version);
        ConfiguracionSubpruebaJpaEntity config =
                new ConfiguracionSubpruebaJpaEntity(
                        version,
                        subprueba,
                        1,
                        null,
                        TipoRespuesta.SELECCION,
                        "Instrucciones",
                        null,
                        null);
        entidades.persist(config);
        PreguntaJpaEntity pregunta =
                new PreguntaJpaEntity(config, "1", 1, "Pregunta ficticia", null);
        entidades.persist(pregunta);
        idPregunta = pregunta.getIdPregunta();
        ruta =
                "/api/v1/versiones/"
                        + version.getIdVersionFormulario()
                        + "/preguntas/"
                        + idPregunta
                        + "/opciones";
        when(autenticacion.validar("editor"))
                .thenReturn(
                        Optional.of(
                                new IdentidadSesion(1L, 1L, "ADMIN", Set.of("OPCIONES_EDITAR"))));
        when(autenticacion.validar("contenido"))
                .thenReturn(
                        Optional.of(
                                new IdentidadSesion(2L, 2L, "ADMIN", Set.of("CONTENIDO_EDITAR"))));
    }

    private ResultActions guardar(String token, String cuerpo) throws Exception {
        return mvc.perform(
                put(ruta)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo));
    }

    @Test
    void reemplazaReordenaYConservaIdentidadesAlReintentar() throws Exception {
        guardar("editor", INICIAL).andExpect(status().isOk());
        Long idA = opciones.buscarPorCodigo(idPregunta, "A").orElseThrow().getIdOpcion();
        String cambio =
                """
                {"opciones":[{"letra":"A","orden":2,"texto":"Editada"},{"letra":"B","orden":1,"texto":"Dos"},{"letra":"D","orden":3,"recursoRef":"imagen-ficticia"}]}
                """;
        for (int i = 0; i < 2; i++) {
            guardar("editor", cambio)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.opciones.length()").value(3))
                    .andExpect(jsonPath("$.opciones[0].letra").value("B"))
                    .andExpect(jsonPath("$.opciones[1].texto").value("Editada"))
                    .andExpect(jsonPath("$.opciones[2].recursoRef").value("imagen-ficticia"));
        }
        assertThat(opciones.buscarPorCodigo(idPregunta, "A").orElseThrow().getIdOpcion())
                .isEqualTo(idA);
        assertThat(opciones.buscarPorCodigo(idPregunta, "C")).isEmpty();
    }

    @Test
    void exigeSesionYPermisoDeOpciones() throws Exception {
        mvc.perform(put(ruta).contentType(MediaType.APPLICATION_JSON).content(INICIAL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
        guardar("contenido", INICIAL)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));
        assertThat(opciones.listarPorPregunta(idPregunta)).isEmpty();
    }

    @Test
    void rechazaListaVacia() throws Exception {
        guardar("editor", "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));
    }

    @Test
    void rechazaVersionPublicada() throws Exception {
        version.aplicarEstado(EstadoVersionFormulario.PUBLICADA, Instant.now(), "Acta ficticia");
        entidades.flush();
        guardar("editor", INICIAL)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("VERSION_NO_EDITABLE"));
    }
}
