package com.universidad.tests.domain.exception;

public class MotorSubpruebasException extends RuntimeException {
    public enum Codigo {
        SUBPRUEBA_NO_ENCONTRADA,
        SUBPRUEBA_FUERA_DE_ORDEN,
        SUBPRUEBA_CERRADA,
        HAY_SUBPRUEBA_ACTIVA,
        LETRA_NO_CONFIGURADA,
        SUBPRUEBA_NO_ACTIVA,
        TIEMPO_AGOTADO,
        SOLICITUD_INVALIDA,
        RESPUESTA_INVALIDA,
        ERROR_INTERNO
    }

    private final Codigo codigo;

    public MotorSubpruebasException(Codigo codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public Codigo codigo() {
        return codigo;
    }
}
