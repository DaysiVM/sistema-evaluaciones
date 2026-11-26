package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.Periodo;
import com.universidad.tests.domain.repository.PeriodoRepository;
import com.universidad.tests.infrastructure.persistence.entity.PeriodoJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.PeriodoJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PeriodoPersistenceAdapter implements PeriodoRepository {

    private final PeriodoJpaRepository periodoJpaRepository;

    public PeriodoPersistenceAdapter(PeriodoJpaRepository periodoJpaRepository) {
        this.periodoJpaRepository = periodoJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Periodo> buscarPorId(Long idPeriodo) {
        return periodoJpaRepository.findById(idPeriodo).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Periodo> listar(boolean soloDisponibles) {
        List<PeriodoJpaEntity> entidades =
                soloDisponibles
                        ? periodoJpaRepository.findByActivoTrueOrderByCodigoDesc()
                        : periodoJpaRepository.findAllByOrderByCodigoDesc();
        return entidades.stream().map(this::aDominio).toList();
    }

    private Periodo aDominio(PeriodoJpaEntity e) {
        return new Periodo(
                e.getIdPeriodo(),
                e.getCodigo(),
                e.getNombre(),
                e.getFechaInicio(),
                e.getFechaFin(),
                e.isActivo());
    }
}
