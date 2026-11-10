# Casos de prueba GEN-03 — presentar preguntas y registrar respuestas (v3)

## Identificación

- **Tarea de Testing:** [GEN-03-TEST](https://app.clickup.com/t/86bcx2xfn)
- **Tareas de Desarrollo:** [GEN-03-FE](https://app.clickup.com/t/86bcvah5a) (PR #22) · [GEN-03-BE](https://app.clickup.com/t/86bcvah59) (PR #34, que también incluye una pantalla de frontend)
- **Historia:** [GEN-03](https://app.clickup.com/t/86bcvah55)
- **Tester:** Rodrigo Romero
- **Versión y fecha:** v3 · 2026-11-09
- **Estado del documento:** diseño de casos. Las ejecuciones de la v2 sobre el PR #34 están en «Registro de ejecución»; la próxima se hace con esta versión.

## Historial de cambios

| Versión | Fecha | Cambios |
| --- | --- | --- |
| v1 | 2026-11-05 | Diseño inicial de 24 casos con valores provisionales (PR #24). |
| v2 | 2026-11-06 | DOC-05 y DOC-06 aprobados: los valores del contrato, las convenciones REST y el motor común dejan de ser provisionales. Se cierra la consulta C-3. Las referencias a casos de uso pasan a la numeración de DOC-04 rev. 2. C-4 y C-5 quedan resueltas en comportamiento y abiertas solo en lo que falta del contrato. TC-GEN-03-004 y TC-GEN-03-022 incorporan escenarios de DOC-04 rev. 2. Se registran los cambios en revisión que pueden afectar estos casos. |
| v3 | 2026-11-09 | Contrato actualizado el 08-nov: E7 de Fluidez Verbal usa `ordenEntrada` y los estados de subprueba quedan unificados. Se agregan los criterios propios de GEN-03-TEST a la matriz. TC-GEN-03-009 y TC-GEN-03-013 verifican el esquema completo de E5 y E7. TC-GEN-03-002 incluye la letra inicial y la recuperación al recargar. Casos nuevos de backend para Fluidez Verbal: TC-GEN-03-025 a TC-GEN-03-028. Consultas nuevas C-6 (E24) y C-7 (errores de E7 en FV). Registro de las ejecuciones del PR #34. |

## Fuentes y estado

| Fuente | Estado al 2026-11-09 | Uso en este documento |
| --- | --- | --- |
| Criterios de aceptación de GEN-03, GEN-03-BE, GEN-03-FE y GEN-03-TEST (ClickUp) | Publicados en las tareas | Base de todos los casos |
| Documento BFA — §5 (contratos E4–E7) y Documento general — §5.2 (convenciones REST) | **Aprobados** ([DOC-05](https://app.clickup.com/t/86bctt9cy) `completed`), con los ajustes del 2026-11-08 (`ordenEntrada` en Fluidez Verbal, `versionFormularioId` en E4) | Códigos HTTP, campos y formato de error |
| Documento general — §5.2.4 (E22 a E31, entre ellos E24 de instrucciones y práctica) | Agregada el 2026-11-08; `docs/api/openapi.yaml` la publica en el PR #37 (GEN-00-API) | Solo referencia: ninguna tarea de GEN-03 lista E24 (consulta C-6) |
| Documento general — §5.1 (arquitectura y motor común) | **Aprobado** ([DOC-06](https://app.clickup.com/t/86bctt9db) `completed`) | Estados, orden y control de tiempo |
| Documento general — §4.2 (entidades ENT-COM) y §4.6 (CA-01 a CA-10 del modelo) | **Aprobado** ([DOC-03](https://app.clickup.com/t/86bctt9bt) `completed`) | Reglas de respuesta, cierre y pertenencia |
| Documento general — §3 (DOC-04 rev. 2: reglas, catálogo de estados y casos de uso CU-GEN y CU-BFA-P01 a P12) | [DOC-04](https://app.clickup.com/t/86bctt9cm) `in review`; el 2026-11-08 unificó los estados con el contrato | Valores **provisionales**, marcados en cada caso |
| [DOC-02](https://app.clickup.com/t/86bctt9b8) — Requisitos | `completed` | Referencia |
| [DOC-07](https://app.clickup.com/t/86bctt9dn) — Calidad documental | `in progress` | Dependencia de GEN-03-TEST en ClickUp |
| [GEN-06](https://app.clickup.com/t/86bcvaj7e) — Acceso y roles | `Open` | Casos de acceso **bloqueados** |
| Estructura y reglas del repositorio GitHub | Vigente | Formato del archivo, PR y datos de prueba |

Los resultados esperados que provienen de documentos aprobados no llevan marca. Los que dependen de DOC-04 se marcan como **provisional (DOC-04)** y se confirman cuando DOC-04 se cierre.

**Numeración de casos de uso:** desde el 2026-11-08 el Documento BFA §3 usa la misma numeración que DOC-04 rev. 2 y que el contrato aprobado (E5 → CU-BFA-P05 «Iniciar y presentar una subprueba»; E7 → CU-BFA-P06 «Registrar y cambiar respuestas», que incluye las palabras de Fluidez Verbal).

**Alcance de E4:** E4 pertenece a GEN-02-BE («registro mínimo, inicio y recuperación»). Estos casos solo lo usan para observar `subpruebaActiva` y el estado de cada subprueba; su esquema completo lo verifica GEN-02-TEST.

Los criterios de las tareas se citan con su texto de ClickUp, porque todavía no tienen ID. Cuando Documentación asigne los IDs `CA-`, se usarán aquí.

### Cambios en revisión que pueden afectar estos casos

Solicitados en DOC-04 el 2026-11-06; no forman parte del contrato aprobado:

- **Estados de subprueba:** resuelto el 2026-11-08. DOC-04 usa ahora `PENDIENTE / ACTIVA / CERRADA`, igual que el contrato.
- **Selección múltiple:** se pidió ajustar E6 y E7 para guardar, recuperar y desmarcar varias opciones en Desplazamiento (S2). Estos casos cubren solo selección única y texto libre.
- **Entrega anticipada:** DOC-04 rev. 2 la menciona como forma de cierre, pero el contrato aprobado no la incluye. No se prueba en esta versión.
- **Permisos del aplicador:** se pidió distinguir su acceso al progreso y al reloj del acceso a las respuestas.

## Glosario

| Término | Significado |
| --- | --- |
| CA | Criterio de aceptación: condición que la funcionalidad debe cumplir para darse por buena. Documentación les asignará IDs `CA-<MOD>-NN` (manual §4.2). |
| TC | Caso de prueba (*test case*): datos, pasos y resultado esperado para comprobar un criterio. ID `TC-GEN-03-NNN`. |
| FE / BE | Frontend (interfaz en el navegador) / backend (API). |
| E4, E5, E6, E7 | Endpoints del contrato: consultar estado de la evaluación, iniciar subprueba, obtener preguntas y registrar respuestas. |
| E24 | Endpoint complementario de instrucciones y ejemplos de práctica de una subprueba, antes de E5 (Documento general §5.2.4). |
| FV | Fluidez Verbal: subprueba de texto libre sin preguntas, con hasta 75 entradas y una letra inicial asignada. |
| `ordenEntrada` | Posición de cada palabra de FV en E6 y E7. |
| PENDIENTE / ACTIVA / CERRADA | Estados de una subprueba dentro de una evaluación, según el contrato aprobado. |
| Aplicador | Función del Académico o Coordinador con permiso sobre la aplicación; no responde por el Sujeto (DOC-04 rev. 2 §2.2). |
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
| Los endpoints cumplen los contratos aprobados de DOC-05. | TC-GEN-03-009, TC-GEN-03-011, TC-GEN-03-013, TC-GEN-03-020, TC-GEN-03-021, TC-GEN-03-025, TC-GEN-03-028 |
| El tiempo oficial comienza únicamente al iniciar la subprueba, después de las instrucciones y prácticas correspondientes. | TC-GEN-03-009 |
| El servidor determina el vencimiento y rechaza respuestas fuera de plazo. | TC-GEN-03-015 |
| Las respuestas se almacenan asociadas a la evaluación, subprueba y pregunta o entrada correspondiente. | TC-GEN-03-013, TC-GEN-03-018, TC-GEN-03-026 |
| Los reintentos no generan respuestas duplicadas ni reinician el tiempo. | TC-GEN-03-010, TC-GEN-03-014, TC-GEN-03-027 |
| Descripción: "Debe admitir respuestas de selección y texto libre […]". | TC-GEN-03-013, TC-GEN-03-025, TC-GEN-03-026 |
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
| Las respuestas se almacenan y pueden recuperarse correctamente. | TC-GEN-03-002, TC-GEN-03-013, TC-GEN-03-026 |
| Se rechazan respuestas fuera del tiempo permitido o correspondientes a subpruebas cerradas. | TC-GEN-03-008, TC-GEN-03-015, TC-GEN-03-017 |
| La aplicación no permite saltar el orden establecido ni regresar a subpruebas finalizadas. | TC-GEN-03-006, TC-GEN-03-016 |
| Backend y frontend funcionan integrados y superan sus pruebas correspondientes. | TEST-01-EJEC (ver abajo) |

**GEN-03-TEST (criterios propios de la tarea de Testing)**

| Criterio | Casos o evidencia |
| --- | --- |
| Los reintentos no duplican respuestas y se rechazan las respuestas tardías. | TC-GEN-03-014, TC-GEN-03-015, TC-GEN-03-027 |
| El cronómetro sigue al servidor. | TC-GEN-03-004, TC-GEN-03-010 |
| Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado. | TC-GEN-03-009, TC-GEN-03-011, TC-GEN-03-013, TC-GEN-03-020, TC-GEN-03-021, TC-GEN-03-025, TC-GEN-03-028; y el formato común de error en todos los casos de backend |
| Cada criterio de las subtareas del grupo tiene al menos un caso `TC-`. | Esta matriz |
| Cada PR del grupo tiene `testing-ok` o sus fallos comentados antes de la fusión. | «Registro de ejecución» y comentarios en cada PR |

**Criterios del modelo relacionados (Documento general §4.6)**

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
| C-1 | ¿Qué subpruebas permiten volver a preguntas anteriores mientras están activas y dónde se configura? DOC-04 rev. 2 §3.2 lo permite "cuando su modalidad lo permita", pero ni la configuración de subprueba ni E2 tienen ese dato. | Documento BFA §2.6 y §5.2; Documento general ENT-COM-13 y §3.2 | TC-GEN-03-006 | Abierta |
| C-2 | ¿Qué código devuelve E7 si la pregunta pertenece a otra subprueba de la misma edición? E7 solo menciona `422 RESPUESTA_INVALIDA` para opción ajena o pregunta de otra edición. | Documento BFA E7; Documento general ENT-COM-09 | TC-GEN-03-018 | Abierta |
| C-3 | ¿E5, E6 y E7 responden `401 NO_AUTENTICADO` y `403 SIN_PERMISO`? | Documento general §5.2.1 | TC-GEN-03-022, TC-GEN-03-024 | **Resuelta:** la convención aprobada §5.2.1 aplica a toda operación protegida. |
| C-4 | ¿Qué código devuelve repetir E5 sobre la misma subprueba ACTIVA? El comportamiento lo define DOC-04 rev. 2 (CU-BFA-P05: mismo inicio y fin), pero E5 lista `409 HAY_SUBPRUEBA_ACTIVA` sin aclarar si aplica a la misma subprueba. | Documento BFA E5; Documento general §3, CU-BFA-P05 | TC-GEN-03-010 | Abierta (solo el código) |
| C-5 | ¿Cómo se comprueba desde la API que una subprueba se cerró una sola vez? El comportamiento lo define DOC-04 rev. 2 (CU-BFA-P07 A1: un cierre repetido devuelve el mismo cierre), pero E4 no expone la fecha de cierre ni eventos de auditoría. | Documento BFA E4; Documento general §3, CU-BFA-P07, ENT-COM-06 y ENT-COM-11 | TC-GEN-03-019 | Abierta (solo la observación) |
| C-6 | ¿Qué tarea implementa y consume E24 (instrucciones y ejemplos de práctica)? GEN-03-BE y GEN-03-FE solo listan E5, E6 y E7, y E4 sigue diciendo que instrucciones y práctica ocurren en el frontend sin estado en el servidor. Mientras no se defina, TC-GEN-03-003 comprueba que haya instrucciones y práctica antes de E5, sin exigir el contenido de E24. | Documento general §5.2.4 (E24); Documento BFA E4; descripciones de GEN-03-BE y GEN-03-FE | TC-GEN-03-003 | Abierta |
| C-7 | ¿Qué código devuelve E7 en Fluidez Verbal si una palabra llega con texto vacío, con un `ordenEntrada` repetido o si se supera `maximoEntradas`? E7 exige texto no vacío, orden único y el límite de entradas, pero solo nombra `400 SOLICITUD_INVALIDA` y `422 RESPUESTA_INVALIDA` (este último para opciones y preguntas). Tampoco nombra el campo con el que E6 devuelve las palabras de FV. | Documento BFA E6 y E7 | TC-GEN-03-026, TC-GEN-03-028 | Abierta |

## Datos de prueba (ficticios)

Se usa la estructura de configuración del BFA publicada en el Documento BFA (códigos, orden, tiempos y tipo de respuesta), con **preguntas, opciones y claves inventadas**. No se usan ítems reales.

- **EV-PRUEBA-01:** aplicación parcial con VOC1 (orden 1, 300 s, selección A–E) y VOC2 (orden 2, 360 s, selección A–E).
- **EV-PRUEBA-02:** misma estructura que EV-PRUEBA-01, de otro sujeto ficticio, para los casos de acceso.
- **EV-PRUEBA-03:** aplicación parcial solo con FV (300 s, texto libre, hasta 75 entradas), con letra inicial asignada.
- **EV-PRUEBA-04:** como EV-PRUEBA-03, pero sin letra inicial, solo para el paso 2 de TC-GEN-03-025. E3 no permite crearla (`409 LETRA_NO_ASIGNADA`), así que se carga directamente en la base de prueba.
- **Cuentas:** una cuenta ficticia de Sujeto dueña de EV-PRUEBA-01 y EV-PRUEBA-03; otra de Sujeto sin relación con EV-PRUEBA-02; y una de Académico o Coordinador con permiso de aplicador sobre EV-PRUEBA-01.

Las migraciones no cargan datos: la colección Postman crea sus propios datos por la API (cuenta y sesión con E13/E14, Sujeto y Periodo con E18/E20, evaluación con E3). Mientras no exista E3 (GEN-02-BE), los datos ficticios, incluidas las claves de TC-GEN-03-012 y la letra de EV-PRUEBA-03, se cargan con SQL en una base local limpia; la ejecución registra cómo se cargaron.

## Entorno

- **Frontend:** hay dos implementaciones de la pantalla de subprueba: la del PR #22 (GEN-03-FE) y la del PR #34. Integración decidirá cuál se fusiona (comentario del 2026-11-07 en el PR #34); los casos TC-GEN-03-001 a 008 se ejecutan sobre esa. Se prueba con `npm ci` y `npm run dev` en `frontend/`, sobre `pull/<n>/merge`.
- **Backend:** PR de GEN-03-BE (hoy, PR #34) sobre `pull/<n>/merge`, con `docker compose up -d db` y `mvn -f backend/pom.xml spring-boot:run`; colección Postman en `tests/api/` con variables `{{base_url}}` y `{{token}}` de una cuenta ficticia. Requiere las tablas de catálogo de GEN-07-BE: por decisión de Integración del 2026-11-07, el PR #23 (GEN-07-BE) se fusiona primero y el PR #34 se adapta a sus tablas.

## Casos — Frontend (navegador)

### TC-GEN-03-001 — Pregunta de selección

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-001 | GEN-03-FE: "Los componentes permiten representar los tipos de preguntas y respuestas definidos por el motor común." | EV-PRUEBA-01, VOC1 activa | 1. Llegar a una pregunta de VOC1.<br>2. Elegir una alternativa. | La pregunta se presenta con sus alternativas (A–E en VOC1) y se puede responder eligiendo una. | Manual (navegador) |

### TC-GEN-03-002 — Respuesta de texto libre

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-002 | GEN-03-FE: "Los componentes permiten representar los tipos de preguntas y respuestas definidos por el motor común."<br>GEN-03: "Las respuestas se almacenan y pueden recuperarse correctamente." | EV-PRUEBA-03, FV activa, con letra inicial asignada | 1. Observar la letra que se muestra.<br>2. Escribir una palabra.<br>3. Agregar una segunda palabra.<br>4. Recargar la página. | Se presenta un campo de texto libre con la letra que devuelve E5 (`letraInicial`). Cada palabra se guarda como una entrada con su `ordenEntrada`, en el orden escrito (E7 de FV), y se informa como confirmada. Al recargar, aparecen las mismas palabras en el mismo orden (E6 de FV devuelve las palabras registradas con su orden). | Manual (navegador) |

### TC-GEN-03-003 — Instrucciones y práctica antes del tiempo oficial

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-003 | GEN-03-FE: "Las instrucciones y prácticas aparecen antes del inicio del tiempo oficial." | EV-PRUEBA-01, VOC1 PENDIENTE | 1. Entrar a VOC1.<br>2. Observar el temporizador durante instrucciones y práctica.<br>3. Completar la práctica. | Instrucciones y práctica aparecen antes de que inicie el tiempo oficial; el temporizador oficial no corre durante ellas. Según el Documento BFA (E4), instrucciones y práctica ocurren en el frontend antes de E5. De dónde salen sus contenidos (E24) queda pendiente de la consulta C-6. | Manual; candidato e2e (TEST-01-EJEC) |

### TC-GEN-03-004 — Temporizador según el backend al recargar

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-004 | GEN-03-FE: "El temporizador visual utiliza el tiempo informado por el backend."<br>GEN-03: "El tiempo oficial es controlado por el servidor y no se reinicia al recargar la página." | EV-PRUEBA-01, VOC1 ACTIVA (300 s); backend disponible | 1. Esperar hasta el segundo 120 aproximadamente.<br>2. Anotar `segundosRestantes` de E4.<br>3. Recargar la página y comparar con E4. | El temporizador coincide con el tiempo que informa E4 y, tras recargar, continúa desde ese tiempo: no restituye los 300 s ni duplica respuestas (provisional (DOC-04): CU-BFA-P04, resultado verificable). | Manual; candidato e2e (TEST-01-EJEC) |

### TC-GEN-03-005 — Respuesta pendiente frente a confirmada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-005 | GEN-03-FE: "Se distingue entre respuestas pendientes de guardar y respuestas confirmadas." | EV-PRUEBA-01, VOC1 ACTIVA | 1. Responder una pregunta.<br>2. Observar el estado antes y después de la confirmación del servidor. | La respuesta se muestra como pendiente de guardar hasta que el servidor confirma, y luego como confirmada; nunca se muestra como guardado un cambio pendiente. | Manual; candidato e2e (TEST-01-EJEC) |

### TC-GEN-03-006 — Navegación dentro y entre subpruebas

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-006 | GEN-03-FE: "La navegación respeta las restricciones establecidas para cada subprueba."<br>GEN-03: "La aplicación no permite saltar el orden establecido ni regresar a subpruebas finalizadas." | EV-PRUEBA-01, VOC1 ACTIVA con varias preguntas respondidas | 1. Volver a una pregunta anterior de VOC1.<br>2. Intentar pasar a VOC2 antes de que venza VOC1.<br>3. Con VOC1 cerrada, intentar volver a VOC1. | Dentro de la subprueba activa se puede volver a preguntas anteriores (Documento BFA §5.2); no se puede pasar a la siguiente antes del vencimiento ni volver a una cerrada (Documento general §5.1.7). Restricciones de navegación interna por subprueba: pendiente de la consulta C-1. | Manual (navegador) |

### TC-GEN-03-007 — Fallo al guardar una respuesta

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-007 | GEN-03-FE: "Se informa correctamente cuando una respuesta falla al guardarse o el tiempo ha terminado." | EV-PRUEBA-01, VOC1 ACTIVA; el guardado falla (backend no disponible o respuesta de error) | 1. Responder una pregunta.<br>2. Observar el mensaje y el estado de la respuesta. | Se informa que la respuesta no se guardó y no se muestra como confirmada. | Manual (navegador) |

### TC-GEN-03-008 — Fin del tiempo oficial

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-008 | GEN-03-FE: "Se informa correctamente cuando una respuesta falla al guardarse o el tiempo ha terminado."<br>GEN-03: "Se rechazan respuestas fuera del tiempo permitido o correspondientes a subpruebas cerradas." | EV-PRUEBA-01, VOC1 ACTIVA | 1. Esperar a que el temporizador llegue a cero (300 s en VOC1).<br>2. Intentar responder. | Se informa que el tiempo terminó y no se aceptan más respuestas en esa subprueba. | Manual; candidato e2e (TEST-01-EJEC) |

## Casos — Backend (Postman)

Contratos aprobados del Documento BFA §5.3 y el Documento general §5.2:

- E4: `GET /api/v1/evaluaciones/{evaluacionId}`
- E5: `POST /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/iniciar`
- E6: `GET /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/preguntas`
- E7: `PUT /api/v1/evaluaciones/{evaluacionId}/subpruebas/{codigo}/respuestas`

En todo error se verifica además el cuerpo común (Documento general §5.2.2): `timestamp`, `status`, `codigo`, `mensaje`, `ruta` y, en errores de validación, `detalles`.

### TC-GEN-03-009 — Iniciar subprueba fija el tiempo oficial

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-009 | GEN-03-BE: "El tiempo oficial comienza únicamente al iniciar la subprueba, después de las instrucciones y prácticas correspondientes." | EV-PRUEBA-01, VOC1 PENDIENTE | 1. Llamar a E4 y comprobar que no hay subprueba activa.<br>2. Llamar a E5 para VOC1. | `200` con la forma de `SubpruebaActivaResponse` del contrato: `codigo` = VOC1, `estado` = `ACTIVA`, `inicio`, `finLimite` (inicio + 300 s) y `segundosRestantes`. `letraInicial` solo aplica a FV (TC-GEN-03-025). | Manual (Postman) |

### TC-GEN-03-010 — Repetir el inicio no reinicia el tiempo

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-010 | GEN-03-BE: "Los reintentos no generan respuestas duplicadas ni reinician el tiempo."<br>GEN-03: "El tiempo oficial es controlado por el servidor y no se reinicia al recargar la página." | VOC1 ya iniciada con E5 | 1. Anotar `inicio` y `finLimite`.<br>2. Esperar unos segundos.<br>3. Repetir E5 para VOC1. | `inicio` y `finLimite` no cambian y no se otorga tiempo adicional (provisional (DOC-04): CU-BFA-P05). Código de respuesta: pendiente de la consulta C-4. | Manual (Postman) |

### TC-GEN-03-011 — Obtener preguntas de la subprueba activa

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-011 | GEN-03-BE: "Los endpoints cumplen los contratos aprobados de DOC-05." | VOC1 ACTIVA | 1. Llamar a E6 para VOC1. | `200` con `codigo`, `segundosRestantes` y `preguntas` (cada una con `id`, `numero`, `enunciado`, `opciones` y `respuestaActual`). | Manual (Postman) |

### TC-GEN-03-012 — Sin claves de corrección en la respuesta

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-012 | GEN-03-BE: "La API no expone claves de corrección ni información restringida." | VOC1 ACTIVA, con claves ficticias conocidas | 1. Llamar a E6 para VOC1.<br>2. Revisar todo el cuerpo de la respuesta. | La respuesta no contiene la clave correcta de ninguna pregunta ni información restringida (Documento BFA E6). | Manual (Postman) |

### TC-GEN-03-013 — Registrar y recuperar una respuesta en plazo

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-013 | GEN-03-BE: "Las respuestas se almacenan asociadas a la evaluación, subprueba y pregunta o entrada correspondiente."<br>GEN-03: "Las respuestas se almacenan y pueden recuperarse correctamente." | VOC1 ACTIVA y dentro de plazo | 1. Enviar con E7 una respuesta a una pregunta de VOC1.<br>2. Llamar a E6 para VOC1. | E7 responde `200` con el avance en la forma de `AvanceSubpruebaResponse` del contrato: `codigo`, `registradas`, `totalRespondidas` y `segundosRestantes`. En E6, `respuestaActual` de esa pregunta es la opción enviada. | Manual (Postman) |

### TC-GEN-03-014 — Reintento sin duplicar respuestas

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-014 | GEN-03-BE: "Los reintentos no generan respuestas duplicadas ni reinician el tiempo." | VOC1 ACTIVA y dentro de plazo | 1. Enviar con E7 una respuesta y anotar `totalRespondidas`.<br>2. Repetir exactamente la misma petición. | `totalRespondidas` no aumenta con el reenvío y la pregunta conserva una sola respuesta (E7 es idempotente según el Documento BFA). | Manual (Postman) |

### TC-GEN-03-015 — Respuesta fuera de plazo

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-015 | GEN-03-BE: "El servidor determina el vencimiento y rechaza respuestas fuera de plazo."<br>GEN-03: "Se rechazan respuestas fuera del tiempo permitido o correspondientes a subpruebas cerradas." | VOC1 ACTIVA | 1. Enviar una respuesta con E7 inmediatamente después de `finLimite`.<br>2. Repetir el envío después del cierre.<br>3. Llamar a E6 o E4. | Paso 1: `409 TIEMPO_AGOTADO`, aunque el cierre aún no se haya procesado (Documento general ENT-COM-06). Paso 2: `409 TIEMPO_AGOTADO` o `409 SUBPRUEBA_NO_ACTIVA`. Ninguna queda almacenada. | Manual (Postman) |

### TC-GEN-03-016 — Iniciar una subprueba fuera de orden

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-016 | GEN-03-BE: "No se permite iniciar subpruebas fuera de orden ni modificar respuestas de pruebas cerradas."<br>GEN-03: "La aplicación no permite saltar el orden establecido ni regresar a subpruebas finalizadas." | EV-PRUEBA-01 con VOC1 PENDIENTE | 1. Llamar a E5 para VOC2.<br>2. Llamar a E4. | `409 SUBPRUEBA_FUERA_DE_ORDEN`; en E4, VOC2 sigue `PENDIENTE`. | Manual (Postman) |

### TC-GEN-03-017 — Modificar respuesta de una subprueba cerrada

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-017 | GEN-03-BE: "No se permite iniciar subpruebas fuera de orden ni modificar respuestas de pruebas cerradas."<br>GEN-03: "Se rechazan respuestas fuera del tiempo permitido o correspondientes a subpruebas cerradas." | VOC1 CERRADA con al menos una respuesta guardada | 1. Enviar con E7 un cambio a esa respuesta. | `409 SUBPRUEBA_NO_ACTIVA`; la respuesta original no cambia. | Manual (Postman) |

### TC-GEN-03-018 — Respuesta a una pregunta de otra subprueba

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-018 | GEN-03-BE: "Las respuestas se almacenan asociadas a la evaluación, subprueba y pregunta o entrada correspondiente."<br>Descripción de GEN-03-BE: "El backend debe validar […] la pertenencia de cada respuesta a la subprueba correspondiente." | VOC1 ACTIVA; `preguntaId` de una pregunta de VOC2 | 1. Enviar con E7, en VOC1, una respuesta a la pregunta de VOC2. | Se rechaza y no queda almacenada (Documento general ENT-COM-09). Código: pendiente de la consulta C-2. | Manual (Postman) |

### TC-GEN-03-019 — Cierre único de la subprueba

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-019 | GEN-03-BE: "Cada subprueba se cierra una sola vez." | VOC1 cerrada por vencimiento del tiempo | 1. Llamar a E4.<br>2. Llamar a E5 para VOC1.<br>3. Llamar de nuevo a E4. | VOC1 aparece `CERRADA` en ambas consultas y E5 responde `409 SUBPRUEBA_CERRADA`. Un cierre repetido devuelve el mismo cierre (provisional (DOC-04): CU-BFA-P07 A1). Cómo comprobar desde la API que hubo un solo cierre: pendiente de la consulta C-5. | Manual (Postman) |

### TC-GEN-03-020 — Evaluación o subprueba inexistente

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-020 | GEN-03-BE: "Los endpoints cumplen los contratos aprobados de DOC-05." | Identificadores de evaluación y subprueba que no existen | 1. Llamar a E5, E6 y E7 con una evaluación inexistente.<br>2. Repetir con una subprueba inexistente. | `404 EVALUACION_NO_ENCONTRADA` y `404 SUBPRUEBA_NO_ENCONTRADA`; no se crea ni modifica nada. | Manual (Postman) |

### TC-GEN-03-021 — Cuerpo inválido al registrar respuesta

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-021 | GEN-03-BE: "Los endpoints cumplen los contratos aprobados de DOC-05." | VOC1 ACTIVA | 1. Enviar E7 con JSON mal formado.<br>2. Enviar E7 sin `preguntaId`.<br>3. Enviar E7 con una opción que no pertenece a la pregunta. | Pasos 1 y 2: `400 SOLICITUD_INVALIDA` con `detalles`. Paso 3: `422 RESPUESTA_INVALIDA`. No se almacena nada. | Manual (Postman) |

### TC-GEN-03-022 — Acceso no permitido

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-022 | Descripción de GEN-03-BE: "El backend debe validar […] los permisos […]." | EV-PRUEBA-02 de otro sujeto; sesión de una cuenta sin permiso sobre ella. Sesión de aplicador sobre EV-PRUEBA-01. | 1. Con la cuenta sin permiso, llamar a E5, E6 y E7 sobre EV-PRUEBA-02.<br>2. Con la cuenta de aplicador, enviar E7 sobre EV-PRUEBA-01. | Paso 1: `403 SIN_PERMISO` (Documento general §5.2.1) y no se modifica nada. Paso 2: se rechaza, porque el aplicador no responde por el Sujeto (provisional (DOC-04): §2.2 y CU-BFA-P06). **BLOQUEADO** hasta que GEN-06 implemente el acceso. | Manual (Postman) |

### TC-GEN-03-023 — Pruebas automatizadas del PR

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-023 | GEN-03-BE: "Las pruebas automatizadas se ejecutan satisfactoriamente." | PR de GEN-03-BE abierto | 1. Revisar los checks del PR.<br>2. Ejecutar `mvn -f backend/pom.xml verify` sobre `pull/<n>/merge`. | El job `backend-build` y la ejecución local terminan sin fallos. | Revisión de CI |

### TC-GEN-03-024 — Llamada sin sesión

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-024 | Descripción de GEN-03-BE: "El backend debe validar […] los permisos […]." | EV-PRUEBA-01; peticiones sin cabecera `Authorization` | 1. Llamar a E5, E6 y E7 sin token. | `401 NO_AUTENTICADO` (Documento general §5.2.1) y no se modifica nada. **BLOQUEADO** hasta que GEN-06 implemente el acceso. | Manual (Postman) |

## Casos — Backend de Fluidez Verbal (Postman)

### TC-GEN-03-025 — Iniciar Fluidez Verbal devuelve la letra inicial

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-025 | GEN-03-BE: "Los endpoints cumplen los contratos aprobados de DOC-05."<br>Descripción de GEN-03-BE: "Debe admitir respuestas de selección y texto libre […]". | EV-PRUEBA-03, FV `PENDIENTE` con letra inicial asignada. EV-PRUEBA-04, FV `PENDIENTE` sin letra. | 1. Llamar a E5 para FV en EV-PRUEBA-03.<br>2. Llamar a E5 para FV en EV-PRUEBA-04. | Paso 1: `200` con `codigo` = FV, `estado` = `ACTIVA`, `inicio`, `finLimite` (inicio + 300 s), `segundosRestantes` y `letraInicial` igual a la letra asignada (Documento BFA E5; RF-FV-06). Paso 2: `409 LETRA_NO_CONFIGURADA` y FV sigue `PENDIENTE`. | Manual (Postman) |

### TC-GEN-03-026 — Registrar y recuperar palabras de Fluidez Verbal

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-026 | GEN-03-BE: "Las respuestas se almacenan asociadas a la evaluación, subprueba y pregunta o entrada correspondiente."<br>GEN-03: "Las respuestas se almacenan y pueden recuperarse correctamente." | EV-PRUEBA-03, FV `ACTIVA` y dentro de plazo | 1. Enviar con E7 `{"palabras":[{"ordenEntrada":1,"texto":"mesa"},{"ordenEntrada":2,"texto":"montaña"}]}`, sin pregunta ni opción.<br>2. Llamar a E6 para FV. | E7 responde `200` con el avance (`codigo`, `registradas`, `totalRespondidas` y `segundosRestantes`). E6 devuelve las dos palabras con su `ordenEntrada` y su texto original ("mesa", "montaña"). El nombre del campo de E6 que las contiene está pendiente de la consulta C-7. | Manual (Postman) |

### TC-GEN-03-027 — Reenviar palabras no las duplica

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-027 | GEN-03-BE: "Los reintentos no generan respuestas duplicadas ni reinician el tiempo."<br>GEN-03-TEST: "Los reintentos no duplican respuestas y se rechazan las respuestas tardías." | EV-PRUEBA-03, FV `ACTIVA` con las palabras de TC-GEN-03-026 | 1. Anotar `totalRespondidas` de TC-GEN-03-026.<br>2. Repetir exactamente la misma petición E7.<br>3. Llamar a E6 para FV. | `200`; `totalRespondidas` no aumenta y E6 sigue devolviendo dos palabras, una por `ordenEntrada` (E7 es idempotente según el Documento BFA). | Manual (Postman) |

### TC-GEN-03-028 — Palabras que no cumplen el contrato

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e |
| --- | --- | --- | --- | --- | --- |
| TC-GEN-03-028 | GEN-03-BE: "Los endpoints cumplen los contratos aprobados de DOC-05." | EV-PRUEBA-03, FV `ACTIVA`; `maximoEntradas` = 75 | 1. Enviar con E7 una palabra con texto vacío.<br>2. Enviar dos palabras con el mismo `ordenEntrada`.<br>3. Enviar las 75 entradas permitidas y después una con `ordenEntrada` 76. | Los envíos de los pasos 1, 2 y la entrada 76 se rechazan con el formato común de error y no se almacenan: E6 no los muestra. En FV el texto debe ser no vacío, el orden único y hasta `maximoEntradas` (Documento BFA E7). El código de cada rechazo está pendiente de la consulta C-7. | Manual (Postman) |

## Registro de ejecución

Las ejecuciones sobre el PR #34 se hicieron con los casos de la v2, en local sobre `pull/34/merge`, con base de datos limpia y datos ficticios cargados con SQL. El detalle está en los comentarios del PR.

| ID | PR/SHA | Fecha | Resultado | Evidencia / bug |
| --- | --- | --- | --- | --- |
| TC-GEN-03-001 a 024 | #34 / `a503aac` | 2026-11-06 | FALLÓ: 10 pasan, 8 fallan, 6 bloqueados (tras la segunda pasada) | [Comentario en el PR #34](https://github.com/Nell16/sistema-evaluaciones/pull/34#issuecomment-6679442680) · [BUG-01](https://app.clickup.com/t/1k5fzqaz5ae) · [BUG-02](https://app.clickup.com/t/1k5fzqaz5af) |
| TC-GEN-03-001 a 024 | #34 / `9297a47` | 2026-11-06 | FALLÓ en dos pasadas: 21 pasan, falla TC-GEN-03-002, bloqueados TC-GEN-03-022 y 024. BUG-01 y BUG-02 corregidos | [Comentario en el PR #34](https://github.com/Nell16/sistema-evaluaciones/pull/34#issuecomment-6696393057) · [BUG-03](https://app.clickup.com/t/1k5fzqaz5m9) |
| TC-GEN-03-001 a 008 | #22 / — | — | Pendiente: PR con conflictos con `main`; Integración debe decidir qué frontend se fusiona | — |
| TC-GEN-03-025 a 028 | — | — | Nuevos en la v3; se ejecutan en la próxima prueba del PR de GEN-03-BE | — |
| TC-GEN-03-022, 024 | — | — | BLOQUEADO: GEN-06 sin implementar | — |
