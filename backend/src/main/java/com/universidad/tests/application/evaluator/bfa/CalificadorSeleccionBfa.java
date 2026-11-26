package com.universidad.tests.application.evaluator.bfa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Compara las respuestas de un bloque BFA de selección con su clave y cuenta aciertos, errores y
 * omisiones (preguntas sin contestar).
 *
 * <p>Sirve para R1, R2, N1-A, N1-B, N2-A y N2-B. No aplica fórmulas de factor (N1, N2, RT, NT): eso
 * corresponde a la consolidación. No consulta ni guarda datos.
 */
public final class CalificadorSeleccionBfa {

    /**
     * Califica un bloque.
     *
     * @param respuestas respuestas registradas en el bloque
     * @param clave respuestas correctas del bloque: id de pregunta a id de opción correcta. Debe
     *     traer solo las preguntas de este bloque.
     * @return aciertos, errores y omisiones del bloque
     * @throws IllegalArgumentException si la clave está vacía, si hay una respuesta a una pregunta
     *     que no está en la clave o si una pregunta tiene más de una respuesta
     */
    public ConteoBloqueBfa calificar(
            Collection<RespuestaSeleccionBfa> respuestas, Map<Long, Long> clave) {
        Objects.requireNonNull(respuestas, "respuestas es obligatorio");
        Objects.requireNonNull(clave, "clave es obligatoria");
        if (clave.isEmpty()) {
            throw new IllegalArgumentException("La clave del bloque no tiene preguntas");
        }

        Map<Long, Long> opcionPorPregunta = new HashMap<>();
        for (RespuestaSeleccionBfa respuesta : respuestas) {
            Objects.requireNonNull(respuesta, "Una respuesta del bloque es nula");
            Long idPregunta = respuesta.idPregunta();
            if (!clave.containsKey(idPregunta)) {
                throw new IllegalArgumentException(
                        "La pregunta " + idPregunta + " no pertenece a la clave del bloque");
            }
            if (opcionPorPregunta.containsKey(idPregunta)) {
                throw new IllegalArgumentException(
                        "La pregunta " + idPregunta + " tiene más de una respuesta");
            }
            opcionPorPregunta.put(idPregunta, respuesta.idOpcion());
        }

        int aciertos = 0;
        int errores = 0;
        int omisiones = 0;
        for (Map.Entry<Long, Long> entrada : clave.entrySet()) {
            Long correcta =
                    Objects.requireNonNull(
                            entrada.getValue(),
                            "La clave no tiene opción correcta para la pregunta "
                                    + entrada.getKey());
            Long elegida = opcionPorPregunta.get(entrada.getKey());
            if (elegida == null) {
                omisiones++;
            } else if (elegida.equals(correcta)) {
                aciertos++;
            } else {
                errores++;
            }
        }
        return new ConteoBloqueBfa(aciertos, errores, omisiones);
    }
}
