# DOC-03 Modelo de datos

**Estado: Propuesta del sprint 03.**

## Diagrama y temas del modelo

El diagrama reúne el núcleo común aprobado en DOC-40, las tablas de claves y baremos de BFA y las propuestas de calificación de DOC-34-BFAW:
[editable en Draw.io](../diagramas/modelo-er.drawio) · [vista PNG](../diagramas/modelo-er.png).
DOC-34-OTR reutiliza las tablas comunes de calificación para Thurstone y Allport. Orientación Vocacional no tiene calificación automática definida. La propuesta de baremos genéricos (OTR-05) y la diferencia de auditoría entre DOC-31 y DOC-35-COM quedan pendientes de decisión.

- [Ítems y formularios](items-formularios.md)
- [Aplicación y respuestas (DOC-33, seguimiento en ClickUp)](https://app.clickup.com/t/1k5fzqb0ft6): el documento del tema aún no está en el repositorio.
- [Calificación, escalas y baremos](calificacion-baremos.md)
- [Identidad, periodos y auditoría](identidad-auditoria.md)

## Decisiones registradas en el repositorio

| Fecha | Tarea | Decisión |
| --- | --- | --- |
| 2026-11-17 | GEN-01-BE | `subprueba.factor` (`VARCHAR(80)`, nulo mientras no se cargue): código del factor al que aporta la subprueba según el Documento BFA §5.3. Varias subpruebas pueden compartir factor (S1 y S1b → S1; N1-A y N1-B → N1). Migración `V202611170600__comun_agregar_factor_subprueba.sql`. |
| 2026-11-17 | GEN-01-BE | E2 no guarda `opciones` ni `numeroPreguntas`: los calcula desde `pregunta` y `opcion_respuesta` de la configuración publicada. |

## Incógnitas y decisiones

Tarea DOC-42. Reúne las decisiones del modelo que tomaron los cuatro temas (DOC-32 a DOC-35) y las incógnitas que siguen abiertas. Cada incógnita queda resuelta con su fuente o marcada «Preguntar al docente». No incluye ítems, claves ni valores de baremos reales.

### Decisiones del modelo

| ID | Decisión | Fuente | Tema |
| --- | --- | --- | --- |
| D-01 | Los nombres que ya existen en `main` o en los PR #89, #41, #91, #50 y #69 no se renombran; los temas cuelgan sus tablas del núcleo. | Núcleo y glosario (DOC-31) | Todos |
| D-02 | Las claves y baremos de BFA conservan `version_clave_bfa`, `entrada_clave_bfa`, `baremo_bfa` y `rango_baremo_bfa`. | PR #50 y #69; DOC-31 | DOC-34 |
| D-03 | Weill Forma A es una sola subprueba de 60 preguntas; la lámina se deriva de la posición (12 por lámina). No se crean cinco subpruebas. | Nota Weill (DOC-36) §1 | DOC-32 / DOC-34 |
| D-04 | Weill usa opción única 1–8 por pregunta; se puede finalizar con preguntas omitidas y valen 0. | Nota Weill §2 y §5 | DOC-33 / DOC-34 |
| D-05 | Weill no tiene límite de tiempo por defecto: `configuracion_subprueba.tiempo_limite_segundos` queda nulo. | Docente 21/11 (H-01); Nota Weill §3 | DOC-32 |
| D-06 | Un dato normativo que no se puede calcular queda nulo con estado y motivo; nunca se rellena con 0. | Documento BFA ENT-BFA-10; Nota Weill §7 | DOC-34 |
| D-07 | `sujeto`, `periodo` y `cuenta` usan los nombres físicos del núcleo (`codigo_institucional`, `fecha_nacimiento`, `fecha_inicio`, `fecha_fin`, `estado` ACTIVA/INHABILITADA…), no los atributos lógicos en camelCase de las fichas ENT-COM. | Núcleo y glosario (DOC-31) | DOC-35 |
| D-08 | La duda I-01 de DOC-32 (estructura de Weill) queda resuelta: la Nota Weill fue aceptada el 24/11. Weill puede seguir el mismo patrón de clave: una entrada por pregunta (60 entradas, la secuencia de 12 repetida en las 5 láminas). | Nota Weill (DOC-36) §1, §2 y §5 | DOC-32 |

### Incógnitas

| ID | Incógnita e impacto en el modelo | Estado | Responsable |
| --- | --- | --- | --- |
| I-01 | Nombres definitivos de las PK de `evaluacion_subprueba`, `respuesta_evaluacion` y `solicitud_calculo_subprueba` (hoy `id` en #41). | Resolver en GEN-37-BE | Backend (GEN-37-BE) |
| I-02 | `baremo_bfa` no tiene FK a `test` ni a `version_formulario`: no se sabe a qué edición pertenece una norma. | Resolver en el modelo | Integración y DOC-34 |
| I-03 | `rango_baremo_bfa.id_factor` es texto y no hay tabla de factores (FactorPsicometrico). | Resolver en el modelo | DOC-34 |
| I-04 | El baremo no tiene grupo de población estructurado (edad mínima y máxima; AN, AD, UN), y Weill lo necesita para elegir la columna. | Resolver en el modelo | DOC-34 |
| I-05 | Dónde se guardan los subtotales por lámina de Weill, que no son factores (H del tema DOC-34). | Resolver en el modelo | DOC-34 |
| I-06 | Columnas de 7 y 9 años no monótonas: E30 las rechaza por rangos superpuestos, pero el docente pidió tomar la tabla tal como está (H-03). | Preguntar al docente | Iván |
| I-07 | Percentil y CI para puntajes fuera de la tabla de Weill (H-04). | Preguntar al docente | Iván |
| I-08 | Cuándo se usa AD o UN para mayores de 16 años (H-06). | Preguntar al docente | Iván |
| I-09 | Lista de categorías de Weill: 7 en la tabla del docente frente a 6 en F-01 (H-02). | Preguntar al docente | Iván |
| I-10 | `permitirRetroceso` (ENT-COM-13) no tiene columna ni aparece en el contrato. | Resolver en el modelo | DOC-32 e Integración |
| I-11 | Cómo se aplica «a lo sumo una clave APROBADA por versión»: índice único parcial o regla de aplicación. | Resolver en el modelo | Backend y custodio de migraciones |
| I-12 | Formato de `recurso_ref` y dónde se guardan las imágenes; además, `opcion_respuesta` no tiene recurso propio y Weill necesita una imagen por opción. | Resolver en el modelo | DOC-32 y Backend |
| I-13 | Catálogo de valores de `accion` y `entidad_tipo` en `evento_auditoria`. | Resolver en el modelo | DOC-35 y Backend |

## Endpoints y tablas

**Tarea:** DOC-41 · **Autores:** Diego Martínez (E1–E15) y Daysi Miranda (E16–E31). Muestra qué tablas lee y escribe cada uno de los 31 endpoints del contrato (`docs/api/openapi.yaml`) y, con ello, cómo el modelo respalda la API.

**Cómo leer la tabla**

- Todos los nombres son los del modelo: el núcleo de «Núcleo y glosario» (DOC-31, más abajo en este documento) y los temas `items-formularios.md` (DOC-32) y `calificacion-baremos.md` (DOC-34). La columna «Origen» de la tabla de referencia de abajo dice si cada tabla ya está en `main`, en un PR o propuesta por un tema.
- **Sesión:** todo endpoint excepto E13, E14, E16 y E17 valida el token Bearer, así que además lee `sesion` y `cuenta_rol` (rol activo y permisos). Para no repetirlo en cada fila, esas lecturas no se escriben en la tabla.
- **—** significa que el endpoint no lee o no escribe ninguna tabla. Cuando el contrato pide guardar algo que el modelo todavía no tiene, se escribe **«sin tabla en el modelo»** y se explica en el comentario; no se inventa un nombre.
- Las lecturas y escrituras de las tablas que aún no están en `main` son el comportamiento esperado según el contrato y el modelo, no código ya implementado.

| Endpoint | Método y ruta | Tablas que lee | Tablas que escribe | Comentario |
| --- | --- | --- | --- | --- |
| E1 | `GET /api/v1/tests` | `test` | — | Lista los tests activos (`activo`). |
| E2 | `GET /api/v1/tests/{testId}` | `test`, `subprueba`, `version_formulario`, `configuracion_subprueba`, `pregunta`, `opcion_respuesta` | — | Lee la edición publicada. `opciones` y `numeroPreguntas` se calculan desde `pregunta` y `opcion_respuesta`, no se guardan (decisión de GEN-01-BE). No lee la clave. |
| E3 | `POST /api/v1/evaluaciones` | `sujeto`, `periodo`, `test`, `version_formulario`, `configuracion_subprueba` | `evaluacion`, `evaluacion_subprueba`, `evento_auditoria` | Fija sujeto, periodo y edición publicada al iniciar; crea una `evaluacion_subprueba` PENDIENTE por subprueba incluida. |
| E4 | `GET /api/v1/evaluaciones/{evaluacionId}` | `evaluacion`, `evaluacion_subprueba`, `respuesta_evaluacion`, `resultado` | — | Estado y avance; `resultadoId` y `estadoResultado` salen de `resultado`. Si el límite venció, muestra la subprueba CERRADA con `cerradaEn` igual al límite (contrato común, comentario de Iván en DOC-02). |
| E5 | `POST /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/iniciar` | `evaluacion`, `evaluacion_subprueba`, `configuracion_subprueba` | `evaluacion_subprueba`, `evento_auditoria` | Registra `fecha_inicio` y, si hay `tiempo_limite_segundos`, `fecha_fin_limite`. En Fluidez Verbal fija `letra_inicial` y `maximo_entradas`. |
| E6 | `GET /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/preguntas` | `evaluacion_subprueba`, `configuracion_subprueba`, `pregunta`, `opcion_respuesta`, `respuesta_evaluacion` | — | Devuelve los reactivos y la selección previa. No lee `entrada_clave_bfa`: la clave nunca se expone. |
| E7 | `PUT /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/respuestas` | `evaluacion_subprueba`, `pregunta`, `opcion_respuesta` | `respuesta_evaluacion`, `evento_auditoria` | Valida que la opción pertenezca a la pregunta y que la ejecución esté ACTIVA. En Fluidez Verbal guarda `valor_texto` y `orden_entrada` sin `id_pregunta` ni `id_opcion`. |
| E8 | `POST /api/v1/evaluaciones/{evaluacionId}/finalizar` | `evaluacion`, `evaluacion_subprueba`, `respuesta_evaluacion`, `sujeto`, `regla_calificacion`, `version_clave_bfa`, `entrada_clave_bfa`, `baremo_bfa`, `rango_baremo_bfa`, `escala` | `evaluacion`, `evaluacion_subprueba`, `solicitud_calculo_subprueba`, `resultado`, `revision_resultado`, `puntaje_subprueba`, `resultado_escala`, `conversion_resultado_escala`, `evento_auditoria` | Cierra y consolida con versiones exactas de regla, clave y baremo; `sujeto.fecha_nacimiento` da la edad para el baremo. `resultado` queda CALCULANDO o PENDIENTE_VALIDACION, nunca DISPONIBLE. Repetir E8 no recalcula. |
| E9 | `GET /api/v1/evaluaciones/{evaluacionId}/resultados` | `evaluacion`, `resultado`, `revision_resultado`, `resultado_escala`, `conversion_resultado_escala`, `escala` | — | Solo la revisión que apunta `resultado.referencia_revision_vigente`; sin ella, `factores` va vacío. No lee la clave. |
| E10 | `PUT /api/v1/evaluaciones/{evaluacionId}/calificaciones/{calificacionId}/palabras/{respuestaId}/revision` | `evaluacion`, `revision_resultado`, `respuesta_evaluacion` | Decisión sobre la palabra: **sin tabla en el modelo**; `evento_auditoria` | Fluidez Verbal (BFA); `calificacionId` es la `revision_resultado`. El modelo aún no define dónde vive la pareja calificación–respuesta con la decisión del Psicólogo (DOC-31 solo pone columnas de FV en `evaluacion_subprueba` y `respuesta_evaluacion`). No modifica `respuesta_evaluacion.valor_texto`. No aplica a Weill. |
| E11 | `POST /api/v1/evaluaciones/{evaluacionId}/recalculos` | `evaluacion`, `resultado`, `revision_resultado`, `evaluacion_subprueba`, `respuesta_evaluacion`, `regla_calificacion`, `version_clave_bfa`, `entrada_clave_bfa`, `baremo_bfa`, `rango_baremo_bfa`, `escala` | `revision_resultado`, `puntaje_subprueba`, `resultado_escala`, `conversion_resultado_escala`, `evento_auditoria` | Crea una revisión nueva con motivo; la vigente y el historial no cambian. |
| E12 | `PUT /api/v1/evaluaciones/{evaluacionId}/resultados/vigente` | `resultado`, `revision_resultado` | `resultado`, `evento_auditoria` | Cambia `referencia_revision_vigente` y `estado` (DISPONIBLE) y registra el evento en la misma transacción, comprobando la revisión esperada. |
| E13 | `POST /api/v1/auth/registro` | `cuenta` | `cuenta`, `cuenta_rol` | Comprueba que el correo sea único; guarda solo `resumen_contrasena`. El rol inicial lo define la regla de registro (por confirmar). |
| E14 | `POST /api/v1/auth/sesion` | `cuenta`, `cuenta_rol` | `sesion` | Abre sesión; guarda `resumen_token`, `rol_activo` y `expira_en`. |
| E15 | `DELETE /api/v1/auth/sesion` | `sesion` | `sesion` | Cierra la sesión actual escribiendo `cerrada_en`; no borra la fila. |
| E16 | `POST /api/v1/auth/recuperacion` | `cuenta` | Token de recuperación: **sin tabla en el modelo** | Sin sesión. Busca la cuenta por `correo` (sin distinguir mayúsculas) y responde igual exista o no. El token de un solo uso debe guardarse solo como resumen, con vencimiento y uso, como `sesion.resumen_token`; esa tabla falta en el modelo. Si el correo falla (503), la incidencia tampoco tiene tabla definida. |
| E17 | `POST /api/v1/auth/recuperacion/confirmar` | Token de recuperación (**sin tabla en el modelo**), `cuenta` | `cuenta`; token (**sin tabla**) | Sin sesión. Valida el token (vigente y sin usar), reemplaza `cuenta.resumen_contrasena` y deja el token inutilizado. No abre sesión: después se usa E14. |
| E18 | `POST /api/v1/sujetos` | `sujeto` | `sujeto` | `identificadorInstitucional` → `codigo_institucional` (único: SUJETO_DUPLICADO). `fecha_nacimiento` solo si la aplicación la requiere. No crea `cuenta` ni `evaluacion`. |
| E19 | `GET /api/v1/sujetos/{sujetoId}` | `sujeto` | — | Solo lectura; nunca crea el registro. Al Decano solo se le devuelve `codigo_institucional`. |
| E20 | `POST /api/v1/periodos` | `periodo` | `periodo` | `denominacion` → `nombre`, `inicio`/`fin` → `fecha_inicio`/`fecha_fin`. `codigo` único (PERIODO_DUPLICADO); fin ≥ inicio (PERIODO_INVALIDO, también como restricción `chk_periodo_fechas`). |
| E21 | `GET /api/v1/periodos` | `periodo` | — | `disponible` no es columna: se deriva de `activo` y de las fechas. Con `disponible=true` devuelve los que usa E3. |
| E22 | `GET /api/v1/evaluaciones` | `evaluacion`, `evaluacion_subprueba`, `respuesta_evaluacion`, `resultado` | — | Filtra por `id_sujeto`, `id_test`, `id_periodo` y `estado`; cada elemento tiene la forma de E4. El modelo aún no relaciona `cuenta` con `sujeto`, así que el filtro «el Sujeto ve las propias» está pendiente de definir. |
| E23 | `GET /api/v1/sujetos?identificadorInstitucional=` | `sujeto` | — | Busca por `codigo_institucional` (único): 0 o 1 resultado. |
| E24 | `GET /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/instrucciones` | `evaluacion`, `evaluacion_subprueba`, `configuracion_subprueba` | — | Lee `instrucciones_ref` y `practica_ref` de la configuración aplicada. No escribe `fecha_inicio`: no consume tiempo y la práctica no se guarda en `respuesta_evaluacion`. |
| E25 | `POST /api/v1/tests/{testId}/versiones` | `test`, `version_formulario`, `configuracion_subprueba`, `pregunta`, `opcion_respuesta` | `version_formulario`, `configuracion_subprueba`, `pregunta`, `opcion_respuesta` | Crea la versión en BORRADOR copiando el contenido de la última PUBLICADA, que no se modifica. Si también se copia la clave (`version_clave_bfa`, `entrada_clave_bfa`) está por confirmar. |
| E26 | `PUT /api/v1/versiones/{versionFormularioId}/configuraciones/{codigo}` | `version_formulario`, `subprueba`, `configuracion_subprueba` | `configuracion_subprueba` | Solo con la versión en BORRADOR (VERSION_NO_EDITABLE). Guarda `orden_aplicacion`, `tiempo_limite_segundos`, `tipo_respuesta`, `maximo_entradas`, `instrucciones_ref` y `practica_ref`. `opciones` no se guarda (lo calcula E2). |
| E27 | `POST /api/v1/versiones/{versionFormularioId}/configuraciones/{codigo}/preguntas` | `version_formulario`, `subprueba`, `configuracion_subprueba`, `pregunta` | `pregunta` | Solo en BORRADOR. Lee `pregunta` para rechazar `numero_visible` u `orden_presentacion` repetidos (PREGUNTA_DUPLICADA). |
| E28 | `PUT /api/v1/versiones/{versionFormularioId}/preguntas/{preguntaId}/opciones` | `version_formulario`, `configuracion_subprueba`, `pregunta`, `opcion_respuesta` | `opcion_respuesta` | Solo en BORRADOR. Comprueba que la pregunta sea de esa versión; la letra es `codigo` y el texto `texto`. |
| E29 | `PUT /api/v1/versiones/{versionFormularioId}/clave` | `version_formulario`, `configuracion_subprueba`, `pregunta`, `opcion_respuesta`, `version_clave_bfa` | `version_clave_bfa`, `entrada_clave_bfa` | Solo en BORRADOR. Una `entrada_clave_bfa` por reactivo cerrado; la opción debe ser de la misma pregunta. Tablas aparte de `opcion_respuesta` para que la clave nunca salga en E2, E6 ni E9. |
| E30 | `POST /api/v1/baremos` | `baremo_bfa` | `baremo_bfa`, `rango_baremo_bfa` | Único por `nombre` y `version`. Rechaza rangos superpuestos del mismo factor (BAREMO_INVALIDO). `id_factor` es el código del factor, sin FK. |
| E31 | `POST /api/v1/versiones/{versionFormularioId}/publicar` | `version_formulario`, `configuracion_subprueba`, `pregunta`, `opcion_respuesta`, `version_clave_bfa`, `entrada_clave_bfa` | `version_formulario` | Comprueba que la versión esté completa (VERSION_INCOMPLETA) y escribe `estado` = PUBLICADA y `publicada_en`; las versiones anteriores se conservan. La letra de Fluidez Verbal por versión aún no tiene columna en el modelo (hoy `letra_inicial` está en `evaluacion_subprueba`). |

**Tablas que usa el mapa y su origen**

| Tabla | Origen | Endpoints |
| --- | --- | --- |
| `test`, `subprueba`, `version_formulario`, `configuracion_subprueba`, `pregunta`, `opcion_respuesta` | `main` (DOC-31) | E1–E3, E5–E8, E24–E29, E31 |
| `cuenta`, `cuenta_rol`, `sesion` | PR #89 (DOC-31) | E13–E17 y la validación de sesión |
| `sujeto`, `periodo` | PR #91 (DOC-31) | E3, E8, E18–E23 |
| `evaluacion`, `evaluacion_subprueba`, `respuesta_evaluacion`, `solicitud_calculo_subprueba` | PR #41 (DOC-31) | E3–E12, E22, E24 |
| `resultado`, `evento_auditoria` | Nuevas del núcleo (DOC-31) | E3–E12, E22 |
| `version_clave_bfa`, `entrada_clave_bfa` | PR #50 (DOC-32) | E8, E11, E29, E31 |
| `baremo_bfa`, `rango_baremo_bfa` | PR #69 (DOC-34) | E8, E11, E30 |
| `escala`, `regla_calificacion`, `revision_resultado`, `puntaje_subprueba`, `resultado_escala`, `conversion_resultado_escala` | Propuestas de DOC-34 | E8–E11 |

**Cómo el modelo respalda la API**

- **Catálogo y configuración (E1, E2, E25–E31):** el camino `test` → `version_formulario` → `configuracion_subprueba` → `pregunta` → `opcion_respuesta` sirve tanto para editar (en BORRADOR) como para aplicar (solo PUBLICADA). Como una versión publicada no cambia, toda evaluación sabe exactamente qué edición se aplicó.
- **Clave protegida (E29):** la clave vive en `version_clave_bfa` y `entrada_clave_bfa`, no en `opcion_respuesta`. Por eso E2, E6 y E9 pueden leer el contenido sin exponerla.
- **Ejecución (E3–E8, E22, E24):** `evaluacion` fija sujeto, periodo y versión; `evaluacion_subprueba` guarda los tiempos que calcula el servidor (sirve para retomar sin reiniciar el reloj) y `respuesta_evaluacion` las respuestas.
- **Cálculo y publicación (E8–E12):** `resultado` es el contenedor único por evaluación. Cada cálculo o recálculo es una `revision_resultado` nueva con sus puntajes, y E12 solo cambia a cuál apunta `referencia_revision_vigente`, así que el historial nunca se pierde.
- **Acceso y personas (E13–E23):** `cuenta` (credencial) y `sujeto` (persona evaluada) están separados; las contraseñas y los tokens se guardan solo como resumen.
- **Trazabilidad:** los endpoints que cambian una evaluación o un resultado escriben en `evento_auditoria`, que es inmutable.

**Huecos del modelo que encontró el mapa** (para DOC-03 / GEN-37-BE; no se inventan nombres aquí)

1. E16–E17: falta la tabla del token de recuperación de contraseña (resumen, vencimiento, uso) y dónde registrar la incidencia si el correo falla.
2. E10: falta dónde guardar la decisión del Psicólogo sobre cada palabra de Fluidez Verbal (pareja revisión–respuesta).
3. E22: falta la relación entre `cuenta` y `sujeto` para que el Sujeto vea solo sus evaluaciones.
4. E31: falta la letra de Fluidez Verbal por versión de formulario (hoy solo está en `evaluacion_subprueba`).
5. E25: por confirmar si la versión nueva copia también la clave de la anterior.

## Criterios de aceptación

- [x] Los 31 endpoints están (E1–E31).
- [x] Solo usa nombres de tablas del modelo (DOC-31, DOC-32 y DOC-34). Donde el modelo todavía no tiene la tabla se indica «sin tabla en el modelo», sin inventar nombres.

## Núcleo y glosario

Tarea DOC-31. Congela las tablas comunes y los nombres canónicos que usan todos los temas de BD. Cada tema (claves, baremos, resultados por test, etc.) cuelga de estas tablas sin renombrarlas.

### Reglas de nombres

1. Lo que ya existe en `main` o en los PR #89, #41, #91, #50 y #69 **no se renombra**; el núcleo adopta esos nombres.
2. Tablas y columnas en `snake_case`, en singular. PK `id_<tabla>` de tipo `BIGINT GENERATED BY DEFAULT AS IDENTITY`. FK con el nombre de la PK a la que apuntan (`id_<tabla>`).
3. Excepción registrada: `cuenta_rol` tiene PK compuesta `(id_cuenta, rol)`.
4. Una migración nueva se llama `V<AAAAMMDDhhmm>__<modulo>_<descripcion>.sql`; el custodio (Kisnner) valida el nombre y nunca se edita una migración fusionada.
5. Fluidez Verbal no tiene tabla propia: son columnas de `evaluacion_subprueba` y `respuesta_evaluacion`.

### Tablas del núcleo

Origen: **main** = ya fusionada; **#89**, **#41**, **#91** = en el PR indicado (se fusionan en GEN-06-BE, GEN-37-BE y GEN-35-BE); **nueva** = falta crearla (este tema la define, no la crea).

| Tabla | Origen | PK | FK | Atributos principales |
| --- | --- | --- | --- | --- |
| `test` | main | `id_test` | — | `codigo` (único), `nombre`, `descripcion`, `activo` |
| `subprueba` | main | `id_subprueba` | `id_test` → test | `codigo` (único por test), `nombre`, `descripcion`, `factor` |
| `version_formulario` | main | `id_version_formulario` | `id_test` → test | `codigo_version` (único por test), `estado` (BORRADOR, PUBLICADA, RETIRADA), `fuente_ref`, `publicada_en`, `evidencia_validacion` |
| `configuracion_subprueba` | main | `id_configuracion_subprueba` | `id_version_formulario` → version_formulario; `id_subprueba` → subprueba | `orden_aplicacion`, `tiempo_limite_segundos`, `tipo_respuesta` (SELECCION, TEXTO_LIBRE), `instrucciones_ref`, `practica_ref`, `maximo_entradas` |
| `pregunta` | main | `id_pregunta` | `id_configuracion_subprueba` → configuracion_subprueba | `numero_visible`, `orden_presentacion`, `enunciado`, `recurso_ref` |
| `opcion_respuesta` | main | `id_opcion` | `id_pregunta` → pregunta | `codigo`, `orden_presentacion`, `texto`, `recurso_ref` |
| `cuenta` | #89 | `id_cuenta` | — | `correo` (único sin distinguir mayúsculas), `resumen_contrasena`, `nombres`, `apellidos`, `estado` (ACTIVA, INHABILITADA) |
| `cuenta_rol` | #89 | `(id_cuenta, rol)` | `id_cuenta` → cuenta | `rol` (SUJETO, ACADEMICO, COORDINADOR, PSICOLOGO, DECANO) |
| `sesion` | #89 | `id_sesion` | `id_cuenta` → cuenta; `(id_cuenta, rol_activo)` → cuenta_rol | `resumen_token` (único), `rol_activo`, `abierta_en`, `expira_en`, `cerrada_en` |
| `sujeto` | #91 | `id_sujeto` | — | `codigo_institucional` (único, opcional), `nombres`, `apellidos`, `fecha_nacimiento`, `sexo_referencia` |
| `periodo` | #91 | `id_periodo` | — | `codigo` (único), `nombre`, `fecha_inicio`, `fecha_fin`, `activo` |
| `evaluacion` | #41 (ids ajustados en GEN-37-BE) | `id_evaluacion` | `id_sujeto` → sujeto; `id_periodo` → periodo; `id_test` → test; `id_version_formulario` → version_formulario | `estado`, `fecha_inicio`, `fecha_fin`, `creado_en`, `actualizado_en` |
| `evaluacion_subprueba` | #41 (ids ajustados en GEN-37-BE) | `id_evaluacion_subprueba` | `id_evaluacion` → evaluacion; `id_configuracion_subprueba` → configuracion_subprueba | `estado` (PENDIENTE, ACTIVA, CERRADA), `fecha_inicio`, `fecha_fin_limite`, `fecha_cierre`, `letra_inicial` y `maximo_entradas` (Fluidez Verbal) |
| `respuesta_evaluacion` | #41 (ids ajustados en GEN-37-BE) | `id_respuesta_evaluacion` | `id_evaluacion` → evaluacion; `id_pregunta` → pregunta (nula en Fluidez Verbal); `id_opcion` → opcion_respuesta (nula en texto libre) | `valor_texto`, `orden_entrada`, `fecha_registro` |
| `solicitud_calculo_subprueba` | #41 | `id_evaluacion_subprueba` (PK y FK) | `id_evaluacion_subprueba` → evaluacion_subprueba; `id_evaluacion` → evaluacion | `codigo_test`, `codigo_subprueba`, `fecha_cierre`, `procesada_en` |
| `resultado` | **nueva** (ENT-COM-10) | `id_resultado` | `id_evaluacion` → evaluacion (única: 0..1 por evaluación) | `estado` (CALCULANDO, PENDIENTE_VALIDACION, DISPONIBLE), `creado_en`, `actualizado_en`, `referencia_revision_vigente` (obligatoria en DISPONIBLE; apunta a la revisión de cálculo del módulo, sin FK en el núcleo) |
| `evento_auditoria` | **nueva** (ENT-COM-11) | `id_evento_auditoria` | `id_evaluacion` → evaluacion (nula en eventos administrativos); `id_evaluacion_subprueba` → evaluacion_subprueba (nula) | `actor_ref`, `accion`, `entidad_tipo`, `entidad_id`, `ocurrido_en`; inmutable (solo inserciones) |

Tablas de los temas que cuelgan del núcleo (nombres ya fijados por sus PR; no se redefinen aquí):

| Tabla | Origen | Entidad del diseño | Cuelga de |
| --- | --- | --- | --- |
| `version_clave_bfa`, `entrada_clave_bfa` | #50 (PK `id_clave`, `id_entrada_clave`) | clave de corrección de BFA | `version_formulario`, `pregunta`, `opcion_respuesta` |
| `baremo_bfa`, `rango_baremo_bfa` | #69 (PK `id_baremo`, `id_rango`) | NormaBaremo, EquivalenciaBaremo | `rango_baremo_bfa.id_factor` es texto (código del factor); aún sin tabla de factores |

Sin tabla definida todavía (no inventar nombres; los fija el tema que los crea): FactorPsicometrico, AplicacionBFA, ResultadoFactor.

### Puntos de extensión

Un test agrega tablas propias con FK a estas tablas del núcleo, sin modificar su estructura:

| Tabla del núcleo | Qué cuelga de ella |
| --- | --- |
| `version_formulario` | claves de corrección y normas por versión (por ejemplo `version_clave_bfa`) |
| `configuracion_subprueba` | reglas propias de una subprueba dentro de una versión |
| `evaluacion_subprueba` | datos propios de una aplicación de subprueba (el patrón de Fluidez Verbal, hoy como columnas) |
| `resultado` | revisiones de cálculo y puntajes por factor del módulo (`referencia_revision_vigente`) |

### Glosario

| Término | Definición |
| --- | --- |
| Test | Instrumento de evaluación del catálogo (tabla `test`). |
| Subprueba | Parte nombrada de un test que se aplica por separado (tabla `subprueba`). |
| Versión de formulario | Edición del contenido de un test, con estados BORRADOR, PUBLICADA y RETIRADA; una vez publicada es inmutable (tabla `version_formulario`). |
| Configuración | Ajuste de una subprueba dentro de una versión: orden, tiempo límite, tipo de respuesta e instrucciones (tabla `configuracion_subprueba`). |
| Ítem / pregunta | Reactivo que se presenta al sujeto dentro de una configuración (tabla `pregunta`). |
| Opción | Alternativa de respuesta de una pregunta de selección (tabla `opcion_respuesta`). |
| Evaluación | Aplicación de una versión de un test a un sujeto en un período (tabla `evaluacion`). |
| Aplicación de subprueba | Ejecución de una subprueba dentro de una evaluación, con su estado y sus tiempos (tabla `evaluacion_subprueba`). |
| Respuesta | Valor elegido o escrito por el sujeto en una aplicación de subprueba (tabla `respuesta_evaluacion`). |
| Escala / factor | Dimensión que se mide y a la que aportan una o varias subpruebas (columna `subprueba.factor`). |
| Puntaje bruto | Número que resulta de corregir las respuestas de una subprueba o factor, sin convertir. |
| Puntaje convertido | Valor que resulta de pasar el puntaje bruto por un baremo (por ejemplo, el percentil). |
| Baremo | Tabla de equivalencias que convierte un puntaje bruto en convertido para una población de referencia. |
| Resultado | Contenedor de salida de una evaluación, con su estado y la referencia a la revisión de cálculo vigente (tabla `resultado`). |
| Cuenta | Credencial de acceso al sistema; no es una persona evaluada (tabla `cuenta`). |
| Rol | Perfil de permisos de una cuenta: SUJETO, ACADEMICO, COORDINADOR, PSICOLOGO o DECANO (tabla `cuenta_rol`). |
| Sujeto | Persona evaluada (tabla `sujeto`). |
| Período | Intervalo institucional en el que se programan las evaluaciones (tabla `periodo`). |

### Pendientes de confirmación en GEN-37-BE

- Nombres definitivos de las PK de `evaluacion_subprueba`, `respuesta_evaluacion` y `solicitud_calculo_subprueba`: #41 hoy usa `id` y, en `solicitud_calculo_subprueba`, `id_subprueba` (que choca con la PK de `subprueba`). Este documento fija `id_evaluacion_subprueba`, `id_respuesta_evaluacion` y `id_evaluacion_subprueba`.
- #41 repite en `evaluacion_subprueba` datos que ya están en la configuración (`codigo`, `nombre`, `orden`, `duracion_segundos`, `instrucciones`); no son atributos del núcleo.
- `id_sujeto` e `id_periodo` entran en `evaluacion` sin FK hasta que GEN-35-BE cree `sujeto` y `periodo`.
