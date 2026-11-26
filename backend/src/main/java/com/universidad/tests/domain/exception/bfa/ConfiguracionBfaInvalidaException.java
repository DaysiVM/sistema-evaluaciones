package com.universidad.tests.domain.exception.bfa;

import java.util.List;

/** La configuracion de una edicion del BFA no coincide con las 13 subpruebas oficiales. */
public class ConfiguracionBfaInvalidaException extends RuntimeException {

    private final List<String> discrepancias;

    public ConfiguracionBfaInvalidaException(List<String> discrepancias) {
        super("Configuracion BFA invalida: " + String.join("; ", discrepancias));
        this.discrepancias = List.copyOf(discrepancias);
    }

    public List<String> getDiscrepancias() {
        return discrepancias;
    }
}
