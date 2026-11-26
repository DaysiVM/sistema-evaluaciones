# Tema 4 — Identidad, periodos y auditoría (tablas comunes a los 5 tests)

**Tarea:** DOC-35-COM · **Autor:** Diego Martínez · **Fecha:** 2026-11-24 · **Estado:** borrador para revisión (Gabriel Pérez Calero)

Modela cuenta, rol, sesión, sujeto, periodo y evento de auditoría para todo el sistema. Todos los datos de ejemplo del proyecto son ficticios.

## Cómo leer este documento

| Marca | Significado |
| --- | --- |
| **Físico** | Nombre de tabla y columnas tomados de la PR #89 (migración `V202611240156__comun_cuenta_y_sesion.sql`, ENT-COM-14), tal cual. |
| **Lógico** | Nombre del atributo en las fichas ENT-COM-03 y ENT-COM-04 del Documento general. El nombre físico lo fija la PR #91 (`sujeto`, `periodo`) y está **por confirmar** al fusionarse. |
| **Propuesto** | Dato que la tarea no entrega y que se infiere de la lógica de la relación, o nombre fijado por la revisión y aún sin migración (`evento_auditoria`); debe confirmarlo quien revisa. |

Pendientes explícitos de este documento: (1) columnas físicas de `sujeto` y `periodo` según la PR #91; (2) tipo de PK de todas las tablas; (3) catálogo de valores de `accion` y `entidad_tipo` de `evento_auditoria`; (4) nombres de `id_evaluacion` e `id_evaluacion_subprueba`, por cruzar con DOC-33; (5) si las acciones del sistema usan una cuenta técnica en `cuenta`.

## 1. Cuenta, rol y sesión (físico, PR #89)

La PR #89 crea tres tablas. La contraseña y el token se guardan solo como resumen (digest), y el correo es único sin distinguir mayúsculas.

### `cuenta`

| Columna | Clave | Descripción |
| --- | --- | --- |
| `id_cuenta` | PK | Identificador de la cuenta. |
| `correo` | UNIQUE (sin distinguir mayúsculas) | Correo de ingreso. |
| `resumen_contrasena` | — | Resumen de la contraseña; nunca la contraseña. |
| `nombres` | — | Nombres de la persona titular de la cuenta. |
| `apellidos` | — | Apellidos. |
| `estado` | — | Estado de la cuenta (valores por confirmar con la PR #89). |

### `cuenta_rol`

| Columna | Clave | Descripción |
| --- | --- | --- |
| `id_cuenta` | FK → `cuenta.id_cuenta` | Cuenta que tiene el rol. |
| `rol` | — | Rol asignado. |

Clave primaria **propuesta**: (`id_cuenta`, `rol`), para que una cuenta no repita el mismo rol.

### `sesion`

| Columna | Clave | Descripción |
| --- | --- | --- |
| `id_sesion` | PK | Identificador de la sesión. |
| `id_cuenta` | FK → `cuenta.id_cuenta` | Cuenta que abrió la sesión. |
| `resumen_token` | — | Resumen del token Bearer; nunca el token. |
| `rol_activo` | — | Rol con el que opera la sesión. |
| `abierta_en` | — | Momento de apertura. |
| `expira_en` | — | Momento de expiración. |
| `cerrada_en` | — | Momento de cierre; vacío mientras la sesión siga abierta. |

La autenticación usa token Bearer con roles (no JWT), según las decisiones del sprint.

## 2. Sujeto y periodo (lógico, ENT-COM-03 y ENT-COM-04)

### `sujeto` (ENT-COM-03)

La persona evaluada. **No es una cuenta**: puede existir sin cuenta, y el nombre nunca es clave.

| Atributo lógico | Clave | Obligatorio | Restricción |
| --- | --- | --- | --- |
| `idSujeto` | PK | Sí | Único e inmutable. |
| `codigoInstitucional` | UNIQUE si se informa | No | Código externo o CIF; uso por confirmar. |
| `nombres` | — | Sí | No vacío. |
| `apellidos` | — | Sí | No vacío. |
| `fechaNacimiento` | — | No | No futura; necesaria antes de operar con edad. |
| `sexoReferencia` | — | No | Dato opcional del núcleo. |

### `periodo` (ENT-COM-04)

| Atributo lógico | Clave | Obligatorio | Restricción |
| --- | --- | --- | --- |
| `idPeriodo` | PK | Sí | Único. |
| `codigo` | UNIQUE | Sí | Código administrativo, no vacío. |
| `nombre` | — | Sí | Denominación visible, no vacía. |
| `fechaInicio` | — | No | Coherente con `fechaFin`. |
| `fechaFin` | — | No | No anterior a `fechaInicio` si ambas existen. |
| `activo` | — | Sí | Disponibilidad administrativa. |

## 3. Evento de auditoría (`evento_auditoria`, propuesto desde ENT-COM-11)

Registra quién hizo qué, sobre qué y cuándo. El nombre físico `evento_auditoria` y las columnas en snake_case, como en la PR #89, los fijó la revisión de Gabriel Pérez Calero (comentario del 24/11 a las 11:22 en DOC-35-COM). La migración todavía no existe: hasta que se fusione, son nombres **propuestos**.

| Pregunta | Columna | Clave | Obligatorio | Descripción |
| --- | --- | --- | --- | --- |
| — | `id_evento` | PK | Sí | Identificador del evento. |
| **Quién** | `id_cuenta` | FK → `cuenta.id_cuenta` | Sí | Cuenta que actuó. No es el `sujeto` por defecto. |
| **Qué** | `accion` | — | Sí | Tipo de acción (catálogo por validar; lo fija el servidor). Se usa `accion` y no `tipo`, para no confundirlo con `entidad_tipo`. |
| **Sobre qué** | `entidad_tipo`, `entidad_id` | — | Sí | Entidad afectada e id; deben ser coherentes entre sí. |
| **Cuándo** | `ocurrido_en` | — | Sí | Momento oficial, fijado por el servidor. |
| Contexto | `id_evaluacion`, `id_evaluacion_subprueba` | FK opcionales | No | Evaluación y ejecución afectadas; obligatoria la evaluación en eventos de ejecución o resultado, no en los administrativos globales. Nombres por cruzar con el tema DOC-33. |
| Detalle | `detalle` | — | No | Motivo y referencias. Nunca imágenes, claves ni respuestas completas. |

El atributo lógico `actorRef` de ENT-COM-11 («cuenta o identidad técnica») se reemplaza por `id_cuenta`, que sí puede ser clave foránea y es lo que sostiene la cardinalidad 1 / 0..N de la sección 4. Si hacen falta eventos de una identidad técnica (acciones del sistema sin persona), se resuelve con una cuenta técnica en `cuenta`, y queda como decisión abierta (ver pendientes).

## 4. Cardinalidades

Formato «padres / hijos» (padres por hijo y hijos por padre).

| Relación | Cardinalidad | Justificación |
| --- | --- | --- |
| `cuenta` → `cuenta_rol` | 1 / 0..N | Una cuenta puede tener varios roles. |
| `cuenta` → `sesion` | 1 / 0..N | Una cuenta abre sesiones en distintos momentos. |
| `cuenta` → `evento_auditoria` (`id_cuenta`) | 1 / 0..N | Cada evento tiene una cuenta como actor; una cuenta genera muchos eventos. |
| `sujeto` → evaluación | 1 / 0..N | Cada intento identifica a su titular; el alta del sujeto puede ser anterior. |
| `periodo` → evaluación | 1 / 0..N | Un periodo reúne muchas evaluaciones. |
| evaluación → `evento_auditoria` | 0..1 / 0..N | El evento administrativo puede no pertenecer a ninguna evaluación. |

## 5. Criterios de aceptación

- [x] Usa los nombres de la PR #89 tal cual en `cuenta`, `cuenta_rol` y `sesion`.
- [x] `sujeto` y `cuenta` son entidades distintas, con propósito y claves propias.
- [x] Cardinalidades escritas (sección 4).
- [x] Auditoría con `evento_auditoria`, columnas en snake_case y `id_cuenta` como FK → `cuenta.id_cuenta` (corrección de la revisión del 24/11).
- [ ] Pendiente: confirmar con la PR #91 los nombres físicos de `sujeto` y `periodo`. Mientras no se fusione, se usan los atributos lógicos de ENT-COM-03 y ENT-COM-04.

## Fuentes

- Tarea ClickUp DOC-35-COM (columnas de #89 y regla «sujeto ≠ cuenta»).
- PR #89 «[GEN-06-BE] Persistir cuentas y sesiones…» (en borrador, no fusionada al 2026-11-24): nombres de tablas, correo único y resúmenes.
- Documento general, fichas ENT-COM-03, ENT-COM-04 y ENT-COM-11, según su transcripción en S02-WEI-DOC-07 v01.
- Comentario de revisión de Gabriel Pérez Calero en DOC-35-COM (24/11/2026, 11:22): nombre físico, snake_case y `id_cuenta`.
