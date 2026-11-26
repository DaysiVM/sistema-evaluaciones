package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Periodo;
import java.util.List;
import java.util.Optional;

public interface PeriodoRepository {

    Optional<Periodo> buscarPorId(Long idPeriodo);

    List<Periodo> listar(boolean soloDisponibles);
}
