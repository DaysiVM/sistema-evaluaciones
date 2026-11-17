package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Subprueba;
import java.util.List;
import java.util.Optional;

public interface SubpruebaRepository {

    Optional<Subprueba> buscarPorId(Long idSubprueba);

    Optional<Subprueba> buscarPorCodigo(Long idTest, String codigo);

    List<Subprueba> listarPorTest(Long idTest);

    boolean existePorCodigo(Long idTest, String codigo);

    Subprueba guardar(Subprueba subprueba);
}
