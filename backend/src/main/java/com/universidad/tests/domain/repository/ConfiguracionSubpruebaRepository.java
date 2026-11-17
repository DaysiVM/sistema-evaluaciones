package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import java.util.List;
import java.util.Optional;

public interface ConfiguracionSubpruebaRepository {

    Optional<ConfiguracionSubprueba> buscarPorId(Long idConfiguracionSubprueba);

    List<ConfiguracionSubprueba> listarPorVersion(Long idVersionFormulario);

    Optional<ConfiguracionSubprueba> buscarPorVersionYSubprueba(
            Long idVersionFormulario, Long idSubprueba);

    boolean existePorVersionYSubprueba(Long idVersionFormulario, Long idSubprueba);

    ConfiguracionSubprueba guardar(ConfiguracionSubprueba configuracion);
}
