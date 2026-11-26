package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.SesionJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SesionJpaRepository extends JpaRepository<SesionJpaEntity, Long> {

    Optional<SesionJpaEntity> findByResumenToken(String resumenToken);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "update SesionJpaEntity s set s.cerradaEn = :cerradaEn"
                    + " where s.resumenToken = :resumenToken and s.cerradaEn is null")
    int cerrar(@Param("resumenToken") String resumenToken, @Param("cerradaEn") Instant cerradaEn);
}
