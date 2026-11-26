package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.exception.EvaluacionNoEncontradaException;
import com.universidad.tests.domain.repository.CierreSubpruebasRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CierreSubpruebasPersistenceAdapter implements CierreSubpruebasRepository {
    private final JdbcTemplate jdbc;
    private final jakarta.persistence.EntityManager em;

    public CierreSubpruebasPersistenceAdapter(
            JdbcTemplate jdbc, jakarta.persistence.EntityManager em) {
        this.jdbc = jdbc;
        this.em = em;
    }

    @Override
    public List<Long> buscarEvaluacionesVencidas(Instant ahora, int limite) {
        return jdbc.queryForList(
                "SELECT DISTINCT s.id_evaluacion FROM evaluacion_subprueba s"
                        + " JOIN evaluacion e USING(id_evaluacion)"
                        + " WHERE e.estado='EN_CURSO' AND s.estado='ACTIVA'"
                        + " AND s.fecha_fin_limite<=? ORDER BY s.id_evaluacion LIMIT ?",
                Long.class,
                Timestamp.from(ahora),
                limite);
    }

    @Override
    @Transactional
    public void cerrarVencidas(Long idEvaluacion, Supplier<Instant> reloj) {
        em.flush();
        var evaluaciones =
                jdbc.queryForList(
                        "SELECT id_evaluacion FROM evaluacion WHERE id_evaluacion=? FOR UPDATE",
                        Long.class,
                        idEvaluacion);
        if (evaluaciones.isEmpty()) throw new EvaluacionNoEncontradaException(idEvaluacion);
        Instant ahora = reloj.get(); // Comprobar el vencimiento después de obtener el bloqueo.

        // El cierre y su solicitud se confirman juntos; repetirlo no altera la fecha ni duplica
        // filas.
        jdbc.update(
                """
                WITH vencidas AS (
                    UPDATE evaluacion_subprueba s SET estado='CERRADA',
                        fecha_cierre=s.fecha_fin_limite
                    FROM evaluacion e
                    WHERE s.id_evaluacion=e.id_evaluacion AND e.estado='EN_CURSO'
                        AND s.id_evaluacion=? AND s.estado='ACTIVA' AND s.fecha_fin_limite<=?
                    RETURNING s.id_evaluacion_subprueba,s.id_evaluacion,s.codigo,s.fecha_cierre
                )
                INSERT INTO solicitud_calculo_subprueba
                    (id_evaluacion_subprueba,id_evaluacion,codigo_test,codigo_subprueba,fecha_cierre)
                SELECT v.id_evaluacion_subprueba,v.id_evaluacion,t.codigo,v.codigo,v.fecha_cierre
                FROM vencidas v JOIN evaluacion e USING(id_evaluacion)
                    JOIN test t ON t.id_test=e.id_test
                ON CONFLICT (id_evaluacion_subprueba) DO NOTHING
                """,
                idEvaluacion,
                Timestamp.from(ahora));
        em.clear();
    }
}
