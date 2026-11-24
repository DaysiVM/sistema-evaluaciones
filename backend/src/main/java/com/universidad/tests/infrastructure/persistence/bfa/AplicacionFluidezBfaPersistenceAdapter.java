package com.universidad.tests.infrastructure.persistence.bfa;

import com.universidad.tests.domain.repository.bfa.AplicacionFluidezBfaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AplicacionFluidezBfaPersistenceAdapter implements AplicacionFluidezBfaRepository {

    private final AplicacionFluidezBfaJpaRepository jpaRepository;

    public AplicacionFluidezBfaPersistenceAdapter(AplicacionFluidezBfaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void registrarLetra(Long idEvaluacionSubprueba, String letraInicial) {
        if (jpaRepository.existsById(idEvaluacionSubprueba)) {
            throw new IllegalStateException("La letra de Fluidez Verbal ya está asignada");
        }
        jpaRepository.saveAndFlush(
                new AplicacionFluidezBfaJpaEntity(idEvaluacionSubprueba, letraInicial));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> buscarLetra(Long idEvaluacionSubprueba) {
        return jpaRepository
                .findById(idEvaluacionSubprueba)
                .map(AplicacionFluidezBfaJpaEntity::getLetraInicial);
    }
}
