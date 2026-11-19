package com.universidad.tests.domain.exception.resultados;

import java.util.Objects;

/**
 * Rechazo de la consulta de resultados (E9). El código y el estado HTTP son los del contrato
 * (docs/api/openapi.yaml); el dominio no depende de Spring, por eso el estado es un entero.
 */
public class ConsultaResultadoException extends RuntimeException {

    public enum Motivo {
        SOLICITUD_INVALIDA(400, "SOLICITUD_INVALIDA"),
        NO_AUTENTICADO(401, "NO_AUTENTICADO"),
        SIN_PERMISO(403, "SIN_PERMISO"),
        EVALUACION_NO_ENCONTRADA(404, "EVALUACION_NO_ENCONTRADA"),
        EVALUACION_NO_FINALIZADA(409, "EVALUACION_NO_FINALIZADA"),
        /** Datos guardados que no cumplen las reglas de E9. Se responde como error interno. */
        RESULTADO_INCONSISTENTE(500, "ERROR_INTERNO");

        private final int status;
        private final String codigo;

        Motivo(int status, String codigo) {
            this.status = status;
            this.codigo = codigo;
        }

        public int status() {
            return status;
        }

        public String codigo() {
            return codigo;
        }
    }

    private final Motivo motivo;

    public ConsultaResultadoException(Motivo motivo, String mensaje) {
        super(mensaje);
        this.motivo = Objects.requireNonNull(motivo, "motivo es obligatorio");
    }

    public Motivo motivo() {
        return motivo;
    }

    public static ConsultaResultadoException sinPermiso() {
        return new ConsultaResultadoException(
                Motivo.SIN_PERMISO, "No tiene permiso para consultar los resultados.");
    }
}
