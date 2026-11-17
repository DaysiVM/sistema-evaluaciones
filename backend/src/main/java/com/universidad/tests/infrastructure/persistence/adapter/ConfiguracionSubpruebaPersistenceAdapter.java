package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.infrastructure.persistence.entity.ConfiguracionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.SubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.VersionFormularioJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.ConfiguracionSubpruebaJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.SubpruebaJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.VersionFormularioJpaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ConfiguracionSubpruebaPersistenceAdapter implements ConfiguracionSubpruebaRepository {

    private final ConfiguracionSubpruebaJpaRepository configuracionRepository;
    private final VersionFormularioJpaRepository versionRepository;
    private final SubpruebaJpaRepository subpruebaRepository;

    public ConfiguracionSubpruebaPersistenceAdapter(
            ConfiguracionSubpruebaJpaRepository configuracionRepository,
            VersionFormularioJpaRepository versionRepository,
            SubpruebaJpaRepository subpruebaRepository) {

        this.configuracionRepository = configuracionRepository;
        this.versionRepository = versionRepository;
        this.subpruebaRepository = subpruebaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConfiguracionSubprueba> buscarPorId(Long idConfiguracionSubprueba) {

        return configuracionRepository.findById(idConfiguracionSubprueba).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionSubprueba> listarPorVersion(Long idVersionFormulario) {

        return configuracionRepository
                .findByVersionFormulario_IdVersionFormularioOrderByOrdenAplicacionAsc(
                        idVersionFormulario)
                .stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConfiguracionSubprueba> buscarPorVersionYSubprueba(
            Long idVersionFormulario, Long idSubprueba) {

        return configuracionRepository
                .findByVersionFormulario_IdVersionFormularioAndSubprueba_IdSubprueba(
                        idVersionFormulario, idSubprueba)
                .map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorVersionYSubprueba(Long idVersionFormulario, Long idSubprueba) {

        return configuracionRepository
                .existsByVersionFormulario_IdVersionFormularioAndSubprueba_IdSubprueba(
                        idVersionFormulario, idSubprueba);
    }

    @Override
    @Transactional
    public ConfiguracionSubprueba guardar(ConfiguracionSubprueba configuracion) {

        if (configuracion == null) {
            throw new IllegalArgumentException("La configuracion es obligatoria");
        }

        ConfiguracionSubpruebaJpaEntity entidad;

        if (configuracion.getIdConfiguracionSubprueba() == null) {

            // Recuperar la version seleccionada
            VersionFormularioJpaEntity version =
                    versionRepository
                            .findById(configuracion.getIdVersionFormulario())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la version indicada"));

            validarBorrador(version);

            // Recuperar la subprueba
            SubpruebaJpaEntity subprueba =
                    subpruebaRepository
                            .findById(configuracion.getIdSubprueba())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la subprueba indicada"));

            // Validar compatibilidad entre test y version
            if (!Objects.equals(version.getIdTest(), subprueba.getIdTest())) {

                throw new IllegalArgumentException(
                        "La subprueba no pertenece al test de la version");
            }

            // Impedir asociaciones duplicadas
            if (configuracionRepository
                    .existsByVersionFormulario_IdVersionFormularioAndSubprueba_IdSubprueba(
                            version.getIdVersionFormulario(), subprueba.getIdSubprueba())) {

                throw new IllegalArgumentException(
                        "La subprueba ya esta configurada en esta version");
            }

            entidad =
                    new ConfiguracionSubpruebaJpaEntity(
                            version,
                            subprueba,
                            configuracion.getOrdenAplicacion(),
                            configuracion.getTiempoLimiteSegundos(),
                            configuracion.getTipoRespuesta(),
                            configuracion.getInstruccionesRef(),
                            configuracion.getPracticaRef(),
                            configuracion.getMaximoEntradas());

        } else {

            // Recuperar configuracion existente
            entidad =
                    configuracionRepository
                            .findById(configuracion.getIdConfiguracionSubprueba())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la configuracion indicada"));

            validarBorrador(entidad.getVersionFormulario());

            // No permitir cambiar las relaciones originales
            if (!Objects.equals(
                            entidad.getIdVersionFormulario(),
                            configuracion.getIdVersionFormulario())
                    || !Objects.equals(entidad.getIdSubprueba(), configuracion.getIdSubprueba())) {

                throw new IllegalArgumentException(
                        "No se puede cambiar la version ni la subprueba");
            }

            entidad.actualizarDatos(
                    configuracion.getOrdenAplicacion(),
                    configuracion.getTiempoLimiteSegundos(),
                    configuracion.getTipoRespuesta(),
                    configuracion.getInstruccionesRef(),
                    configuracion.getPracticaRef(),
                    configuracion.getMaximoEntradas());
        }

        return aDominio(configuracionRepository.saveAndFlush(entidad));
    }

    private void validarBorrador(VersionFormularioJpaEntity version) {

        if (version.getEstado() != EstadoVersionFormulario.BORRADOR) {

            throw new IllegalStateException("Solo se puede modificar una version en BORRADOR");
        }
    }

    private ConfiguracionSubprueba aDominio(ConfiguracionSubpruebaJpaEntity entidad) {

        return new ConfiguracionSubprueba(
                entidad.getIdConfiguracionSubprueba(),
                entidad.getIdVersionFormulario(),
                entidad.getIdSubprueba(),
                entidad.getOrdenAplicacion(),
                entidad.getTiempoLimiteSegundos(),
                entidad.getTipoRespuesta(),
                entidad.getInstruccionesRef(),
                entidad.getPracticaRef(),
                entidad.getMaximoEntradas());
    }
}
