package com.universidad.tests.infrastructure.persistence.resultados;

import com.universidad.tests.domain.port.resultados.AuditoriaResultadoPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Registro provisional de consultas de resultados en el log de la aplicación.
 *
 * <p>EventoAuditoria (ENT-COM-11) corresponde a GEN-08. Cuando exista su tabla, se reemplaza por un
 * adaptador que guarde el evento con el actor autenticado y la fecha, activado con el perfil {@code
 * auditoria-integrada}. Solo se registran identificadores, sin puntuaciones ni datos personales.
 */
@Component
@Profile("!auditoria-integrada")
public class AuditoriaResultadoRegistroAdapter implements AuditoriaResultadoPort {

    private static final Logger AUDITORIA = LoggerFactory.getLogger("auditoria.resultados");

    @Override
    public void registrarConsulta(Long evaluacionId, Long resultadoId, Long calificacionId) {
        AUDITORIA.info(
                "CONSULTA_RESULTADO evaluacionId={} resultadoId={} calificacionId={}",
                evaluacionId,
                resultadoId,
                calificacionId);
    }
}
