package com.universidad.tests.application.evaluator.bfa;

import java.util.Map;
import java.util.Set;

/**
 * Alternativas permitidas en la familia Verbal (criterios de BFA-BE-03): VOC1 y VOC2 aceptan una
 * selección entre cinco alternativas A-E; CV, entre tres alternativas A-C.
 */
final class AlternativasVerbalBfa {

    private static final Map<String, Set<String>> POR_SUBPRUEBA =
            Map.of(
                    "VOC1", Set.of("A", "B", "C", "D", "E"),
                    "VOC2", Set.of("A", "B", "C", "D", "E"),
                    "CV", Set.of("A", "B", "C"));

    private AlternativasVerbalBfa() {}

    static boolean esSubpruebaVerbal(String codigoSubprueba) {
        return codigoSubprueba != null && POR_SUBPRUEBA.containsKey(codigoSubprueba);
    }

    static Set<String> de(String codigoSubprueba) {
        if (!esSubpruebaVerbal(codigoSubprueba)) {
            throw new IllegalArgumentException("La subprueba no pertenece a la familia Verbal");
        }
        return POR_SUBPRUEBA.get(codigoSubprueba);
    }
}
