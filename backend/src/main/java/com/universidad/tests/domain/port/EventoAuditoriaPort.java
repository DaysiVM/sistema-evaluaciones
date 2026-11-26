package com.universidad.tests.domain.port;

public interface EventoAuditoriaPort {

    void insertar(long idCuenta, String tipo, String entidad, long idEntidad);
}
