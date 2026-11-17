package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import com.universidad.tests.infrastructure.persistence.entity.TestJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.VersionFormularioJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.TestJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.VersionFormularioJpaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class VersionFormularioPersistenceAdapter implements VersionFormularioRepository {

    private final VersionFormularioJpaRepository versionJpaRepository;
    private final TestJpaRepository testJpaRepository;

    public VersionFormularioPersistenceAdapter(
            VersionFormularioJpaRepository versionJpaRepository,
            TestJpaRepository testJpaRepository) {

        this.versionJpaRepository = versionJpaRepository;
        this.testJpaRepository = testJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VersionFormulario> buscarPorId(Long idVersionFormulario) {

        return versionJpaRepository.findById(idVersionFormulario).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VersionFormulario> listarPorTest(Long idTest) {

        return versionJpaRepository.findByTest_IdTestOrderByCodigoVersionAsc(idTest).stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VersionFormulario> buscarPorTestYCodigo(Long idTest, String codigoVersion) {

        return versionJpaRepository
                .findByTest_IdTestAndCodigoVersion(idTest, codigoVersion)
                .map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorTestYCodigo(Long idTest, String codigoVersion) {

        return versionJpaRepository.existsByTest_IdTestAndCodigoVersion(idTest, codigoVersion);
    }

    @Override
    @Transactional
    public VersionFormulario guardar(VersionFormulario version) {

        if (version == null) {
            throw new IllegalArgumentException("La version es obligatoria");
        }

        VersionFormularioJpaEntity entidad;

        if (version.getIdVersionFormulario() == null) {

            if (version.getEstado() != EstadoVersionFormulario.BORRADOR) {
                throw new IllegalArgumentException("Una version nueva debe iniciar en BORRADOR");
            }

            TestJpaEntity test =
                    testJpaRepository
                            .findById(version.getIdTest())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe el test con ID: "
                                                            + version.getIdTest()));

            entidad =
                    new VersionFormularioJpaEntity(
                            test,
                            version.getCodigoVersion(),
                            version.getDescripcion(),
                            version.getFuenteRef());

        } else {

            entidad =
                    versionJpaRepository
                            .findById(version.getIdVersionFormulario())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la version con ID: "
                                                            + version.getIdVersionFormulario()));

            if (!Objects.equals(entidad.getIdTest(), version.getIdTest())) {
                throw new IllegalArgumentException("No se puede cambiar el test de la version");
            }

            if (!Objects.equals(entidad.getCodigoVersion(), version.getCodigoVersion())
                    || !Objects.equals(entidad.getDescripcion(), version.getDescripcion())
                    || !Objects.equals(entidad.getFuenteRef(), version.getFuenteRef())) {

                throw new IllegalArgumentException(
                        "No se permite modificar los datos " + "de una version existente");
            }

            entidad.aplicarEstado(
                    version.getEstado(),
                    version.getPublicadaEn(),
                    version.getEvidenciaValidacion());
        }

        return aDominio(versionJpaRepository.saveAndFlush(entidad));
    }

    private VersionFormulario aDominio(VersionFormularioJpaEntity entidad) {

        return VersionFormulario.restaurar(
                entidad.getIdVersionFormulario(),
                entidad.getIdTest(),
                entidad.getCodigoVersion(),
                entidad.getDescripcion(),
                entidad.getFuenteRef(),
                entidad.getEstado(),
                entidad.getPublicadaEn(),
                entidad.getEvidenciaValidacion());
    }
}
