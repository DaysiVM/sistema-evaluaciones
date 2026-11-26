package com.universidad.tests.application.evaluator.bfa;

import com.universidad.tests.domain.model.OpcionRespuesta;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Califica la familia Verbal del BFA (BFA-BE-03): VOC1 Sinónimos, VOC2 Palabra Excluida y CV
 * Proverbios.
 *
 * <p>Valida las alternativas (VOC1 y VOC2: A-E; CV: A-C) y entrega el conteo a la consolidación
 * BFA-BE-08. La puntuación directa de cada subprueba es el número de aciertos, sin penalización por
 * errores; las omisiones quedan aparte y no restan (Documento BFA §2.4).
 *
 * <p>Función interna sin HTTP, acceso a datos ni claves incorporadas: la clave aprobada llega desde
 * el catálogo versionado y permanece protegida. Quedan fuera de esta pieza, a la espera del motor
 * de GEN-03-BE: la integración con respuestas y cronómetros, la pausa opcional previa a CV y la
 * persistencia al cerrar cada bloque.
 */
public final class CalificadorVerbalBfa {

    private final CalificadorSeleccionBfa calificadorSeleccion = new CalificadorSeleccionBfa();

    /**
     * Comprueba el bloque aplicado y que la clave cubra exactamente sus preguntas antes del conteo.
     */
    public ConteoBloqueBfa calificarVersionado(
            BloqueVerbalVersionado bloque,
            Collection<RespuestaSeleccionBfa> respuestas,
            Map<Long, Long> clave,
            Collection<OpcionRespuesta> opciones) {
        Objects.requireNonNull(bloque, "El bloque versionado es obligatorio");
        Objects.requireNonNull(clave, "La clave es obligatoria");
        if (!bloque.idsPreguntas().equals(clave.keySet())) {
            throw new IllegalArgumentException(
                    "La clave no cubre exactamente las preguntas del bloque aplicado");
        }
        return calificar(bloque.codigoSubprueba(), respuestas, clave, opciones);
    }

    /**
     * Califica un bloque Verbal.
     *
     * @param codigoSubprueba VOC1, VOC2 o CV
     * @param respuestas respuestas registradas; {@code idOpcion} nulo es una omisión
     * @param clave id de pregunta a id de la opción correcta, solo de este bloque
     * @param opciones alternativas de las preguntas del bloque
     * @return aciertos (puntuación directa), errores y omisiones
     */
    public ConteoBloqueBfa calificar(
            String codigoSubprueba,
            Collection<RespuestaSeleccionBfa> respuestas,
            Map<Long, Long> clave,
            Collection<OpcionRespuesta> opciones) {
        Objects.requireNonNull(respuestas, "respuestas es obligatorio");
        Objects.requireNonNull(clave, "clave es obligatoria");
        Objects.requireNonNull(opciones, "opciones es obligatorio");
        Set<String> permitidas = AlternativasVerbalBfa.de(codigoSubprueba);

        Map<Long, Map<Long, String>> opcionesPorPregunta = new HashMap<>();
        Set<Long> idsOpciones = new HashSet<>();
        for (OpcionRespuesta opcion : opciones) {
            Objects.requireNonNull(opcion, "Una opcion del bloque es nula");
            if (opcion.getIdOpcion() == null || !clave.containsKey(opcion.getIdPregunta())) {
                throw new IllegalArgumentException(
                        "La opcion no pertenece a una pregunta del bloque");
            }
            if (!permitidas.contains(opcion.getCodigo())) {
                throw new IllegalArgumentException(
                        "La alternativa no esta permitida para el bloque");
            }
            Map<Long, String> porId =
                    opcionesPorPregunta.computeIfAbsent(
                            opcion.getIdPregunta(), ignorada -> new HashMap<>());
            if (!idsOpciones.add(opcion.getIdOpcion()) || porId.containsValue(opcion.getCodigo())) {
                throw new IllegalArgumentException("El catalogo tiene opciones duplicadas");
            }
            porId.put(opcion.getIdOpcion(), opcion.getCodigo());
        }

        for (Map.Entry<Long, Long> entrada : clave.entrySet()) {
            Map<Long, String> porId = opcionesPorPregunta.get(entrada.getKey());
            if (porId == null || !new HashSet<>(porId.values()).equals(permitidas)) {
                throw new IllegalArgumentException("Las alternativas del bloque estan incompletas");
            }
            if (entrada.getValue() == null || !porId.containsKey(entrada.getValue())) {
                throw new IllegalArgumentException("La opcion correcta no pertenece a su pregunta");
            }
        }

        for (RespuestaSeleccionBfa respuesta : respuestas) {
            Objects.requireNonNull(respuesta, "Una respuesta del bloque es nula");
            Map<Long, String> porId = opcionesPorPregunta.get(respuesta.idPregunta());
            if (porId == null
                    || (respuesta.idOpcion() != null && !porId.containsKey(respuesta.idOpcion()))) {
                throw new IllegalArgumentException(
                        "La respuesta no pertenece a las opciones de su pregunta");
            }
        }

        return calificadorSeleccion.calificar(respuestas, clave);
    }
}
