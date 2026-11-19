package com.universidad.tests.domain.port.resultados;

/**
 * Registro de la consulta de resultados (CU-BFA-P09, CU-GEN-15: la consulta queda registrada).
 *
 * <p>El actor se obtiene del contexto autenticado en la implementación. Solo se registran
 * identificadores, sin puntuaciones ni datos personales.
 */
public interface AuditoriaResultadoPort {

    void registrarConsulta(Long evaluacionId, Long resultadoId, Long calificacionId);
}
