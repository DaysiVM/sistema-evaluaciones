package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Test;
import java.util.List;
import java.util.Optional;

public interface TestRepository {

    Optional<Test> buscarPorId(Long idTest);

    Optional<Test> buscarPorCodigo(String codigo);

    List<Test> listarActivos();

    boolean existePorCodigo(String codigo);

    Test guardar(Test test);
}
