package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.infrastructure.persistence.entity.EvaluacionJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EvaluacionJpaRepository extends JpaRepository<EvaluacionJpaEntity, Long> {

    boolean existsByIdSujetoAndIdTestAndIdPeriodoAndEstado(
            Long idSujeto, Long idTest, Long idPeriodo, EstadoEvaluacion estado);

    /** Carga la evaluación con sus ejecuciones, configuración y subprueba en una consulta. */
    @Query(
            "select distinct e from EvaluacionJpaEntity e"
                    + " left join fetch e.subpruebas es"
                    + " left join fetch es.configuracion c"
                    + " left join fetch c.subprueba"
                    + " where e.idEvaluacion = :id")
    Optional<EvaluacionJpaEntity> buscarConSubpruebas(@Param("id") Long idEvaluacion);
}
