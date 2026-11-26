package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.CuentaJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CuentaJpaRepository extends JpaRepository<CuentaJpaEntity, Long> {

    // Misma comparacion que el indice unico uq_cuenta_correo
    @Query("select c from CuentaJpaEntity c where lower(c.correo) = lower(:correo)")
    Optional<CuentaJpaEntity> buscarPorCorreo(@Param("correo") String correo);

    @Query("select count(c) > 0 from CuentaJpaEntity c where lower(c.correo) = lower(:correo)")
    boolean existePorCorreo(@Param("correo") String correo);
}
