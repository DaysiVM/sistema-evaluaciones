package com.universidad.tests.domain.exception;

import java.util.List;

/**
 * Error esperado del negocio con un código estable (Documento general §5.2.2). El frontend decide
 * por {@code codigo}; {@code getMessage()} es el texto para mostrar.
 */
public class ErrorNegocioException extends RuntimeException {

    private final TipoErrorNegocio tipo;
    private final String codigo;
    private final List<DetalleError> detalles;

    public ErrorNegocioException(TipoErrorNegocio tipo, String codigo, String mensaje) {
        this(tipo, codigo, mensaje, List.of());
    }

    public ErrorNegocioException(
            TipoErrorNegocio tipo, String codigo, String mensaje, List<DetalleError> detalles) {
        super(mensaje);
        this.tipo = tipo;
        this.codigo = codigo;
        this.detalles = List.copyOf(detalles);
    }

    public TipoErrorNegocio getTipo() {
        return tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public List<DetalleError> getDetalles() {
        return detalles;
    }

    /** Detalle por campo de un error de validación. */
    public record DetalleError(String campo, String mensaje) {}
}
