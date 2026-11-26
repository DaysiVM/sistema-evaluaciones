package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.SujetoJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SujetoJpaRepository extends JpaRepository<SujetoJpaEntity, Long> {
    // GEN-48-N1
    Optional<SujetoJpaEntity> findByCodigoInstitucional(String codigoInstitucional);
}
