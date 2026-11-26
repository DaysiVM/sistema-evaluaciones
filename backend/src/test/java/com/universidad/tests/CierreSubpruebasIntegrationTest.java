package com.universidad.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.CierreSubpruebasService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.config.TemporizadorEvaluacionesConfig;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.EstadoSubprueba;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.infrastructure.persistence.entity.ConfiguracionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.PeriodoJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.SubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.SujetoJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.TestJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.VersionFormularioJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** Regresión de BUG-01 sobre el modelo numérico del PR #104 y transacciones confirmadas. */
@SpringBootTest
@AutoConfigureMockMvc
class CierreSubpruebasIntegrationTest {
    @Autowired private EvaluacionRepository evaluaciones;
    @Autowired private CierreSubpruebasService cierre;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private MockMvc mvc;
    @MockitoBean private AutenticacionService autenticacion;
    @Autowired private PlatformTransactionManager transacciones;
    @PersistenceContext private EntityManager entityManager;
    private Long id;
    private Long cuentaId;
    private Instant limite;

    @BeforeEach
    void preparar() {
        when(autenticacion.validar("cierre-ficticio"))
                .thenReturn(
                        Optional.of(
                                new IdentidadSesion(
                                        cuentaId, 1L, "SUJETO", Set.of("RECUPERAR_INTENTO"))));
        limite = Instant.now().minusSeconds(2).truncatedTo(ChronoUnit.MICROS);
        id =
                new TransactionTemplate(transacciones)
                        .execute(
                                tx -> {
                                    var sujeto =
                                            new SujetoJpaEntity(null, "Sujeto", "Ficticio", null);
                                    var periodo =
                                            new PeriodoJpaEntity(
                                                    UUID.randomUUID().toString(),
                                                    "Periodo ficticio",
                                                    null,
                                                    null,
                                                    true);
                                    entityManager.persist(sujeto);
                                    entityManager.persist(periodo);
                                    var test =
                                            new TestJpaEntity(
                                                    "CIERRE_" + UUID.randomUUID(),
                                                    "Test ficticio",
                                                    null,
                                                    true);
                                    entityManager.persist(test);
                                    var version =
                                            new VersionFormularioJpaEntity(
                                                    test, "v1", null, "FUENTE-FICTICIA");
                                    entityManager.persist(version);
                                    var a = configuracion(test, version, "VOC1", 1);
                                    var b = configuracion(test, version, "VOC2", 2);
                                    entityManager.flush();
                                    version.aplicarEstado(
                                            EstadoVersionFormulario.PUBLICADA,
                                            limite.minusSeconds(300),
                                            "Fixture ficticia de cierre");
                                    entityManager.flush();
                                    return evaluaciones
                                            .guardar(
                                                    new Evaluacion(
                                                            null,
                                                            sujeto.getIdSujeto(),
                                                            periodo.getIdPeriodo(),
                                                            test.getIdTest(),
                                                            version.getIdVersionFormulario(),
                                                            EstadoEvaluacion.EN_CURSO,
                                                            limite.minusSeconds(300),
                                                            null,
                                                            List.of(
                                                                    new EvaluacionSubprueba(
                                                                            null,
                                                                            null,
                                                                            a
                                                                                    .getIdConfiguracionSubprueba(),
                                                                            "VOC1",
                                                                            "Ficticia 1",
                                                                            1,
                                                                            300,
                                                                            EstadoSubprueba.ACTIVA,
                                                                            "INSTR",
                                                                            true,
                                                                            limite.minusSeconds(
                                                                                    300),
                                                                            limite,
                                                                            null,
                                                                            null,
                                                                            75),
                                                                    new EvaluacionSubprueba(
                                                                            null,
                                                                            null,
                                                                            b
                                                                                    .getIdConfiguracionSubprueba(),
                                                                            "VOC2",
                                                                            "Ficticia 2",
                                                                            2,
                                                                            300,
                                                                            EstadoSubprueba
                                                                                    .PENDIENTE,
                                                                            "INSTR",
                                                                            true,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            75))))
                                            .idEvaluacion();
                                });
        cuentaId =
                jdbc.queryForObject(
                        "insert into cuenta(correo,resumen_contrasena,nombres,apellidos,estado) values (?,'hash-ficticio','Prueba','Cierre','ACTIVA') returning id_cuenta",
                        Long.class,
                        "cierre-" + UUID.randomUUID() + "@ejemplo.test");
        jdbc.update(
                "insert into cuenta_sujeto_asignado(id_cuenta,id_sujeto) select ?,id_sujeto from evaluacion where id_evaluacion=?",
                cuentaId,
                id);
        when(autenticacion.validar("cierre-ficticio"))
                .thenReturn(
                        Optional.of(
                                new IdentidadSesion(
                                        cuentaId, 1L, "SUJETO", Set.of("RECUPERAR_INTENTO"))));
    }

    private ConfiguracionSubpruebaJpaEntity configuracion(
            TestJpaEntity test, VersionFormularioJpaEntity version, String codigo, int orden) {
        var sub = new SubpruebaJpaEntity(test, codigo, "Subprueba ficticia", null);
        entityManager.persist(sub);
        var config =
                new ConfiguracionSubpruebaJpaEntity(
                        version,
                        sub,
                        orden,
                        300,
                        TipoRespuesta.SELECCION,
                        "INSTR-FICTICIA",
                        null,
                        null);
        entityManager.persist(config);
        return config;
    }

    @AfterEach
    void limpiar() {
        if (id != null) jdbc.update("DELETE FROM evaluacion WHERE id_evaluacion=?", id);
    }

    @Test
    void e4ConfirmaElCierreYPermiteActivarLaSiguienteSubprueba() throws Exception {
        assertThat(
                        jdbc.queryForObject(
                                "SELECT estado FROM evaluacion_subprueba"
                                        + " WHERE id_evaluacion=? AND codigo='VOC1'",
                                String.class,
                                id))
                .isEqualTo("ACTIVA");
        consultarCerrada();
        comprobarCierreUnico();
        consultarCerrada();
        comprobarCierreUnico();

        Instant ahora = Instant.now();
        assertThat(
                        jdbc.update(
                                "UPDATE evaluacion_subprueba SET estado='ACTIVA',fecha_inicio=?,fecha_fin_limite=?"
                                        + " WHERE id_evaluacion=? AND codigo='VOC2'",
                                Timestamp.from(ahora),
                                Timestamp.from(ahora.plusSeconds(300)),
                                id))
                .isEqualTo(1);
        mvc.perform(
                        get("/api/v1/evaluaciones/" + id)
                                .header("Authorization", "Bearer cierre-ficticio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subpruebaActiva.codigo").value("VOC2"));
    }

    @Test
    void temporizadorRegistraElCierreSinConsultarLaApi() {
        new TemporizadorEvaluacionesConfig(cierre).cerrarVencidas();
        comprobarCierreUnico();
        new TemporizadorEvaluacionesConfig(cierre).cerrarVencidas();
        comprobarCierreUnico();
    }

    @Test
    void cierresConcurrentesGeneranUnaSolaSolicitud() throws Exception {
        var pool = Executors.newFixedThreadPool(3);
        try {
            Callable<Void> cerrar =
                    () -> {
                        cierre.cerrarVencidas(id);
                        return null;
                    };
            for (var futuro : pool.invokeAll(List.of(cerrar, cerrar, cerrar))) {
                futuro.get(10, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }
        comprobarCierreUnico();
    }

    @Test
    void subpruebaConTiempoNoSeCierraNiSolicitaCalculo() throws Exception {
        Instant ahora = Instant.now();
        jdbc.update(
                "UPDATE evaluacion_subprueba SET fecha_inicio=?,fecha_fin_limite=?"
                        + " WHERE id_evaluacion=? AND codigo='VOC1'",
                Timestamp.from(ahora),
                Timestamp.from(ahora.plusSeconds(300)),
                id);
        mvc.perform(
                        get("/api/v1/evaluaciones/" + id)
                                .header("Authorization", "Bearer cierre-ficticio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subpruebas[0].estado").value("ACTIVA"))
                .andExpect(jsonPath("$.subpruebas[0].cerradaEn").isEmpty());
        assertThat(
                        jdbc.queryForObject(
                                "SELECT count(*) FROM solicitud_calculo_subprueba"
                                        + " WHERE id_evaluacion=?",
                                Long.class,
                                id))
                .isZero();
    }

    @Test
    void evaluacionInexistenteConservaElError404() throws Exception {
        mvc.perform(
                        get("/api/v1/evaluaciones/9223372036854775807")
                                .header("Authorization", "Bearer cierre-ficticio"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("EVALUACION_NO_ENCONTRADA"));
    }

    private void consultarCerrada() throws Exception {
        mvc.perform(
                        get("/api/v1/evaluaciones/" + id)
                                .header("Authorization", "Bearer cierre-ficticio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subpruebas[0].estado").value("CERRADA"))
                .andExpect(jsonPath("$.subpruebas[0].cerradaEn").value(limite.toString()));
    }

    private void comprobarCierreUnico() {
        assertThat(
                        jdbc.queryForObject(
                                        "SELECT fecha_cierre FROM evaluacion_subprueba"
                                                + " WHERE id_evaluacion=? AND codigo='VOC1'",
                                        Timestamp.class,
                                        id)
                                .toInstant())
                .isEqualTo(limite);
        assertThat(
                        jdbc.queryForObject(
                                "SELECT count(*) FROM solicitud_calculo_subprueba"
                                        + " WHERE id_evaluacion=?",
                                Long.class,
                                id))
                .isEqualTo(1);
        assertThat(
                        jdbc.queryForObject(
                                        "SELECT fecha_cierre FROM solicitud_calculo_subprueba"
                                                + " WHERE id_evaluacion=?",
                                        Timestamp.class,
                                        id)
                                .toInstant())
                .isEqualTo(limite);
    }
}
