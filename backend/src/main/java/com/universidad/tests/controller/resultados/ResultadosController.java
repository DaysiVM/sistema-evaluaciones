package com.universidad.tests.controller.resultados;

import com.universidad.tests.application.usecase.resultados.ConsultarResultadosUseCase;
import com.universidad.tests.application.usecase.resultados.ResultadoVista;
import com.universidad.tests.dto.response.resultados.ResultadoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** E9 — Consultar resultados autorizados (GEN-05). */
@RestController
@Tag(name = "Evaluaciones")
public class ResultadosController {

    private final ConsultarResultadosUseCase consultarResultados;

    public ResultadosController(ConsultarResultadosUseCase consultarResultados) {
        this.consultarResultados =
                Objects.requireNonNull(consultarResultados, "consultarResultados es obligatorio");
    }

    @GetMapping(
            path = "/api/v1/evaluaciones/{evaluacionId}/resultados",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "consultarResultados",
            summary = "E9 — Consultar resultados autorizados")
    public ResultadoResponse consultar(@PathVariable("evaluacionId") Long evaluacionId) {
        return aRespuesta(consultarResultados.consultar(evaluacionId));
    }

    private static ResultadoResponse aRespuesta(ResultadoVista vista) {
        return new ResultadoResponse(
                vista.evaluacionId(),
                vista.resultadoId(),
                vista.estadoResultado(),
                vista.versionFormularioId(),
                vista.calificacionId(),
                vista.revision(),
                vista.versionReglas(),
                vista.idClave(),
                vista.idBaremo(),
                vista.factores().stream()
                        .map(
                                f ->
                                        new ResultadoResponse.FactorResponse(
                                                f.codigo(),
                                                f.puntuacionDirecta(),
                                                f.estadoCalculo(),
                                                f.percentil(),
                                                f.idRango(),
                                                f.estadoNormativo(),
                                                f.motivo()))
                        .toList());
    }
}
