package com.universidad.tests.resultados;

import static com.universidad.tests.resultados.ResultadosDatosPrueba.EV_CALCULANDO;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.EV_DISPONIBLE;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.EV_EN_CURSO;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.EV_PENDIENTE;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.SUJETO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.application.usecase.resultados.ConsultarResultadosUseCase;
import com.universidad.tests.controller.resultados.ResultadosController;
import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException;
import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException.Motivo;
import com.universidad.tests.domain.model.resultados.ContenedorResultado;
import com.universidad.tests.domain.model.resultados.EvaluacionConsultada;
import com.universidad.tests.domain.port.resultados.AuditoriaResultadoPort;
import com.universidad.tests.domain.port.resultados.AutorizacionResultadoPort;
import com.universidad.tests.domain.port.resultados.EvaluacionConsultaPort;
import com.universidad.tests.domain.port.resultados.ResultadoConsultaPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * GEN-05: flujo completo de E9 dentro del backend (HTTP → caso de uso → puertos → JSON), con
 * permisos.
 *
 * <p>Los puertos son dobles en memoria. Las reglas de rol de {@link ActorDePrueba} solo sirven para
 * probar que el caso de uso respeta la decisión de autorización; los permisos definitivos los fija
 * DOC-04.
 */
@WebMvcTest(ResultadosController.class)
@Import({ConsultarResultadosUseCase.class, ConsultaResultadosPermisosTest.Dobles.class})
class ConsultaResultadosPermisosTest {

    private static final String RUTA = "/api/v1/evaluaciones/{id}/resultados";

    @Autowired private MockMvc mvc;
    @Autowired private ActorDePrueba actor;
    @Autowired private AuditoriaEnMemoria auditoria;

    @BeforeEach
    void reiniciar() {
        actor.sinSesion();
        auditoria.eventos.clear();
    }

    @Test
    void profesionalConPermisoYAlcanceConsultaElResultado() throws Exception {
        actor.inicia("PSICOLOGO", Set.of(SUJETO));

        mvc.perform(get(RUTA, EV_DISPONIBLE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoResultado").value("DISPONIBLE"))
                .andExpect(jsonPath("$.factores", hasSize(4)))
                .andExpect(jsonPath("$.factores[1].percentil").isEmpty())
                .andExpect(jsonPath("$.factores[2].puntuacionDirecta").value(0))
                .andExpect(jsonPath("$.factores[3].puntuacionDirecta").isEmpty());

        assertThat(auditoria.eventos).containsExactly("16/101/501");
    }

    @Test
    void sinSesionResponde401YNoRegistraConsulta() throws Exception {
        mvc.perform(get(RUTA, EV_DISPONIBLE))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));

        assertThat(auditoria.eventos).isEmpty();
    }

    @Test
    void rolSinPermisoResponde403AunqueLaEvaluacionNoExista() throws Exception {
        actor.inicia("ACADEMICO", Set.of(SUJETO));

        mvc.perform(get(RUTA, EV_DISPONIBLE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));
        mvc.perform(get(RUTA, 999))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));

        assertThat(auditoria.eventos).isEmpty();
    }

    @Test
    void evaluadoNoRecibeResultadosProtegidos() throws Exception {
        actor.inicia("SUJETO", Set.of(SUJETO));

        mvc.perform(get(RUTA, EV_DISPONIBLE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"))
                .andExpect(jsonPath("$.factores").doesNotExist());
    }

    @Test
    void profesionalFueraDeSuAlcanceResponde403() throws Exception {
        actor.inicia("PSICOLOGO", Set.of(77L));

        mvc.perform(get(RUTA, EV_DISPONIBLE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.factores").doesNotExist());

        assertThat(auditoria.eventos).isEmpty();
    }

    @Test
    void conPermisoDistingueLosEstadosYErrores() throws Exception {
        actor.inicia("COORDINADOR", Set.of(SUJETO));

        mvc.perform(get(RUTA, EV_PENDIENTE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoResultado").value("PENDIENTE_VALIDACION"))
                .andExpect(jsonPath("$.calificacionId").isEmpty())
                .andExpect(jsonPath("$.factores", hasSize(0)));
        mvc.perform(get(RUTA, EV_CALCULANDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoResultado").value("CALCULANDO"))
                .andExpect(jsonPath("$.factores", hasSize(0)));
        mvc.perform(get(RUTA, EV_EN_CURSO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("EVALUACION_NO_FINALIZADA"));
        mvc.perform(get(RUTA, 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("EVALUACION_NO_ENCONTRADA"));

        assertThat(auditoria.eventos).containsExactly("17/102/null", "18/102/null");
    }

    // ----- Dobles de prueba -----

    /** Actor autenticado simulado. Reglas solo de prueba: PSICOLOGO y COORDINADOR consultan. */
    static class ActorDePrueba implements AutorizacionResultadoPort {
        private static final Set<String> ROLES_CON_PERMISO = Set.of("PSICOLOGO", "COORDINADOR");
        private String rol;
        private Set<Long> sujetosEnAlcance = Set.of();

        void sinSesion() {
            rol = null;
            sujetosEnAlcance = Set.of();
        }

        void inicia(String rolActivo, Set<Long> alcance) {
            rol = rolActivo;
            sujetosEnAlcance = alcance;
        }

        @Override
        public void exigirPermisoConsultarResultados() {
            if (rol == null) {
                throw new ConsultaResultadoException(Motivo.NO_AUTENTICADO, "No hay sesión.");
            }
            if (!ROLES_CON_PERMISO.contains(rol)) {
                throw ConsultaResultadoException.sinPermiso();
            }
        }

        @Override
        public void exigirAlcanceSobre(EvaluacionConsultada evaluacion) {
            if (!sujetosEnAlcance.contains(evaluacion.sujetoId())) {
                throw ConsultaResultadoException.sinPermiso();
            }
        }
    }

    static class AuditoriaEnMemoria implements AuditoriaResultadoPort {
        final List<String> eventos = new ArrayList<>();

        @Override
        public void registrarConsulta(Long evaluacionId, Long resultadoId, Long calificacionId) {
            eventos.add(evaluacionId + "/" + resultadoId + "/" + calificacionId);
        }
    }

    @TestConfiguration
    static class Dobles {
        @Bean
        ActorDePrueba actorDePrueba() {
            return new ActorDePrueba();
        }

        @Bean
        AuditoriaEnMemoria auditoriaEnMemoria() {
            return new AuditoriaEnMemoria();
        }

        @Bean
        EvaluacionConsultaPort evaluacionesEnMemoria() {
            Map<Long, EvaluacionConsultada> datos =
                    Map.of(
                            EV_DISPONIBLE, ResultadosDatosPrueba.finalizada(EV_DISPONIBLE),
                            EV_PENDIENTE, ResultadosDatosPrueba.finalizada(EV_PENDIENTE),
                            EV_CALCULANDO, ResultadosDatosPrueba.finalizada(EV_CALCULANDO),
                            EV_EN_CURSO, ResultadosDatosPrueba.enCurso(EV_EN_CURSO));
            return id -> Optional.ofNullable(datos.get(id));
        }

        @Bean
        ResultadoConsultaPort resultadosEnMemoria() {
            Map<Long, ContenedorResultado> datos =
                    Map.of(
                            EV_DISPONIBLE,
                            ResultadosDatosPrueba.disponible(EV_DISPONIBLE),
                            EV_PENDIENTE,
                            ResultadosDatosPrueba.sinRevision(
                                    EV_PENDIENTE, ContenedorResultado.PENDIENTE_VALIDACION),
                            EV_CALCULANDO,
                            ResultadosDatosPrueba.sinRevision(
                                    EV_CALCULANDO, ContenedorResultado.CALCULANDO));
            return id -> Optional.ofNullable(datos.get(id));
        }
    }
}
