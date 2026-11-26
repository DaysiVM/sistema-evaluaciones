# Alcance de evaluaciones

Nelson Lazo Rocha autorizó el 25-nov-2026 exigir el permiso de la operación y una asignación explícita cuenta–sujeto. Ningún rol da acceso global. La tabla `cuenta_sujeto_asignado` conserva esas asignaciones separadas de ambas identidades; nunca se supone que sus IDs coincidan.

E3 requiere OPERACION_AUTORIZAR y asignación. E4 requiere RECUPERAR_INTENTO u OPERACION_AUTORIZAR y asignación al sujeto de la evaluación. E19 requiere SUJETO_LEER y asignación; al Decano solo se devuelve la identificación institucional. E21 requiere PERIODO_LEER. Los nombres de permisos son los ya definidos en GEN-33-BE.

Las asignaciones se deben aprovisionar de forma controlada; no hay altas automáticas de alcance por rol ni endpoint nuevo de concesión pública. Las pruebas cargan exclusivamente asignaciones ficticias. Antes de la demostración se deben preparar las asignaciones de los datos demo, revisar la migración con su custodio y probar los roles en la URL publicada.

Para un sujeto no asignado E3/E19 responden 403 antes de consultar sus datos, incluso si el identificador no existe. No se expone su existencia como alternativa al control de alcance.
