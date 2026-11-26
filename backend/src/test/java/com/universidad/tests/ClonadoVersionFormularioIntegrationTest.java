package com.universidad.tests;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.ContextoSesion;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.application.service.VersionFormularioService;
import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.domain.model.MatrizPermisos;
import com.universidad.tests.domain.model.Rol;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** E25 con autorizacion y persistencia reales; solo la validacion del token se sustituye. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClonadoVersionFormularioIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private VersionFormularioService versiones;
    @MockitoBean private AutenticacionService autenticacion;
    private long testId;
    private long origenId;

    @BeforeEach
    void prepararContenidoFicticioPublicado() {
        testId =
                jdbc.queryForObject(
                        "insert into test(codigo,nombre) values (?, 'Catalogo ficticio') returning id_test",
                        Long.class,
                        "E25-" + UUID.randomUUID());
        long subprueba =
                jdbc.queryForObject(
                        "insert into subprueba(id_test,codigo,nombre) values (?, 'DEMO', 'Subprueba ficticia') returning id_subprueba",
                        Long.class,
                        testId);
        origenId =
                jdbc.queryForObject(
                        "insert into version_formulario(id_test,codigo_version,fuente_ref) values (?, 'v1', 'fuente-ficticia') returning id_version_formulario",
                        Long.class,
                        testId);
        long configuracion =
                jdbc.queryForObject(
                        "insert into configuracion_subprueba(id_version_formulario,id_subprueba,orden_aplicacion,tipo_respuesta,instrucciones_ref,opciones_esperadas) values (?, ?, 1, 'SELECCION', 'instruccion-ficticia', 2) returning id_configuracion_subprueba",
                        Long.class,
                        origenId,
                        subprueba);
        long pregunta =
                jdbc.queryForObject(
                        "insert into pregunta(id_configuracion_subprueba,numero_visible,orden_presentacion,enunciado) values (?, '1', 1, 'Pregunta ficticia') returning id_pregunta",
                        Long.class,
                        configuracion);
        jdbc.update(
                "insert into opcion_respuesta(id_pregunta,codigo,orden_presentacion,texto) values (?, 'A', 1, 'Opcion ficticia A'), (?, 'B', 2, 'Opcion ficticia B')",
                pregunta,
                pregunta);
        jdbc.update(
                "update version_formulario set estado='PUBLICADA',publicada_en=current_timestamp,evidencia_validacion='evidencia-ficticia' where id_version_formulario=?",
                origenId);
    }

    @AfterEach
    void limpiarSesion() {
        ContextoSesion.limpiar();
    }

    private IdentidadSesion identidad(Rol rol) {
        return new IdentidadSesion(1L, 1L, rol.name(), MatrizPermisos.aprobada().permisosDe(rol));
    }

    @Test
    void coordinadorClonaLaPublicadaSinModificarElOrigen() throws Exception {
        when(autenticacion.validar("token-ficticio"))
                .thenReturn(Optional.of(identidad(Rol.COORDINADOR)));
        mvc.perform(
                        post("/api/v1/tests/{id}/versiones", testId)
                                .header("Authorization", "Bearer token-ficticio")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"codigoVersion\":\"v2\",\"fuenteRef\":\"fuente-ficticia\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("BORRADOR"));
        assertThat(
                        jdbc.queryForObject(
                                "select estado from version_formulario where id_version_formulario=?",
                                String.class,
                                origenId))
                .isEqualTo("PUBLICADA");
        for (String codigo : new String[] {"v1", "v2"}) {
            assertThat(
                            jdbc.queryForObject(
                                    "select count(*) from opcion_respuesta o join pregunta p using(id_pregunta) join configuracion_subprueba c using(id_configuracion_subprueba) join version_formulario v using(id_version_formulario) where v.id_test=? and v.codigo_version=?",
                                    Integer.class,
                                    testId,
                                    codigo))
                    .isEqualTo(2);
        }
    }

    @Test
    void rolSinPermisoNoCreaOtroBorrador() throws Exception {
        when(autenticacion.validar("token-ficticio"))
                .thenReturn(Optional.of(identidad(Rol.SUJETO)));
        mvc.perform(
                        post("/api/v1/tests/{id}/versiones", testId)
                                .header("Authorization", "Bearer token-ficticio")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"codigoVersion\":\"v2\",\"fuenteRef\":\"fuente-ficticia\"}"))
                .andExpect(status().isForbidden());
        assertThat(
                        jdbc.queryForObject(
                                "select count(*) from version_formulario where id_test=?",
                                Integer.class,
                                testId))
                .isEqualTo(1);
    }

    @Test
    void consultaDirectaDelContenidoSigueDenegada() {
        ContextoSesion.establecer(identidad(Rol.COORDINADOR));
        assertThatThrownBy(() -> versiones.buscarPorId(origenId))
                .isInstanceOf(SinPermisoException.class);
    }
}
