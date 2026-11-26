package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.port.FinalizacionEvaluacionPort;
import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FinalizacionEvaluacionJdbcAdapter implements FinalizacionEvaluacionPort {
    private final JdbcTemplate jdbc;
    private final EntityManager em;

    public FinalizacionEvaluacionJdbcAdapter(JdbcTemplate jdbc, EntityManager em) {
        this.jdbc = jdbc;
        this.em = em;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Cierre finalizar(Long id, Instant ahora) {
        em.flush();
        jdbc.update(
                "update evaluacion set estado='FINALIZADA',fecha_fin=? where id_evaluacion=? and estado='EN_CURSO'",
                Timestamp.from(ahora),
                id);
        // Si aun faltan insumos aprobados, el contenedor queda pendiente y sin revision publicada.
        jdbc.update(
                "insert into resultado(id_evaluacion,estado,creado_en) values (?,'PENDIENTE_VALIDACION',?) on conflict(id_evaluacion) do nothing",
                id,
                Timestamp.from(ahora));
        em.clear();
        return jdbc.queryForObject(
                "select id_resultado,estado from resultado where id_evaluacion=?",
                (rs, n) -> new Cierre(rs.getLong(1), rs.getString(2)),
                id);
    }
}
