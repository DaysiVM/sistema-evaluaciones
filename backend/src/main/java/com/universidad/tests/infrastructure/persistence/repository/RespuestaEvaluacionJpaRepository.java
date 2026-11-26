package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.RespuestaEvaluacionJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespuestaEvaluacionJpaRepository
        extends JpaRepository<RespuestaEvaluacionJpaEntity, Long> {
    List<RespuestaEvaluacionJpaEntity> findByIdEvaluacionAndSubpruebaCodigoOrderByOrdenEntrada(
            Long idEvaluacion, String subpruebaCodigo);

    Optional<RespuestaEvaluacionJpaEntity>
            findByIdEvaluacionAndSubpruebaCodigoAndIdPreguntaAndOrdenEntrada(
                    Long idEvaluacion, String subpruebaCodigo, Long idPregunta, int ordenEntrada);

    long countByIdEvaluacionAndSubpruebaCodigo(Long idEvaluacion, String subpruebaCodigo);
}
