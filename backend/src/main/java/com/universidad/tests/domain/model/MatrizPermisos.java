package com.universidad.tests.domain.model;

import static com.universidad.tests.domain.model.Rol.ACADEMICO;
import static com.universidad.tests.domain.model.Rol.COORDINADOR;
import static com.universidad.tests.domain.model.Rol.DECANO;
import static com.universidad.tests.domain.model.Rol.PSICOLOGO;
import static com.universidad.tests.domain.model.Rol.SUJETO;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// Permisos concedidos a cada rol activo (Documento general §2.3: lo no concedido se deniega).
// GEN-33-BE: matriz aprobada a partir del contrato (docs/api/openapi.yaml) y, donde el contrato
// nombra el permiso sin rol, del Documento general (CU-GEN-04, CU-GEN-19 y CU-GEN-20).
// La matriz solo dice que permiso tiene cada rol; el alcance (recurso propio, poblacion o ambito
// institucional) lo comprueba cada endpoint.
public final class MatrizPermisos {

    // E25, E30 y E31 (y E26 junto con INSTRUCCIONES_EDITAR)
    public static final String CONFIGURAR_BFA = "CONFIGURAR_BFA";
    // E26
    public static final String INSTRUCCIONES_EDITAR = "INSTRUCCIONES_EDITAR";
    // E27
    public static final String CONTENIDO_EDITAR = "CONTENIDO_EDITAR";
    // E28 y E29
    public static final String OPCIONES_EDITAR = "OPCIONES_EDITAR";
    // E10 (Psicologo; nombre del permiso en CU-GEN-11 y CU-GEN-15)
    public static final String REVISAR_FLUIDEZ = "REVISAR_FLUIDEZ";
    // E11 (Psicologo; nombre del permiso en CU-FV-03)
    public static final String RECALCULAR_BFA = "RECALCULAR_BFA";
    // E12 (Psicologo o Coordinador; nombre del permiso en CU-GEN-14)
    public static final String PUBLICAR_RESULTADOS = "PUBLICAR_RESULTADOS";
    // E18
    public static final String SUJETO_CREAR = "SUJETO_CREAR";
    // E19 y E23
    public static final String SUJETO_LEER = "SUJETO_LEER";
    // E20
    public static final String PERIODO_CREAR = "PERIODO_CREAR";
    // E21
    public static final String PERIODO_LEER = "PERIODO_LEER";
    // E22
    public static final String RECUPERAR_INTENTO = "RECUPERAR_INTENTO";
    // E22
    public static final String OPERACION_AUTORIZAR = "OPERACION_AUTORIZAR";

    // E5-E7: captura de respuestas reservada al sujeto dentro de su asignacion.
    public static final String SUBPRUEBA_INICIAR = "SUBPRUEBA_INICIAR";
    public static final String EVALUACION_FINALIZAR = "EVALUACION_FINALIZAR";
    public static final String ITEMS_LEER = "ITEMS_LEER";
    public static final String RESPUESTA_ESCRIBIR = "RESPUESTA_ESCRIBIR";

    // Permiso -> roles que lo reciben
    private static final Map<String, Set<Rol>> APROBADA = new LinkedHashMap<>();

    static {
        APROBADA.put(SUBPRUEBA_INICIAR, Set.of(SUJETO));
        APROBADA.put(EVALUACION_FINALIZAR, Set.of(SUJETO));
        APROBADA.put(ITEMS_LEER, Set.of(SUJETO));
        APROBADA.put(RESPUESTA_ESCRIBIR, Set.of(SUJETO));
        // E25-E31: Coordinador (contrato y GEN-33-BE)
        APROBADA.put(CONFIGURAR_BFA, Set.of(COORDINADOR));
        APROBADA.put(INSTRUCCIONES_EDITAR, Set.of(COORDINADOR));
        APROBADA.put(CONTENIDO_EDITAR, Set.of(COORDINADOR));
        APROBADA.put(OPCIONES_EDITAR, Set.of(COORDINADOR));
        // E10 y E11: Psicologo; E12: Psicologo o Coordinador (contrato)
        APROBADA.put(REVISAR_FLUIDEZ, Set.of(PSICOLOGO));
        APROBADA.put(RECALCULAR_BFA, Set.of(PSICOLOGO));
        APROBADA.put(PUBLICAR_RESULTADOS, Set.of(PSICOLOGO, COORDINADOR));
        // CU-GEN-20: Academico o Coordinador sobre poblacion institucional autorizada
        APROBADA.put(SUJETO_CREAR, Set.of(ACADEMICO, COORDINADOR));
        // CU-GEN-20: Sujeto sobre su identidad; Academico, Coordinador o Psicologo en su alcance;
        // Decano solo la identificacion institucional expresamente autorizada
        APROBADA.put(SUJETO_LEER, Set.of(SUJETO, ACADEMICO, COORDINADOR, PSICOLOGO, DECANO));
        // CU-GEN-20: Coordinador dentro del ambito institucional administrado
        APROBADA.put(PERIODO_CREAR, Set.of(COORDINADOR));
        // CU-GEN-20: profesionales con alcance; Sujeto respecto del Periodo de su asignacion
        APROBADA.put(PERIODO_LEER, Set.of(SUJETO, ACADEMICO, COORDINADOR, PSICOLOGO, DECANO));
        // CU-GEN-19: Sujeto propio abierto; Academico/Coordinador para conduccion e incidencia
        APROBADA.put(RECUPERAR_INTENTO, Set.of(SUJETO, ACADEMICO, COORDINADOR));
        // CU-GEN-04: solo Academico/Coordinador con asignacion
        APROBADA.put(OPERACION_AUTORIZAR, Set.of(ACADEMICO, COORDINADOR));
    }

    private final Map<Rol, Set<String>> permisosPorRol;

    public MatrizPermisos(Map<Rol, Set<String>> permisosPorRol) {
        Map<Rol, Set<String>> copia = new EnumMap<>(Rol.class);
        if (permisosPorRol != null) {
            permisosPorRol.forEach((rol, permisos) -> copia.put(rol, Set.copyOf(permisos)));
        }
        this.permisosPorRol = Map.copyOf(copia);
    }

    public static MatrizPermisos aprobada() {
        Map<Rol, Set<String>> porRol = new EnumMap<>(Rol.class);
        APROBADA.forEach(
                (permiso, roles) ->
                        roles.forEach(
                                rol ->
                                        porRol.computeIfAbsent(rol, r -> new HashSet<>())
                                                .add(permiso)));
        return new MatrizPermisos(porRol);
    }

    public Set<String> permisosDe(Rol rol) {
        return permisosPorRol.getOrDefault(rol, Set.of());
    }
}
