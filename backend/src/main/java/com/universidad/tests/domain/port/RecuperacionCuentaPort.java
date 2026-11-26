package com.universidad.tests.domain.port;

import java.time.Instant;

public interface RecuperacionCuentaPort {
    void reemplazar(Long idCuenta, String resumenToken, Instant creadaEn, Instant expiraEn);

    boolean confirmar(String resumenToken, String nuevoResumenContrasena, Instant ahora);
}
