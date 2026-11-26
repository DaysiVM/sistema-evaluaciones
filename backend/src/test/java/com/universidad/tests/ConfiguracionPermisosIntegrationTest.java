package com.universidad.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** E26 real hasta PostgreSQL; solo se sustituye la validación del token de prueba. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ConfiguracionPermisosIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private EntityManager em;
    @Autowired private JdbcTemplate jdbc;
    @MockitoSpyBean private AutenticacionService autenticacion;
    private VersionFormularioJpaEntity version;
    private String ruta;
    private static final String TOKEN = "sesion-sintetica-e26";
    private static final String CUERPO =
            "{\"orden\":1,\"tipoRespuesta\":\"SELECCION\",\"opciones\":2,\"tiempoLimiteSegundos\":300,\"instrucciones\":\"Instruccion ficticia\"}";

    @BeforeEach
    void datos() {
        var test = new TestJpaEntity("E26-" + UUID.randomUUID(), "Test ficticio", "Fixture", true);
        em.persist(test);
        version = new VersionFormularioJpaEntity(test, "v1", "Fixture", "fuente-ficticia");
        em.persist(version);
        em.persist(new SubpruebaJpaEntity(test, "R1", "Subprueba ficticia", "Fixture"));
        em.flush();
        ruta = "/api/v1/versiones/" + version.getIdVersionFormulario() + "/configuraciones/R1";
    }

    private void permisos(String... permisos) {
        doReturn(Optional.of(new IdentidadSesion(1L, 1L, "COORDINADOR", Set.of(permisos))))
                .when(autenticacion)
                .validar(TOKEN);
    }

    private long contar() {
        return jdbc.queryForObject(
                "select count(*) from configuracion_subprueba where id_version_formulario = ?",
                Long.class,
                version.getIdVersionFormulario());
    }

    @Test
    void sinTokenNoEscribe() throws Exception {
        mvc.perform(put(ruta).contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isUnauthorized());
        assertThat(contar()).isZero();
    }

    @Test
    void contenidoEditarNoAutorizaE26() throws Exception {
        permisos("CONTENIDO_EDITAR");
        mvc.perform(
                        put(ruta)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));
        assertThat(contar()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"CONFIGURAR_BFA", "INSTRUCCIONES_EDITAR"})
    void unSoloPermisoNoAlcanza(String permiso) throws Exception {
        permisos(permiso);
        mvc.perform(
                        put(ruta)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));
        assertThat(contar()).isZero();
    }

    @Test
    void ambosPermisosGuardanSinExigirContenidoEditar() throws Exception {
        permisos("CONFIGURAR_BFA", "INSTRUCCIONES_EDITAR");
        mvc.perform(
                        put(ruta)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opciones").value(2));
        em.flush();
        em.clear();
        assertThat(contar()).isEqualTo(1);
        assertThat(
                        jdbc.queryForObject(
                                "select instrucciones_ref from configuracion_subprueba where id_version_formulario = ?",
                                String.class,
                                version.getIdVersionFormulario()))
                .isEqualTo("Instruccion ficticia");
    }

    @Test
    void publicadaSigueSinEditarAunqueTengaPermisos() throws Exception {
        permisos("CONFIGURAR_BFA", "INSTRUCCIONES_EDITAR");
        mvc.perform(
                        put(ruta)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isOk());
        em.flush();
        version.aplicarEstado(
                EstadoVersionFormulario.PUBLICADA, Instant.now(), "evidencia-ficticia");
        em.flush();
        permisos("CONFIGURAR_BFA", "INSTRUCCIONES_EDITAR");
        mvc.perform(
                        put(ruta)
                                .header("Authorization", "Bearer " + TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(CUERPO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("VERSION_NO_EDITABLE"));
        assertThat(contar()).isEqualTo(1);
    }
}
