# Casos de prueba GEN-07 — formularios y preguntas versionados (v1)

## Identificación

- **Tarea de Testing:** [GEN-07-TEST](https://app.clickup.com/t/86bcx2z6c)
- **Tarea de Desarrollo:** [GEN-07-BE](https://app.clickup.com/t/86bcw9yvz) (PR #23, Max Martínez)
- **Historia:** [GEN-07](https://app.clickup.com/t/86bcx2z61)
- **Tester:** José Ariel Zeledón
- **Versión y fecha:** v1 · 2026-11-09
- **PR y SHA probados:** #23 · head `69c7312` (`pull/23/merge` `bb03b31` sobre `main` `f0348c2`)
- **Resultado:** 34 PASÓ · 1 FALLÓ (TC-GEN-07-031) · 13 BLOQUEADO. **Sin `testing-ok`** hasta corregir TC-GEN-07-031 (ver «Defecto encontrado»).

## Historial de cambios

| Versión | Fecha | Cambios |
| --- | --- | --- |
| v1 | 2026-11-09 | Diseño de 48 casos y primera ejecución sobre el PR #23: 31 casos de base de datos, 4 de revisión de código y del CI (más `mvn verify` en local), y 13 bloqueados (evaluación con versión y API E25–E31). |

## Fuentes y estado

| Fuente | Estado al 2026-11-09 | Uso en este documento |
| --- | --- | --- |
| Criterios de aceptación de GEN-07-BE y GEN-07-TEST (ClickUp) | Publicados en las tareas | Base de todos los casos |
| Documento general §4: ENT-COM-05, 07, 08, 12 y 13 y criterios CA-01 a CA-10 del modelo | **Aprobado** ([DOC-03](https://app.clickup.com/t/86bctt9bt) `completed`) | Restricciones de versiones, configuraciones, preguntas y opciones |
| Documento general §5.2 (convenciones REST, formato de error, E25–E31) y Documento BFA §5 (E3, ajustes §5.4.1) | **Aprobado** ([DOC-05](https://app.clickup.com/t/86bctt9cy) `completed`) | Casos de API (bloqueados) |
| Documento general §5.1 (arquitectura y motor común) | **Aprobado** ([DOC-06](https://app.clickup.com/t/86bctt9db) `completed`) | Capas y protección por rol |
| Documento BFA §2: RNF-BFA-GEN-05 y RNF-BFA-GEN-09 | **Aprobado** ([DOC-02](https://app.clickup.com/t/86bctt9b8) `completed`) | Seguridad y versionado |
| Documento general §3: §3.1 y CU-GEN-05 a CU-GEN-07 | [DOC-04](https://app.clickup.com/t/86bctt9cm) `in review` | Valores **provisionales (DOC-04)**, marcados en cada caso |
| [DOC-07](https://app.clickup.com/t/86bctt9dn) — Calidad documental | `in progress` | Dependencia de GEN-07-TEST en ClickUp |
| `docs/api/openapi.yaml` | En `main` sin rutas; E25–E31 se publican en el PR #37 (GEN-00-API), sin fusionar | Referencia |

Los resultados esperados que provienen de documentos aprobados no llevan marca. Los que dependen de DOC-04 se marcan como **provisional (DOC-04)**.

## Alcance del PR #23

El PR implementa la **capa de persistencia y los servicios** del contenido versionado: entidades y repositorios de test, subprueba, versión de formulario, configuración, pregunta y opción; servicios de edición, consulta, publicación y retiro; la migración `V202611040249__comun_catalogo_versionado.sql`, cuyos triggers protegen el contenido publicado; un puerto de autorización con un adaptador que deniega todo por defecto; y 24 pruebas automatizadas.

**No agrega endpoints REST** (lo indica su descripción) **ni crea evaluaciones**. Por eso:

- Los criterios de versionado se prueban **directamente sobre la base de datos** con la migración del PR (TC-GEN-07-001 a 031), más revisión de código y del CI (TC-GEN-07-032 a 035).
- «Una evaluación conserva su versión» depende de E3 (GEN-02-BE): **BLOQUEADO** (TC-GEN-07-036 a 039).
- Los casos de Postman contra E25–E31 quedan diseñados y **BLOQUEADOS** (TC-GEN-07-040 a 048). No se crea una colección en `tests/api/` porque no hay rutas implementadas (`tests/api/README.md`).

## Matriz de cobertura

**GEN-07-TEST**

| Criterio | Casos | Estado |
| --- | --- | --- |
| Una versión publicada no se modifica: un cambio crea otra versión. | TC-GEN-07-005 a 015, 019, 023, 035 | PASÓ |
| Una evaluación conserva la versión con la que se creó. | TC-GEN-07-036 | BLOQUEADO |
| Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado. | TC-GEN-07-040 a 048 | BLOQUEADO |
| Cada criterio de las subtareas del grupo tiene al menos un caso `TC-`. | Esta matriz | Cumplido |
| Cada PR del grupo tiene `testing-ok` o sus fallos comentados antes de la fusión. | «Registro de ejecución» y comentario en el PR #23 | Fallo comentado |

**GEN-07-BE**

| Criterio | Casos | Estado |
| --- | --- | --- |
| El sistema permite almacenar distintas versiones de un mismo test. | TC-GEN-07-001, 002, 005, 015 | PASÓ |
| Cada versión conserva sus subpruebas, preguntas, alternativas, recursos y configuraciones correspondientes. | TC-GEN-07-013, 016, 018 | PASÓ |
| Las evaluaciones mantienen asociada la versión utilizada al momento de su creación. | TC-GEN-07-036 | BLOQUEADO |
| Los cambios en formularios publicados no alteran evaluaciones existentes. | TC-GEN-07-006 a 009, 016 (contenido); TC-GEN-07-037 (evaluación) | PASÓ en contenido · BLOQUEADO en evaluación |
| Se impide utilizar configuraciones o preguntas incompatibles con la versión seleccionada. | TC-GEN-07-014, 020 a 023; TC-GEN-07-038, 039 | PASÓ en base de datos · BLOQUEADO en E3 |
| El contenido restringido no se expone a usuarios sin autorización. | TC-GEN-07-034, 042 | PASÓ (código) · BLOQUEADO en API |
| La implementación cumple el modelo de datos y las reglas de versionado aprobadas. | TC-GEN-07-003, 004, 024 a 031, 033 | **FALLÓ (TC-GEN-07-031)** |
| Las pruebas automatizadas se ejecutan satisfactoriamente. | TC-GEN-07-032 | PASÓ |

**Criterios del modelo relacionados (Documento general §4.6)**

| Criterio del modelo | Casos |
| --- | --- |
| CA-01 — Edición: una evaluación fija su versión; configuración, pregunta y opción deben pertenecer a material compatible. | TC-GEN-07-014, 020 a 023 (material); TC-GEN-07-036 (evaluación, bloqueado) |
| CA-09 — Seguridad: ninguna respuesta del catálogo entrega claves. | Sin caso: el PR no tiene claves ni respuestas de catálogo; se cubre en GEN-01-TEST |

## Consultas pendientes

Se dejan en la tarea [GEN-07-TEST](https://app.clickup.com/t/86bcx2z6c). Mientras sigan abiertas, los casos afectados no fijan ese resultado.

| ID | Consulta | Para | Casos afectados | Estado |
| --- | --- | --- | --- | --- |
| C-1 | GEN-07-TEST pide probar con Postman y verificar el contrato HTTP, pero el PR #23 no tiene endpoints. ¿Qué tarea implementa E25–E31? ¿Se acepta el `testing-ok` del PR #23 con esos casos bloqueados? | Scrum de Testing e Integración | TC-GEN-07-040 a 048 | Abierta |
| C-2 | «La evaluación conserva su versión» necesita E3 (GEN-02-BE). ¿Se verifica en GEN-02-TEST y en TEST-01-EJEC una vez fusionados ambos? | Scrum de Testing | TC-GEN-07-036 a 039 | Abierta |
| C-3 | E25 crea la versión «a partir de la última PUBLICADA». ¿Debe copiar su contenido? `crearBorrador` crea un borrador vacío. | Documentación (DOC-05) | TC-GEN-07-040 | Abierta |
| C-4 | En E3, ¿qué código se devuelve si el `testId` recibido no coincide con el test de `versionFormularioId`: `422 VERSION_INCOMPATIBLE`, `404 TEST_NO_ENCONTRADO` u otro? | Documentación (DOC-05) | TC-GEN-07-039 | Abierta |

## Datos de prueba (ficticios)

Todos los datos son inventados: no hay ítems reales de pruebas psicológicas ni datos personales.

| Dato | Contenido | Usado en |
| --- | --- | --- |
| TEST-FICTICIO (id 1) | Subpruebas SP-A y SP-B | Todos |
| TEST-OTRO (id 2) | Subprueba SP-X | TC-GEN-07-020, 021 |
| V1 (id 100) | SP-A SELECCION 300 s con 2 preguntas y 2 opciones cada una; SP-B TEXTO_LIBRE 240 s | TC-GEN-07-004 a 019 |
| V2 (id 101) | SP-A SELECCION 360 s con 1 pregunta corregida y 3 opciones | TC-GEN-07-001, 015, 016, 023 |
| V-VACIA (id 102) | BORRADOR sin configuraciones | TC-GEN-07-003 |
| V3 (id 103) | BORRADOR: SP-B TEXTO_LIBRE (máximo 75 entradas) y SP-A SELECCION | TC-GEN-07-014, 020 a 030 |

## Entorno

- **Base de datos:** PostgreSQL 16.15 local, base `evaluaciones` vacía. Se aplicó con `psql` la migración del PR (`md5 6e6d2a1197a4`, idéntica en el head y en `pull/23/merge`) y después el script [`docs/evidencias/GEN-07/GEN-07-casos-bd.sql`](../evidencias/GEN-07/GEN-07-casos-bd.sql). Cada caso de rechazo se ejecuta en un bloque que se revierte, así no altera los siguientes.
- **Diferencia con el proyecto:** el proyecto usa PostgreSQL 17. La migración no usa nada exclusivo de la versión 17, y las pruebas de integración del CI ya corren en 17 (TC-GEN-07-032). Para repetir en 17: `docker compose up -d db`, aplicar la migración y correr el script.
- **Backend:** no se levantó como servidor, porque el PR no expone endpoints. Las pruebas automatizadas se ejecutaron en el CI del SHA probado y en local (Windows, IntelliJ IDEA 2026.2.3 con su Maven, JDK 27, PostgreSQL 18.6, base `evaluaciones` vacía). En local se saltó solo la revisión de formato (`-Dspotless.check.skip=true`), porque Windows cambia los saltos de línea; el formato lo valida el CI. Flyway avisa que PostgreSQL 18 es más nuevo que su última versión probada (17); no afectó la migración.

## Casos — Base de datos (migración del PR #23)

### TC-GEN-07-001 — Dos versiones del mismo test

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-001 | GEN-07-BE: "El sistema permite almacenar distintas versiones de un mismo test." | TEST-FICTICIO con V1 en BORRADOR | 1. Insertar V2 del mismo test. | Se acepta; V1 y V2 coexisten. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-002 — Código de versión repetido

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-002 | GEN-07-BE: "El sistema permite almacenar distintas versiones de un mismo test."<br>ENT-COM-12: codigoVersion "Única por Test." | V1 existe | 1. Insertar otra versión V1 del mismo test. | Rechazo por `uq_version_test_codigo`. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-003 — Publicar una versión sin configuraciones

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-003 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-12: "1..N [configuraciones] al publicarse." | V-VACIA en BORRADOR, sin configuraciones | 1. Cambiar V-VACIA a PUBLICADA con fecha y evidencia. | Rechazo: «Una version necesita configuraciones para publicarse». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-004 — Publicar sin fecha ni evidencia

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-004 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-12: publicadaEn y evidenciaValidacion "Obligatoria al publicar." | V1 completa en BORRADOR | 1. Cambiar V1 a PUBLICADA sin `publicada_en` ni `evidencia_validacion`. | Rechazo por `chk_version_publicacion`. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-005 — Publicar una versión completa

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-005 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>GEN-07-BE: "El sistema permite almacenar distintas versiones de un mismo test." | V1 con 2 configuraciones (selección y texto libre), 2 preguntas y 4 opciones | 1. Publicar V1 con fecha y evidencia ficticia. | Se acepta; V1 queda PUBLICADA. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-006 — Cambiar el enunciado de una pregunta publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-006 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>GEN-07-BE: "Los cambios en formularios publicados no alteran evaluaciones existentes." | V1 PUBLICADA | 1. Actualizar el enunciado de la pregunta 1 de V1. | Rechazo: «No se puede modificar una version publicada o retirada». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-007 — Agregar una pregunta a una versión publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-007 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>GEN-07-BE: "Los cambios en formularios publicados no alteran evaluaciones existentes." | V1 PUBLICADA | 1. Insertar la pregunta 3 en una configuración de V1. | Rechazo: «No se puede modificar una version publicada o retirada». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-008 — Borrar una opción de una versión publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-008 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>GEN-07-BE: "Los cambios en formularios publicados no alteran evaluaciones existentes." | V1 PUBLICADA | 1. Borrar una opción de V1. | Rechazo: «No se puede modificar una version publicada o retirada». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-009 — Cambiar la duración de una configuración publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-009 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>GEN-07-BE: "Los cambios en formularios publicados no alteran evaluaciones existentes." | V1 PUBLICADA | 1. Cambiar `tiempo_limite_segundos` de una configuración de V1. | Rechazo: «No se puede modificar una version publicada o retirada». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-010 — Cambiar los datos de una versión publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-010 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>ENT-COM-12: "No modificar contenido/configuración publicada; crear otra versión." | V1 PUBLICADA | 1. Cambiar `fuente_ref` de V1. | Rechazo: «El contenido de una version publicada es inmutable». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-011 — Borrar una versión publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-011 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>ENT-COM-12: "Retirar impide nuevos usos, no borra anteriores." | V1 PUBLICADA | 1. Borrar V1. | Rechazo: «No se puede eliminar una version publicada o retirada». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-012 — Devolver una versión publicada a borrador

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-012 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión." | V1 PUBLICADA | 1. Cambiar V1 a BORRADOR quitando fecha y evidencia. | Rechazo: «Transicion de estado no permitida». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-013 — Redefinir una subprueba usada en una versión publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-013 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>GEN-07-BE: "Cada versión conserva sus subpruebas, preguntas, alternativas, recursos y configuraciones correspondientes." | SP-A usada por V1 PUBLICADA | 1. Cambiar el nombre de SP-A. | Rechazo: «No se puede redefinir una subprueba utilizada en versiones publicadas». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-014 — Mover una pregunta de un borrador a una versión publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-014 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>GEN-07-BE: "Se impide utilizar configuraciones o preguntas incompatibles con la versión seleccionada." | Pregunta de V3 (BORRADOR); V1 PUBLICADA | 1. Cambiar la configuración de la pregunta de V3 por una de V1. | Rechazo: «No se puede modificar una version publicada o retirada». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-015 — Publicar el cambio como otra versión

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-015 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>GEN-07-BE: "El sistema permite almacenar distintas versiones de un mismo test." | V1 PUBLICADA; V2 en BORRADOR con una pregunta corregida y 3 opciones | 1. Publicar V2. | Se acepta; V1 y V2 quedan PUBLICADAS a la vez. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-016 — La versión anterior queda intacta

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-016 | GEN-07-BE: "Cada versión conserva sus subpruebas, preguntas, alternativas, recursos y configuraciones correspondientes."<br>GEN-07-BE: "Los cambios en formularios publicados no alteran evaluaciones existentes." | Huella md5 del contenido de V1 tomada al publicarla (TC-005) | 1. Después de TC-015, recalcular la huella de V1. | La huella es idéntica: configuraciones, preguntas y opciones de V1 no cambiaron. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-017 — Retirar una versión publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-017 | ENT-COM-12: estados BORRADOR, PUBLICADA o RETIRADA; "Retirar impide nuevos usos, no borra anteriores." | V1 PUBLICADA | 1. Cambiar V1 a RETIRADA. | Se acepta. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-018 — La versión retirada conserva fecha y contenido

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-018 | ENT-COM-12: publicadaEn "conservar al retirar".<br>GEN-07-BE: "Cada versión conserva sus subpruebas, preguntas, alternativas, recursos y configuraciones correspondientes." | V1 RETIRADA | 1. Consultar `publicada_en` y las preguntas de V1. | `publicada_en` conservada y las 2 preguntas siguen. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-019 — Modificar una versión retirada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-019 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión." | V1 RETIRADA | 1. Cambiar V1 a PUBLICADA. | Rechazo: «No se puede modificar una version retirada». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-020 — Configuración con una subprueba de otro test

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-020 | GEN-07-BE: "Se impide utilizar configuraciones o preguntas incompatibles con la versión seleccionada."<br>ENT-COM-13: idSubprueba "Mismo Test de la edición." | V3 de TEST-FICTICIO; SP-X de TEST-OTRO | 1. Insertar en V3 una configuración de SP-X. | Rechazo: «La subprueba no pertenece al mismo test que la version». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-021 — Cambiar la subprueba de una configuración por una de otro test

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-021 | GEN-07-BE: "Se impide utilizar configuraciones o preguntas incompatibles con la versión seleccionada." | Configuración de V3 (BORRADOR) | 1. Cambiar su subprueba por SP-X. | Rechazo: «La subprueba no pertenece al mismo test que la version». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-022 — Opciones en una pregunta de texto libre

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-022 | GEN-07-BE: "Se impide utilizar configuraciones o preguntas incompatibles con la versión seleccionada."<br>ENT-COM-08: "cero [opciones] si no aplica". | Pregunta de una configuración TEXTO_LIBRE de V3 | 1. Insertarle la opción A. | Rechazo: «Las opciones solo pertenecen a preguntas de seleccion». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-023 — Pasar una configuración de borrador a una versión publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-023 | GEN-07-BE: "Se impide utilizar configuraciones o preguntas incompatibles con la versión seleccionada."<br>GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión." | Configuración de V3 (BORRADOR); V2 PUBLICADA | 1. Cambiar su versión por V2. | Rechazo: «No se puede modificar una version publicada o retirada». | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-024 — Orden de aplicación repetido

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-024 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-13: ordenAplicacion "Positivo y único por versión". | V3 tiene una configuración en orden 2 | 1. Insertar otra configuración en orden 2. | Rechazo por `uq_config_version_orden`. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-025 — Duración igual a cero

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-025 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-13: tiempoLimiteSegundos "Positivo si existe". | V3 en BORRADOR | 1. Insertar una configuración con `tiempo_limite_segundos = 0`. | Rechazo por `chk_config_tiempo`. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-026 — Tipo de respuesta no permitido

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-026 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-13: tipoRespuesta "SELECCION o TEXTO_LIBRE". | V3 en BORRADOR | 1. Insertar una configuración con tipo `DIBUJO`. | Rechazo por `chk_config_tipo`. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-027 — Máximo de entradas en una configuración de selección

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-027 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-13: maximoEntradas "nulo en selección". | V3 en BORRADOR | 1. Insertar una configuración SELECCION con `maximo_entradas = 10`. | Rechazo por `chk_config_entradas_seleccion`. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-028 — Número visible repetido en una configuración

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-028 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-07: numeroVisible "Única por configuración". | Pregunta 1 en una configuración de V3 | 1. Insertar otra pregunta con número 1. | Rechazo por `uq_pregunta_numero`. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-029 — Pregunta sin enunciado ni recurso

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-029 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-07: "Exigir texto o recurso según formato." | Configuración de V3 | 1. Insertar una pregunta sin `enunciado` ni `recurso_ref`. | Rechazo por `chk_pregunta_contenido`. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-030 — Código de opción repetido

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-030 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-08: codigo "Único por pregunta". | Pregunta de V3 con la opción A | 1. Insertar otra opción A en la misma pregunta. | Rechazo por `uq_opcion_codigo`. | Manual (script SQL) | **PASÓ** |

### TC-GEN-07-031 — Insertar una versión ya publicada y vacía

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-031 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-12: "1..N [configuraciones] al publicarse"; "Publicar exige validación de contenido." | TEST-FICTICIO | 1. Insertar directamente una versión con `estado = PUBLICADA`, fecha y evidencia, sin configuraciones. | Rechazo, igual que en TC-003. | Manual (script SQL) | **FALLÓ** |


## Casos — Revisión de código y CI

### TC-GEN-07-032 — Pruebas automatizadas del PR

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-032 | GEN-07-BE: "Las pruebas automatizadas se ejecutan satisfactoriamente." | Run de CI [42687147493](https://github.com/Nell16/sistema-evaluaciones/actions/runs/42687147493) sobre `69c7312`; ejecución local sobre `pull/23/merge` `bb03b31` | 1. Revisar el job `backend-build` (`mvn verify` con PostgreSQL 17).<br>2. Ejecutar en local `mvn verify -Dspotless.check.skip=true` sobre `bb03b31` con PostgreSQL 18.6. | CI y local: `Tests run: 24, Failures: 0, Errors: 0, Skipped: 0` y `BUILD SUCCESS`. En local, Flyway aplica la migración `202611040249` sobre una base vacía. | CI y ejecución local | **PASÓ** |

### TC-GEN-07-033 — La publicación valida el contenido

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-033 | GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas."<br>ENT-COM-12: "Publicar exige validación de contenido." | `PublicacionVersionFormularioService` y su prueba unitaria | 1. Revisar `validarContenido` y `PublicacionVersionFormularioServiceTest`. | Rechaza publicar sin subpruebas, con una subprueba de otro test, con una configuración sin preguntas o con preguntas de selección sin opciones; exige evidencia; solo publica desde BORRADOR y solo retira desde PUBLICADA. Las 6 pruebas unitarias pasan (TC-032). | Revisión de código | **PASÓ** |

### TC-GEN-07-034 — Sin autorización no se expone contenido

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-034 | GEN-07-BE: "El contenido restringido no se expone a usuarios sin autorización."<br>RNF-BFA-GEN-05 (DOC-02). | `AutorizacionContenidoDenegadaAdapter` (activo salvo el perfil `seguridad-integrada`) | 1. Revisar que cada operación de los 4 servicios consulte el puerto de autorización antes de leer o escribir. | El adaptador deniega crear, editar, publicar, retirar y consultar. Los servicios lo llaman antes de devolver contenido. Ver la observación O-2. | Revisión de código | **PASÓ** |

### TC-GEN-07-035 — La aplicación solo crea versiones en borrador

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-035 | GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión."<br>GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas." | `VersionFormularioService.crearBorrador` y el constructor de `VersionFormulario` | 1. Revisar cómo se crea una versión desde Java. | Toda versión nueva nace en BORRADOR; una versión publicada solo se obtiene con `publicar`. Por eso el fallo de TC-031 no se alcanza desde los servicios. | Revisión de código | **PASÓ** |


## Casos bloqueados — Evaluación con versión (requieren E3 y E4 de GEN-02-BE)

### TC-GEN-07-036 — La evaluación conserva su versión

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-036 | GEN-07-TEST: "Una evaluación conserva la versión con la que se creó."<br>GEN-07-BE: "Las evaluaciones mantienen asociada la versión utilizada al momento de su creación."<br>Documento general CA-01: "una evaluación fija su versión". | V1 PUBLICADA; Sujeto y Periodo ficticios | 1. Crear una evaluación con E3 y `versionFormularioId` de V1.<br>2. Publicar V2.<br>3. Consultar la evaluación con E4. | Paso 3: `versionFormularioId` sigue siendo el de V1. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-037 — Una evaluación existente no cambia al publicar otra versión

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-037 | GEN-07-BE: "Los cambios en formularios publicados no alteran evaluaciones existentes."<br>CU-GEN-05 (provisional DOC-04): "tras publicar una versión nueva, los intentos ya iniciados conservan el identificador de la versión anterior". | Evaluación de TC-036 con preguntas presentadas | 1. Publicar V2 con una pregunta corregida.<br>2. Pedir las preguntas de la evaluación (E6). | Se devuelven las preguntas de V1, no las de V2. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-038 — No se crea una evaluación con una versión no publicada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-038 | GEN-07-BE: "Se impide utilizar configuraciones o preguntas incompatibles con la versión seleccionada."<br>E3: "409 VERSION_NO_PUBLICADA: la versión no está publicada." | V3 en BORRADOR | 1. Llamar a E3 con `versionFormularioId` de V3. | `409 VERSION_NO_PUBLICADA`; no se crea la evaluación. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-039 — No se crea una evaluación con una versión incompatible

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-039 | GEN-07-BE: "Se impide utilizar configuraciones o preguntas incompatibles con la versión seleccionada."<br>E3: "422 VERSION_INCOMPATIBLE"; §5.4.1: "testId, si se recibe, debe coincidir". | V1 de TEST-FICTICIO | 1. Llamar a E3 con `versionFormularioId` de V1 y `testId` de TEST-OTRO. | Se rechaza y no se crea la evaluación. El contrato lista `422 VERSION_INCOMPATIBLE`, pero no fija qué código corresponde a un `testId` que no coincide (consulta C-4). | Manual (Postman) | **BLOQUEADO** |


## Casos bloqueados — API E25–E31 (Postman)

Contrato aprobado del Documento general §5.2.4. En todo error se verifica además el cuerpo común (§5.2.2): `timestamp`, `status`, `codigo`, `mensaje`, `ruta` y, en errores de validación, `detalles`. Variables `{{base_url}}` y `{{token}}` de cuentas ficticias.

### TC-GEN-07-040 — Crear una versión (E25)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-040 | GEN-07-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado."<br>GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión." | TEST-FICTICIO con una versión PUBLICADA; cuenta ficticia con CONFIGURAR_BFA | 1. `POST {{base_url}}/tests/{testId}/versiones`. | `201` con `{ "id", "testId", "estado": "BORRADOR" }`. El contenido de la nueva versión depende de la consulta C-3. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-041 — Crear una versión de un test inexistente (E25)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-041 | GEN-07-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado." | testId que no existe | 1. `POST {{base_url}}/tests/999999/versiones`. | `404 TEST_NO_ENCONTRADO` con el cuerpo de error común. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-042 — Crear una versión sin sesión o sin permiso (E25)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-042 | GEN-07-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado."<br>GEN-07-BE: "El contenido restringido no se expone a usuarios sin autorización." | Sin token; luego cuenta ficticia sin CONFIGURAR_BFA | 1. Llamar a E25 sin `Authorization`.<br>2. Llamar con la cuenta sin permiso. | Paso 1: `401 NO_AUTENTICADO`. Paso 2: `403 SIN_PERMISO`. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-043 — Agregar una pregunta repetida (E27)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-043 | GEN-07-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado."<br>GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas." | Versión en BORRADOR con la pregunta número 1 | 1. `POST …/versiones/{id}/configuraciones/{codigo}/preguntas` con número 1. | `422 PREGUNTA_DUPLICADA`. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-044 — Editar una versión publicada (E26, E27 y E28)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-044 | GEN-07-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado."<br>GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión." | Versión PUBLICADA | 1. Llamar a E26, E27 y E28 sobre esa versión. | `409 VERSION_NO_EDITABLE` en las tres; el contenido no cambia. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-045 — Publicar una versión completa (E31)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-045 | GEN-07-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado."<br>GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión." | Versión en BORRADOR con contenido, instrucciones y clave ficticias | 1. `POST …/versiones/{id}/publicar`. | `200` con `{ "id", "estado": "PUBLICADA" }`; la versión anterior sigue disponible. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-046 — Publicar una versión incompleta (E31)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-046 | GEN-07-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado."<br>GEN-07-BE: "La implementación cumple el modelo de datos y las reglas de versionado aprobadas." | Versión en BORRADOR sin instrucciones o sin clave de un reactivo cerrado | 1. Llamar a E31. | `422 VERSION_INCOMPLETA`; la versión sigue en BORRADOR. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-047 — Publicar dos veces (E31)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-047 | GEN-07-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado."<br>GEN-07-TEST: "Una versión publicada no se modifica: un cambio crea otra versión." | Versión ya PUBLICADA | 1. Llamar a E31 otra vez. | `409 VERSION_NO_EDITABLE`. | Manual (Postman) | **BLOQUEADO** |

### TC-GEN-07-048 — Solicitud mal formada (E26)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-07-048 | GEN-07-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado." | Versión en BORRADOR | 1. `PUT …/configuraciones/{codigo}` con JSON inválido y luego sin `tipoRespuesta`. | `400 SOLICITUD_INVALIDA` con `detalles` por campo. | Manual (Postman) | **BLOQUEADO** |


## Defecto encontrado

**TC-GEN-07-031 — Se puede insertar una versión ya publicada y sin contenido.**

- **Pasos:** sobre la base con la migración del PR, ejecutar
  `INSERT INTO version_formulario (id_test, codigo_version, fuente_ref, estado, publicada_en, evidencia_validacion) VALUES (1, 'V-DIRECTA', 'Material ficticio', 'PUBLICADA', now(), 'ev-ficticia');`
- **Esperado:** rechazo, como en TC-GEN-07-003. ENT-COM-12 (DOC-03, aprobado) exige 1..N configuraciones al publicarse y que «Publicar exige validación de contenido». Criterio de GEN-07-BE: «La implementación cumple el modelo de datos y las reglas de versionado aprobadas».
- **Obtenido:** se acepta. `trg_control_version` es `BEFORE UPDATE OR DELETE`; un `INSERT` no pasa por la validación. La versión queda PUBLICADA sin configuraciones y ya no se le puede agregar contenido, porque los demás triggers exigen BORRADOR. Lo mismo ocurre con un `INSERT` directo en estado `RETIRADA`.
- **Alcance:** desde los servicios Java no se alcanza, porque toda versión nueva nace en BORRADOR (TC-GEN-07-035). Sí se alcanza con una carga directa por SQL, que es como puede llegar el contenido autorizado («mecanismo controlado para incorporar contenido»).
- **Severidad:** media. Rompe una regla del modelo aprobado en la capa que el PR presenta como protección del contenido publicado.
- **Evidencia:** [`docs/evidencias/GEN-07/GEN-07-ejecucion-bd-2026-11-09.txt`](../evidencias/GEN-07/GEN-07-ejecucion-bd-2026-11-09.txt).
- **Registro:** comentario en el PR #23 y tarea en la lista Bugs de ClickUp, enlazada a GEN-07-BE. Testing no corrige código; al recibir la corrección se repiten TC-GEN-07-001 a 031.

## Observaciones (no bloquean)

| ID | Observación | Dónde |
| --- | --- | --- |
| O-1 | La rama del PR #23 está atrasada respecto a `main` (`mergeable_state: behind`). La ruleset exige actualizarla; si cambia la migración o los servicios, se repiten los casos afectados. | PR #23 |
| O-2 | `VersionFormularioService.listarPorTest` responde «No existe el test» antes de consultar permisos y, si el test no tiene versiones, devuelve una lista vacía sin pasar por la autorización. No expone contenido, pero sí si un test existe. Ninguna prueba automatizada cubre la denegación de las consultas (solo la de publicar). | `VersionFormularioService` |
| O-3 | El PR no valida la cantidad de opciones ni de claves por módulo (CU-GEN-07, provisional DOC-04: A–E, A–D, A–C, A/B). Son reglas del módulo BFA, fuera del motor común; se anotan para BFA-TEST-01. | CU-GEN-07 |

## Registro de ejecución

| Fecha | PR/SHA | Tester | Resultado | Evidencia / bug |
| --- | --- | --- | --- | --- |
| 2026-11-09 | #23 / `69c7312` (`pull/23/merge` `bb03b31`) | José Ariel Zeledón | Base de datos: 30 PASÓ, 1 FALLÓ (TC-GEN-07-031). Código y CI: 4 PASÓ. Bloqueados: 13. | [Script](../evidencias/GEN-07/GEN-07-casos-bd.sql) · [salida](../evidencias/GEN-07/GEN-07-ejecucion-bd-2026-11-09.txt) · [CI 42687147493](https://github.com/Nell16/sistema-evaluaciones/actions/runs/42687147493) · defecto TC-GEN-07-031 |
| 2026-11-09 | #23 / `pull/23/merge` `bb03b31` | José Ariel Zeledón | `mvn verify` local (PostgreSQL 18.6, JDK 27): 24/24 pruebas, `BUILD SUCCESS` (TC-GEN-07-032). | [salida resumida](../evidencias/GEN-07/GEN-07-mvn-verify-local-2026-11-09.txt) |

### Revisión final

- [x] Cada caso corresponde a un criterio aprobado y a la tarea correcta.
- [x] Se ejecutó contra el commit vigente del PR (`69c7312`).
- [x] Se registraron resultados reales y evidencia verificable.
- [x] No contiene secretos ni datos personales reales.
- [ ] Se comentó al PR, se registró en Bugs y se informó a Integración.
- [ ] Solo después de aprobar, se colocó o solicitó `testing-ok` cuando corresponde.
