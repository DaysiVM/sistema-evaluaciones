package com.universidad.tests.controller;

import com.universidad.tests.domain.exception.CredencialesInvalidasException;
import com.universidad.tests.domain.exception.NoAutenticadoException;
import com.universidad.tests.domain.exception.RolActivoNoDeterminadoException;
import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// GEN-06-BE: errores de autenticacion y autorizacion con el formato comun (Documento general
// §5.2.2).
// Separado de ManejadorErroresApi para no modificar el manejador global.
@RestControllerAdvice
public class ManejadorErroresSeguridad {

    @ExceptionHandler(NoAutenticadoException.class)
    public ResponseEntity<ErrorResponse> noAutenticado(
            NoAutenticadoException ex, HttpServletRequest request) {

        return error(HttpStatus.UNAUTHORIZED, "NO_AUTENTICADO", ex.getMessage(), request);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> credencialesInvalidas(
            CredencialesInvalidasException ex, HttpServletRequest request) {

        return error(HttpStatus.UNAUTHORIZED, "CREDENCIALES_INVALIDAS", ex.getMessage(), request);
    }

    @ExceptionHandler(SinPermisoException.class)
    public ResponseEntity<ErrorResponse> sinPermiso(
            SinPermisoException ex, HttpServletRequest request) {

        return error(HttpStatus.FORBIDDEN, "SIN_PERMISO", ex.getMessage(), request);
    }

    // SIN_PERMISO cubre "sin rol activo" (components.responses.SinPermiso)
    @ExceptionHandler(RolActivoNoDeterminadoException.class)
    public ResponseEntity<ErrorResponse> sinRolActivo(
            RolActivoNoDeterminadoException ex, HttpServletRequest request) {

        return error(HttpStatus.FORBIDDEN, "SIN_PERMISO", ex.getMessage(), request);
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
