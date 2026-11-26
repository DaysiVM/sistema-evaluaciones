package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.infrastructure.persistence.entity.EvaluacionJpaEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EvaluacionJpaRepository extends JpaRepository<EvaluacionJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EvaluacionJpaEntity e where e.idEvaluacion = :id")
    Optional<EvaluacionJpaEntity> buscarParaActualizar(@Param("id") Long id);

    boolean existsByIdSujetoAndIdTestAndIdPeriodoAndEstado(
            Long idSujeto, Long idTest, Long idPeriodo, EstadoEvaluacion estado);
}
