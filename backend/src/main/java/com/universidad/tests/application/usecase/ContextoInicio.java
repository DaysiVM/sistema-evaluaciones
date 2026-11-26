package com.universidad.tests.application.usecase;

import java.util.List;

/** Lo que una regla propia de un test necesita saber al crear la evaluación. */
public record ContextoInicio(
        String codigoTest, List<String> codigosSubpruebasIncluidas, String letraInicial) {

    public boolean incluye(String codigoSubprueba) {
        return codigosSubpruebasIncluidas.contains(codigoSubprueba);
    }
}
