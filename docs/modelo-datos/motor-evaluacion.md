# Motor de evaluación (tablas)

Migración `V202611240800__comun_motor_evaluacion.sql` (GEN-37-BE; el nombre definitivo lo fija el custodio). Todas las claves son `BIGINT` con `id_<tabla>`, como pide el contrato (int64).

| Tabla | Clave | Notas |
| --- | --- | --- |
| `evaluacion` | `id_evaluacion` | `id_test` y `id_version_formulario` con FK. `id_sujeto` e `id_periodo` sin FK hasta GEN-35-BE. Estado `EN_CURSO` / `FINALIZADA`. |
| `evaluacion_subprueba` | `id_evaluacion_subprueba` | Única por `(id_evaluacion, codigo)`. Estado `PENDIENTE` → `ACTIVA` → `CERRADA`; una sola `ACTIVA` por evaluación. Incluye `letra_inicial` y `maximo_entradas` (Fluidez Verbal). |
| `respuesta_evaluacion` | `id_respuesta_evaluacion` | Con `id_pregunta` nulo solo se admiten palabras de la subprueba `FV` (hasta 75 entradas). |
| `solicitud_calculo_subprueba` | `id_evaluacion_subprueba` | Entrega durable al evaluador; `procesada_en` nulo = pendiente. |

E4 (`GET /api/v1/evaluaciones/{evaluacionId}`) lee `evaluacion` y `evaluacion_subprueba`; el tiempo restante se calcula en el servidor.
