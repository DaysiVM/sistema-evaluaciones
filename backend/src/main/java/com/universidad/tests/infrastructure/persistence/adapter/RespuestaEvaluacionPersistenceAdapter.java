package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.RespuestaEvaluacion;
import com.universidad.tests.domain.repository.RespuestaEvaluacionRepository;
import com.universidad.tests.infrastructure.persistence.entity.RespuestaEvaluacionJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.RespuestaEvaluacionJpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RespuestaEvaluacionPersistenceAdapter implements RespuestaEvaluacionRepository {
    private final RespuestaEvaluacionJpaRepository respuestas;

    public RespuestaEvaluacionPersistenceAdapter(RespuestaEvaluacionJpaRepository respuestas) {
        this.respuestas = respuestas;
    }

    @Override
    public List<RespuestaEvaluacion> listar(Long evaluacionId, String codigo) {
        return respuestas
                .findByIdEvaluacionAndSubpruebaCodigoOrderByOrdenEntrada(evaluacionId, codigo)
                .stream()
                .map(
                        r ->
                                new RespuestaEvaluacion(
                                        r.getIdRespuestaEvaluacion(),
                                        r.getIdEvaluacion(),
                                        r.getSubpruebaCodigo(),
                                        r.getIdPregunta(),
                                        r.getIdOpcion(),
                                        r.getValorTexto(),
                                        r.getOrdenEntrada(),
                                        r.getFechaRegistro()))
                .toList();
    }

    @Override
    public long contar(Long evaluacionId, String codigo) {
        return respuestas.countByIdEvaluacionAndSubpruebaCodigo(evaluacionId, codigo);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void registrar(RespuestaEvaluacion r) {
        respuestas
                .findByIdEvaluacionAndSubpruebaCodigoAndIdPreguntaAndOrdenEntrada(
                        r.idEvaluacion(), r.subpruebaCodigo(), r.idPregunta(), r.ordenEntrada())
                .ifPresentOrElse(
                        existente ->
                                existente.actualizar(
                                        r.idOpcion(), r.valorTexto(), r.fechaRegistro()),
                        () ->
                                respuestas.save(
                                        new RespuestaEvaluacionJpaEntity(
                                                r.idEvaluacion(),
                                                r.subpruebaCodigo(),
                                                r.idPregunta(),
                                                r.idOpcion(),
                                                r.valorTexto(),
                                                r.ordenEntrada(),
                                                r.fechaRegistro())));
    }
}
