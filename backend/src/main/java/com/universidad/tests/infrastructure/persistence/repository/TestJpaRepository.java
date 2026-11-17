package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.TestJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestJpaRepository extends JpaRepository<TestJpaEntity, Long> {

    Optional<TestJpaEntity> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<TestJpaEntity> findByActivoTrueOrderByNombreAsc();
}
