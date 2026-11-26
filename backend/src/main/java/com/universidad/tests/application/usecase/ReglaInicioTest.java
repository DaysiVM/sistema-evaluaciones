package com.universidad.tests.application.usecase;

import com.universidad.tests.domain.model.Evaluacion;

/**
 * Punto de extensión del flujo común de inicio (Documento general §5.1.7): cada test aporta las
 * comprobaciones propias que exige E3 sin que el flujo común conozca sus reglas.
 */
public interface ReglaInicioTest {

    boolean aplicaA(String codigoTest);

    /** Se llama antes de crear la evaluación; lanza un error de negocio si no se cumple. */
    void validar(ContextoInicio contexto);

    /** Se llama dentro de la misma transacción, después de guardar la evaluación. */
    void registrar(ContextoInicio contexto, Evaluacion evaluacionGuardada);
}
