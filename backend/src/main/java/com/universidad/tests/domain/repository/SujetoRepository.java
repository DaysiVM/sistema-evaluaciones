package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Sujeto;
import java.util.Optional;

public interface SujetoRepository {

    Optional<Sujeto> buscarPorId(Long idSujeto);
}
