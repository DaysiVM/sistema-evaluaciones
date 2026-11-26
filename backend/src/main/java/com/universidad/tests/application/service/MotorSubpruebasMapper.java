package com.universidad.tests.application.service;

import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.model.OpcionRespuesta;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.dto.response.PreguntasSubpruebaResponse;
import com.universidad.tests.dto.response.SubpruebaActivaResponse;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class MotorSubpruebasMapper {
    private MotorSubpruebasMapper() {}

    public static SubpruebaActivaResponse activa(EvaluacionSubprueba s, Instant ahora) {
        return new SubpruebaActivaResponse(
                s.codigo(),
                s.estado().name(),
                s.fechaInicio(),
                s.fechaFinLimite(),
                s.segundosRestantes(ahora),
                s.esFluidezVerbal() ? s.letraInicial() : null);
    }

    public static PreguntasSubpruebaResponse.Pregunta pregunta(
            Pregunta pregunta, List<OpcionRespuesta> opciones, Long idOpcionRegistrada) {
        String seleccion =
                opciones.stream()
                        .filter(o -> Objects.equals(o.getIdOpcion(), idOpcionRegistrada))
                        .map(OpcionRespuesta::getCodigo)
                        .findFirst()
                        .orElse(null);
        int numero;
        try {
            numero = Integer.parseInt(pregunta.getNumeroVisible());
        } catch (NumberFormatException ex) {
            numero = pregunta.getOrdenPresentacion();
        }
        return new PreguntasSubpruebaResponse.Pregunta(
                pregunta.getIdPregunta(),
                numero,
                pregunta.getEnunciado(),
                pregunta.getRecursoRef(),
                opciones.stream()
                        .map(
                                o ->
                                        new PreguntasSubpruebaResponse.Opcion(
                                                o.getCodigo(), o.getTexto(), o.getRecursoRef()))
                        .toList(),
                seleccion);
    }
}
