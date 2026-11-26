package com.universidad.tests.controller;

import com.universidad.tests.application.service.AgregarPreguntaVersionadaService.PreguntaDuplicadaException;
import com.universidad.tests.application.service.VersionFormularioService.VersionDuplicadaException;
import com.universidad.tests.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Errores propios del versionado con el formato comun (Documento general §5.2.2).
// NO_AUTENTICADO y SIN_PERMISO los responde ManejadorErroresSeguridad.
@RestControllerAdvice(
        assignableTypes = {
            ConfiguracionVersionadaController.class,
            PreguntaVersionadaController.class,
            VersionFormularioController.class
        })
public class VersionadoErrorHandler {

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        HttpMessageNotReadableException.class,
        IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> solicitudInvalida(
            Exception error, HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA", error, request);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(Exception error, HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO", error, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> conflicto(Exception error, HttpServletRequest request) {
        return responder(HttpStatus.CONFLICT, "VERSION_NO_EDITABLE", error, request);
    }

    @ExceptionHandler(PreguntaDuplicadaException.class)
    public ResponseEntity<ErrorResponse> duplicada(Exception error, HttpServletRequest request) {
        return responder(HttpStatus.UNPROCESSABLE_ENTITY, "PREGUNTA_DUPLICADA", error, request);
    }

    @ExceptionHandler(VersionDuplicadaException.class)
    public ResponseEntity<ErrorResponse> versionDuplicada(
            Exception error, HttpServletRequest request) {
        return responder(HttpStatus.CONFLICT, "VERSION_DUPLICADA", error, request);
    }

    private ResponseEntity<ErrorResponse> responder(
            HttpStatus estado, String codigo, Exception error, HttpServletRequest request) {
        return ResponseEntity.status(estado)
                .body(
                        new ErrorResponse(
                                Instant.now(),
                                estado.value(),
                                codigo,
                                error.getMessage(),
                                request.getRequestURI()));
    }
}
