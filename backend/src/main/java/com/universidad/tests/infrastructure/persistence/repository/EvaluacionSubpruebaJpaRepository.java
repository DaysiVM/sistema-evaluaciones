package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.EvaluacionSubpruebaJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluacionSubpruebaJpaRepository
        extends JpaRepository<EvaluacionSubpruebaJpaEntity, Long> {

    List<EvaluacionSubpruebaJpaEntity> findByIdEvaluacionOrderByOrdenAsc(Long idEvaluacion);

    List<EvaluacionSubpruebaJpaEntity> findByIdEvaluacionInOrderByOrdenAsc(List<Long> ids);
}
