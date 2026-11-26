package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.port.AlcanceSujetoPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AlcanceSujetoJdbcAdapter implements AlcanceSujetoPort {
    private final JdbcTemplate jdbc;

    public AlcanceSujetoJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean estaAsignado(Long cuenta, Long sujeto) {
        if (cuenta == null || sujeto == null) return false;
        return Boolean.TRUE.equals(
                jdbc.queryForObject(
                        "select exists(select 1 from cuenta_sujeto_asignado where id_cuenta=? and id_sujeto=?)",
                        Boolean.class,
                        cuenta,
                        sujeto));
    }
}
