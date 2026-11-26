package com.universidad.tests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.ContextoSesion;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.dto.response.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// GEN-06-BE: toda operacion de /api exige Authorization Bearer emitido por E14,
// salvo E13, E14, E16 y E17 (docs/api/openapi.yaml). Sin sesion valida: 401 NO_AUTENTICADO.
@Component
public class FiltroSesionBearer extends OncePerRequestFilter {

    private static final String PREFIJO_API = "/api/";
    private static final String PREFIJO_BEARER = "Bearer ";
    private static final Set<String> RUTAS_PUBLICAS_POST =
            Set.of(
                    "/api/v1/auth/registro",
                    "/api/v1/auth/sesion",
                    "/api/v1/auth/recuperacion",
                    "/api/v1/auth/recuperacion/confirmar");

    private final AutenticacionService autenticacion;
    private final ObjectMapper objectMapper;

    public FiltroSesionBearer(AutenticacionService autenticacion, ObjectMapper objectMapper) {
        this.autenticacion = autenticacion;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = rutaDe(request);
        boolean publica =
                HttpMethod.POST.matches(request.getMethod()) && RUTAS_PUBLICAS_POST.contains(ruta);
        return !ruta.startsWith(PREFIJO_API) || publica;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        Optional<IdentidadSesion> identidad = tokenDe(request).flatMap(autenticacion::validar);

        if (identidad.isEmpty()) {
            responderNoAutenticado(request, response);
            return;
        }

        ContextoSesion.establecer(identidad.get());
        try {
            chain.doFilter(request, response);
        } finally {
            ContextoSesion.limpiar();
        }
    }

    // Token de la cabecera Authorization: Bearer <token>; E15 lo usa para cerrar la sesion
    static Optional<String> tokenDe(HttpServletRequest request) {
        String cabecera = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera == null || !cabecera.regionMatches(true, 0, PREFIJO_BEARER, 0, 7)) {
            return Optional.empty();
        }
        String token = cabecera.substring(PREFIJO_BEARER.length()).trim();
        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }

    private void responderNoAutenticado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        objectMapper.writeValue(
                response.getOutputStream(),
                new ErrorResponse(
                        Instant.now(),
                        status.value(),
                        "NO_AUTENTICADO",
                        "No hay una sesion valida",
                        request.getRequestURI()));
    }

    private static String rutaDe(HttpServletRequest request) {
        String contexto = request.getContextPath();
        String uri = request.getRequestURI();
        return contexto != null && !contexto.isEmpty() && uri.startsWith(contexto)
                ? uri.substring(contexto.length())
                : uri;
    }
}
