package com.universidad.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.EstadoSubprueba;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.infrastructure.persistence.entity.ConfiguracionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.PeriodoJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.RespuestaEvaluacionJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.SubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.SujetoJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.TestJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.VersionFormularioJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// GEN-37-BE: tablas del motor de evaluacion y lectura de E4 (datos ficticios)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EvaluacionPersistenciaIntegrationTest {

    @Autowired private EvaluacionRepository evaluacionRepository;

    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;
    @MockitoBean private AutenticacionService autenticacion;
    @PersistenceContext private EntityManager entityManager;

    private Long idTest;
    private Long idVersion;
    private Long idConfiguracion;
    private Long idSujeto;
    private Long idPeriodo;

    private void prepararCatalogo() {
        TestJpaEntity test =
                new TestJpaEntity("EVAL_" + UUID.randomUUID(), "Test ficticio", null, true);
        entityManager.persist(test);
        SubpruebaJpaEntity subprueba = new SubpruebaJpaEntity(test, "SUB", "Subprueba", null);
        entityManager.persist(subprueba);
        VersionFormularioJpaEntity version =
                new VersionFormularioJpaEntity(test, "v1", null, "FUENTE-001");
        entityManager.persist(version);
        ConfiguracionSubpruebaJpaEntity config =
                new ConfiguracionSubpruebaJpaEntity(
                        version, subprueba, 1, 300, TipoRespuesta.SELECCION, "INSTR", null, null);
        entityManager.persist(config);

        SujetoJpaEntity sujeto =
                new SujetoJpaEntity("COD_" + UUID.randomUUID(), "Juan", "Perez", null);
        entityManager.persist(sujeto);
        PeriodoJpaEntity periodo =
                new PeriodoJpaEntity("PER_" + UUID.randomUUID(), "Periodo 2026", null, null, true);
        entityManager.persist(periodo);

        entityManager.flush();
        idTest = test.getIdTest();
        idVersion = version.getIdVersionFormulario();
        idConfiguracion = config.getIdConfiguracionSubprueba();
        idSujeto = sujeto.getIdSujeto();
        idPeriodo = periodo.getIdPeriodo();
    }

    private EvaluacionSubprueba subprueba(String codigo, int orden, EstadoSubprueba estado) {
        Instant inicio =
                estado == EstadoSubprueba.PENDIENTE ? null : Instant.parse("2026-10-30T15:30:00Z");
        Instant limite = inicio == null ? null : inicio.plusSeconds(300);
        Instant cierre = estado == EstadoSubprueba.CERRADA ? limite : null;
        return new EvaluacionSubprueba(
                null,
                null,
                idConfiguracion,
                codigo,
                "Subprueba " + codigo,
                orden,
                300,
                estado,
                null,
                true,
                inicio,
                limite,
                cierre,
                null,
                75);
    }

    private Evaluacion evaluacion(List<EvaluacionSubprueba> subpruebas) {
        return new Evaluacion(
                null,
                idSujeto,
                idPeriodo,
                idTest,
                idVersion,
                EstadoEvaluacion.EN_CURSO,
                Instant.parse("2026-10-30T15:30:00Z"),
                null,
                subpruebas);
    }

    @Test
    void guardaYLeeLaEvaluacionConSusSubpruebasEnOrden() {
        prepararCatalogo();

        Evaluacion guardada =
                evaluacionRepository.guardar(
                        evaluacion(
                                List.of(
                                        subprueba("B", 2, EstadoSubprueba.PENDIENTE),
                                        subprueba("A", 1, EstadoSubprueba.ACTIVA))));
        entityManager.clear();

        Evaluacion leida = evaluacionRepository.buscarPorId(guardada.idEvaluacion()).orElseThrow();

        assertThat(leida.idSujeto()).isEqualTo(idSujeto);
        assertThat(leida.estado()).isEqualTo(EstadoEvaluacion.EN_CURSO);
        assertThat(leida.subpruebas())
                .extracting(EvaluacionSubprueba::codigo)
                .containsExactly("A", "B");
        assertThat(leida.subpruebaActiva().orElseThrow().codigo()).isEqualTo("A");
    }

    @Test
    void noPermiteDosSubpruebasActivasEnLaMismaEvaluacion() {
        prepararCatalogo();

        Evaluacion dosActivas =
                evaluacion(
                        List.of(
                                subprueba("A", 1, EstadoSubprueba.ACTIVA),
                                subprueba("B", 2, EstadoSubprueba.ACTIVA)));

        assertThatThrownBy(() -> evaluacionRepository.guardar(dosActivas))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void rechazaPalabraDeFluidezVerbalFueraDeLaSubpruebaFv() {
        prepararCatalogo();
        Evaluacion guardada =
                evaluacionRepository.guardar(
                        evaluacion(List.of(subprueba("A", 1, EstadoSubprueba.ACTIVA))));

        assertThatThrownBy(
                        () -> {
                            entityManager.persist(
                                    new RespuestaEvaluacionJpaEntity(
                                            guardada.idEvaluacion(),
                                            "A",
                                            null,
                                            null,
                                            "palabra",
                                            1,
                                            Instant.now()));
                            entityManager.flush();
                        })
                .isInstanceOf(Exception.class);
    }

    @Test
    void e22AplicaFiltrosYRestringeAlSujetoAunqueTengaPermiso() throws Exception {
        prepararCatalogo();
        var propia =
                evaluacionRepository.guardar(
                        evaluacion(List.of(subprueba("SUB", 1, EstadoSubprueba.PENDIENTE))));
        var otro = new SujetoJpaEntity("OTRO_" + UUID.randomUUID(), "Nombre", "Ficticio", null);
        entityManager.persist(otro);
        var ajena =
                evaluacionRepository.guardar(
                        new Evaluacion(
                                null,
                                otro.getIdSujeto(),
                                idPeriodo,
                                idTest,
                                idVersion,
                                EstadoEvaluacion.FINALIZADA,
                                propia.fechaInicio(),
                                propia.fechaInicio().plusSeconds(60),
                                List.of()));
        Long cuenta =
                jdbc.queryForObject(
                        "insert into cuenta(correo,resumen_contrasena,nombres,apellidos,estado)"
                                + " values (?, 'ficticio', 'Nombre', 'Ficticio', 'ACTIVA') returning id_cuenta",
                        Long.class,
                        UUID.randomUUID() + "@ejemplo.test");
        jdbc.update("insert into cuenta_sujeto_asignado values (?, ?)", cuenta, idSujeto);
        when(autenticacion.validar("sujeto"))
                .thenReturn(
                        Optional.of(
                                new IdentidadSesion(
                                        cuenta, 1L, "SUJETO", Set.of("RECUPERAR_INTENTO"))));
        when(autenticacion.validar("profesional"))
                .thenReturn(
                        Optional.of(
                                new IdentidadSesion(
                                        cuenta, 1L, "ACADEMICO", Set.of("OPERACION_AUTORIZAR"))));

        mvc.perform(
                        get("/api/v1/evaluaciones")
                                .header("Authorization", "Bearer sujeto")
                                .param("testId", idTest.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(propia.idEvaluacion()))
                .andExpect(jsonPath("$[0].subpruebas[0].codigo").value("SUB"));
        mvc.perform(
                        get("/api/v1/evaluaciones")
                                .header("Authorization", "Bearer sujeto")
                                .param("sujetoId", otro.getIdSujeto().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        mvc.perform(
                        get("/api/v1/evaluaciones")
                                .header("Authorization", "Bearer profesional")
                                .param("sujetoId", otro.getIdSujeto().toString())
                                .param("testId", idTest.toString())
                                .param("periodoId", idPeriodo.toString())
                                .param("estado", "FINALIZADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ajena.idEvaluacion()));
    }

    @Test
    void e22RechazaSolicitudesSinSesionOSinPermiso() throws Exception {
        when(autenticacion.validar("sin-permiso"))
                .thenReturn(Optional.of(new IdentidadSesion(1L, 1L, "PSICOLOGO", Set.of())));
        mvc.perform(get("/api/v1/evaluaciones")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/evaluaciones").header("Authorization", "Bearer sin-permiso"))
                .andExpect(status().isForbidden());
    }
}
