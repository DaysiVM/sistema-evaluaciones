package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.SubpruebaJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubpruebaJpaRepository extends JpaRepository<SubpruebaJpaEntity, Long> {

    List<SubpruebaJpaEntity> findByTest_IdTestOrderByCodigoAsc(Long idTest);

    Optional<SubpruebaJpaEntity> findByTest_IdTestAndCodigo(Long idTest, String codigo);

    boolean existsByTest_IdTestAndCodigo(Long idTest, String codigo);
}
