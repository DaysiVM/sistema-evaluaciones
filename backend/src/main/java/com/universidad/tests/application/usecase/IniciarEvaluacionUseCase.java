package com.universidad.tests.application.usecase;

import com.universidad.tests.application.service.AutorizacionService;
import com.universidad.tests.application.service.RegistroAuditoria;
import com.universidad.tests.application.usecase.vista.EvaluacionVista;
import com.universidad.tests.domain.exception.ErroresEvaluacion;
import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.model.Periodo;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.Test;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.port.AutorizacionEvaluacionPort;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.domain.repository.PeriodoRepository;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.domain.repository.SujetoRepository;
import com.universidad.tests.domain.repository.TestRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * E3 — Iniciar una evaluación completa o parcial (CU-BFA-P03, CU-GEN-04). Común a todos los tests:
 * las reglas propias de cada uno llegan por {@link ReglaInicioTest}.
 */
@Service
public class IniciarEvaluacionUseCase {

    private final AutorizacionEvaluacionPort autorizacion;
    private final AutorizacionService sesiones;
    private final RegistroAuditoria auditoria;
    private final TestRepository testRepository;
    private final VersionFormularioRepository versionRepository;
    private final ConfiguracionSubpruebaRepository configuracionRepository;
    private final SubpruebaRepository subpruebaRepository;
    private final SujetoRepository sujetoRepository;
    private final PeriodoRepository periodoRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final List<ReglaInicioTest> reglas;
    private final Clock reloj;

    public IniciarEvaluacionUseCase(
            AutorizacionEvaluacionPort autorizacion,
            AutorizacionService sesiones,
            RegistroAuditoria auditoria,
            TestRepository testRepository,
            VersionFormularioRepository versionRepository,
            ConfiguracionSubpruebaRepository configuracionRepository,
            SubpruebaRepository subpruebaRepository,
            SujetoRepository sujetoRepository,
            PeriodoRepository periodoRepository,
            EvaluacionRepository evaluacionRepository,
            List<ReglaInicioTest> reglas,
            Clock reloj) {
        this.autorizacion = autorizacion;
        this.sesiones = sesiones;
        this.auditoria = auditoria;
        this.testRepository = testRepository;
        this.versionRepository = versionRepository;
        this.configuracionRepository = configuracionRepository;
        this.subpruebaRepository = subpruebaRepository;
        this.sujetoRepository = sujetoRepository;
        this.periodoRepository = periodoRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.reglas = List.copyOf(reglas);
        this.reloj = reloj;
    }

    @Transactional
    public EvaluacionVista iniciar(IniciarEvaluacionComando comando) {
        long idCuenta = sesiones.exigirSesion().idCuenta();
        autorizacion.exigirPermisoIniciarEvaluacion(comando.sujetoId());

        if (comando.testId() != null) {
            buscarTestActivo(comando.testId());
        }

        VersionFormulario version =
                versionRepository
                        .buscarPorId(comando.versionFormularioId())
                        .orElseThrow(
                                () ->
                                        ErroresEvaluacion.versionIncompatible(
                                                "No existe la versión "
                                                        + comando.versionFormularioId()
                                                        + " del formulario."));

        if (comando.testId() != null && !comando.testId().equals(version.getIdTest())) {
            throw ErroresEvaluacion.versionIncompatible(
                    "La versión "
                            + version.getIdVersionFormulario()
                            + " no pertenece al test "
                            + comando.testId()
                            + ".");
        }

        Test test = buscarTestActivo(version.getIdTest());

        if (!version.estaPublicada()) {
            throw ErroresEvaluacion.versionNoPublicada(version.getIdVersionFormulario());
        }

        sujetoRepository
                .buscarPorId(comando.sujetoId())
                .orElseThrow(() -> ErroresEvaluacion.sujetoNoEncontrado(comando.sujetoId()));

        Periodo periodo =
                periodoRepository
                        .buscarPorId(comando.periodoId())
                        .orElseThrow(
                                () -> ErroresEvaluacion.periodoNoEncontrado(comando.periodoId()));
        if (!periodo.admiteNuevasEvaluaciones()) {
            throw ErroresEvaluacion.periodoNoDisponible(periodo.idPeriodo());
        }

        List<EvaluacionSubprueba> seleccion =
                seleccionarSubpruebas(test, version, comando.subpruebas());

        ContextoInicio contexto =
                new ContextoInicio(
                        test.getCodigo(),
                        seleccion.stream().map(EvaluacionSubprueba::getCodigo).toList(),
                        comando.letraInicial());
        List<ReglaInicioTest> reglasDelTest =
                reglas.stream().filter(r -> r.aplicaA(test.getCodigo())).toList();
        reglasDelTest.forEach(r -> r.validar(contexto));

        if (evaluacionRepository.existeEnCurso(
                comando.sujetoId(), test.getIdTest(), periodo.idPeriodo())) {
            throw ErroresEvaluacion.evaluacionEnCurso();
        }

        Evaluacion nueva =
                Evaluacion.iniciar(
                        comando.sujetoId(),
                        version.getIdVersionFormulario(),
                        test.getIdTest(),
                        periodo.idPeriodo(),
                        seleccion,
                        reloj.instant());
        Evaluacion guardada = evaluacionRepository.guardarNueva(nueva);
        reglasDelTest.forEach(r -> r.registrar(contexto, guardada));
        auditoria.registrar(
                idCuenta, "INICIAR_EVALUACION", "EVALUACION", guardada.getIdEvaluacion());

        return EvaluacionVista.desde(guardada, reloj.instant());
    }

    private Test buscarTestActivo(Long idTest) {
        return testRepository
                .buscarPorId(idTest)
                .filter(Test::isActivo)
                .orElseThrow(() -> ErroresEvaluacion.testNoEncontrado(idTest));
    }

    /**
     * Sin lista: todas las configuraciones de la versión. Con lista: solo esas, siempre en el orden
     * oficial de la versión y sin duplicados.
     */
    private List<EvaluacionSubprueba> seleccionarSubpruebas(
            Test test, VersionFormulario version, List<String> solicitadas) {

        List<ConfiguracionSubprueba> configuraciones =
                configuracionRepository.listarPorVersion(version.getIdVersionFormulario()).stream()
                        .sorted(Comparator.comparingInt(ConfiguracionSubprueba::getOrdenAplicacion))
                        .toList();
        if (configuraciones.isEmpty()) {
            throw ErroresEvaluacion.versionIncompatible(
                    "La versión "
                            + version.getIdVersionFormulario()
                            + " no tiene subpruebas configuradas.");
        }

        Map<Long, Subprueba> subpruebaPorId =
                subpruebaRepository.listarPorTest(test.getIdTest()).stream()
                        .collect(Collectors.toMap(Subprueba::getIdSubprueba, Function.identity()));

        List<EvaluacionSubprueba> disponibles = new ArrayList<>();
        for (ConfiguracionSubprueba configuracion : configuraciones) {
            Subprueba subprueba = subpruebaPorId.get(configuracion.getIdSubprueba());
            if (subprueba == null) {
                throw ErroresEvaluacion.versionIncompatible(
                        "La configuración "
                                + configuracion.getIdConfiguracionSubprueba()
                                + " no pertenece al test de la versión.");
            }
            int duracion =
                    configuracion.getTiempoLimiteSegundos() != null
                            ? configuracion.getTiempoLimiteSegundos()
                            : 0;
            int maximo =
                    configuracion.getMaximoEntradas() != null
                            ? configuracion.getMaximoEntradas()
                            : 75;
            disponibles.add(
                    EvaluacionSubprueba.pendiente(
                            configuracion.getIdConfiguracionSubprueba(),
                            subprueba.getCodigo(),
                            subprueba.getNombre(),
                            configuracion.getOrdenAplicacion(),
                            duracion,
                            configuracion.getInstruccionesRef(),
                            true,
                            null,
                            maximo));
        }

        if (solicitadas == null) {
            return disponibles;
        }
        if (solicitadas.isEmpty()) {
            throw ErroresEvaluacion.solicitudInvalida(
                    "subpruebas", "si se envía, debe incluir al menos una subprueba");
        }

        Set<String> pedidas = new LinkedHashSet<>();
        Set<String> repetidas = new LinkedHashSet<>();
        for (String codigo : solicitadas) {
            String limpio = codigo == null ? "" : codigo.strip();
            if (limpio.isEmpty()) {
                throw ErroresEvaluacion.solicitudInvalida(
                        "subpruebas", "los códigos no pueden estar vacíos");
            }
            if (!pedidas.add(limpio)) {
                repetidas.add(limpio);
            }
        }
        if (!repetidas.isEmpty()) {
            throw ErroresEvaluacion.solicitudInvalida(
                    "subpruebas", "códigos repetidos: " + String.join(", ", repetidas));
        }

        Map<String, EvaluacionSubprueba> porCodigo =
                disponibles.stream()
                        .collect(
                                Collectors.toMap(
                                        EvaluacionSubprueba::getCodigo, Function.identity()));
        List<String> desconocidas =
                pedidas.stream().filter(c -> !porCodigo.containsKey(c)).toList();
        if (!desconocidas.isEmpty()) {
            throw ErroresEvaluacion.subpruebaNoIncluida(desconocidas);
        }

        Set<String> incluidas = new HashSet<>(pedidas);
        return disponibles.stream().filter(s -> incluidas.contains(s.getCodigo())).toList();
    }
}
