# DOC-41 — Mapa endpoint → tablas

**Fecha:** 24/11/2026 · **Autores:** Diego Martínez (E1–E15, PR #98) y Daysi Miranda (E16–E31 y ajuste final) · **Revisa:** Iván

## Qué cambia

Sección «Endpoints y tablas» de `docs/modelo-datos/DOC-03-MODELO.md`:

- Se agregan los endpoints **E16–E31**, con lo que el mapa cubre los 31 del contrato.
- En **E1–E15** las entidades en cursiva «por confirmar» se reemplazan por los nombres que ya fijó el modelo (DOC-31 en `main`, DOC-32 y DOC-34). Ejemplos: *respuesta* → `respuesta_evaluacion`, *evento de auditoría* → `evento_auditoria`, *revisión de cálculo* → `revision_resultado`, *insumos de cálculo* → `regla_calificacion`, `version_clave_bfa`, `entrada_clave_bfa`, `baremo_bfa`, `rango_baremo_bfa`.
- Se agregan: una tabla con el origen de cada tabla usada, un resumen de cómo el modelo respalda la API (lo que pide la rúbrica) y la lista de huecos que encontró el mapa.

## Cómo se verificó

1. Rutas y métodos: se compararon las 31 filas con `docs/api/openapi.yaml` (método, ruta y número E coinciden).
2. Nombres de tabla: todas las tablas de las columnas «lee» y «escribe» están en el núcleo de DOC-31 o en los temas `items-formularios.md` y `calificacion-baremos.md`. Donde el modelo no tiene tabla se escribe «sin tabla en el modelo».

## Huecos que quedan abiertos (no se inventan nombres)

| Endpoint | Falta en el modelo |
| --- | --- |
| E16, E17 | Tabla del token de recuperación de contraseña y registro de la incidencia si el correo falla |
| E10 | Dónde se guarda la decisión sobre cada palabra de Fluidez Verbal |
| E22 | Relación `cuenta` ↔ `sujeto` para que el Sujeto vea solo sus evaluaciones |
| E31 | Letra de Fluidez Verbal por versión de formulario |
| E25 | Si la versión nueva copia la clave de la anterior |

Sin datos reales de tests ni de personas.
