package com.universidad.tests.domain.port.resultados;

import com.universidad.tests.domain.model.resultados.EvaluacionConsultada;

/**
 * Decide quién puede consultar resultados (E9). La identidad del actor sale del contexto
 * autenticado, nunca de un dato enviado por el cliente (Documento BFA §5.5).
 *
 * <p>Qué roles tienen el permiso y con qué alcance lo define DOC-04 (D05/D11). Mientras no exista
 * el módulo de sesión (E14), el adaptador provisional niega toda consulta.
 *
 * <p>Ambos métodos lanzan {@code ConsultaResultadoException} con {@code NO_AUTENTICADO} o {@code
 * SIN_PERMISO} cuando se rechaza el acceso.
 */
public interface AutorizacionResultadoPort {

    /**
     * Comprueba que hay sesión y que el rol activo tiene el permiso de consultar resultados. Se
     * llama antes de buscar la evaluación, para no revelar si existe.
     */
    void exigirPermisoConsultarResultados();

    /**
     * Comprueba que la evaluación está dentro del alcance del actor (su población o su propia
     * evaluación, según lo que apruebe DOC-04).
     */
    void exigirAlcanceSobre(EvaluacionConsultada evaluacion);
}
