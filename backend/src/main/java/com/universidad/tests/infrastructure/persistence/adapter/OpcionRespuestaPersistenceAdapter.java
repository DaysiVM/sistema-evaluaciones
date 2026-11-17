package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.OpcionRespuesta;
import com.universidad.tests.domain.repository.OpcionRespuestaRepository;
import com.universidad.tests.infrastructure.persistence.entity.OpcionRespuestaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.PreguntaJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.OpcionRespuestaJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.PreguntaJpaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OpcionRespuestaPersistenceAdapter implements OpcionRespuestaRepository {

    private final OpcionRespuestaJpaRepository opcionJpaRepository;
    private final PreguntaJpaRepository preguntaJpaRepository;

    public OpcionRespuestaPersistenceAdapter(
            OpcionRespuestaJpaRepository opcionJpaRepository,
            PreguntaJpaRepository preguntaJpaRepository) {

        this.opcionJpaRepository = opcionJpaRepository;
        this.preguntaJpaRepository = preguntaJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OpcionRespuesta> buscarPorId(Long idOpcion) {

        return opcionJpaRepository.findById(idOpcion).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpcionRespuesta> listarPorPregunta(Long idPregunta) {

        return opcionJpaRepository
                .findByPregunta_IdPreguntaOrderByOrdenPresentacionAsc(idPregunta)
                .stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OpcionRespuesta> buscarPorCodigo(Long idPregunta, String codigo) {

        return opcionJpaRepository
                .findByPregunta_IdPreguntaAndCodigo(idPregunta, codigo)
                .map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCodigo(Long idPregunta, String codigo) {

        return opcionJpaRepository.existsByPregunta_IdPreguntaAndCodigo(idPregunta, codigo);
    }

    @Override
    @Transactional
    public OpcionRespuesta guardar(OpcionRespuesta opcion) {

        if (opcion == null) {
            throw new IllegalArgumentException("La opcion de respuesta es obligatoria");
        }

        OpcionRespuestaJpaEntity entidad;

        if (opcion.getIdOpcion() == null) {

            // Recuperar la pregunta correspondiente
            PreguntaJpaEntity pregunta =
                    preguntaJpaRepository
                            .findById(opcion.getIdPregunta())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la pregunta indicada"));

            // Solo agregar opciones a versiones en BORRADOR
            validarBorrador(pregunta);

            // Comprobar que el codigo no este duplicado
            if (opcionJpaRepository.existsByPregunta_IdPreguntaAndCodigo(
                    pregunta.getIdPregunta(), opcion.getCodigo())) {

                throw new IllegalArgumentException("Ya existe una opcion con ese codigo");
            }

            entidad =
                    new OpcionRespuestaJpaEntity(
                            pregunta,
                            opcion.getCodigo(),
                            opcion.getOrdenPresentacion(),
                            opcion.getTexto(),
                            opcion.getRecursoRef());

        } else {

            // Recuperar la opcion existente
            entidad =
                    opcionJpaRepository
                            .findById(opcion.getIdOpcion())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la opcion indicada"));

            PreguntaJpaEntity pregunta = entidad.getPregunta();

            // Proteger el contenido publicado o retirado
            validarBorrador(pregunta);

            // Impedir que una opcion cambie de pregunta
            if (!Objects.equals(pregunta.getIdPregunta(), opcion.getIdPregunta())) {

                throw new IllegalArgumentException(
                        "No se puede cambiar la pregunta " + "de una opcion existente");
            }

            // Impedir codigos duplicados al actualizar
            if (!Objects.equals(entidad.getCodigo(), opcion.getCodigo())
                    && opcionJpaRepository.existsByPregunta_IdPreguntaAndCodigo(
                            opcion.getIdPregunta(), opcion.getCodigo())) {

                throw new IllegalArgumentException("Ya existe una opcion con ese codigo");
            }

            entidad.actualizarDatos(
                    opcion.getCodigo(),
                    opcion.getOrdenPresentacion(),
                    opcion.getTexto(),
                    opcion.getRecursoRef());
        }

        OpcionRespuestaJpaEntity guardada = opcionJpaRepository.saveAndFlush(entidad);

        return aDominio(guardada);
    }

    private void validarBorrador(PreguntaJpaEntity pregunta) {

        EstadoVersionFormulario estado =
                pregunta.getConfiguracionSubprueba().getVersionFormulario().getEstado();

        if (estado != EstadoVersionFormulario.BORRADOR) {

            throw new IllegalStateException(
                    "Solo se pueden modificar opciones " + "de una version en BORRADOR");
        }
    }

    private OpcionRespuesta aDominio(OpcionRespuestaJpaEntity entidad) {

        return new OpcionRespuesta(
                entidad.getIdOpcion(),
                entidad.getPregunta().getIdPregunta(),
                entidad.getCodigo(),
                entidad.getOrdenPresentacion(),
                entidad.getTexto(),
                entidad.getRecursoRef());
    }
}
