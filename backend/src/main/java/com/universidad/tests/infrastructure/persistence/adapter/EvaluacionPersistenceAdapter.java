package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.exception.ErroresEvaluacion;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.infrastructure.persistence.entity.ConfiguracionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.EvaluacionJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.EvaluacionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.ConfiguracionSubpruebaJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.EvaluacionJpaRepository;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EvaluacionPersistenceAdapter implements EvaluacionRepository {

    /** Índice único parcial de la migración: una evaluación abierta por sujeto/test/período. */
    static final String INDICE_EN_CURSO = "uq_evaluacion_en_curso";

    private final EvaluacionJpaRepository evaluacionJpaRepository;
    private final ConfiguracionSubpruebaJpaRepository configuracionJpaRepository;

    public EvaluacionPersistenceAdapter(
            EvaluacionJpaRepository evaluacionJpaRepository,
            ConfiguracionSubpruebaJpaRepository configuracionJpaRepository) {
        this.evaluacionJpaRepository = evaluacionJpaRepository;
        this.configuracionJpaRepository = configuracionJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Evaluacion> buscarPorId(Long idEvaluacion) {
        return evaluacionJpaRepository.buscarConSubpruebas(idEvaluacion).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEnCurso(Long idSujeto, Long idTest, Long idPeriodo) {
        return evaluacionJpaRepository.existsByIdSujetoAndIdTestAndIdPeriodoAndEstado(
                idSujeto, idTest, idPeriodo, EstadoEvaluacion.EN_CURSO);
    }

    @Override
    @Transactional
    public Evaluacion guardarNueva(Evaluacion evaluacion) {
        if (evaluacion.getIdEvaluacion() != null) {
            throw new IllegalArgumentException("La evaluación ya existe");
        }

        EvaluacionJpaEntity entidad =
                new EvaluacionJpaEntity(
                        evaluacion.getIdSujeto(),
                        evaluacion.getIdVersionFormulario(),
                        evaluacion.getIdTest(),
                        evaluacion.getIdPeriodo(),
                        evaluacion.getEstado(),
                        evaluacion.getIniciadaEn());

        for (EvaluacionSubprueba subprueba : evaluacion.getSubpruebas()) {
            ConfiguracionSubpruebaJpaEntity configuracion =
                    configuracionJpaRepository
                            .findById(subprueba.getIdConfiguracionSubprueba())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la configuración "
                                                            + subprueba
                                                                    .getIdConfiguracionSubprueba()));
            entidad.agregarSubprueba(new EvaluacionSubpruebaJpaEntity(entidad, configuracion));
        }

        try {
            return aDominio(evaluacionJpaRepository.saveAndFlush(entidad));
        } catch (DataIntegrityViolationException e) {
            if (violaIndiceEnCurso(e)) {
                // Otra solicitud creó la evaluación entre la comprobación y el guardado.
                throw ErroresEvaluacion.evaluacionEnCurso();
            }
            throw e;
        }
    }

    private static boolean violaIndiceEnCurso(DataIntegrityViolationException e) {
        Throwable causa = e;
        while (causa != null) {
            String mensaje = causa.getMessage();
            if (mensaje != null && mensaje.toLowerCase(Locale.ROOT).contains(INDICE_EN_CURSO)) {
                return true;
            }
            causa = causa.getCause();
        }
        return false;
    }

    private Evaluacion aDominio(EvaluacionJpaEntity entidad) {
        return Evaluacion.restaurar(
                entidad.getIdEvaluacion(),
                entidad.getIdSujeto(),
                entidad.getIdVersionFormulario(),
                entidad.getIdTest(),
                entidad.getIdPeriodo(),
                entidad.getEstado(),
                entidad.getIniciadaEn(),
                entidad.getFinalizadaEn(),
                entidad.getSubpruebas().stream().map(this::aDominio).toList());
    }

    private EvaluacionSubprueba aDominio(EvaluacionSubpruebaJpaEntity entidad) {
        ConfiguracionSubpruebaJpaEntity configuracion = entidad.getConfiguracion();
        return EvaluacionSubprueba.restaurar(
                entidad.getIdEvaluacionSubprueba(),
                configuracion.getIdConfiguracionSubprueba(),
                configuracion.getSubprueba().getCodigo(),
                configuracion.getOrdenAplicacion(),
                entidad.getEstado(),
                entidad.getIniciadaEn(),
                entidad.getFinLimite(),
                entidad.getCerradaEn());
    }
}
