package com.universidad.tests.application.evaluator.bfa;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.model.VersionFormulario;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Bloque Verbal (VOC1, VOC2 o CV) tal como se aplicó, sin persistencia ni contrato HTTP.
 *
 * <p>BFA-BE-03. Comprueba que la configuración, la subprueba y las preguntas pertenecen a la
 * versión aplicada, de modo que se califica con la numeración de la edición autorizada. El llamador
 * entrega solo las preguntas puntuables: los ejemplos de Vocabulario A y B no se califican
 * (Documento general §3.1). No carga ni certifica la procedencia de los datos en la base.
 */
public final class BloqueVerbalVersionado {

    private final Long idVersionFormulario;
    private final String codigoSubprueba;
    private final Set<Long> idsPreguntas;

    private BloqueVerbalVersionado(Long version, String codigo, Set<Long> preguntas) {
        idVersionFormulario = version;
        codigoSubprueba = codigo;
        idsPreguntas = Set.copyOf(preguntas);
    }

    public static BloqueVerbalVersionado desde(
            VersionFormulario versionAplicada,
            ConfiguracionSubprueba configuracion,
            Subprueba subprueba,
            Collection<Pregunta> preguntasPuntuables) {
        Objects.requireNonNull(versionAplicada, "La version aplicada es obligatoria");
        Objects.requireNonNull(configuracion, "La configuracion es obligatoria");
        Objects.requireNonNull(subprueba, "La subprueba es obligatoria");
        Objects.requireNonNull(preguntasPuntuables, "Las preguntas son obligatorias");
        if (!tieneIdValido(versionAplicada.getIdVersionFormulario())
                || !versionAplicada
                        .getIdVersionFormulario()
                        .equals(configuracion.getIdVersionFormulario())) {
            throw new IllegalArgumentException(
                    "La configuracion no pertenece a la version aplicada");
        }
        if (!tieneIdValido(configuracion.getIdConfiguracionSubprueba())
                || !tieneIdValido(subprueba.getIdSubprueba())
                || !subprueba.getIdSubprueba().equals(configuracion.getIdSubprueba())
                || !subprueba.getIdTest().equals(versionAplicada.getIdTest())) {
            throw new IllegalArgumentException(
                    "La subprueba y su configuracion no pertenecen al test aplicado");
        }
        if (!AlternativasVerbalBfa.esSubpruebaVerbal(subprueba.getCodigo())) {
            throw new IllegalArgumentException("La subprueba no pertenece a la familia Verbal");
        }
        if (configuracion.getTipoRespuesta() != TipoRespuesta.SELECCION) {
            throw new IllegalArgumentException("Verbal requiere una configuracion de seleccion");
        }
        if (preguntasPuntuables.isEmpty()) {
            throw new IllegalArgumentException("El bloque no tiene preguntas puntuables");
        }
        Set<Long> ids = new HashSet<>();
        for (Pregunta pregunta : preguntasPuntuables) {
            Objects.requireNonNull(pregunta, "Una pregunta del bloque es nula");
            if (!tieneIdValido(pregunta.getIdPregunta())
                    || !configuracion
                            .getIdConfiguracionSubprueba()
                            .equals(pregunta.getIdConfiguracionSubprueba())
                    || !ids.add(pregunta.getIdPregunta())) {
                throw new IllegalArgumentException(
                        "Las preguntas no identifican un bloque unico de la configuracion");
            }
        }
        return new BloqueVerbalVersionado(
                versionAplicada.getIdVersionFormulario(), subprueba.getCodigo(), ids);
    }

    private static boolean tieneIdValido(Long id) {
        return id != null && id > 0;
    }

    public Long idVersionFormulario() {
        return idVersionFormulario;
    }

    public String codigoSubprueba() {
        return codigoSubprueba;
    }

    public Set<Long> idsPreguntas() {
        return idsPreguntas;
    }
}
