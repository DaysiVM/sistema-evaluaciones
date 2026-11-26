package com.universidad.tests.evaluacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.port.AutorizacionEvaluacionPort;
import com.universidad.tests.infrastructure.persistence.entity.ConfiguracionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.PeriodoJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.SubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.SujetoJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.TestJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.VersionFormularioJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.ConfiguracionSubpruebaJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.PeriodoJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.SubpruebaJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.SujetoJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.TestJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.VersionFormularioJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Escenarios principales de GEN-02 contra PostgreSQL (E2, E3, E4, E19, E21). Cada prueba crea sus
 * propios datos ficticios y se revierte al terminar.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EvaluacionApiIntegrationTest {

    // Token ficticio; la validacion real se prueba en AutenticacionServiceTest
    private static final String BEARER = "Bearer token-ficticio";

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AutenticacionService autenticacion;
    @MockitoSpyBean private AutorizacionEvaluacionPort autorizacionEvaluacion;
    @Autowired private ObjectMapper json;
    @Autowired private JdbcTemplate jdbc;
    @PersistenceContext private EntityManager entityManager;

    @Autowired private TestJpaRepository testRepository;
    @Autowired private SubpruebaJpaRepository subpruebaRepository;
    @Autowired private VersionFormularioJpaRepository versionRepository;
    @Autowired private ConfiguracionSubpruebaJpaRepository configuracionRepository;
    @Autowired private SujetoJpaRepository sujetoRepository;
    @Autowired private PeriodoJpaRepository periodoRepository;

    private TestJpaEntity test;
    private VersionFormularioJpaEntity version;
    private Map<String, ConfiguracionSubpruebaJpaEntity> configuraciones;
    private Long sujetoId;
    private Long periodoId;
    private Long cuentaId;
    private String rolPrueba = "COORDINADOR";
    private Set<String> permisosPrueba =
            Set.of("OPERACION_AUTORIZAR", "RECUPERAR_INTENTO", "SUJETO_LEER", "PERIODO_LEER");

    @BeforeEach
    void prepararCatalogo() {
        test = crearTest("GEN02_" + corto());
        version = crearVersionPublicada(test, List.of("VOC1", "VOC2", "CV"));
        configuraciones = configuracionesDe(version);
        sujetoId = crearSujeto("Ana", "Ruiz");
        periodoId = crearPeriodo(true);
        cuentaId =
                jdbc.queryForObject(
                        "insert into cuenta(correo,resumen_contrasena,nombres,apellidos,estado) values (?,?,?,?,'ACTIVA') returning id_cuenta",
                        Long.class,
                        "alcance-" + java.util.UUID.randomUUID() + "@ejemplo.test",
                        "hash-ficticio-sin-credencial",
                        "Persona",
                        "Ficticia");
        jdbc.update(
                "insert into cuenta_sujeto_asignado(id_cuenta,id_sujeto) values (?,?)",
                cuentaId,
                sujetoId);
    }

    // ---------- E3: crear ----------

    @Test
    void creaUnaEvaluacionCompletaAsociadaAlSujetoPeriodoYVersion() throws Exception {
        MvcResult resultado =
                ejecutar(iniciar(cuerpo(null, null)))
                        .andExpect(status().isCreated())
                        .andExpect(
                                header().string(
                                                "Location",
                                                org.hamcrest.Matchers.startsWith(
                                                        "/api/v1/evaluaciones/")))
                        .andExpect(jsonPath("$.estado").value("EN_CURSO"))
                        .andExpect(jsonPath("$.testId").value(test.getIdTest()))
                        .andExpect(
                                jsonPath("$.versionFormularioId")
                                        .value(version.getIdVersionFormulario()))
                        .andExpect(jsonPath("$.sujetoId").value(sujetoId))
                        .andExpect(jsonPath("$.periodoId").value(periodoId))
                        .andExpect(jsonPath("$.fechaFin").value(nullValue()))
                        .andExpect(jsonPath("$.subpruebaActiva").value(nullValue()))
                        .andExpect(
                                jsonPath("$.subpruebas[*].codigo", contains("VOC1", "VOC2", "CV")))
                        .andExpect(
                                jsonPath(
                                        "$.subpruebas[*].estado",
                                        contains("PENDIENTE", "PENDIENTE", "PENDIENTE")))
                        .andReturn();

        long id = idDe(resultado);
        Map<String, Object> fila =
                jdbc.queryForMap(
                        "select id_sujeto, id_periodo, id_version_formulario, id_test, estado"
                                + " from evaluacion where id_evaluacion = ?",
                        id);
        assertThat(((Number) fila.get("id_sujeto")).longValue()).isEqualTo(sujetoId);
        assertThat(((Number) fila.get("id_periodo")).longValue()).isEqualTo(periodoId);
        assertThat(((Number) fila.get("id_version_formulario")).longValue())
                .isEqualTo(version.getIdVersionFormulario());
        assertThat(fila.get("estado")).isEqualTo("EN_CURSO");
        assertThat(contar("evaluacion_subprueba where id_evaluacion = " + id)).isEqualTo(3);
    }

    @Test
    void aplicacionParcialGuardaSoloLasSubpruebasElegidasEnOrdenOficial() throws Exception {
        MvcResult resultado =
                ejecutar(iniciar(cuerpo(List.of("CV", "VOC1"), null)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.subpruebas[*].codigo", contains("VOC1", "CV")))
                        .andReturn();

        assertThat(contar("evaluacion_subprueba where id_evaluacion = " + idDe(resultado)))
                .isEqualTo(2);
    }

    @Test
    void noDuplicaLaEvaluacionAbiertaDelMismoSujetoTestYPeriodo() throws Exception {
        long id =
                idDe(
                        ejecutar(iniciar(cuerpo(null, null)))
                                .andExpect(status().isCreated())
                                .andReturn());

        ejecutar(iniciar(cuerpo(null, null)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("EVALUACION_EN_CURSO"))
                .andExpect(jsonPath("$.ruta").value("/api/v1/evaluaciones"));

        assertThat(contar("evaluacion where id_sujeto = " + sujetoId)).isEqualTo(1);
        assertThat(
                        jdbc.queryForList(
                                """
                                select id_cuenta, accion, entidad_tipo, entidad_id, id_evaluacion,
                                       ocurrido_en is not null as fechado
                                from evento_auditoria where id_cuenta = ?
                                """,
                                cuentaId))
                .containsExactly(
                        Map.of(
                                "id_cuenta",
                                cuentaId,
                                "accion",
                                "INICIAR_EVALUACION",
                                "entidad_tipo",
                                "EVALUACION",
                                "entidad_id",
                                id,
                                "id_evaluacion",
                                id,
                                "fechado",
                                true));
    }

    @Test
    void rechazaDatosInvalidosConElFormatoComunDeError() throws Exception {
        ejecutar(post("/api/v1/evaluaciones").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalles[*].campo", hasItem("sujetoId")))
                .andExpect(jsonPath("$.detalles[*].campo", hasItem("periodoId")))
                .andExpect(jsonPath("$.detalles[*].campo", hasItem("versionFormularioId")));

        ejecutar(
                        post("/api/v1/evaluaciones")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"sujetoId\": \"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));

        assertThat(contar("evaluacion where id_sujeto = " + sujetoId)).isZero();
    }

    @Test
    void rechazaReferenciasInexistentesONoValidas() throws Exception {
        Map<String, Object> sinSujeto = cuerpo(null, null);
        sinSujeto.put("sujetoId", 999_999_999L);
        ejecutar(iniciar(sinSujeto))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));

        Map<String, Object> sinPeriodo = cuerpo(null, null);
        sinPeriodo.put("periodoId", 999_999_999L);
        ejecutar(iniciar(sinPeriodo))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("PERIODO_NO_ENCONTRADO"));

        ejecutar(iniciar(cuerpo(List.of("VOC1", "NO_EXISTE"), null)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("SUBPRUEBA_NO_INCLUIDA"));

        Map<String, Object> periodoCerrado = cuerpo(null, null);
        periodoCerrado.put("periodoId", crearPeriodo(false));
        ejecutar(iniciar(periodoCerrado))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("PERIODO_NO_DISPONIBLE"));

        VersionFormularioJpaEntity borrador =
                versionRepository.saveAndFlush(
                        new VersionFormularioJpaEntity(test, "borrador", null, "FUENTE-PRUEBA"));
        Map<String, Object> noPublicada = cuerpo(null, null);
        noPublicada.put("versionFormularioId", borrador.getIdVersionFormulario());
        ejecutar(iniciar(noPublicada))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("VERSION_NO_PUBLICADA"));

        TestJpaEntity otroTest = crearTest("OTRO_" + corto());
        Map<String, Object> deOtroTest = cuerpo(null, null);
        deOtroTest.put("testId", otroTest.getIdTest());
        ejecutar(iniciar(deOtroTest))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("VERSION_INCOMPATIBLE"));

        assertThat(contar("evaluacion where id_sujeto = " + sujetoId)).isZero();
    }

    @Test
    void bfaConFluidezExigeLetraYLaGuardaEnMayuscula() throws Exception {
        TestJpaEntity bfa =
                testRepository
                        .findByCodigo("BFA")
                        .orElseGet(
                                () ->
                                        testRepository.saveAndFlush(
                                                new TestJpaEntity(
                                                        "BFA",
                                                        "Batería Factorial de Aptitudes",
                                                        null,
                                                        true)));
        VersionFormularioJpaEntity versionBfa = crearVersionPublicada(bfa, List.of("VOC1", "FV"));
        Map<String, Object> sinLetra = new LinkedHashMap<>();
        sinLetra.put("versionFormularioId", versionBfa.getIdVersionFormulario());
        sinLetra.put("sujetoId", sujetoId);
        sinLetra.put("periodoId", periodoId);

        ejecutar(iniciar(sinLetra))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("LETRA_NO_ASIGNADA"));
        assertThat(contar("evaluacion where id_sujeto = " + sujetoId)).isZero();

        Map<String, Object> conLetra = new LinkedHashMap<>(sinLetra);
        conLetra.put("fluidez", Map.of("letraInicial", "m"));
        MvcResult resultado =
                ejecutar(iniciar(conLetra))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.subpruebas[*].codigo", contains("VOC1", "FV")))
                        .andReturn();

        String letra =
                jdbc.queryForObject(
                        "select es.letra_inicial from evaluacion_subprueba es"
                                + " where es.id_evaluacion = ? and es.codigo = 'FV'",
                        String.class,
                        idDe(resultado));
        assertThat(letra).isEqualTo("M");
    }

    @Test
    void e3Devuelve403CuandoElPuertoDeniegaLaCreacion() throws Exception {
        doThrow(new SinPermisoException())
                .when(autorizacionEvaluacion)
                .exigirPermisoIniciarEvaluacion(sujetoId);

        ejecutar(iniciar(cuerpo(null, null)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));

        assertThat(contar("evaluacion where id_sujeto = " + sujetoId)).isZero();
    }

    @Test
    void e3RechazaLaCreacionSinToken() throws Exception {
        mockMvc.perform(iniciar(cuerpo(null, null)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));

        assertThat(contar("evaluacion where id_sujeto = " + sujetoId)).isZero();
    }

    // ---------- E4: consultar ----------

    @Test
    void e4ConsultaLaEvaluacionPersistidaPorE3() throws Exception {
        MvcResult creada =
                ejecutar(iniciar(cuerpo(null, null))).andExpect(status().isCreated()).andReturn();
        long id = idDe(creada);
        String ruta = "/api/v1/evaluaciones/" + id;
        assertThat(creada.getResponse().getHeader("Location")).isEqualTo(ruta);
        entityManager.flush();
        entityManager.clear();

        ejecutar(get(ruta))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.testId").value(test.getIdTest()))
                .andExpect(
                        jsonPath("$.versionFormularioId").value(version.getIdVersionFormulario()))
                .andExpect(jsonPath("$.sujetoId").value(sujetoId))
                .andExpect(jsonPath("$.periodoId").value(periodoId))
                .andExpect(jsonPath("$.estado").value("EN_CURSO"))
                .andExpect(jsonPath("$.subpruebaActiva").value(nullValue()))
                .andExpect(jsonPath("$.subpruebas[*].codigo", contains("VOC1", "VOC2", "CV")))
                .andExpect(
                        jsonPath(
                                "$.subpruebas[*].estado",
                                contains("PENDIENTE", "PENDIENTE", "PENDIENTE")));
    }

    @Test
    void e4RechazaLaConsultaSinToken() throws Exception {
        MvcResult creada =
                ejecutar(iniciar(cuerpo(null, null))).andExpect(status().isCreated()).andReturn();

        mockMvc.perform(get("/api/v1/evaluaciones/{id}", idDe(creada)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }

    // ---------- E2, E19 y E21 ----------

    @Test
    void detalleDelTestDeGen01EntregaLaVersionQueUsaE3() throws Exception {
        ejecutar(get("/api/v1/tests/{id}", test.getIdTest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(test.getCodigo()))
                .andExpect(
                        jsonPath("$.versionFormularioId").value(version.getIdVersionFormulario()))
                .andExpect(jsonPath("$.subpruebas[*].codigo", contains("VOC1", "VOC2", "CV")))
                .andExpect(jsonPath("$.subpruebas[0].tiempoLimiteSegundos").value(300))
                .andExpect(jsonPath("$.subpruebas[0].tipoRespuesta").value("SELECCION"))
                .andExpect(jsonPath("$.subpruebas[0].numeroPreguntas").value(0));

        ejecutar(get("/api/v1/tests/{id}", 999_999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("TEST_NO_ENCONTRADO"));
    }

    @Test
    void consultaElSujetoYLosPeriodosDisponibles() throws Exception {
        ejecutar(get("/api/v1/sujetos/{id}", sujetoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Ana"))
                .andExpect(jsonPath("$.apellidos").value("Ruiz"));

        ejecutar(get("/api/v1/sujetos/{id}", 999_999_999L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));

        Long cerrado = crearPeriodo(false);
        ejecutar(get("/api/v1/periodos").param("disponible", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(periodoId.intValue())))
                .andExpect(jsonPath("$[*].id", not(hasItem(cerrado.intValue()))))
                .andExpect(jsonPath("$[?(@.disponible == false)]", hasSize(0)));

        ejecutar(get("/api/v1/periodos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(cerrado.intValue())));
    }

    // ---------- Base de datos ----------

    @Test
    void laBaseDeDatosImpideDosEvaluacionesAbiertasIgualesAunqueLleguenALaVez() throws Exception {
        ejecutar(iniciar(cuerpo(null, null))).andExpect(status().isCreated());
        entityManager.flush();

        assertThatThrownBy(
                        () ->
                                jdbc.update(
                                        "insert into evaluacion (id_sujeto, id_version_formulario,"
                                                + " id_test, id_periodo, estado, fecha_inicio)"
                                                + " values (?, ?, ?, ?, 'EN_CURSO', now())",
                                        sujetoId,
                                        version.getIdVersionFormulario(),
                                        test.getIdTest(),
                                        periodoId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void e3DeniegaSesionSinPermisoRealYNoEscribe() throws Exception {
        permisosPrueba = Set.of();
        ejecutar(iniciar(cuerpo(null, null)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));
        assertThat(contar("evaluacion where id_sujeto = " + sujetoId)).isZero();
    }

    @Test
    void e3DeniegaPermisoSinAsignacionYNoEscribe() throws Exception {
        jdbc.update("delete from cuenta_sujeto_asignado where id_cuenta=?", cuentaId);
        ejecutar(iniciar(cuerpo(null, null))).andExpect(status().isForbidden());
        assertThat(contar("evaluacion where id_sujeto = " + sujetoId)).isZero();
    }

    @Test
    void e4NoExponeDatosCuandoSeRetiraLaAsignacion() throws Exception {
        long id =
                idDe(
                        ejecutar(iniciar(cuerpo(null, null)))
                                .andExpect(status().isCreated())
                                .andReturn());
        jdbc.update("delete from cuenta_sujeto_asignado where id_cuenta=?", cuentaId);
        ejecutar(get("/api/v1/evaluaciones/" + id))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void e4DeniegaAunqueConserveAsignacionSiFaltaPermiso() throws Exception {
        long id =
                idDe(
                        ejecutar(iniciar(cuerpo(null, null)))
                                .andExpect(status().isCreated())
                                .andReturn());
        permisosPrueba = Set.of("SUJETO_LEER");
        ejecutar(get("/api/v1/evaluaciones/" + id)).andExpect(status().isForbidden());
    }

    @Test
    void decanoAsignadoSoloRecibeIdentificacionInstitucional() throws Exception {
        rolPrueba = "DECANO";
        permisosPrueba = Set.of("SUJETO_LEER");
        ejecutar(get("/api/v1/sujetos/" + sujetoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").doesNotExist())
                .andExpect(jsonPath("$.apellidos").doesNotExist());
    }

    @Test
    void e8FinalizaSinInventarResultadosYRepiteSinDuplicarAuditoria() throws Exception {
        long id =
                idDe(
                        ejecutar(iniciar(cuerpo(null, null)))
                                .andExpect(status().isCreated())
                                .andReturn());
        jdbc.update(
                "update evaluacion_subprueba set estado='CERRADA',fecha_inicio=statement_timestamp(),fecha_fin_limite=statement_timestamp()+interval '5 minutes',fecha_cierre=statement_timestamp() where id_evaluacion=?",
                id);
        rolPrueba = "SUJETO";
        permisosPrueba = Set.of("EVALUACION_FINALIZAR", "RECUPERAR_INTENTO");
        var primero =
                ejecutar(post("/api/v1/evaluaciones/" + id + "/finalizar"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.estado").value("FINALIZADA"))
                        .andExpect(jsonPath("$.estadoResultado").value("PENDIENTE_VALIDACION"))
                        .andExpect(jsonPath("$.resultadoId").isNumber())
                        .andReturn();
        String cuerpoPrimero = primero.getResponse().getContentAsString();
        var repetido =
                ejecutar(post("/api/v1/evaluaciones/" + id + "/finalizar"))
                        .andExpect(status().isOk())
                        .andReturn();
        assertThat(repetido.getResponse().getContentAsString()).isEqualTo(cuerpoPrimero);
        assertThat(contar("resultado where id_evaluacion=" + id)).isEqualTo(1);
        assertThat(
                        contar(
                                "evento_auditoria where entidad_id="
                                        + id
                                        + " and accion='FINALIZAR_EVALUACION'"))
                .isEqualTo(1);
    }

    @Test
    void e8NoFinalizaConSubpruebasPendientesNiCreaResultado() throws Exception {
        long id =
                idDe(
                        ejecutar(iniciar(cuerpo(null, null)))
                                .andExpect(status().isCreated())
                                .andReturn());
        rolPrueba = "SUJETO";
        permisosPrueba = Set.of("EVALUACION_FINALIZAR");
        ejecutar(post("/api/v1/evaluaciones/" + id + "/finalizar"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("SUBPRUEBAS_PENDIENTES"));
        assertThat(contar("resultado where id_evaluacion=" + id)).isZero();
        assertThat(
                        jdbc.queryForObject(
                                "select estado from evaluacion where id_evaluacion=?",
                                String.class,
                                id))
                .isEqualTo("EN_CURSO");
    }

    @Test
    void e8SinPermisoOAsignacionNoModificaLaEvaluacion() throws Exception {
        long id =
                idDe(
                        ejecutar(iniciar(cuerpo(null, null)))
                                .andExpect(status().isCreated())
                                .andReturn());
        ejecutar(post("/api/v1/evaluaciones/" + id + "/finalizar"))
                .andExpect(status().isForbidden());
        rolPrueba = "SUJETO";
        permisosPrueba = Set.of("EVALUACION_FINALIZAR");
        jdbc.update("delete from cuenta_sujeto_asignado where id_cuenta=?", cuentaId);
        ejecutar(post("/api/v1/evaluaciones/" + id + "/finalizar"))
                .andExpect(status().isForbidden());
        assertThat(contar("resultado where id_evaluacion=" + id)).isZero();
        assertThat(
                        jdbc.queryForObject(
                                "select estado from evaluacion where id_evaluacion=?",
                                String.class,
                                id))
                .isEqualTo("EN_CURSO");
    }

    @Test
    void e8CierraActivaVencidaSinBloquearFinalizacion() throws Exception {
        long id =
                idDe(
                        ejecutar(iniciar(cuerpo(null, null)))
                                .andExpect(status().isCreated())
                                .andReturn());
        jdbc.update(
                "update evaluacion set fecha_inicio=statement_timestamp()-interval '2 hours' where id_evaluacion=?",
                id);
        jdbc.update(
                "update evaluacion_subprueba set estado='CERRADA',fecha_inicio=statement_timestamp()-interval '1 hour',fecha_fin_limite=statement_timestamp()-interval '30 minutes',fecha_cierre=statement_timestamp()-interval '30 minutes' where id_evaluacion=?",
                id);
        jdbc.update(
                "update evaluacion_subprueba set estado='ACTIVA',fecha_cierre=null where id_evaluacion=? and codigo='VOC1'",
                id);
        rolPrueba = "SUJETO";
        permisosPrueba = Set.of("EVALUACION_FINALIZAR", "RECUPERAR_INTENTO");
        ejecutar(post("/api/v1/evaluaciones/" + id + "/finalizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADA"));
        assertThat(
                        jdbc.queryForObject(
                                "select fecha_cierre=fecha_fin_limite from evaluacion_subprueba where id_evaluacion=? and codigo='VOC1'",
                                Boolean.class,
                                id))
                .isTrue();
    }

    // ---------- Datos ficticios ----------

    private ResultActions ejecutar(RequestBuilder solicitud) throws Exception {
        when(autenticacion.validar(anyString())).thenReturn(java.util.Optional.empty());
        when(autenticacion.validar("token-ficticio"))
                .thenReturn(
                        java.util.Optional.of(
                                new IdentidadSesion(cuentaId, 1L, rolPrueba, permisosPrueba)));
        return mockMvc.perform(
                context -> {
                    var peticion = solicitud.buildRequest(context);
                    peticion.addHeader("Authorization", BEARER);
                    return peticion;
                });
    }

    private org.springframework.test.web.servlet.RequestBuilder iniciar(Map<String, Object> cuerpo)
            throws Exception {
        return post("/api/v1/evaluaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(cuerpo));
    }

    private Map<String, Object> cuerpo(List<String> subpruebas, String letra) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("testId", test.getIdTest());
        cuerpo.put("versionFormularioId", version.getIdVersionFormulario());
        cuerpo.put("sujetoId", sujetoId);
        cuerpo.put("periodoId", periodoId);
        if (subpruebas != null) {
            cuerpo.put("subpruebas", subpruebas);
        }
        if (letra != null) {
            cuerpo.put("fluidez", Map.of("letraInicial", letra));
        }
        return cuerpo;
    }

    private long idDe(MvcResult resultado) throws Exception {
        return json.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();
    }

    private int contar(String desde) {
        entityManager.flush();
        Integer total = jdbc.queryForObject("select count(*) from " + desde, Integer.class);
        return total == null ? 0 : total;
    }

    private static String corto() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private TestJpaEntity crearTest(String codigo) {
        return testRepository.saveAndFlush(
                new TestJpaEntity(codigo, "Test ficticio " + codigo, null, true));
    }

    private VersionFormularioJpaEntity crearVersionPublicada(
            TestJpaEntity propietario, List<String> codigos) {
        VersionFormularioJpaEntity nueva =
                versionRepository.saveAndFlush(
                        new VersionFormularioJpaEntity(
                                propietario, "v-" + corto(), null, "FUENTE-PRUEBA"));
        // GEN-07-BE exige configuraciones antes de publicar y no admite cambios después:
        // primero se configuran las subpruebas en BORRADOR y al final se publica.
        int orden = 1;
        for (String codigo : codigos) {
            SubpruebaJpaEntity subprueba =
                    subpruebaRepository
                            .findByTest_IdTestAndCodigo(propietario.getIdTest(), codigo)
                            .orElseGet(
                                    () ->
                                            subpruebaRepository.saveAndFlush(
                                                    new SubpruebaJpaEntity(
                                                            propietario,
                                                            codigo,
                                                            "Subprueba " + codigo,
                                                            null)));
            boolean libre = codigo.equals("FV");
            configuracionRepository.saveAndFlush(
                    new ConfiguracionSubpruebaJpaEntity(
                            nueva,
                            subprueba,
                            orden++,
                            300,
                            libre ? TipoRespuesta.TEXTO_LIBRE : TipoRespuesta.SELECCION,
                            "instrucciones-ficticias",
                            null,
                            libre ? 75 : null));
        }

        nueva.aplicarEstado(
                EstadoVersionFormulario.PUBLICADA, Instant.now(), "Evidencia ficticia de prueba");
        return versionRepository.saveAndFlush(nueva);
    }

    private Map<String, ConfiguracionSubpruebaJpaEntity> configuracionesDe(
            VersionFormularioJpaEntity versionFormulario) {
        Map<String, ConfiguracionSubpruebaJpaEntity> porCodigo = new LinkedHashMap<>();
        configuracionRepository
                .findByVersionFormulario_IdVersionFormularioOrderByOrdenAplicacionAsc(
                        versionFormulario.getIdVersionFormulario())
                .forEach(c -> porCodigo.put(c.getSubprueba().getCodigo(), c));
        return porCodigo;
    }

    private Long crearSujeto(String nombres, String apellidos) {
        return sujetoRepository
                .saveAndFlush(new SujetoJpaEntity("T-" + corto(), nombres, apellidos, null))
                .getIdSujeto();
    }

    private Long crearPeriodo(boolean activo) {
        return periodoRepository
                .saveAndFlush(
                        new PeriodoJpaEntity(
                                "P-" + corto(), "Período de prueba", null, null, activo))
                .getIdPeriodo();
    }
}
