package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.exception.ErroresEvaluacion;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.infrastructure.persistence.entity.EvaluacionJpaEntity;
import com.universidad.tests.infrastructure.persistence.entity.EvaluacionSubpruebaJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.EvaluacionJpaRepository;
import com.universidad.tests.infrastructure.persistence.repository.EvaluacionSubpruebaJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EvaluacionPersistenceAdapter implements EvaluacionRepository {

    private final EvaluacionJpaRepository evaluacionJpa;
    private final EvaluacionSubpruebaJpaRepository subpruebaJpa;

    public EvaluacionPersistenceAdapter(
            EvaluacionJpaRepository evaluacionJpa, EvaluacionSubpruebaJpaRepository subpruebaJpa) {
        this.evaluacionJpa = evaluacionJpa;
        this.subpruebaJpa = subpruebaJpa;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Evaluacion> buscarPorId(Long idEvaluacion) {
        return evaluacionJpa.findById(idEvaluacion).map(this::aDominio);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Evaluacion> buscarPorIdParaActualizar(Long idEvaluacion) {
        return evaluacionJpa.buscarParaActualizar(idEvaluacion).map(this::aDominio);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void iniciarSubprueba(EvaluacionSubprueba subprueba) {
        subpruebaJpa
                .findById(subprueba.idEvaluacionSubprueba())
                .orElseThrow()
                .iniciar(subprueba.fechaInicio(), subprueba.fechaFinLimite());
    }

    private Evaluacion aDominio(EvaluacionJpaEntity evaluacion) {
        return aDominio(
                evaluacion,
                subpruebaJpa.findByIdEvaluacionOrderByOrdenAsc(evaluacion.getIdEvaluacion()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEnCurso(Long idSujeto, Long idTest, Long idPeriodo) {
        return evaluacionJpa.existsByIdSujetoAndIdTestAndIdPeriodoAndEstado(
                idSujeto, idTest, idPeriodo, EstadoEvaluacion.EN_CURSO);
    }

    @Override
    @Transactional
    public Evaluacion guardarNueva(Evaluacion evaluacion) {
        if (existeEnCurso(evaluacion.idSujeto(), evaluacion.idTest(), evaluacion.idPeriodo())) {
            throw ErroresEvaluacion.evaluacionEnCurso();
        }
        return guardar(evaluacion);
    }

    @Override
    @Transactional
    public Evaluacion guardar(Evaluacion evaluacion) {
        if (evaluacion.idEvaluacion() != null) {
            throw new IllegalArgumentException("Solo se admite insertar evaluaciones nuevas");
        }

        EvaluacionJpaEntity guardada =
                evaluacionJpa.saveAndFlush(
                        new EvaluacionJpaEntity(
                                evaluacion.idSujeto(),
                                evaluacion.idPeriodo(),
                                evaluacion.idTest(),
                                evaluacion.idVersionFormulario(),
                                evaluacion.estado(),
                                evaluacion.fechaInicio(),
                                evaluacion.fechaFin()));

        List<EvaluacionSubpruebaJpaEntity> subpruebas =
                subpruebaJpa.saveAllAndFlush(
                        evaluacion.subpruebas().stream()
                                .map(s -> aEntidad(guardada.getIdEvaluacion(), s))
                                .toList());

        return aDominio(guardada, subpruebas);
    }

    private EvaluacionSubpruebaJpaEntity aEntidad(Long idEvaluacion, EvaluacionSubprueba s) {
        return new EvaluacionSubpruebaJpaEntity(
                idEvaluacion,
                s.idConfiguracionSubprueba(),
                s.codigo(),
                s.nombre(),
                s.orden(),
                s.duracionSegundos(),
                s.estado(),
                s.instrucciones(),
                s.permitirRetroceso(),
                s.fechaInicio(),
                s.fechaFinLimite(),
                s.fechaCierre(),
                s.letraInicial(),
                s.maximoEntradas());
    }

    private Evaluacion aDominio(
            EvaluacionJpaEntity e, List<EvaluacionSubpruebaJpaEntity> subpruebas) {
        return new Evaluacion(
                e.getIdEvaluacion(),
                e.getIdSujeto(),
                e.getIdPeriodo(),
                e.getIdTest(),
                e.getIdVersionFormulario(),
                e.getEstado(),
                e.getFechaInicio(),
                e.getFechaFin(),
                subpruebas.stream()
                        .map(
                                s ->
                                        new EvaluacionSubprueba(
                                                s.getIdEvaluacionSubprueba(),
                                                s.getIdEvaluacion(),
                                                s.getIdConfiguracionSubprueba(),
                                                s.getCodigo(),
                                                s.getNombre(),
                                                s.getOrden(),
                                                s.getDuracionSegundos(),
                                                s.getEstado(),
                                                s.getInstrucciones(),
                                                s.isPermitirRetroceso(),
                                                s.getFechaInicio(),
                                                s.getFechaFinLimite(),
                                                s.getFechaCierre(),
                                                s.getLetraInicial(),
                                                s.getMaximoEntradas()))
                        .toList());
    }
}
