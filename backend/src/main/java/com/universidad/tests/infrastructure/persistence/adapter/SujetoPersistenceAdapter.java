package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.Sujeto;
import com.universidad.tests.domain.repository.SujetoRepository;
import com.universidad.tests.infrastructure.persistence.entity.SujetoJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.SujetoJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SujetoPersistenceAdapter implements SujetoRepository {

    private final SujetoJpaRepository sujetoJpaRepository;

    public SujetoPersistenceAdapter(SujetoJpaRepository sujetoJpaRepository) {
        this.sujetoJpaRepository = sujetoJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sujeto> buscarPorId(Long idSujeto) {
        return sujetoJpaRepository.findById(idSujeto).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sujeto> buscarPorCodigoInstitucional(String codigoInstitucional) {
        return sujetoJpaRepository
                .findByCodigoInstitucional(codigoInstitucional)
                .map(this::aDominio);
    }

    @Override
    @Transactional
    public Sujeto guardar(Sujeto sujeto) {
        SujetoJpaEntity entidad =
                new SujetoJpaEntity(
                        sujeto.codigoInstitucional(),
                        sujeto.nombres(),
                        sujeto.apellidos(),
                        sujeto.fechaNacimiento());
        return aDominio(sujetoJpaRepository.save(entidad));
    }

    private Sujeto aDominio(SujetoJpaEntity e) {
        return new Sujeto(
                e.getIdSujeto(),
                e.getCodigoInstitucional(),
                e.getNombres(),
                e.getApellidos(),
                e.getFechaNacimiento());
    }
}
