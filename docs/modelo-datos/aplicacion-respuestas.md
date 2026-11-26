# Aplicación y respuestas

**Tarea:** DOC-33 (subtarea DOC-33-BFAW) · **Estado:** borrador para revisión del verificador.

Este documento propone las tablas de «Aplicación y respuestas» que necesitan BFA y Weill. Todas cuelgan del núcleo de DOC-31 (`DOC-03-MODELO.md`, sección «Núcleo y glosario») y usan sus nombres y su glosario. No contiene ítems, claves ni valores de baremos reales.

## BFA y Weill

**Insumos:** núcleo y glosario (DOC-31); Documento BFA, ENT-BFA-01 y ENT-BFA-11; Nota Weill (DOC-36); decisiones D-03, D-04, D-05 y D-08 de DOC-42.

### Qué se reutiliza del núcleo

Primero se reutilizan las tablas del núcleo. Solo se crea una tabla propia cuando el dato no cabe en ellas.

| Necesidad | Dónde se guarda | Test |
| --- | --- | --- |
| Evaluación: sujeto, período, versión de formulario, estado y fechas | `evaluacion` | BFA y Weill |
| Subpruebas incluidas en una aplicación completa o parcial | Una fila de `evaluacion_subprueba` por cada subprueba incluida | BFA |
| Estado y tiempos de cada aplicación de subprueba | `evaluacion_subprueba`: `estado`, `fecha_inicio`, `fecha_fin_limite`, `fecha_cierre` | BFA y Weill |
| Respuesta de selección (alternativas de BFA; opciones 1 a 8 de Weill) | `respuesta_evaluacion.id_opcion` | BFA y Weill |
| Respuesta de texto libre (Fluidez Verbal) | `respuesta_evaluacion`: `valor_texto` y `orden_entrada`, con `id_pregunta` e `id_opcion` nulas | BFA |
| Letra inicial de Fluidez Verbal | `evaluacion_subprueba.letra_inicial` | BFA |
| Cambios de respuesta | `evento_auditoria`: el endpoint E7 escribe la respuesta y un evento | BFA y Weill |
| Cierre por tiempo | `evaluacion_subprueba`: `fecha_cierre` frente a `fecha_fin_limite` | BFA |
| Weill sin límite de tiempo | `configuracion_subprueba.tiempo_limite_segundos` nulo y `fecha_fin_limite` nula | Weill |
| Horas de inicio y finalización de la hoja de aplicación de Weill | `evaluacion.fecha_inicio` y `evaluacion.fecha_fin` | Weill |

### Tabla nueva: `aplicacion_bfa`

Extensión de `evaluacion` con el contexto propio de una aplicación BFA (ENT-BFA-01).

| Campo | Detalle |
| --- | --- |
| Nombre | `aplicacion_bfa` |
| PK | `id_evaluacion` (también es FK) |
| FK | `id_evaluacion` → `evaluacion` |
| Atributos principales | `alcance` (COMPLETA, PARCIAL; obligatorio), `modalidad` (INDIVIDUAL, COLECTIVA; obligatorio), `finalidad_examen` (texto; opcional), `edad_al_aplicar` (entero; opcional) |
| Cardinalidad | `evaluacion` 1 — 0..1 `aplicacion_bfa`. Una evaluación BFA iniciada exige exactamente una. |

**Por qué es tabla propia:** `alcance`, `modalidad`, `finalidad_examen` y `edad_al_aplicar` son datos propios de BFA y no caben en `evaluacion` sin alterar el núcleo. La versión de formulario no se repite aquí: se obtiene desde `evaluacion.id_version_formulario`. Tampoco se repiten sujeto, período, fechas ni estado.

**Reglas de la tabla:**

- Con `alcance` COMPLETA, las filas de `evaluacion_subprueba` deben corresponder a toda la configuración de la versión.
- Con `alcance` PARCIAL, deben ser un subconjunto no vacío y sin duplicados.
- `edad_al_aplicar` es un dato histórico derivable de `sujeto.fecha_nacimiento` y de la fecha de aplicación. No es una nota normativa.

### Relaciones y cardinalidades

| Relación | Cardinalidad | Nota |
| --- | --- | --- |
| `evaluacion` — `aplicacion_bfa` | 1:0..1 | La FK es la propia PK de `aplicacion_bfa`. |
| `evaluacion` — `evaluacion_subprueba` | 1:N | Núcleo. Una fila por subprueba incluida. |
| `configuracion_subprueba` — `evaluacion_subprueba` | 1:N | Núcleo. La configuración puede aplicarse en muchas evaluaciones. |
| `evaluacion` — `respuesta_evaluacion` | 1:N | Núcleo. |
| `pregunta` — `respuesta_evaluacion` | 1:N | Núcleo. Nula en Fluidez Verbal. |
| `opcion_respuesta` — `respuesta_evaluacion` | 1:N | Núcleo. Nula en texto libre. |
| `evaluacion` — `evento_auditoria` | 1:N | Núcleo. Registra los cambios de respuesta. |

### Fluidez Verbal

ENT-BFA-11 (AplicacionFluidezBFA) no crea tabla. Según la regla 5 del núcleo, Fluidez Verbal vive en columnas de `evaluacion_subprueba` y `respuesta_evaluacion`:

- La letra asignada se guarda en `evaluacion_subprueba.letra_inicial`. Se fija antes de iniciar y no cambia durante la ejecución.
- Cada palabra es una fila de `respuesta_evaluacion` con `valor_texto` (texto original) y `orden_entrada`. Se admiten hasta 75 entradas por ejecución, y no son 75 preguntas.
- La calificación de las palabras corresponde a DOC-34, no a este tema.

### Weill

Weill Forma A no necesita tablas propias en este tema, porque cabe por completo en el núcleo:

- Es una sola subprueba de 60 preguntas (D-03), por lo que hay una fila de `evaluacion_subprueba` por evaluación.
- La lámina no se guarda: se deriva de la posición de la pregunta, doce por lámina.
- Cada pregunta tiene una opción única, de 1 a 8, guardada en `respuesta_evaluacion.id_opcion`.
- Se puede finalizar con preguntas omitidas, y valen 0 (D-04).
- No tiene límite de tiempo por defecto (D-05). Si se aplicara uno, se configura en `configuracion_subprueba.tiempo_limite_segundos`.
- Los subtotales por lámina, el grupo y el baremo se resuelven en DOC-34.

### Incógnitas para el Registro de incógnitas

| ID | Incógnita e impacto | Quién |
| --- | --- | --- |
| A-01 | La PK de `aplicacion_bfa` es `id_evaluacion` y no `id_aplicacion_bfa`, porque ENT-BFA-01 prohíbe un segundo identificador. Es una excepción a la regla 2 del núcleo, como la de `solicitud_calculo_subprueba`. Confirmarla. | Integración |
| A-02 | `alcance` puede derivarse de las filas de `evaluacion_subprueba`. ENT-BFA-01 lo guarda como atributo. Decidir si se guarda o se deriva. | Integración y DOC-34 |
| A-03 | La modalidad COLECTIVA exige la entidad común de sesión colectiva (D02 de ENT-BFA-01), que no existe. | Iván |
| A-04 | Confirmar si `finalidad_examen` y `edad_al_aplicar` son obligatorias (D01 de ENT-BFA-01). | Iván |
| A-05 | `maximo_entradas` aparece en `configuracion_subprueba` y en `evaluacion_subprueba`. ENT-BFA-11 (D12) pide consultarlo desde la configuración. Decidir si la columna de `evaluacion_subprueba` sobra. | GEN-37-BE |
| A-06 | `respuesta_evaluacion` no tiene FK a `evaluacion_subprueba`. La aplicación de subprueba se deduce por la pregunta, pero en Fluidez Verbal la pregunta es nula. Confirmar cómo se enlaza. | GEN-37-BE |
| A-07 | Cambios de respuesta: E7 guarda una fila por pregunta. Decidir si basta `evento_auditoria` o se necesita un historial consultable. | Integración |
| A-08 | Weill no tiene límite de tiempo. Definir quién cierra la aplicación de subprueba y con qué `fecha_cierre`, y si el cierre por tiempo se distingue por `fecha_cierre` igual a `fecha_fin_limite`. | Integración e Iván |
| A-09 | El núcleo lista `recurso_ref` en `opcion_respuesta`, pero la incógnita I-12 de DOC-42 dice que no tiene recurso propio. Weill necesita una imagen por opción. | DOC-32 y Backend |
| A-10 | La Nota Weill dice que se registra «una opción o una omisión». El núcleo solo prevé `id_opcion` nula en texto libre. Decidir si una omisión es una fila con `id_opcion` nula o la ausencia de fila. | DOC-34 y Backend |

## Thurstone, Allport y Orientación vocacional
