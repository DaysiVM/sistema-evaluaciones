package com.universidad.tests.application.usecase.resultados;

import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException;
import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException.Motivo;
import com.universidad.tests.domain.model.resultados.ContenedorResultado;
import com.universidad.tests.domain.model.resultados.EvaluacionConsultada;
import com.universidad.tests.domain.model.resultados.FactorResultado;
import com.universidad.tests.domain.model.resultados.RevisionResultado;
import com.universidad.tests.domain.port.resultados.AuditoriaResultadoPort;
import com.universidad.tests.domain.port.resultados.AutorizacionResultadoPort;
import com.universidad.tests.domain.port.resultados.EvaluacionConsultaPort;
import com.universidad.tests.domain.port.resultados.ResultadoConsultaPort;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * GEN-05 — Consulta autorizada de resultados (E9, CU-BFA-P09, RF-BFA-CAL-05).
 *
 * <p>Es común a todos los tests: no conoce factores ni reglas de BFA. Solo lee lo que guardó el
 * evaluador, comprueba permisos y decide qué se puede presentar como definitivo. No calcula,
 * redondea, interpola ni modifica resultados.
 *
 * <p>Orden de las comprobaciones:
 *
 * <ol>
 *   <li>Identificador válido → si no, 400 {@code SOLICITUD_INVALIDA}.
 *   <li>Permiso del rol → si no, 401/403, antes de revelar si la evaluación existe.
 *   <li>Evaluación existente → si no, 404 {@code EVALUACION_NO_ENCONTRADA}.
 *   <li>Alcance sobre la evaluación → si no, 403 {@code SIN_PERMISO}.
 *   <li>Evaluación finalizada → si no, 409 {@code EVALUACION_NO_FINALIZADA}.
 *   <li>Lectura del resultado y de su revisión vigente exacta.
 *   <li>Registro de la consulta en auditoría.
 * </ol>
 */
@Service
public class ConsultarResultadosUseCase {

    private final AutorizacionResultadoPort autorizacion;
    private final EvaluacionConsultaPort evaluaciones;
    private final ResultadoConsultaPort resultados;
    private final AuditoriaResultadoPort auditoria;

    public ConsultarResultadosUseCase(
            AutorizacionResultadoPort autorizacion,
            EvaluacionConsultaPort evaluaciones,
            ResultadoConsultaPort resultados,
            AuditoriaResultadoPort auditoria) {
        this.autorizacion = Objects.requireNonNull(autorizacion, "autorizacion es obligatoria");
        this.evaluaciones = Objects.requireNonNull(evaluaciones, "evaluaciones es obligatorio");
        this.resultados = Objects.requireNonNull(resultados, "resultados es obligatorio");
        this.auditoria = Objects.requireNonNull(auditoria, "auditoria es obligatoria");
    }

    public ResultadoVista consultar(Long evaluacionId) {
        if (evaluacionId == null || evaluacionId <= 0) {
            throw new ConsultaResultadoException(
                    Motivo.SOLICITUD_INVALIDA,
                    "El ID de la evaluación debe ser un número entero positivo.");
        }

        autorizacion.exigirPermisoConsultarResultados();

        EvaluacionConsultada evaluacion =
                evaluaciones
                        .buscarPorId(evaluacionId)
                        .orElseThrow(
                                () ->
                                        new ConsultaResultadoException(
                                                Motivo.EVALUACION_NO_ENCONTRADA,
                                                "No existe la evaluación " + evaluacionId + "."));

        autorizacion.exigirAlcanceSobre(evaluacion);

        if (!evaluacion.estaFinalizada()) {
            throw new ConsultaResultadoException(
                    Motivo.EVALUACION_NO_FINALIZADA,
                    "La evaluación " + evaluacionId + " aún no ha finalizado.");
        }

        ResultadoVista vista =
                resultados
                        .buscarPorEvaluacion(evaluacionId)
                        .map(contenedor -> desdeContenedor(evaluacion, contenedor))
                        .orElseGet(() -> enCalculo(evaluacion));

        auditoria.registrarConsulta(evaluacionId, vista.resultadoId(), vista.calificacionId());
        return vista;
    }

    /**
     * La evaluación finalizó pero el resultado todavía no está guardado (E8 aún consolidando). Se
     * informa CALCULANDO, sin revisión ni factores.
     */
    private ResultadoVista enCalculo(EvaluacionConsultada evaluacion) {
        return new ResultadoVista(
                evaluacion.evaluacionId(),
                null,
                ContenedorResultado.CALCULANDO,
                evaluacion.versionFormularioId(),
                null,
                null,
                null,
                null,
                null,
                List.of());
    }

    private ResultadoVista desdeContenedor(
            EvaluacionConsultada evaluacion, ContenedorResultado contenedor) {
        if (!contenedor.evaluacionId().equals(evaluacion.evaluacionId())) {
            throw inconsistente("el resultado pertenece a otra evaluación");
        }

        if (!contenedor.tieneRevisionVigente()) {
            // Un resultado DISPONIBLE sin revisión publicada se presentaría como definitivo sin
            // datos: se rechaza en lugar de responder algo engañoso.
            if (ContenedorResultado.DISPONIBLE.equals(contenedor.estadoResultado())) {
                throw inconsistente("resultado DISPONIBLE sin revisión vigente");
            }
            return new ResultadoVista(
                    evaluacion.evaluacionId(),
                    contenedor.resultadoId(),
                    contenedor.estadoResultado(),
                    evaluacion.versionFormularioId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of());
        }

        RevisionResultado revision = contenedor.revisionVigente();
        List<ResultadoVista.FactorVista> factores =
                revision.factores().stream()
                        .map(FactorResultado::paraPresentar)
                        .map(ConsultarResultadosUseCase::aVista)
                        .toList();

        return new ResultadoVista(
                evaluacion.evaluacionId(),
                contenedor.resultadoId(),
                contenedor.estadoResultado(),
                evaluacion.versionFormularioId(),
                revision.calificacionId(),
                revision.numero(),
                revision.versionReglas(),
                revision.idClave(),
                revision.idBaremo(),
                factores);
    }

    private static ResultadoVista.FactorVista aVista(FactorResultado factor) {
        return new ResultadoVista.FactorVista(
                factor.codigo(),
                factor.puntuacionDirecta(),
                factor.estadoCalculo(),
                factor.percentil(),
                factor.idRango(),
                factor.estadoNormativo(),
                factor.motivo());
    }

    private static ConsultaResultadoException inconsistente(String detalle) {
        return new ConsultaResultadoException(
                Motivo.RESULTADO_INCONSISTENTE, "Resultado inconsistente: " + detalle + ".");
    }
}
