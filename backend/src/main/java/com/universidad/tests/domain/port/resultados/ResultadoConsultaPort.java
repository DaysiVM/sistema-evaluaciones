package com.universidad.tests.domain.port.resultados;

import com.universidad.tests.domain.model.resultados.ContenedorResultado;
import java.util.Optional;

/**
 * Lectura del resultado de una evaluación. No recalcula ni modifica nada.
 *
 * <p>Reglas que debe cumplir la implementación:
 *
 * <ul>
 *   <li>La revisión devuelta es exactamente la referenciada como vigente ({@code
 *       referenciaRevisionVigente}); nunca la de número mayor ni la última calculada (CU-BFA-P09,
 *       CA-08).
 *   <li>Sin revisión publicada, {@code revisionVigente} es nula.
 *   <li>Los factores se devuelven en el orden en que los guardó el evaluador.
 *   <li>Solo se leen identificadores de clave, reglas y baremo, nunca su contenido.
 * </ul>
 */
public interface ResultadoConsultaPort {

    Optional<ContenedorResultado> buscarPorEvaluacion(Long evaluacionId);
}
