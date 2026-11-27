package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.repository.ListadoEvaluacionesRepository;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ListadoEvaluacionesJdbcAdapter implements ListadoEvaluacionesRepository {
    private final JdbcClient jdbc;

    public ListadoEvaluacionesJdbcAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Long> buscarIds(
            Long cuentaRestringida,
            Long sujetoId,
            Long testId,
            Long periodoId,
            EstadoEvaluacion estado) {
        return jdbc.sql(
                        """
                select e.id_evaluacion
                from evaluacion e
                where e.id_sujeto = coalesce(:sujetoId, e.id_sujeto)
                  and e.id_test = coalesce(:testId, e.id_test)
                  and e.id_periodo = coalesce(:periodoId, e.id_periodo)
                  and e.estado = coalesce(:estado, e.estado)
                  and (:cuenta is null or exists (
                      select 1 from cuenta_sujeto_asignado a
                      where a.id_cuenta = :cuenta and a.id_sujeto = e.id_sujeto))
                order by e.id_evaluacion
                """)
                .param("sujetoId", sujetoId, Types.BIGINT)
                .param("testId", testId, Types.BIGINT)
                .param("periodoId", periodoId, Types.BIGINT)
                .param("estado", estado == null ? null : estado.name(), Types.VARCHAR)
                .param("cuenta", cuentaRestringida, Types.BIGINT)
                .query(Long.class)
                .list();
    }
}
