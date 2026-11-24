package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.SujetoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SujetoJpaRepository extends JpaRepository<SujetoJpaEntity, Long> {}
