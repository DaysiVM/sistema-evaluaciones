package com.universidad.tests.domain.port;

import com.universidad.tests.domain.port.AutorizacionMotorSubpruebasPort.Permiso;

/** Consulta el alcance de una cuenta y su rol activo sobre una evaluación. */
public interface AlcanceMotorSubpruebasPort {
    boolean permite(
            Long cuentaId, String rolActivo, Permiso permiso, Long evaluacionId, Long sujetoId);
}
