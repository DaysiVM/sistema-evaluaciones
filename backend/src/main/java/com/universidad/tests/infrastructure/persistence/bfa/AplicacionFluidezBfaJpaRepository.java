package com.universidad.tests.infrastructure.persistence.bfa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AplicacionFluidezBfaJpaRepository
        extends JpaRepository<AplicacionFluidezBfaJpaEntity, Long> {}
