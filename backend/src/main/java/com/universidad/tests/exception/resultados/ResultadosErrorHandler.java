package com.universidad.tests.exception.resultados;

import com.universidad.tests.controller.resultados.ResultadosController;
import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException;
import com.universidad.tests.dto.response.resultados.ErrorResultadoResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Errores de E9 con el formato común (Documento general §5.2.2).
 *
 * <p>Solo atiende a {@link ResultadosController}, para no cambiar el manejo de errores de otros
 * endpoints mientras no exista un manejador global acordado. Un error no previsto responde 500
 * {@code ERROR_INTERNO} sin detalles internos.
 */
@RestControllerAdvice(assignableTypes = ResultadosController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResultadosErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ResultadosErrorHandler.class);
    private static final String MENSAJE_INTERNO =
            "No se pudo consultar el resultado. Intente de nuevo más tarde.";

    @ExceptionHandler(ConsultaResultadoException.class)
    public ResponseEntity<ErrorResultadoResponse> rechazo(
            ConsultaResultadoException ex, HttpServletRequest solicitud) {
        ConsultaResultadoException.Motivo motivo = ex.motivo();
        if (motivo == ConsultaResultadoException.Motivo.RESULTADO_INCONSISTENTE) {
            LOG.error("E9 {}: {}", solicitud.getRequestURI(), ex.getMessage());
            return error(500, motivo.codigo(), MENSAJE_INTERNO, solicitud, null);
        }
        return error(motivo.status(), motivo.codigo(), ex.getMessage(), solicitud, null);
    }

    /** Los adaptadores provisionales del equipo niegan el acceso con {@code SecurityException}. */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResultadoResponse> sinPermiso(
            SecurityException ex, HttpServletRequest solicitud) {
        return error(
                403,
                "SIN_PERMISO",
                "No tiene permiso para consultar los resultados.",
                solicitud,
                null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResultadoResponse> idInvalido(
            MethodArgumentTypeMismatchException ex, HttpServletRequest solicitud) {
        return error(
                400,
                "SOLICITUD_INVALIDA",
                "El ID de la evaluación debe ser un número entero positivo.",
                solicitud,
                List.of(
                        new ErrorResultadoResponse.Detalle(
                                ex.getName(), "debe ser un número entero positivo")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResultadoResponse> noPrevisto(
            Exception ex, HttpServletRequest solicitud) {
        LOG.error("E9 {}: error no previsto", solicitud.getRequestURI(), ex);
        return error(500, "ERROR_INTERNO", MENSAJE_INTERNO, solicitud, null);
    }

    private static ResponseEntity<ErrorResultadoResponse> error(
            int status,
            String codigo,
            String mensaje,
            HttpServletRequest solicitud,
            List<ErrorResultadoResponse.Detalle> detalles) {
        ErrorResultadoResponse cuerpo =
                new ErrorResultadoResponse(
                        Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                        status,
                        codigo,
                        mensaje,
                        solicitud.getRequestURI(),
                        detalles);
        return ResponseEntity.status(status).body(cuerpo);
    }
}
