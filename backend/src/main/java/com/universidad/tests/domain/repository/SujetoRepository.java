package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Sujeto;
import java.util.Optional;

public interface SujetoRepository {

    Optional<Sujeto> buscarPorId(Long idSujeto);

    // GEN-48-N1
    Optional<Sujeto> buscarPorCodigoInstitucional(String codigoInstitucional);

    Sujeto guardar(Sujeto sujeto);
}
