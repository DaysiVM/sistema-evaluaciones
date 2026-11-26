package com.universidad.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.tests.application.service.AutorizacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.application.service.MotorSubpruebasService;
import com.universidad.tests.domain.exception.MotorSubpruebasException;
import com.universidad.tests.domain.exception.MotorSubpruebasException.Codigo;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.EstadoSubprueba;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.port.AutorizacionMotorSubpruebasPort;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.dto.request.RegistrarRespuestasRequest;
import com.universidad.tests.dto.request.RegistrarRespuestasRequest.Palabra;
import com.universidad.tests.dto.request.RegistrarRespuestasRequest.Seleccion;
import com.universidad.tests.infrastructure.persistence.entity.ConfiguracionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.OpcionRespuestaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.PeriodoJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.PreguntaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.SubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.SujetoJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.TestJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.VersionFormularioJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MotorSubpruebasIntegrationTest {
    @Autowired private MotorSubpruebasService motor;
    @Autowired private EvaluacionRepository evaluaciones;
    @Autowired private EntityManager em;
    @Autowired private TransactionTemplate transacciones;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @MockitoBean private AutorizacionMotorSubpruebasPort autorizacion;
    @MockitoBean private AutorizacionService sesiones;

    private Long idTest;
    private Long idEvaluacion;
    private Long primera;
    private Long segunda;
    private Long otraVersion;
    private Long idCuenta;

    @BeforeEach
    void preparar() {
        transacciones.executeWithoutResult(
                estado -> {
                    idCuenta =
                            jdbc.queryForObject(
                                    """
                                    insert into cuenta (correo, resumen_contrasena, nombres, apellidos, estado)
                                    values (?, 'resumen-ficticio', 'Cuenta', 'Ficticia', 'ACTIVA')
                                    returning id_cuenta
                                    """,
                                    Long.class,
                                    "motor-" + UUID.randomUUID() + "@example.test");
                    var sujeto = new SujetoJpaEntity(null, "Sujeto", "Ficticio", null);
                    var periodo =
                            new PeriodoJpaEntity(
                                    UUID.randomUUID().toString(),
                                    "Periodo ficticio",
                                    null,
                                    null,
                                    true);
                    em.persist(sujeto);
                    em.persist(periodo);
                    var test =
                            new TestJpaEntity(
                                    "MOTOR_" + UUID.randomUUID(), "Prueba ficticia", null, true);
                    em.persist(test);
                    idTest = test.getIdTest();
                    var seleccion =
                            new SubpruebaJpaEntity(test, "SUB", "SelecciÃ³n ficticia", null);
                    var fv = new SubpruebaJpaEntity(test, "FV", "Fluidez ficticia", null);
                    em.persist(seleccion);
                    em.persist(fv);
                    var version = new VersionFormularioJpaEntity(test, "v1", null, "FUENTE");
                    var versionDistinta =
                            new VersionFormularioJpaEntity(test, "v2", null, "FUENTE");
                    em.persist(version);
                    em.persist(versionDistinta);
                    var config =
                            new ConfiguracionSubpruebaJpaEntity(
                                    version,
                                    seleccion,
                                    1,
                                    300,
                                    TipoRespuesta.SELECCION,
                                    "INSTR",
                                    null,
                                    null);
                    var configFv =
                            new ConfiguracionSubpruebaJpaEntity(
                                    version,
                                    fv,
                                    2,
                                    300,
                                    TipoRespuesta.TEXTO_LIBRE,
                                    "INSTR",
                                    null,
                                    2);
                    var configDistinta =
                            new ConfiguracionSubpruebaJpaEntity(
                                    versionDistinta,
                                    seleccion,
                                    1,
                                    300,
                                    TipoRespuesta.SELECCION,
                                    "INSTR",
                                    null,
                                    null);
                    em.persist(config);
                    em.persist(configFv);
                    em.persist(configDistinta);
                    primera = pregunta(config, 1, "A", "B");
                    segunda = pregunta(config, 2, "C");
                    otraVersion = pregunta(configDistinta, 1, "Z");
                    idEvaluacion =
                            evaluaciones
                                    .guardar(
                                            new Evaluacion(
                                                    null,
                                                    sujeto.getIdSujeto(),
                                                    periodo.getIdPeriodo(),
                                                    idTest,
                                                    version.getIdVersionFormulario(),
                                                    EstadoEvaluacion.EN_CURSO,
                                                    Instant.now(),
                                                    null,
                                                    List.of(
                                                            subprueba(config, "SUB", 1, null),
                                                            subprueba(configFv, "FV", 2, "M"))))
                                    .idEvaluacion();
                });
        when(sesiones.exigirSesion())
                .thenReturn(new IdentidadSesion(idCuenta, 1L, "SUJETO", Set.of()));
    }

    @AfterEach
    void limpiar() {
        if (idEvaluacion == null) {
            return;
        }
        transacciones.executeWithoutResult(
                estado -> {
                    jdbc.update(
                            "delete from evento_auditoria where id_evaluacion = ?", idEvaluacion);
                    jdbc.update("delete from cuenta where id_cuenta = ?", idCuenta);
                    jdbc.update("delete from evaluacion where id_evaluacion = ?", idEvaluacion);
                    jdbc.update(
                            """
                    delete from opcion_respuesta where id_pregunta in (
                        select p.id_pregunta from pregunta p
                        join configuracion_subprueba c using (id_configuracion_subprueba)
                        join version_formulario v using (id_version_formulario) where v.id_test = ?)
                    """,
                            idTest);
                    jdbc.update(
                            """
                    delete from pregunta where id_configuracion_subprueba in (
                        select c.id_configuracion_subprueba from configuracion_subprueba c
                        join version_formulario v using (id_version_formulario) where v.id_test = ?)
                    """,
                            idTest);
                    jdbc.update(
                            """
                    delete from configuracion_subprueba where id_version_formulario in (
                        select id_version_formulario from version_formulario where id_test = ?)
                    """,
                            idTest);
                    jdbc.update("delete from version_formulario where id_test = ?", idTest);
                    jdbc.update("delete from subprueba where id_test = ?", idTest);
                    jdbc.update("delete from test where id_test = ?", idTest);
                });
    }

    @Test
    void e5AuditaLaTransicionUnaSolaVezAunqueSeReintente() throws Exception {
        String ruta = "/api/v1/evaluaciones/" + idEvaluacion + "/subpruebas/SUB/iniciar";
        mvc.perform(post(ruta)).andExpect(status().isOk());
        mvc.perform(post(ruta)).andExpect(status().isOk());

        Long idSubprueba =
                jdbc.queryForObject(
                        "select id_evaluacion_subprueba from evaluacion_subprueba"
                                + " where id_evaluacion = ? and codigo = 'SUB'",
                        Long.class,
                        idEvaluacion);
        assertThat(
                        jdbc.queryForList(
                                """
                        select id_cuenta, accion, entidad_tipo, entidad_id, id_evaluacion_subprueba
                        from evento_auditoria where id_evaluacion = ?
                        """,
                                idEvaluacion))
                .containsExactly(
                        Map.of(
                                "id_cuenta", idCuenta,
                                "accion", "INICIAR_SUBPRUEBA",
                                "entidad_tipo", "EVALUACION_SUBPRUEBA",
                                "entidad_id", idSubprueba,
                                "id_evaluacion_subprueba", idSubprueba));
    }

    @Test
    void e5RevierteElInicioSiFallaLaInsercionDelEvento() {
        when(sesiones.exigirSesion())
                .thenReturn(new IdentidadSesion(Long.MAX_VALUE, 1L, "SUJETO", Set.of()));

        assertThatThrownBy(() -> motor.iniciar(idEvaluacion, "SUB"))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(evaluaciones.buscarPorId(idEvaluacion).orElseThrow().subpruebaActiva())
                .isEmpty();
    }

    @Test
    void e5e6e7GuardanYRecuperanSeleccionVersionadaPorHttp() throws Exception {
        String ruta = "/api/v1/evaluaciones/" + idEvaluacion + "/subpruebas/SUB";
        var cuerpoInicio =
                mvc.perform(post(ruta + "/iniciar"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.estado").value("ACTIVA"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        var inicio = json.readTree(cuerpoInicio);
        var instanteInicio = Instant.parse(inicio.path("inicio").asText());
        var limite = instanteInicio.plusSeconds(300).toString();
        mvc.perform(post(ruta + "/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inicio").value(instanteInicio.toString()))
                .andExpect(jsonPath("$.finLimite").value(limite));

        var lote = seleccion(new Seleccion(primera, "A"), new Seleccion(segunda, "C"));
        mvc.perform(
                        put(ruta + "/respuestas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsBytes(lote)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registradas").value(2));
        mvc.perform(
                        put(ruta + "/respuestas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsBytes(lote)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRespondidas").value(2));
        var cambio = seleccion(new Seleccion(primera, "B"));
        mvc.perform(
                        put(ruta + "/respuestas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsBytes(cambio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRespondidas").value(2));

        mvc.perform(get(ruta + "/preguntas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preguntas.length()").value(2))
                .andExpect(jsonPath("$.preguntas[0].id").value(primera))
                .andExpect(jsonPath("$.preguntas[1].id").value(segunda))
                .andExpect(jsonPath("$.preguntas[0].respuestaActual").value("B"))
                .andExpect(jsonPath("$.preguntas[1].respuestaActual").value("C"))
                .andExpect(jsonPath("$.preguntas[0].recursoUrl").value("/recursos/v1/p1.png"))
                .andExpect(
                        jsonPath("$.preguntas[0].opciones[0].recursoUrl")
                                .value("/recursos/v1/A.png"));
        mvc.perform(post(ruta + "/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finLimite").value(limite));
    }

    @Test
    void e7DevuelveTiempoAgotadoAntesYDespuesDelCierreTemporalPorHttp() throws Exception {
        motor.iniciar(idEvaluacion, "SUB");
        jdbc.update(
                """
                update evaluacion_subprueba
                set fecha_inicio = current_timestamp - interval '301 seconds',
                    fecha_fin_limite = current_timestamp - interval '1 second'
                where id_evaluacion = ? and codigo = 'SUB'
                """,
                idEvaluacion);
        var solicitud =
                put("/api/v1/evaluaciones/{id}/subpruebas/SUB/respuestas", idEvaluacion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsBytes(seleccion(new Seleccion(primera, "A"))));
        mvc.perform(solicitud)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("TIEMPO_AGOTADO"));

        jdbc.update(
                """
                update evaluacion_subprueba set estado = 'CERRADA', fecha_cierre = fecha_fin_limite
                where id_evaluacion = ? and codigo = 'SUB'
                """,
                idEvaluacion);
        mvc.perform(solicitud)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("TIEMPO_AGOTADO"));
        assertThat(
                        jdbc.queryForObject(
                                "select count(*) from respuesta_evaluacion where id_evaluacion = ?",
                                Long.class,
                                idEvaluacion))
                .isZero();
    }

    @Test
    void loteInvalidoNoPersisteCambiosParciales() {
        motor.iniciar(idEvaluacion, "SUB");
        for (var invalida : List.of(new Seleccion(otraVersion, "Z"), new Seleccion(primera, "C"))) {
            assertThatThrownBy(
                            () ->
                                    motor.registrarRespuestas(
                                            idEvaluacion,
                                            "SUB",
                                            seleccion(new Seleccion(segunda, "C"), invalida)))
                    .isInstanceOfSatisfying(
                            MotorSubpruebasException.class,
                            ex -> assertThat(ex.codigo()).isEqualTo(Codigo.RESPUESTA_INVALIDA));
        }
        assertThat(motor.consultarPreguntas(idEvaluacion, "SUB").preguntas())
                .allMatch(p -> p.respuestaActual() == null);
    }

    @Test
    void fluidezRecuperaLasPalabrasOrdenadasConTextoOriginalYPermiteCambios() {
        iniciarFluidez();
        var palabras =
                new RegistrarRespuestasRequest(
                        null, List.of(new Palabra(2, "  ejemplo  "), new Palabra(1, "ficticia")));
        assertThat(motor.registrarRespuestas(idEvaluacion, "FV", palabras).totalRespondidas())
                .isEqualTo(2);
        assertThat(motor.registrarRespuestas(idEvaluacion, "FV", palabras).totalRespondidas())
                .isEqualTo(2);
        motor.registrarRespuestas(
                idEvaluacion,
                "FV",
                new RegistrarRespuestasRequest(null, List.of(new Palabra(1, "otra"))));

        var contenido = motor.consultarPreguntas(idEvaluacion, "FV");
        assertThat(contenido.preguntas()).isEmpty();
        assertThat(contenido.palabras()).extracting(p -> p.ordenEntrada()).containsExactly(1, 2);
        assertThat(contenido.palabras())
                .extracting(p -> p.texto())
                .containsExactly("otra", "  ejemplo  ");
    }

    @Test
    void e7RechazaTextoNoRepresentableEnPostgresSinGuardarElLote() throws Exception {
        iniciarFluidez();
        var lote =
                new RegistrarRespuestasRequest(
                        null, List.of(new Palabra(1, "mesa"), new Palabra(2, "m\0ar")));

        mvc.perform(
                        put("/api/v1/evaluaciones/{id}/subpruebas/FV/respuestas", idEvaluacion)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsBytes(lote)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));
        assertThat(motor.consultarPreguntas(idEvaluacion, "FV").palabras()).isEmpty();
    }

    @Test
    void inicioYRespuestasConcurrentesSonIdempotentes() throws Exception {
        var inicios =
                ejecutarEnParalelo(
                        () -> motor.iniciar(idEvaluacion, "SUB"),
                        () -> motor.iniciar(idEvaluacion, "SUB"));
        assertThat(inicios.get(0).inicio()).isEqualTo(inicios.get(1).inicio());
        assertThat(
                        jdbc.queryForObject(
                                """
                select count(*) from evaluacion_subprueba where id_evaluacion = ? and estado = 'ACTIVA'
                """,
                                Long.class,
                                idEvaluacion))
                .isEqualTo(1L);

        var avances =
                ejecutarEnParalelo(
                        () ->
                                motor.registrarRespuestas(
                                        idEvaluacion,
                                        "SUB",
                                        seleccion(new Seleccion(primera, "A"))),
                        () ->
                                motor.registrarRespuestas(
                                        idEvaluacion,
                                        "SUB",
                                        seleccion(new Seleccion(primera, "B"))));
        assertThat(avances).allMatch(avance -> avance.totalRespondidas() == 1);
        assertThat(
                        jdbc.queryForObject(
                                "select count(*) from respuesta_evaluacion where id_evaluacion = ?",
                                Long.class,
                                idEvaluacion))
                .isEqualTo(1L);
    }

    @Test
    void dosCodigosConcurrentesSoloActivanLaSiguienteSubprueba() throws Exception {
        var resultados =
                ejecutarEnParalelo(
                        () -> motor.iniciar(idEvaluacion, "SUB").estado(),
                        () -> {
                            try {
                                return motor.iniciar(idEvaluacion, "FV").estado();
                            } catch (MotorSubpruebasException ex) {
                                return ex.codigo().name();
                            }
                        });
        assertThat(resultados.get(0)).isEqualTo("ACTIVA");
        assertThat(resultados.get(1)).isIn("SUBPRUEBA_FUERA_DE_ORDEN", "HAY_SUBPRUEBA_ACTIVA");
        assertThat(evaluaciones.buscarPorId(idEvaluacion).orElseThrow().subpruebas())
                .extracting(EvaluacionSubprueba::estado)
                .containsExactly(EstadoSubprueba.ACTIVA, EstadoSubprueba.PENDIENTE);
    }

    private <T> List<T> ejecutarEnParalelo(
            Callable<T> primeraOperacion, Callable<T> segundaOperacion) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch listos = new CountDownLatch(2);
        try {
            Future<T> primero =
                    executor.submit(() -> ejecutarAlEstarListos(listos, primeraOperacion));
            Future<T> segundo =
                    executor.submit(() -> ejecutarAlEstarListos(listos, segundaOperacion));
            return List.of(primero.get(10, TimeUnit.SECONDS), segundo.get(10, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
        }
    }

    private void iniciarFluidez() {
        motor.iniciar(idEvaluacion, "SUB");
        jdbc.update(
                """
                update evaluacion_subprueba set estado = 'CERRADA', fecha_cierre = fecha_inicio
                where id_evaluacion = ? and codigo = 'SUB'
                """,
                idEvaluacion);
        assertThat(motor.iniciar(idEvaluacion, "FV").letraInicial()).isEqualTo("M");
    }

    private <T> T ejecutarAlEstarListos(CountDownLatch listos, Callable<T> operacion)
            throws Exception {
        listos.countDown();
        if (!listos.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Las solicitudes no se iniciaron en paralelo");
        }
        return operacion.call();
    }

    private Long pregunta(
            ConfiguracionSubpruebaJpaEntity configuracion, int numero, String... letras) {
        var pregunta =
                new PreguntaJpaEntity(
                        configuracion,
                        Integer.toString(numero),
                        numero,
                        "Reactivo ficticio",
                        "/recursos/v1/p" + numero + ".png");
        em.persist(pregunta);
        for (int i = 0; i < letras.length; i++) {
            em.persist(
                    new OpcionRespuestaJpaEntity(
                            pregunta,
                            letras[i],
                            i + 1,
                            "OpciÃ³n ficticia",
                            "/recursos/v1/" + letras[i] + ".png"));
        }
        return pregunta.getIdPregunta();
    }

    private EvaluacionSubprueba subprueba(
            ConfiguracionSubpruebaJpaEntity configuracion, String codigo, int orden, String letra) {
        return new EvaluacionSubprueba(
                null,
                null,
                configuracion.getIdConfiguracionSubprueba(),
                codigo,
                "Subprueba ficticia",
                orden,
                300,
                EstadoSubprueba.PENDIENTE,
                null,
                true,
                null,
                null,
                null,
                letra,
                2);
    }

    private RegistrarRespuestasRequest seleccion(Seleccion... items) {
        return new RegistrarRespuestasRequest(List.of(items), null);
    }
}
