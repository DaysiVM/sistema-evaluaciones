package com.universidad.tests.infrastructure.persistence.bfa;

import com.universidad.tests.domain.repository.bfa.AplicacionFluidezBfaRepository;
import com.universidad.tests.infrastructure.persistence.entity.EvaluacionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.EvaluacionSubpruebaJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AplicacionFluidezBfaPersistenceAdapter implements AplicacionFluidezBfaRepository {

    private final EvaluacionSubpruebaJpaRepository subpruebaJpa;

    public AplicacionFluidezBfaPersistenceAdapter(EvaluacionSubpruebaJpaRepository subpruebaJpa) {
        this.subpruebaJpa = subpruebaJpa;
    }

    @Override
    @Transactional
    public void registrarLetra(Long idEvaluacionSubprueba, String letraInicial) {
        EvaluacionSubpruebaJpaEntity entity =
                subpruebaJpa
                        .findById(idEvaluacionSubprueba)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "No existe la subprueba " + idEvaluacionSubprueba));
        if (entity.getLetraInicial() != null) {
            throw new IllegalStateException("La letra de Fluidez Verbal ya está asignada");
        }
        entity.setLetraInicial(letraInicial);
        subpruebaJpa.saveAndFlush(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> buscarLetra(Long idEvaluacionSubprueba) {
        return subpruebaJpa
                .findById(idEvaluacionSubprueba)
                .map(EvaluacionSubpruebaJpaEntity::getLetraInicial);
    }
}
