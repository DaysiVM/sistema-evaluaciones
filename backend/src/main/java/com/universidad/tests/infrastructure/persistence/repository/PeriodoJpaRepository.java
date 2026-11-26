package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.PeriodoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodoJpaRepository extends JpaRepository<PeriodoJpaEntity, Long> {

    List<PeriodoJpaEntity> findAllByOrderByCodigoDesc();

    List<PeriodoJpaEntity> findByActivoTrueOrderByCodigoDesc();
}
