# Casos de prueba GEN-03 — presentar preguntas y registrar respuestas (v1)

## Identificación

- **Tarea de Testing:** [GEN-03-TEST](https://app.clickup.com/t/86bcx2xfn)
- **Tareas de Desarrollo:** [GEN-03-FE](https://app.clickup.com/t/86bcvah5a) (PR #22) · [GEN-03-BE](https://app.clickup.com/t/86bcvah59) (sin PR)
- **Historia:** [GEN-03](https://app.clickup.com/t/86bcvah55)
- **Tester:** Rodrigo Romero
- **Versión y fecha:** v1 · 2026-11-05
- **Estado del documento:** diseño de casos; ninguno ejecutado.

## Fuentes y estado

| Fuente | Estado al 2026-11-05 | Uso en este documento |
| --- | --- | --- |
| Criterios de aceptación de GEN-03, GEN-03-BE y GEN-03-FE (ClickUp) | Publicados en las tareas | Base de todos los casos |
| Documento BFA — §2.6 (orden y tiempos), §3.2 (CU-BFA-P03, P04, P06, P07) y §5 (contratos E4–E7, versión simplificada del 2026-11-03 que reemplaza a la v0.6) | Especificación para revisión DOC-07; sin aprobación registrada | Códigos HTTP, campos y reglas **provisionales** |
| Documento general — §4.2 (ENT-COM-06, 09, 13), §4.6 (CA-01 a CA-10 del modelo), §5.1.7 (motor común) y §5.2 (convenciones REST) | Borrador pendiente de DOC-07; aprobación del Scrum Master no registrada | Reglas comunes y formato de error **provisionales** |
| Estructura y reglas del repositorio GitHub | Vigente | Formato del archivo, PR y datos de prueba |
| [DOC-02](https://app.clickup.com/t/86bctt9b8) — Requisitos | `completed` | Referencia |
| [DOC-04](https://app.clickup.com/t/86bctt9cm) — Casos de uso | `in review` | Pendiente |
| [DOC-05](https://app.clickup.com/t/86bctt9cy) — Contratos API | `in review` | Pendiente |
| [DOC-07](https://app.clickup.com/t/86bctt9dn) — Calidad documental | `in progress` | Dependencia de GEN-03-TEST |
| [GEN-06](https://app.clickup.com/t/86bcvaj7e) — Acceso y roles | `Open` | Casos de acceso **bloqueados** |

Los códigos y campos marcados como **provisionales** provienen de documentos todavía en revisión. Se confirman cuando DOC-07 registre la aprobación; si cambian, se actualizan estos casos antes de ejecutarlos.

Los criterios de las tareas se citan con su texto de ClickUp, porque todavía no tienen ID. Cuando Documentación asigne los IDs `CA-`, se usarán aquí.

## Glosario

| Término | Significado |
| --- | --- |
| CA | Criterio de aceptación: condición que la funcionalidad debe cumplir para darse por buena. Documentación les asignará IDs `CA-<MOD>-NN` (manual §4.2). |
| TC | Caso de prueba (*test case*): datos, pasos y resultado esperado para comprobar un criterio. ID `TC-GEN-03-NNN`. |
| FE / BE | Frontend (interfaz en el navegador) / backend (API). |
| E4, E5, E6, E7 | Endpoints del contrato: consultar estado de la evaluación, iniciar subprueba, obtener preguntas y registrar respuestas. |
| PENDIENTE / ACTIVA / CERRADA | Estados de una subprueba dentro de una evaluación. |
| PR / SHA | *Pull request* que se prueba / identificador del commit exacto probado. |
| e2e | Prueba de extremo a extremo automatizada con Playwright (`tests/e2e/`). |
| EV-PRUEBA | Evaluaciones ficticias definidas en «Datos de prueba». |

## Matriz de cobertura

**GEN-03-FE**

| Criterio | Casos |
| --- | --- |
| Los componentes permiten representar los tipos de preguntas y respuestas definidos por el motor común. | TC-GEN-03-001, TC-GEN-03-002 |
| Las instrucciones y prácticas aparecen antes del inicio del tiempo oficial. | TC-GEN-03-003 |
| El temporizador visual utiliza el tiempo informado por el backend. | TC-GEN-03-004 |
| Se distingue entre respuestas pendientes de guardar y respuestas confirmadas. | TC-GEN-03-005 |
| La navegación respeta las restricciones establecidas para cada subprueba. | TC-GEN-03-006 |
| Se informa correctamente cuando una respuesta falla al guardarse o el tiempo ha terminado. | TC-GEN-03-007, TC-GEN-03-008 |
| Los componentes pueden reutilizarse en distintas familias BFA sin duplicar la lógica general. | Sin caso funcional (ver abajo) |

**GEN-03-BE**

| Criterio | Casos |
| --- | --- |
| Los endpoints cumplen los contratos aprobados de DOC-05. | TC-GEN-03-011, TC-GEN-03-020, TC-GEN-03-021 |
| El tiempo oficial comienza únicamente al iniciar la subprueba, después de las instrucciones y prácticas correspondientes. | TC-GEN-03-009 |
| El servidor determina el vencimiento y rechaza respuestas fuera de plazo. | TC-GEN-03-015 |
| Las respuestas se almacenan asociadas a la evaluación, subprueba y pregunta o entrada correspondiente. | TC-GEN-03-013, TC-GEN-03-018 |
| Los reintentos no generan respuestas duplicadas ni reinician el tiempo. | TC-GEN-03-010, TC-GEN-03-014 |
| No se permite iniciar subpruebas fuera de orden ni modificar respuestas de pruebas cerradas. | TC-GEN-03-016, TC-GEN-03-017 |
| La API no expone claves de corrección ni información restringida. | TC-GEN-03-012 |
| Las pruebas automatizadas se ejecutan satisfactoriamente. | TC-GEN-03-023 |
| Cada subprueba se cierra una sola vez. | TC-GEN-03-019 |
| Descripción: "El backend debe validar […] los permisos […]". | TC-GEN-03-022, TC-GEN-03-024 |

**GEN-03 (historia)**

| Criterio | Casos |
| --- | --- |
| Las subpruebas se ejecutan según su configuración y reglas aprobadas. | Parcial: TC-GEN-03-006, TC-GEN-03-016 (ver abajo) |
| El tiempo oficial es controlado por el servidor y no se reinicia al recargar la página. | TC-GEN-03-004, TC-GEN-03-010 |
| Las respuestas se almacenan y pueden recuperarse correctamente. | TC-GEN-03-013 |
| Se rechazan respuestas fuera del tiempo permitido o correspondientes a subpruebas cerradas. | TC-GEN-03-008, TC-GEN-03-015, TC-GEN-03-017 |
| La aplicación no permite saltar el orden establecido ni regresar a subpruebas finalizadas. | TC-GEN-03-006, TC-GEN-03-016 |
| Backend y frontend funcionan integrados y superan sus pruebas correspondientes. | TEST-01-EJEC (ver abajo) |

**Criterios del modelo relacionados (Documento general §4.6, en revisión)**

Son criterios documentales del modelo de datos, no de la tarea GEN-03; se listan como referencia adicional.

| Criterio del modelo | Casos |
| --- | --- |
| CA-01 — Edición: rechazar una opción de otra pregunta. | TC-GEN-03-018, TC-GEN-03-021 |
| CA-02 — Recuperación y aislamiento: recuperar conserva respuestas y `finLimite`; se rechaza la consulta de otro sujeto sin permiso. | TC-GEN-03-004, TC-GEN-03-013, TC-GEN-03-022 |
| CA-09 — Seguridad: ninguna respuesta del catálogo o del evaluado entrega claves. | TC-GEN-03-012 |

**Criterios sin caso funcional en este documento:**

- **GEN-03-FE: "Los componentes pueden reutilizarse en distintas familias BFA sin duplicar la lógica general."** No es observable desde el navegador; corresponde a la revisión de código de Integración.
- **GEN-03: "Las subpruebas se ejecutan según su configuración y reglas aprobadas."** No hay configuración aprobada que permita fijar todos los resultados esperados; TC-GEN-03-006 y TC-GEN-03-016 cubren la parte de navegación y orden.
- **GEN-03: "Backend y frontend funcionan integrados y superan sus pruebas correspondientes."** Se ejecuta en [TEST-01-EJEC](https://app.clickup.com/t/86bcvajag).

## Consultas pendientes a Documentación

Huecos o contradicciones encontrados al diseñar los casos. Se comunican en la tarea [DOC-05](https://app.clickup.com/t/86bctt9cy); mientras sigan abiertas, los casos afectados no fijan ese resultado.

| ID | Consulta | Fuentes | Casos afectados | Estado |
| --- | --- | --- | --- | --- |
| C-1 | ¿Qué subpruebas permiten volver a preguntas anteriores mientras están activas y dónde se configura? La configuración de subprueba y E2 no tienen ese dato. | Documento BFA §2.6 y §5.2; Documento general ENT-COM-13 | TC-GEN-03-006 | Abierta |
| C-2 | ¿Qué código devuelve E7 si la pregunta pertenece a otra subprueba de la misma edición? E7 solo menciona `422 RESPUESTA_INVALIDA` para opción ajena o pregunta de otra edición. | Documento BFA E7; Documento general ENT-COM-09 | TC-GEN-03-018 | Abierta |
| C-3 | ¿E5, E6 y E7 responden `401 NO_AUTENTICADO` y `403 SIN_PERMISO`? §5.2.1 los exige en toda operación protegida, pero los contratos de E5–E7 no los listan y §5.2.2 limita el 403 a resultados. | Documento general §5.2.1 y §5.2.2; Documento BFA E5–E7 | TC-GEN-03-022, TC-GEN-03-024 | Abierta |
| C-4 | ¿Qué código devuelve repetir E5 sobre la misma subprueba ACTIVA? CU-BFA-P03 dice que devuelve el mismo inicio, pero E5 lista `409 HAY_SUBPRUEBA_ACTIVA`. | Documento BFA §3.2 y E5 | TC-GEN-03-010 | Abierta |
| C-5 | ¿Cómo se comprueba desde la API que una subprueba se cerró una sola vez? E4 muestra el estado, pero no la fecha de cierre ni eventos de auditoría. | Documento BFA E4 y CU-BFA-P07; Documento general ENT-COM-06 y ENT-COM-11 | TC-GEN-03-019 | Abierta |

## Datos de prueba (ficticios)

Se usa la estructura de configuración del BFA publicada en el Documento BFA (códigos, orden, tiempos y tipo de respuesta), con **preguntas, opciones y claves inventadas**. No se usan ítems reales.

- **EV-PRUEBA-01:** aplicación parcial con VOC1 (orden 1, 300 s, selección A–E) y VOC2 (orden 2, 360 s, selección A–E).
- **EV-PRUEBA-02:** misma estructura que EV-PRUEBA-01, de otro sujeto ficticio, para los casos de acceso.
- **EV-PRUEBA-03:** aplicación parcial solo con FV (300 s, texto libre, hasta 75 entradas), con letra inicial asignada.
- **Cuentas:** una cuenta ficticia con permiso sobre EV-PRUEBA-01 y EV-PRUEBA-03, y otra sin permiso sobre EV-PRUEBA-02 (roles según DOC-04, pendiente).

Las migraciones no cargan datos: la colección Postman crea sus propios datos por la API (cuenta y sesión con E13/E14, Sujeto y Periodo con E18/E20, evaluación con E3). **Pendiente:** cómo cargar claves ficticias en el entorno local para TC-GEN-03-012.

## Entorno

- **Frontend:** PR #22 sobre `pull/22/merge`, con `npm ci` y `npm run dev` en `frontend/`. El PR incluye una vista de prueba con datos simulados (`/dev/gen-03`); usarla para ejecutar estos casos debe acordarse con el Scrum Master de Testing e Integración.
- **Backend:** PR de GEN-03-BE sobre `pull/<n>/merge`, con `docker compose up -d db` y `mvn -f backend/pom.xml spring-boot:run`; colección Postman en `tests/api/` con variables `{{base_url}}` y `{{token}}` de una cuenta ficticia.

## Casos — Frontend (navegador)

### TC-GEN-03-001 — Pregunta de selección

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-001 | GEN-03-FE: "Los componentes permiten representar los tipos de preguntas y respuestas definidos por el motor común." | EV-PRUEBA-01, VOC1 activa | 1. Llegar a una pregunta de VOC1.<br>2. Elegir una alternativa. | La pregunta se presenta con sus alternativas (A–E en VOC1) y se puede responder eligiendo una. | Manual (navegador) |

### TC-GEN-03-002 — Respuesta de texto libre

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-002 | GEN-03-FE: "Los componentes permiten representar los tipos de preguntas y respuestas definidos por el motor común." | EV-PRUEBA-03, FV activa | 1. Escribir una palabra.<br>2. Agregar una segunda palabra. | Se presenta un campo de texto libre y cada palabra se captura como una entrada, en el orden escrito. | Manual (navegador) |

### TC-GEN-03-003 — Instrucciones y práctica antes del tiempo oficial

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-003 | GEN-03-FE: "Las instrucciones y prácticas aparecen antes del inicio del tiempo oficial." | EV-PRUEBA-01, VOC1 PENDIENTE | 1. Entrar a VOC1.<br>2. Observar el temporizador durante instrucciones y práctica.<br>3. Completar la práctica. | Instrucciones y práctica aparecen antes de que inicie el tiempo oficial; el temporizador oficial no corre durante ellas. Según el Documento BFA (E4), instrucciones y práctica ocurren en el frontend antes de E5. | Manual; candidato e2e (TEST-01-EJEC) |

### TC-GEN-03-004 — Temporizador según el backend al recargar

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-004 | GEN-03-FE: "El temporizador visual utiliza el tiempo informado por el backend."<br>GEN-03: "El tiempo oficial es controlado por el servidor y no se reinicia al recargar la página." | EV-PRUEBA-01, VOC1 ACTIVA; backend disponible | 1. Anotar `segundosRestantes` de E4.<br>2. Esperar unos segundos.<br>3. Recargar la página y comparar con E4. | El temporizador coincide con el tiempo que informa E4 y, tras recargar, continúa desde ese tiempo sin volver al valor inicial. | Manual; candidato e2e (TEST-01-EJEC) |

### TC-GEN-03-005 — Respuesta pendiente frente a confirmada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-005 | GEN-03-FE: "Se distingue entre respuestas pendientes de guardar y respuestas confirmadas." | EV-PRUEBA-01, VOC1 ACTIVA | 1. Responder una pregunta.<br>2. Observar el estado antes y después de la confirmación del servidor. | La respuesta se muestra como pendiente de guardar hasta que el servidor confirma, y luego como confirmada; ambos estados se distinguen. | Manual; candidato e2e (TEST-01-EJEC) |

### TC-GEN-03-006 — Navegación dentro y entre subpruebas

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-006 | GEN-03-FE: "La navegación respeta las restricciones establecidas para cada subprueba."<br>GEN-03: "La aplicación no permite saltar el orden establecido ni regresar a subpruebas finalizadas." | EV-PRUEBA-01, VOC1 ACTIVA con varias preguntas respondidas | 1. Volver a una pregunta anterior de VOC1.<br>2. Intentar pasar a VOC2 antes de que venza VOC1.<br>3. Con VOC1 cerrada, intentar volver a VOC1. | Provisional: dentro de la subprueba activa se puede volver a preguntas anteriores (Documento BFA §5.2); no se puede pasar a la siguiente antes del vencimiento ni volver a una cerrada (Documento general §5.1.7). Restricciones de navegación interna por subprueba: pendiente de la consulta C-1. | Manual (navegador) |

### TC-GEN-03-007 — Fallo al guardar una respuesta

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-007 | GEN-03-FE: "Se informa correctamente cuando una respuesta falla al guardarse o el tiempo ha terminado." | EV-PRUEBA-01, VOC1 ACTIVA; el guardado falla (backend no disponible o respuesta de error) | 1. Responder una pregunta.<br>2. Observar el mensaje y el estado de la respuesta. | Se informa que la respuesta no se guardó y no se muestra como confirmada. | Manual (navegador) |

### TC-GEN-03-008 — Fin del tiempo oficial

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-008 | GEN-03-FE: "Se informa correctamente cuando una respuesta falla al guardarse o el tiempo ha terminado."<br>GEN-03: "Se rechazan respuestas fuera del tiempo permitido o correspondientes a subpruebas cerradas." | EV-PRUEBA-01, VOC1 ACTIVA | 1. Esperar a que el temporizador llegue a cero (300 s en VOC1).<br>2. Intentar responder. | Se informa que el tiempo terminó y no se aceptan más respuestas en esa subprueba. | Manual; candidato e2e (TEST-01-EJEC) |

## Casos — Backend (Postman)

Contratos según el Documento BFA §5.3 y el Documento general §5.2 (**provisionales**):

- E4: `GET /api/v1/evaluaciones/{evaluacionId}`
- E5: `POST /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/iniciar`
- E6: `GET /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/preguntas`
- E7: `PUT /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/respuestas`

En todo error se verifica además el cuerpo común (Documento general §5.2.2): `timestamp`, `status`, `codigo`, `mensaje`, `ruta` y, en errores de validación, `detalles`.

### TC-GEN-03-009 — Iniciar subprueba fija el tiempo oficial

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-009 | GEN-03-BE: "El tiempo oficial comienza únicamente al iniciar la subprueba, después de las instrucciones y prácticas correspondientes." | EV-PRUEBA-01, VOC1 PENDIENTE | 1. Llamar a E4 y comprobar que no hay subprueba activa.<br>2. Llamar a E5 para VOC1. | Provisional: `200` con VOC1 en estado `ACTIVA`, `inicio`, `finLimite` (inicio + 300 s) y `segundosRestantes`. | Manual (Postman) |

### TC-GEN-03-010 — Repetir el inicio no reinicia el tiempo

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-010 | GEN-03-BE: "Los reintentos no generan respuestas duplicadas ni reinician el tiempo."<br>GEN-03: "El tiempo oficial es controlado por el servidor y no se reinicia al recargar la página." | VOC1 ya iniciada con E5 | 1. Anotar `inicio` y `finLimite`.<br>2. Esperar unos segundos.<br>3. Repetir E5 para VOC1. | `inicio` y `finLimite` no cambian (Documento BFA, CU-BFA-P03). Código de respuesta: pendiente de la consulta C-4. | Manual (Postman) |

### TC-GEN-03-011 — Obtener preguntas de la subprueba activa

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-011 | GEN-03-BE: "Los endpoints cumplen los contratos aprobados de DOC-05." | VOC1 ACTIVA | 1. Llamar a E6 para VOC1. | Provisional: `200` con `codigo`, `segundosRestantes` y `preguntas` (cada una con `id`, `numero`, `enunciado`, `opciones` y `respuestaActual`). | Manual (Postman) |

### TC-GEN-03-012 — Sin claves de corrección en la respuesta

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-012 | GEN-03-BE: "La API no expone claves de corrección ni información restringida." | VOC1 ACTIVA, con claves ficticias conocidas | 1. Llamar a E6 para VOC1.<br>2. Revisar todo el cuerpo de la respuesta. | La respuesta no contiene la clave correcta de ninguna pregunta ni información restringida (Documento BFA E6). | Manual (Postman) |

### TC-GEN-03-013 — Registrar y recuperar una respuesta en plazo

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-013 | GEN-03-BE: "Las respuestas se almacenan asociadas a la evaluación, subprueba y pregunta o entrada correspondiente."<br>GEN-03: "Las respuestas se almacenan y pueden recuperarse correctamente." | VOC1 ACTIVA y dentro de plazo | 1. Enviar con E7 una respuesta a una pregunta de VOC1.<br>2. Llamar a E6 para VOC1. | Provisional: E7 responde `200` con el avance; en E6, `respuestaActual` de esa pregunta es la opción enviada. | Manual (Postman) |

### TC-GEN-03-014 — Reintento sin duplicar respuestas

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-014 | GEN-03-BE: "Los reintentos no generan respuestas duplicadas ni reinician el tiempo." | VOC1 ACTIVA y dentro de plazo | 1. Enviar con E7 una respuesta y anotar `totalRespondidas`.<br>2. Repetir exactamente la misma petición. | `totalRespondidas` no aumenta con el reenvío y la pregunta conserva una sola respuesta (E7 es idempotente según el Documento BFA). | Manual (Postman) |

### TC-GEN-03-015 — Respuesta fuera de plazo

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-015 | GEN-03-BE: "El servidor determina el vencimiento y rechaza respuestas fuera de plazo."<br>GEN-03: "Se rechazan respuestas fuera del tiempo permitido o correspondientes a subpruebas cerradas." | VOC1 ACTIVA | 1. Enviar una respuesta con E7 inmediatamente después de `finLimite`.<br>2. Repetir el envío después del cierre.<br>3. Llamar a E6 o E4. | Provisional: ambas se rechazan con `409 TIEMPO_AGOTADO` o `409 SUBPRUEBA_NO_ACTIVA` y no quedan almacenadas, aunque el cierre aún no se haya procesado (Documento general ENT-COM-06). | Manual (Postman) |

### TC-GEN-03-016 — Iniciar una subprueba fuera de orden

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-016 | GEN-03-BE: "No se permite iniciar subpruebas fuera de orden ni modificar respuestas de pruebas cerradas."<br>GEN-03: "La aplicación no permite saltar el orden establecido ni regresar a subpruebas finalizadas." | EV-PRUEBA-01 con VOC1 PENDIENTE | 1. Llamar a E5 para VOC2.<br>2. Llamar a E4. | Provisional: `409 SUBPRUEBA_FUERA_DE_ORDEN`; en E4, VOC2 sigue `PENDIENTE`. | Manual (Postman) |

### TC-GEN-03-017 — Modificar respuesta de una subprueba cerrada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-017 | GEN-03-BE: "No se permite iniciar subpruebas fuera de orden ni modificar respuestas de pruebas cerradas."<br>GEN-03: "Se rechazan respuestas fuera del tiempo permitido o correspondientes a subpruebas cerradas." | VOC1 CERRADA con al menos una respuesta guardada | 1. Enviar con E7 un cambio a esa respuesta. | Provisional: `409 SUBPRUEBA_NO_ACTIVA`; la respuesta original no cambia. | Manual (Postman) |

### TC-GEN-03-018 — Respuesta a una pregunta de otra subprueba

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-018 | GEN-03-BE: "Las respuestas se almacenan asociadas a la evaluación, subprueba y pregunta o entrada correspondiente."<br>Descripción de GEN-03-BE: "El backend debe validar […] la pertenencia de cada respuesta a la subprueba correspondiente." | VOC1 ACTIVA; `preguntaId` de una pregunta de VOC2 | 1. Enviar con E7, en VOC1, una respuesta a la pregunta de VOC2. | Se rechaza y no queda almacenada (Documento general ENT-COM-09). Código: pendiente de la consulta C-2. | Manual (Postman) |

### TC-GEN-03-019 — Cierre único de la subprueba

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-019 | GEN-03-BE: "Cada subprueba se cierra una sola vez." | VOC1 cerrada por vencimiento del tiempo | 1. Llamar a E4.<br>2. Llamar a E5 para VOC1.<br>3. Llamar de nuevo a E4. | Provisional: VOC1 aparece `CERRADA` en ambas consultas y E5 responde `409 SUBPRUEBA_CERRADA`. Comprobación de que el cierre ocurrió una sola vez: pendiente de la consulta C-5. | Manual (Postman) |

### TC-GEN-03-020 — Evaluación o subprueba inexistente

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-020 | GEN-03-BE: "Los endpoints cumplen los contratos aprobados de DOC-05." | Identificadores de evaluación y subprueba que no existen | 1. Llamar a E5, E6 y E7 con una evaluación inexistente.<br>2. Repetir con una subprueba inexistente. | Provisional: `404 EVALUACION_NO_ENCONTRADA` y `404 SUBPRUEBA_NO_ENCONTRADA`; no se crea ni modifica nada. | Manual (Postman) |

### TC-GEN-03-021 — Cuerpo inválido al registrar respuesta

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-021 | GEN-03-BE: "Los endpoints cumplen los contratos aprobados de DOC-05." | VOC1 ACTIVA | 1. Enviar E7 con JSON mal formado.<br>2. Enviar E7 sin `preguntaId`.<br>3. Enviar E7 con una opción que no pertenece a la pregunta. | Provisional: pasos 1 y 2, `400 SOLICITUD_INVALIDA` con `detalles`; paso 3, `422 RESPUESTA_INVALIDA`. No se almacena nada. | Manual (Postman) |

### TC-GEN-03-022 — Acceso a una evaluación ajena

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-022 | Descripción de GEN-03-BE: "El backend debe validar […] los permisos […]." | EV-PRUEBA-02 de otro sujeto; sesión de una cuenta sin permiso sobre ella | 1. Llamar a E5, E6 y E7 sobre EV-PRUEBA-02. | Provisional: `403 SIN_PERMISO` (Documento general §5.2.1) y no se modifica nada; pendiente de la consulta C-3. **BLOQUEADO** hasta que GEN-06 implemente el acceso. | Manual (Postman) |

### TC-GEN-03-023 — Pruebas automatizadas del PR

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-023 | GEN-03-BE: "Las pruebas automatizadas se ejecutan satisfactoriamente." | PR de GEN-03-BE abierto | 1. Revisar los checks del PR.<br>2. Ejecutar `mvn -f backend/pom.xml verify` sobre `pull/<n>/merge`. | El job `backend-build` y la ejecución local terminan sin fallos. | Revisión de CI |

### TC-GEN-03-024 — Llamada sin sesión

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-024 | Descripción de GEN-03-BE: "El backend debe validar […] los permisos […]." | EV-PRUEBA-01; peticiones sin cabecera `Authorization` | 1. Llamar a E5, E6 y E7 sin token. | Provisional: `401 NO_AUTENTICADO` (Documento general §5.2.1) y no se modifica nada; pendiente de la consulta C-3. **BLOQUEADO** hasta que GEN-06 implemente el acceso. | Manual (Postman) |

## Registro de ejecución

| ID | PR/SHA | Fecha | Resultado | Evidencia / bug |
| --- | --- | --- | --- | --- |
| TC-GEN-03-001 a 008 | #22 / — | — | Pendiente: PR con conflictos con `main` | — |
| TC-GEN-03-009 a 021, 023 | — | — | BLOQUEADO: sin PR de GEN-03-BE y contratos sin aprobar | — |
| TC-GEN-03-022, 024 | — | — | BLOQUEADO: GEN-06 y consulta C-3 | — |
