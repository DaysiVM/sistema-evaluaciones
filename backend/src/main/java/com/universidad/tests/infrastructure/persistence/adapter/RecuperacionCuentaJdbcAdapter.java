package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.port.RecuperacionCuentaPort;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RecuperacionCuentaJdbcAdapter implements RecuperacionCuentaPort {
    private final JdbcTemplate jdbc;

    public RecuperacionCuentaJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void reemplazar(Long id, String resumen, Instant ahora, Instant expira) {
        jdbc.queryForObject(
                "select id_cuenta from cuenta where id_cuenta = ? for update", Long.class, id);
        // Medir la vigencia al adquirir el bloqueo, no antes de esperar por otra solicitud.
        Instant bloqueoAdquirido = Instant.now();
        expira = bloqueoAdquirido.plus(Duration.between(ahora, expira));
        ahora = bloqueoAdquirido;
        jdbc.update(
                "update recuperacion_cuenta set usada_en = ? where id_cuenta = ? and usada_en is null",
                Timestamp.from(ahora),
                id);
        jdbc.update(
                "insert into recuperacion_cuenta(id_cuenta,resumen_token,creada_en,expira_en) values (?,?,?,?)",
                id,
                resumen,
                Timestamp.from(ahora),
                Timestamp.from(expira));
    }

    @Override
    @Transactional
    public boolean confirmar(String resumen, String hash, Instant ahora) {
        // Serializar por cuenta tanto las solicitudes como las confirmaciones concurrentes.
        List<Long> ids =
                jdbc.queryForList(
                        "select c.id_cuenta from cuenta c join recuperacion_cuenta r on r.id_cuenta=c.id_cuenta where r.resumen_token=? for update of c",
                        Long.class,
                        resumen);
        if (ids.isEmpty()) return false;
        Long id = ids.get(0);
        ahora = Instant.now();
        int usadas =
                jdbc.update(
                        "update recuperacion_cuenta set usada_en=? where resumen_token=? and usada_en is null and expira_en>? and exists(select 1 from cuenta c where c.id_cuenta=recuperacion_cuenta.id_cuenta and c.estado='ACTIVA')",
                        Timestamp.from(ahora),
                        resumen,
                        Timestamp.from(ahora));
        if (usadas != 1) return false;
        jdbc.update("update cuenta set resumen_contrasena=? where id_cuenta=?", hash, id);
        jdbc.update(
                "update sesion set cerrada_en=? where id_cuenta=? and cerrada_en is null",
                Timestamp.from(ahora),
                id);
        return true;
    }
}
