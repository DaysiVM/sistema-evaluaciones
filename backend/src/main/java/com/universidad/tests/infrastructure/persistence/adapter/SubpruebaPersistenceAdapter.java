package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.infrastructure.persistence.entity.SubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.TestJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.SubpruebaJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.TestJpaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SubpruebaPersistenceAdapter implements SubpruebaRepository {

    private final SubpruebaJpaRepository subpruebaJpaRepository;
    private final TestJpaRepository testJpaRepository;

    public SubpruebaPersistenceAdapter(
            SubpruebaJpaRepository subpruebaJpaRepository, TestJpaRepository testJpaRepository) {

        this.subpruebaJpaRepository = subpruebaJpaRepository;
        this.testJpaRepository = testJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subprueba> buscarPorId(Long idSubprueba) {

        return subpruebaJpaRepository.findById(idSubprueba).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subprueba> buscarPorCodigo(Long idTest, String codigo) {

        return subpruebaJpaRepository
                .findByTest_IdTestAndCodigo(idTest, codigo)
                .map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subprueba> listarPorTest(Long idTest) {

        return subpruebaJpaRepository.findByTest_IdTestOrderByCodigoAsc(idTest).stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCodigo(Long idTest, String codigo) {

        return subpruebaJpaRepository.existsByTest_IdTestAndCodigo(idTest, codigo);
    }

    @Override
    @Transactional
    public Subprueba guardar(Subprueba subprueba) {

        if (subprueba == null) {
            throw new IllegalArgumentException("La subprueba es obligatoria");
        }

        SubpruebaJpaEntity entidad;

        if (subprueba.getIdSubprueba() == null) {

            // Comprobar que el Test exista
            TestJpaEntity test =
                    testJpaRepository
                            .findById(subprueba.getIdTest())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe el test con ID: "
                                                            + subprueba.getIdTest()));

            // Crear una nueva subprueba
            entidad =
                    new SubpruebaJpaEntity(
                            test,
                            subprueba.getCodigo(),
                            subprueba.getNombre(),
                            subprueba.getDescripcion());

        } else {

            // Buscar una subprueba existente
            entidad =
                    subpruebaJpaRepository
                            .findById(subprueba.getIdSubprueba())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la subprueba con ID: "
                                                            + subprueba.getIdSubprueba()));

            // Evitar cambiar el Test al que pertenece
            if (!entidad.getIdTest().equals(subprueba.getIdTest())) {

                throw new IllegalArgumentException("No se puede cambiar el test de una subprueba");
            }

            entidad.actualizarDatos(
                    subprueba.getCodigo(), subprueba.getNombre(), subprueba.getDescripcion());
        }

        SubpruebaJpaEntity guardada = subpruebaJpaRepository.saveAndFlush(entidad);

        return aDominio(guardada);
    }

    private Subprueba aDominio(SubpruebaJpaEntity entidad) {

        return new Subprueba(
                entidad.getIdSubprueba(),
                entidad.getIdTest(),
                entidad.getCodigo(),
                entidad.getNombre(),
                entidad.getDescripcion());
    }
}
