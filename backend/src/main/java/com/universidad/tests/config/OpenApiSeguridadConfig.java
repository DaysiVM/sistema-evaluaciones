package com.universidad.tests.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// GEN-33-BE: esquema bearerAuth del contrato (docs/api/openapi.yaml) en springdoc, para que el
// boton «Authorize» de Swagger envie Authorization: Bearer <token emitido por E14>.
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Sistema Integrado de Evaluaciones", version = "0.1.0"),
        security = @SecurityRequirement(name = OpenApiSeguridadConfig.ESQUEMA_BEARER))
@SecurityScheme(
        name = OpenApiSeguridadConfig.ESQUEMA_BEARER,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        description = "Token emitido por E14.")
public class OpenApiSeguridadConfig {

    public static final String ESQUEMA_BEARER = "bearerAuth";
}
