package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.VersionFormulario;
import java.util.List;
import java.util.Optional;

public interface VersionFormularioRepository {

    Optional<VersionFormulario> buscarPorId(Long idVersionFormulario);

    List<VersionFormulario> listarPorTest(Long idTest);

    Optional<VersionFormulario> buscarPorTestYCodigo(Long idTest, String codigoVersion);

    boolean existePorTestYCodigo(Long idTest, String codigoVersion);

    VersionFormulario guardar(VersionFormulario versionFormulario);
}
