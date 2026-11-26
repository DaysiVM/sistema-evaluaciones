package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.port.EventoAuditoriaPort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class EventoAuditoriaPersistenceAdapter implements EventoAuditoriaPort {

    private final JdbcClient jdbc;

    public EventoAuditoriaPersistenceAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void insertar(long idCuenta, String tipo, String entidad, long idEntidad) {
        jdbc.sql(
                        """
                        INSERT INTO evento_auditoria (
                            id_cuenta, accion, entidad_tipo, entidad_id,
                            id_evaluacion, id_evaluacion_subprueba
                        ) VALUES (
                            :idCuenta, :tipo, :entidad, :idEntidad,
                            CASE :entidad
                                WHEN 'EVALUACION' THEN :idEntidad
                                WHEN 'EVALUACION_SUBPRUEBA' THEN (
                                    SELECT id_evaluacion FROM evaluacion_subprueba
                                    WHERE id_evaluacion_subprueba = :idEntidad
                                )
                            END,
                            CASE WHEN :entidad = 'EVALUACION_SUBPRUEBA' THEN :idEntidad END
                        )
                        """)
                .param("idCuenta", idCuenta)
                .param("tipo", tipo)
                .param("entidad", entidad)
                .param("idEntidad", idEntidad)
                .update();
    }
}
