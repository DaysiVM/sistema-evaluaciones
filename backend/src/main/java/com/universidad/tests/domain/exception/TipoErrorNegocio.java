package com.universidad.tests.domain.exception;

/** Categoría de un error de negocio; la capa HTTP la traduce al código de estado. */
public enum TipoErrorNegocio {
    SOLICITUD_INVALIDA,
    SIN_PERMISO,
    NO_ENCONTRADO,
    CONFLICTO,
    REGLA_DE_NEGOCIO
}
