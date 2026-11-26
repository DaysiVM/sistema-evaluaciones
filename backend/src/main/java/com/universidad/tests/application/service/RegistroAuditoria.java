package com.universidad.tests.application.service;

import com.universidad.tests.domain.port.EventoAuditoriaPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroAuditoria {

    private final EventoAuditoriaPort eventos;

    public RegistroAuditoria(EventoAuditoriaPort eventos) {
        this.eventos = eventos;
    }

    @Transactional
    public void registrar(long idCuenta, String tipo, String entidad, long idEntidad) {
        eventos.insertar(idCuenta, tipo, entidad, idEntidad);
    }
}
