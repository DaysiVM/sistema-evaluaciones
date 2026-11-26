package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.model.Sesion;
import com.universidad.tests.domain.repository.SesionRepository;
import com.universidad.tests.infrastructure.persistence.entity.SesionJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.SesionJpaRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SesionPersistenceAdapter implements SesionRepository {

    private final SesionJpaRepository jpaRepository;

    public SesionPersistenceAdapter(SesionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Sesion guardar(Sesion sesion) {

        if (sesion == null) {
            throw new IllegalArgumentException("La sesion es obligatoria");
        }
        if (sesion.getIdSesion() != null) {
            throw new IllegalArgumentException("Una sesion guardada no se modifica; se cierra");
        }

        SesionJpaEntity entidad =
                new SesionJpaEntity(
                        sesion.getIdCuenta(),
                        sesion.getResumenToken(),
                        sesion.getRolActivo().name(),
                        sesion.getAbiertaEn(),
                        sesion.getExpiraEn(),
                        sesion.getCerradaEn());

        return aDominio(jpaRepository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sesion> buscarPorResumenToken(String resumenToken) {
        return jpaRepository.findByResumenToken(resumenToken).map(this::aDominio);
    }

    @Override
    @Transactional
    public boolean cerrar(String resumenToken, Instant cerradaEn) {
        return jpaRepository.cerrar(resumenToken, cerradaEn) > 0;
    }

    private Sesion aDominio(SesionJpaEntity entidad) {
        return new Sesion(
                entidad.getIdSesion(),
                entidad.getIdCuenta(),
                entidad.getResumenToken(),
                Rol.valueOf(entidad.getRolActivo()),
                entidad.getAbiertaEn(),
                entidad.getExpiraEn(),
                entidad.getCerradaEn());
    }
}
