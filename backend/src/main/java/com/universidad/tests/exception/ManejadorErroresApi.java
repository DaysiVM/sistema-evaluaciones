package com.universidad.tests.exception;

import com.universidad.tests.domain.exception.EvaluacionNoEncontradaException;
import com.universidad.tests.domain.exception.MotorSubpruebasException;
import com.universidad.tests.domain.exception.SubpruebasPendientesException;
import com.universidad.tests.domain.exception.TestNoEncontradoException;
import com.universidad.tests.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErroresApi {
    private static final Logger LOG = LoggerFactory.getLogger(ManejadorErroresApi.class);

    @ExceptionHandler(SubpruebasPendientesException.class)
    public ResponseEntity<ErrorResponse> subpruebasPendientes(
            SubpruebasPendientesException ex, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "SUBPRUEBAS_PENDIENTES", ex.getMessage(), request);
    }

    @ExceptionHandler(TestNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> testNoEncontrado(
            TestNoEncontradoException ex, HttpServletRequest request) {

        return error(HttpStatus.NOT_FOUND, "TEST_NO_ENCONTRADO", ex.getMessage(), request);
    }

    @ExceptionHandler(EvaluacionNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> evaluacionNoEncontrada(
            EvaluacionNoEncontradaException ex, HttpServletRequest request) {

        return error(HttpStatus.NOT_FOUND, "EVALUACION_NO_ENCONTRADA", ex.getMessage(), request);
    }

    @ExceptionHandler(MotorSubpruebasException.class)
    public ResponseEntity<ErrorResponse> errorMotor(
            MotorSubpruebasException ex, HttpServletRequest request) {
        HttpStatus estado =
                switch (ex.codigo()) {
                    case SUBPRUEBA_NO_ENCONTRADA -> HttpStatus.NOT_FOUND;
                    case SOLICITUD_INVALIDA -> HttpStatus.BAD_REQUEST;
                    case RESPUESTA_INVALIDA -> HttpStatus.UNPROCESSABLE_ENTITY;
                    case ERROR_INTERNO -> HttpStatus.INTERNAL_SERVER_ERROR;
                    default -> HttpStatus.CONFLICT;
                };
        if (estado == HttpStatus.BAD_REQUEST) {
            return solicitudInvalida(ex.getMessage(), request);
        }
        return error(estado, ex.codigo().name(), ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> cuerpoInvalido(HttpServletRequest request) {
        return solicitudInvalida("El cuerpo de la solicitud no es vÃ¡lido.", request);
    }

    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ResponseEntity<ErrorResponse> errorPersistencia(
            RuntimeException ex, HttpServletRequest request) {
        LOG.error("FallÃ³ una operaciÃ³n de persistencia: {}", ex.getClass().getSimpleName());
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERROR_INTERNO",
                "No se pudo completar la operaciÃ³n.",
                request);
    }

    private ResponseEntity<ErrorResponse> solicitudInvalida(
            String mensaje, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                400,
                                "SOLICITUD_INVALIDA",
                                mensaje,
                                request.getRequestURI(),
                                List.of(new ErrorResponse.DetalleCampo("cuerpo", mensaje))));
    }

    private ResponseEntity<ErrorResponse> error(
            HttpStatus status, String codigo, String mensaje, HttpServletRequest request) {

        return ResponseEntity.status(status)
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                codigo,
                                mensaje,
                                request.getRequestURI()));
    }
}
