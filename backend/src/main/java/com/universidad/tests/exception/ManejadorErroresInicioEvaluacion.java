package com.universidad.tests.exception;

import com.universidad.tests.controller.InicioEvaluacionController;
import com.universidad.tests.controller.PeriodoController;
import com.universidad.tests.controller.SujetoController;
import com.universidad.tests.domain.exception.ErrorNegocioException;
import com.universidad.tests.domain.exception.TipoErrorNegocio;
import com.universidad.tests.dto.response.ErrorResponse;
import com.universidad.tests.dto.response.ErrorResponse.DetalleCampo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Errores de los controladores de GEN-02-BE (E3, E19 y E21), con el formato común del Documento
 * general §5.2.2. Está limitado a esos controladores ({@code assignableTypes}) porque el Scrum
 * Master pidió no agregar manejadores globales nuevos (decisión del 23/11/2026 en el PR #41); el
 * resto de la API sigue con {@code ManejadorErroresApi}. Un error no previsto nunca expone detalles
 * internos.
 */
@RestControllerAdvice(
        assignableTypes = {
            InicioEvaluacionController.class,
            SujetoController.class,
            PeriodoController.class
        })
public class ManejadorErroresInicioEvaluacion {

    private static final Logger LOG =
            LoggerFactory.getLogger(ManejadorErroresInicioEvaluacion.class);
    private static final String SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";
    private static final String MENSAJE_SOLICITUD_INVALIDA = "La solicitud tiene datos inválidos.";

    private final Clock reloj;

    /**
     * El reloj de {@code RelojConfig} es opcional: las pruebas {@code @WebMvcTest} de otros
     * controladores cargan este manejador sin esa configuración y, en ese caso, se usa el reloj del
     * sistema.
     */
    public ManejadorErroresInicioEvaluacion(ObjectProvider<Clock> reloj) {
        this.reloj = reloj.getIfAvailable(Clock::systemUTC);
    }

    @ExceptionHandler(ErrorNegocioException.class)
    public ResponseEntity<ErrorResponse> negocio(
            ErrorNegocioException e, HttpServletRequest request) {
        List<DetalleCampo> detalles =
                e.getDetalles().stream()
                        .map(d -> new DetalleCampo(d.campo(), d.mensaje()))
                        .toList();
        return responder(estado(e.getTipo()), e.getCodigo(), e.getMessage(), request, detalles);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> cuerpoInvalido(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        List<DetalleCampo> detalles =
                e.getBindingResult().getFieldErrors().stream()
                        .map(ManejadorErroresInicioEvaluacion::detalle)
                        .toList();
        return responder(
                HttpStatus.BAD_REQUEST,
                SOLICITUD_INVALIDA,
                MENSAJE_SOLICITUD_INVALIDA,
                request,
                detalles);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> parametroInvalido(
            HandlerMethodValidationException e, HttpServletRequest request) {
        List<DetalleCampo> detalles =
                e.getParameterValidationResults().stream()
                        .flatMap(
                                r ->
                                        r.getResolvableErrors().stream()
                                                .map(
                                                        (MessageSourceResolvable m) ->
                                                                new DetalleCampo(
                                                                        r.getMethodParameter()
                                                                                .getParameterName(),
                                                                        m.getDefaultMessage())))
                        .toList();
        return responder(
                HttpStatus.BAD_REQUEST,
                SOLICITUD_INVALIDA,
                MENSAJE_SOLICITUD_INVALIDA,
                request,
                detalles);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> restriccionInvalida(
            ConstraintViolationException e, HttpServletRequest request) {
        List<DetalleCampo> detalles =
                e.getConstraintViolations().stream()
                        .map(v -> new DetalleCampo(v.getPropertyPath().toString(), v.getMessage()))
                        .toList();
        return responder(
                HttpStatus.BAD_REQUEST,
                SOLICITUD_INVALIDA,
                MENSAJE_SOLICITUD_INVALIDA,
                request,
                detalles);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> tipoIncorrecto(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        return responder(
                HttpStatus.BAD_REQUEST,
                SOLICITUD_INVALIDA,
                MENSAJE_SOLICITUD_INVALIDA,
                request,
                List.of(new DetalleCampo(e.getName(), "tiene un tipo incorrecto")));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonIlegible(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        return responder(
                HttpStatus.BAD_REQUEST,
                SOLICITUD_INVALIDA,
                "El cuerpo de la solicitud no es un JSON válido o tiene tipos incorrectos.",
                request,
                List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaInexistente(
            NoResourceFoundException e, HttpServletRequest request) {
        return responder(
                HttpStatus.NOT_FOUND,
                "RUTA_NO_ENCONTRADA",
                "La ruta solicitada no existe.",
                request,
                List.of());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> sinPermiso(
            SecurityException e, HttpServletRequest request) {
        return responder(
                HttpStatus.FORBIDDEN,
                "SIN_PERMISO",
                "No tiene permiso para realizar esta acción.",
                request,
                List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception e, HttpServletRequest request) {
        // Otros errores de Spring MVC con estado 4xx (método no permitido, tipo de contenido…).
        if (e instanceof org.springframework.web.ErrorResponse errorWeb
                && errorWeb.getStatusCode().is4xxClientError()) {
            return responder(
                    errorWeb.getStatusCode(),
                    SOLICITUD_INVALIDA,
                    MENSAJE_SOLICITUD_INVALIDA,
                    request,
                    List.of());
        }
        LOG.error("Error no previsto en {}", request.getRequestURI(), e);
        return responder(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERROR_INTERNO",
                "Ocurrió un error inesperado. Intente de nuevo más tarde.",
                request,
                List.of());
    }

    private static HttpStatus estado(TipoErrorNegocio tipo) {
        return switch (tipo) {
            case SOLICITUD_INVALIDA -> HttpStatus.BAD_REQUEST;
            case SIN_PERMISO -> HttpStatus.FORBIDDEN;
            case NO_ENCONTRADO -> HttpStatus.NOT_FOUND;
            case CONFLICTO -> HttpStatus.CONFLICT;
            case REGLA_DE_NEGOCIO -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }

    private static DetalleCampo detalle(FieldError error) {
        return new DetalleCampo(error.getField(), error.getDefaultMessage());
    }

    private ResponseEntity<ErrorResponse> responder(
            HttpStatusCode estado,
            String codigo,
            String mensaje,
            HttpServletRequest request,
            List<DetalleCampo> detalles) {
        ErrorResponse cuerpo =
                new ErrorResponse(
                        Instant.now(reloj),
                        estado.value(),
                        codigo,
                        mensaje,
                        request.getRequestURI(),
                        detalles);
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
