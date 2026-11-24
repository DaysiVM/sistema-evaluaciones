package com.universidad.tests.domain.repository.bfa;

import java.util.Optional;

/** Letra inicial de Fluidez Verbal por ejecución FV (ENT-BFA-11). */
public interface AplicacionFluidezBfaRepository {

    void registrarLetra(Long idEvaluacionSubprueba, String letraInicial);

    Optional<String> buscarLetra(Long idEvaluacionSubprueba);
}
