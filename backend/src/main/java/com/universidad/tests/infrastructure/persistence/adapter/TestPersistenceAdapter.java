package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.Test;
import com.universidad.tests.domain.repository.TestRepository;
import com.universidad.tests.infrastructure.persistence.entity.TestJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.TestJpaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TestPersistenceAdapter implements TestRepository {

    private final TestJpaRepository jpaRepository;

    public TestPersistenceAdapter(TestJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Test> buscarPorId(Long idTest) {
        return jpaRepository.findById(idTest).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Test> buscarPorCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Test> listarActivos() {
        return jpaRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCodigo(String codigo) {
        return jpaRepository.existsByCodigo(codigo);
    }

    @Override
    @Transactional
    public Test guardar(Test test) {

        if (test == null) {
            throw new IllegalArgumentException("El test es obligatorio");
        }

        TestJpaEntity entidad;

        if (test.getIdTest() == null) {

            // Crear un nuevo test
            entidad =
                    new TestJpaEntity(
                            test.getCodigo(),
                            test.getNombre(),
                            test.getDescripcion(),
                            test.isActivo());

        } else {

            // Recuperar y actualizar un test existente
            entidad =
                    jpaRepository
                            .findById(test.getIdTest())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe el test con ID: "
                                                            + test.getIdTest()));

            entidad.actualizarDatos(
                    test.getCodigo(), test.getNombre(), test.getDescripcion(), test.isActivo());
        }

        TestJpaEntity guardada = jpaRepository.saveAndFlush(entidad);

        return aDominio(guardada);
    }

    private Test aDominio(TestJpaEntity entidad) {
        return new Test(
                entidad.getIdTest(),
                entidad.getCodigo(),
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.isActivo());
    }
}
