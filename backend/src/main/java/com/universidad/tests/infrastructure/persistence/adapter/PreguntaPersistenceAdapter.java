package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.repository.PreguntaRepository;
import com.universidad.tests.infrastructure.persistence.entity.ConfiguracionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.PreguntaJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.ConfiguracionSubpruebaJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.PreguntaJpaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PreguntaPersistenceAdapter implements PreguntaRepository {

    private final PreguntaJpaRepository preguntaJpaRepository;
    private final ConfiguracionSubpruebaJpaRepository configuracionJpaRepository;

    public PreguntaPersistenceAdapter(
            PreguntaJpaRepository preguntaJpaRepository,
            ConfiguracionSubpruebaJpaRepository configuracionJpaRepository) {

        this.preguntaJpaRepository = preguntaJpaRepository;
        this.configuracionJpaRepository = configuracionJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pregunta> buscarPorId(Long idPregunta) {

        return preguntaJpaRepository.findById(idPregunta).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> listarPorConfiguracion(Long idConfiguracionSubprueba) {

        return preguntaJpaRepository
                .findByConfiguracionSubprueba_IdConfiguracionSubpruebaOrderByOrdenPresentacionAsc(
                        idConfiguracionSubprueba)
                .stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pregunta> buscarPorNumeroVisible(
            Long idConfiguracionSubprueba, String numeroVisible) {

        return preguntaJpaRepository
                .findByConfiguracionSubprueba_IdConfiguracionSubpruebaAndNumeroVisible(
                        idConfiguracionSubprueba, numeroVisible)
                .map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNumeroVisible(Long idConfiguracionSubprueba, String numeroVisible) {

        return preguntaJpaRepository
                .existsByConfiguracionSubprueba_IdConfiguracionSubpruebaAndNumeroVisible(
                        idConfiguracionSubprueba, numeroVisible);
    }

    @Override
    @Transactional
    public Pregunta guardar(Pregunta pregunta) {

        if (pregunta == null) {
            throw new IllegalArgumentException("La pregunta es obligatoria");
        }

        PreguntaJpaEntity entidad;

        if (pregunta.getIdPregunta() == null) {

            // Recuperar la configuracion de subprueba
            ConfiguracionSubpruebaJpaEntity configuracion =
                    configuracionJpaRepository
                            .findById(pregunta.getIdConfiguracionSubprueba())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la configuracion indicada"));

            // La version debe estar en BORRADOR
            validarBorrador(configuracion);

            // Evitar numeros visibles duplicados
            if (preguntaJpaRepository
                    .existsByConfiguracionSubprueba_IdConfiguracionSubpruebaAndNumeroVisible(
                            configuracion.getIdConfiguracionSubprueba(),
                            pregunta.getNumeroVisible())) {

                throw new IllegalArgumentException("Ya existe una pregunta con ese numero visible");
            }

            entidad =
                    new PreguntaJpaEntity(
                            configuracion,
                            pregunta.getNumeroVisible(),
                            pregunta.getOrdenPresentacion(),
                            pregunta.getEnunciado(),
                            pregunta.getRecursoRef());

        } else {

            // Recuperar la pregunta existente
            entidad =
                    preguntaJpaRepository
                            .findById(pregunta.getIdPregunta())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la pregunta indicada"));

            // Impedir ediciones sobre versiones publicadas
            validarBorrador(entidad.getConfiguracionSubprueba());

            // Una pregunta no puede cambiar de configuracion
            if (!Objects.equals(
                    entidad.getIdConfiguracionSubprueba(),
                    pregunta.getIdConfiguracionSubprueba())) {

                throw new IllegalArgumentException(
                        "No se puede cambiar la configuracion " + "de una pregunta existente");
            }

            // Comprobar duplicados si cambia el numero visible
            if (!Objects.equals(entidad.getNumeroVisible(), pregunta.getNumeroVisible())
                    && preguntaJpaRepository
                            .existsByConfiguracionSubprueba_IdConfiguracionSubpruebaAndNumeroVisible(
                                    pregunta.getIdConfiguracionSubprueba(),
                                    pregunta.getNumeroVisible())) {

                throw new IllegalArgumentException("Ya existe una pregunta con ese numero visible");
            }

            entidad.actualizarDatos(
                    pregunta.getNumeroVisible(),
                    pregunta.getOrdenPresentacion(),
                    pregunta.getEnunciado(),
                    pregunta.getRecursoRef());
        }

        PreguntaJpaEntity guardada = preguntaJpaRepository.saveAndFlush(entidad);

        return aDominio(guardada);
    }

    private void validarBorrador(ConfiguracionSubpruebaJpaEntity configuracion) {

        if (configuracion.getVersionFormulario().getEstado() != EstadoVersionFormulario.BORRADOR) {

            throw new IllegalStateException(
                    "Solo se pueden modificar preguntas " + "de una version en BORRADOR");
        }
    }

    private Pregunta aDominio(PreguntaJpaEntity entidad) {

        return new Pregunta(
                entidad.getIdPregunta(),
                entidad.getIdConfiguracionSubprueba(),
                entidad.getNumeroVisible(),
                entidad.getOrdenPresentacion(),
                entidad.getEnunciado(),
                entidad.getRecursoRef());
    }
}
