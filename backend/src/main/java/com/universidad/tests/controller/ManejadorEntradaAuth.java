package com.universidad.tests.controller;

import com.universidad.tests.application.usecase.RegistroCuentaException;
import com.universidad.tests.domain.exception.RecuperacionException;
import com.universidad.tests.dto.response.ErrorResponse;
import com.universidad.tests.dto.response.ErrorResponse.DetalleCampo;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Conserva el formato de error aprobado para errores de entrada HTTP de acceso. */
@RestControllerAdvice(
        assignableTypes = {
            SesionAuthController.class,
            RegistroAuthController.class,
            RecuperacionAuthController.class
        })
public class ManejadorEntradaAuth extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalStateException.class, DataAccessException.class})
    public ResponseEntity<ErrorResponse> interno(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                500,
                                "ERROR_INTERNO",
                                "No se pudo completar la solicitud",
                                request.getRequestURI()));
    }

    @ExceptionHandler(RecuperacionException.class)
    public ResponseEntity<ErrorResponse> recuperacion(
            RecuperacionException ex, HttpServletRequest request) {
        int status =
                ex.getCodigo() == RecuperacionException.Codigo.CORREO_NO_DISPONIBLE ? 503 : 400;
        return ResponseEntity.status(status)
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                status,
                                ex.getCodigo().name(),
                                "No se pudo completar la recuperacion",
                                request.getRequestURI()));
    }

    @ExceptionHandler(RegistroCuentaException.class)
    public ResponseEntity<ErrorResponse> registro(
            RegistroCuentaException ex, HttpServletRequest request) {
        HttpStatus status =
                switch (ex.getCodigo()) {
                    case CUENTA_EXISTENTE -> HttpStatus.CONFLICT;
                    case ROL_NO_AUTORIZADO -> HttpStatus.FORBIDDEN;
                    case SOLICITUD_INVALIDA -> HttpStatus.BAD_REQUEST;
                };
        return ResponseEntity.status(status)
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                ex.getCodigo().name(),
                                ex.getMessage(),
                                request.getRequestURI()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String ruta = ((ServletWebRequest) request).getRequest().getRequestURI();
        List<DetalleCampo> detalles = List.of();
        if (ex instanceof MethodArgumentNotValidException validacion) {
            detalles =
                    validacion.getBindingResult().getFieldErrors().stream()
                            .map(
                                    error ->
                                            new DetalleCampo(
                                                    error.getField(),
                                                    "El campo es obligatorio o su formato no es valido"))
                            .toList();
        } else if (ex instanceof HttpMessageNotReadableException) {
            detalles =
                    List.of(
                            new DetalleCampo(
                                    "cuerpo",
                                    "Debe enviar JSON valido con los tipos de campo indicados"));
        }
        return ResponseEntity.status(status)
                .headers(headers)
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                "SOLICITUD_INVALIDA",
                                "La solicitud no es valida",
                                ruta,
                                detalles));
    }
}
