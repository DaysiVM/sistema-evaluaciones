package com.universidad.tests.domain.port;

public interface AutorizacionMotorSubpruebasPort {
    enum Permiso {
        SUBPRUEBA_INICIAR,
        ITEMS_LEER,
        RESPUESTA_ESCRIBIR
    }

    void exigirPermiso(Permiso permiso);

    /** Comprueba el alcance sobre la evaluación después de validar el permiso de la operación. */
    void exigirAcceso(Permiso permiso, Long evaluacionId, Long sujetoId);
}
