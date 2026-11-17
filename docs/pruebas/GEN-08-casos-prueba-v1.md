# Casos de prueba GEN-08 — auditoría del flujo de evaluaciones (v1)

## Identificación

- **Tarea de Testing:** [GEN-08-TEST](https://app.clickup.com/t/86bcx2z6d)
- **Tarea de Desarrollo:** [GEN-08-BE](https://app.clickup.com/t/86bcw9yw0) (Emilio Carranza y Gessler Herrera)
- **Historia:** [GEN-08](https://app.clickup.com/t/86bcx2z62)
- **Integración:** [GEN-08-INT](https://app.clickup.com/t/86bcx306b)
- **Tester:** José Ariel Zeledón
- **Versión y fecha:** v1 · 2026-11-14
- **Estado del documento:** diseño de casos. GEN-08-BE todavía no tiene PR, así que todos los casos quedan **BLOQUEADOS** hasta que exista uno que probar.

## Historial de cambios

| Versión | Fecha | Cambios |
| --- | --- | --- |
| v1 | 2026-11-14 | Diseño de 29 casos a partir de los criterios de GEN-08-BE y GEN-08-TEST y de los documentos aprobados. Consultas C-1 a C-5. |

## Fuentes y estado

| Fuente | Estado al 2026-11-14 | Uso en este documento |
| --- | --- | --- |
| Criterios de aceptación de GEN-08-BE y GEN-08-TEST (ClickUp) | Publicados en las tareas | Base de todos los casos |
| Documento BFA §2: RNF-BFA-GEN-05 y RNF-BFA-GEN-08 · Documento general §8.1: RF-GEN-13 y RF-GEN-20 | **Aprobado** ([DOC-02](https://app.clickup.com/t/86bctt9b8) `completed`) | Qué eventos se registran y cómo se relacionan con el intento |
| Documento general §4: ENT-COM-11 EventoAuditoria y criterio CA-09 del modelo | **Aprobado** ([DOC-03](https://app.clickup.com/t/86bctt9bt) `completed`) | Atributos y restricciones del evento |
| Documento general §3: CU-GEN-04, 13, 15 y 19; CU-BFA-P03 a P12 | **Aprobado** ([DOC-04](https://app.clickup.com/t/86bctt9cm) `completed`) | Qué registra cada operación del flujo |
| Documento general §5.2 y `docs/api/openapi.yaml` (E3 a E12, E14, E17 y E31) | **Aprobado** ([DOC-05](https://app.clickup.com/t/86bctt9cy) `completed`) | Rutas, permisos y códigos de error |
| Documento general §5.1.7 (motor común: auditoría) | **Aprobado** ([DOC-06](https://app.clickup.com/t/86bctt9db) `completed`) | Capacidad de auditoría y capas donde vive |
| [DOC-07](https://app.clickup.com/t/86bctt9dn) — Calidad documental | `in progress` | Dependencia de GEN-08-TEST en ClickUp |

Todos los resultados esperados salen de documentos aprobados. Lo que esos documentos dejan abierto no se fija en los casos: queda en «Consultas pendientes».

## Qué se puede verificar y qué no

- **El contrato (E1 a E31) no tiene una ruta para leer la auditoría.** Por eso los eventos se revisan en la base de datos del entorno local, con consultas de solo lectura, después de ejecutar cada operación con Postman.
- **El catálogo de acciones no está definido.** ENT-COM-11 dice que `accion` es «Catálogo por validar» y deja pendientes «D02/D05: acciones, identidades técnicas, permisos y retención». Los casos piden que cada evento se pueda distinguir por su tipo, pero no exigen un código concreto (consulta C-1).
- **No hay colección de Postman todavía.** No hay rutas implementadas que probar (`tests/api/README.md`). La colección se arma cuando llegue el PR de GEN-08-BE y estén las rutas del flujo.
- Los nombres de los campos son los atributos de ENT-COM-11 (`idEvento`, `idEvaluacion`, `idEvaluacionSubprueba`, `actorRef`, `accion`, `entidadTipo`, `entidadId`, `ocurridoEn`, `detalle`). Las columnas reales se anotan al ejecutar, según la migración del PR.

## Matriz de cobertura

**GEN-08-TEST**

| Criterio | Casos | Estado |
| --- | --- | --- |
| Inicio, recuperación, respuestas, cierre, calificación y consulta quedan registrados. | TC-GEN-08-001 a 009 | BLOQUEADO |
| Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado. | TC-GEN-08-029 | BLOQUEADO |
| Cada criterio de las subtareas del grupo tiene al menos un caso `TC-`. | Esta matriz | Cumplido |
| Cada PR del grupo tiene `testing-ok` o sus fallos comentados antes de la fusión. | «Registro de ejecución» | Sin PR |

**GEN-08-BE**

| Criterio | Casos | Estado |
| --- | --- | --- |
| Los eventos definidos quedan registrados correctamente en la base de datos. | TC-GEN-08-001 a 013 | BLOQUEADO |
| Cada evento conserva su tipo, fecha, evaluación relacionada e identidad del actor cuando corresponda. | TC-GEN-08-014 a 017 | BLOQUEADO |
| Los registros permiten reconstruir las operaciones relevantes realizadas durante una evaluación. | TC-GEN-08-018, 019 | BLOQUEADO |
| La auditoría no altera las respuestas, puntuaciones ni estados originales. | TC-GEN-08-022, 023 | BLOQUEADO |
| No se almacenan contraseñas, claves de corrección ni datos sensibles innecesarios. | TC-GEN-08-024, 025 | BLOQUEADO |
| El acceso a los registros está restringido a componentes y usuarios autorizados. | TC-GEN-08-026, 027 | BLOQUEADO |
| Las pruebas automatizadas verifican el registro y la integridad de los eventos. | TC-GEN-08-028 | BLOQUEADO |

**Restricciones del modelo (ENT-COM-11)**

| Restricción | Casos |
| --- | --- |
| `idEvaluacion` «Obligatoria en ejecución/resultado; no en administración global.» | TC-GEN-08-013, 016 |
| `idEvaluacionSubprueba` «De la misma evaluación.» | TC-GEN-08-017 |
| `ocurridoEn` «Momento oficial.» · «Servidor.» | TC-GEN-08-015 |
| «No editar/borrar mediante flujo ordinario. Una corrección produce otro evento.» | TC-GEN-08-020, 021 |
| `detalle` «Sin secretos o contenido sensible innecesario.» | TC-GEN-08-024, 025 |

## Consultas pendientes

Se dejan en la tarea [GEN-08-TEST](https://app.clickup.com/t/86bcx2z6d). Mientras sigan abiertas, los casos afectados no fijan ese resultado.

| ID | Consulta | Para | Casos afectados | Estado |
| --- | --- | --- | --- | --- |
| C-1 | ENT-COM-11 deja `accion` y `entidadTipo` como «Catálogo por validar» (D02/D05). ¿Cuáles son los códigos aprobados de cada evento (inicio, respuesta, cierre, calificación, recuperación, consulta, etc.)? | Documentación (DOC-03 / DOC-05) | TC-GEN-08-001 a 014 | Abierta |
| C-2 | El contrato no tiene una ruta para leer la auditoría. ¿Quién puede consultarla (rol o componente) y por qué vía? GEN-08-BE dice «restringido a componentes y usuarios autorizados». | Documentación (DOC-05 / DOC-06) | TC-GEN-08-026 | Abierta |
| C-3 | Si se repite una operación idempotente (por ejemplo el mismo E7 o el mismo E8), ¿se registra otro evento o no? Para el cierre está claro («se registra un único evento de cierre», CU-GEN-13); para las demás no está definido. | Documentación (DOC-04) | TC-GEN-08-003, 007, 023 | Abierta |
| C-4 | «No editar/borrar mediante flujo ordinario»: ¿basta con que la aplicación no tenga forma de editar o borrar, o la base de datos también debe impedirlo? Lo mismo para las restricciones de `idEvaluacion` e `idEvaluacionSubprueba`. | Documentación (DOC-03) | TC-GEN-08-016, 017, 020 | Abierta |
| C-5 | GEN-08-BE enlaza CU-BFA-P07 a P12. La auditoría de E10, E11 y E12 (revisión de FV, recálculo y publicación), ¿se prueba en GEN-08-TEST o en las tareas BFA-TEST de esos PR? | Scrum de Testing e Integración | TC-GEN-08-010 a 012 | Abierta |

## Datos de prueba (ficticios)

Todo es inventado: no hay ítems reales de pruebas psicológicas ni datos personales.

| Dato | Contenido | Usado en |
| --- | --- | --- |
| VF-08 | Versión publicada ficticia con una subprueba de selección (SEL-08) y una de texto libre tipo FV (FV-08), con preguntas y opciones inventadas | Todos |
| SUJ-08-A y SUJ-08-B | Dos Sujetos ficticios con cuenta propia de rol Sujeto | Todos (B en 019 y 027) |
| ACA-08 | Académico ficticio con permiso para asignar aplicaciones | 001, 018 |
| PSI-08 | Psicólogo ficticio con permiso para revisar FV, recalcular y publicar | 010 a 012, 021 |
| COO-08 | Coordinador ficticio con permiso para publicar versiones | 013 |
| PER-08 | Periodo ficticio activo | Todos |
| EV-08-xx | Evaluación nueva creada con E3 para cada caso | Según el caso |

Para ver un vencimiento en local se puede usar una duración corta en VF-08; se anota en la evidencia.

## Entorno

- **Código:** `pull/<n>/merge` del PR de GEN-08-BE (PR + `main` actual), como dice el manual §3.5.
- **Local:** `docker compose up -d db` y `mvn -f backend/pom.xml spring-boot:run`. Base vacía con las migraciones del PR y los datos ficticios de arriba.
- **API:** Postman con `{{base_url}}` y `{{token}}` de las cuentas ficticias, sin secretos en el repositorio.
- **Lectura de eventos:** consultas `SELECT` de solo lectura sobre la tabla que implemente ENT-COM-11, filtrando por la evaluación del caso y ordenando por `ocurridoEn`. Nunca contra la base del entorno compartido.
- **CI:** el job `backend-build` del SHA probado (TC-GEN-08-028).

## Casos — Registro de cada operación

### TC-GEN-08-001 — Crear la evaluación (E3)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-001 | GEN-08-BE: "Los eventos definidos quedan registrados correctamente en la base de datos."<br>GEN-08-TEST: inicio registrado.<br>CU-BFA-P03: "Registra al responsable y las versiones y confirma la asignación." | ACA-08 con sesión; SUJ-08-A, PER-08 y VF-08 existen | 1. E3 `POST /api/v1/evaluaciones` como ACA-08.<br>2. Leer los eventos de la evaluación creada. | Paso 1: 201. Paso 2: un evento de creación con `idEvaluacion` de la evaluación nueva, `actorRef` de ACA-08 y `ocurridoEn`. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-002 — Iniciar una subprueba (E5)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-002 | GEN-08-BE: "Los eventos definidos quedan registrados correctamente en la base de datos."<br>CU-BFA-P05: "Registra inicio y vencimiento autoritativos de esa ejecución una sola vez." | EV-08-02 de SUJ-08-A con SEL-08 PENDIENTE | 1. E5 `POST …/subpruebas/SEL-08/iniciar` como SUJ-08-A.<br>2. Leer los eventos de EV-08-02. | Un evento de inicio con `idEvaluacion` de EV-08-02 e `idEvaluacionSubprueba` de SEL-08. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-003 — Guardar una respuesta (E7)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-003 | GEN-08-TEST: respuestas registradas.<br>CU-BFA-P06: "Audita y confirma el guardado; un cambio pendiente nunca se muestra como aceptado." | EV-08-03 con SEL-08 ACTIVA | 1. E7 `PUT …/subpruebas/SEL-08/respuestas` con una respuesta.<br>2. Leer los eventos de EV-08-03. | Paso 1: guardado confirmado. Paso 2: un evento del guardado con `idEvaluacion`, `idEvaluacionSubprueba` y `ocurridoEn`. Si se repite el mismo E7, el número de eventos no se fija (C-3). | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-004 — Cambiar una respuesta conserva el historial

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-004 | RF-GEN-13: "Permitir sustituir o anular una respuesta antes del cierre, conservando solo la vigente para calificación y el historial para auditoría." | EV-08-04 con SEL-08 ACTIVA y una respuesta guardada | 1. E7 cambiando la opción de la misma pregunta.<br>2. Leer los eventos de EV-08-04. | Hay un evento por el guardado original y otro por el cambio. El primero no se modifica. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-005 — Cierre por vencimiento

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-005 | GEN-08-TEST: cierre registrado.<br>CU-GEN-13: "Registra respuestas confirmadas, instante y causa de cierre…"<br>CU-BFA-P07: "Cierra una sola vez, registra instante/causa y bloquea cambios nuevos; conserva lo ya aceptado." | EV-08-05 con SEL-08 ACTIVA y duración corta | 1. Esperar el vencimiento sin enviar nada.<br>2. E4 `GET /api/v1/evaluaciones/{id}`.<br>3. Leer los eventos de EV-08-05. | Un evento de cierre de SEL-08 con su instante y la causa (vencimiento). | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-006 — Calificación de la subprueba

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-006 | GEN-08-TEST: calificación registrada.<br>RNF-BFA-GEN-08: "Eventos de inicio, respuestas, cierre, recuperación, calificación, consulta y administración deben poder relacionarse con el intento correspondiente." | EV-08-05 después del cierre (TC-GEN-08-005) | 1. Leer los eventos de EV-08-05. | Un evento de calificación de SEL-08 relacionado con EV-08-05, con `ocurridoEn`. | Manual (SQL) | **BLOQUEADO** |

### TC-GEN-08-007 — Finalizar la evaluación (E8)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-007 | GEN-08-BE: "Los eventos definidos quedan registrados correctamente en la base de datos."<br>RF-GEN-20: "Registrar responsable, instante y motivo de cada operación que afecte cuentas, configuración, intentos, revisiones o resultados." | EV-08-07 con todas sus subpruebas cerradas | 1. E8 `POST …/finalizar`.<br>2. Leer los eventos de EV-08-07. | Un evento de finalización con `idEvaluacion`, `actorRef` y `ocurridoEn`. Si se repite E8, el número de eventos no se fija (C-3). | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-008 — Recuperación después de una interrupción

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-008 | GEN-08-TEST: recuperación registrada.<br>CU-GEN-19: "Registra la interrupción y la reanudación con su instante (RF-GEN-20)."<br>CU-BFA-P04: "Audita interrupción, recuperación y motivo de denegación cuando corresponda." | EV-08-08 con SEL-08 ACTIVA y una respuesta guardada | 1. Cerrar el navegador o la sesión.<br>2. Volver a entrar como SUJ-08-A y llamar E4.<br>3. Leer los eventos de EV-08-08. | Eventos de interrupción y reanudación de EV-08-08, cada uno con su instante. | Manual (navegador o Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-009 — Consultar resultados (E9)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-009 | GEN-08-TEST: consulta registrada.<br>CU-GEN-15: "Registra el acceso con el alcance permitido." · "la consulta no modifica respuestas ni puntuaciones y queda registrada."<br>CU-BFA-P09: "Registra la consulta sin recalcular, crear intentos ni exponer claves o decisiones clínicas restringidas." | EV-08-07 finalizada; usuario con permiso para ver su resultado | 1. E9 `GET …/resultados`.<br>2. Leer los eventos de EV-08-07. | Un evento de consulta con el `actorRef` de quien consultó y `ocurridoEn`. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-010 — Decisión sobre una palabra de FV (E10)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-010 | E10 (DOC-05): "Audita actor y cambio…"<br>CU-BFA-P10: "Registra decisión y motivo por cada entrada…" | EV-08-10 con FV-08 cerrada y palabras pendientes; PSI-08 con sesión | 1. E10 `PUT …/palabras/{respuestaId}/revision` con una decisión y su motivo.<br>2. Leer los eventos de EV-08-10. | Un evento con `actorRef` de PSI-08, la palabra afectada y el motivo en `detalle`. Alcance según C-5. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-011 — Recálculo (E11)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-011 | CU-BFA-P11: "Registra diferencias, versiones, autor, fecha y motivo." | EV-08-10 con un cálculo previo; PSI-08 con sesión | 1. E11 `POST …/recalculos` con un motivo.<br>2. Leer los eventos de EV-08-10. | Un evento del recálculo con `actorRef` de PSI-08, `ocurridoEn` y el motivo. Alcance según C-5. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-012 — Publicar una revisión (E12)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-012 | CU-BFA-P12: "Registra revisión publicada, alcance, responsable e instante; conserva publicaciones previas como historial." | EV-08-10 con una revisión completada y validada; PSI-08 con sesión | 1. E12 `PUT …/resultados/vigente`.<br>2. Leer los eventos de EV-08-10. | Un evento de publicación con `actorRef` de PSI-08 y `ocurridoEn`. Alcance según C-5. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-013 — Operación administrativa sin evaluación (E31)

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-013 | RF-GEN-20: operaciones que afecten "configuración".<br>ENT-COM-11: "Un evento administrativo global puede no tener evaluación y se vincula por tipo/ID." | Una versión ficticia completa en BORRADOR; COO-08 con sesión | 1. E31 `POST /api/v1/versiones/{id}/publicar`.<br>2. Leer el último evento registrado. | Un evento sin `idEvaluacion`, con `entidadTipo` y `entidadId` de la versión publicada, `actorRef` de COO-08 y `ocurridoEn`. | Manual (Postman + SQL) | **BLOQUEADO** |

## Casos — Contenido del evento

### TC-GEN-08-014 — Campos de cada evento

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-014 | GEN-08-BE: "Cada evento conserva su tipo, fecha, evaluación relacionada e identidad del actor cuando corresponda."<br>ENT-COM-11 (atributos obligatorios). | Eventos generados en TC-GEN-08-001 a 013 | 1. Revisar cada evento. | Todos tienen `idEvento` único, `actorRef`, `accion`, `entidadTipo`, `entidadId` y `ocurridoEn`. Los de ejecución también tienen `idEvaluacion`. El código exacto de `accion` se revisa contra C-1. | Manual (SQL) | **BLOQUEADO** |

### TC-GEN-08-015 — La hora la pone el servidor

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-015 | ENT-COM-11: `ocurridoEn` "Momento oficial." · "Servidor." | EV-08-15 con SEL-08 ACTIVA | 1. Enviar un E7 y anotar la cabecera `Date` de la respuesta.<br>2. Leer el evento del guardado. | `ocurridoEn` corresponde a la hora del servidor de esa operación, no a un valor enviado por el cliente. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-016 — Evento de ejecución sin evaluación

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-016 | ENT-COM-11: `idEvaluacion` "Obligatoria en ejecución/resultado; no en administración global." | Componente de auditoría del PR | 1. Intentar registrar un evento de ejecución sin `idEvaluacion` mediante el componente (prueba o servicio).<br>2. Intentar lo mismo con un `INSERT` directo en la base. | Paso 1: se rechaza. Paso 2: se anota si la base lo acepta o no; lo que se espera ahí depende de C-4. | Manual (código + SQL) | **BLOQUEADO** |

### TC-GEN-08-017 — Subprueba de otra evaluación

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-017 | ENT-COM-11: `idEvaluacionSubprueba` "De la misma evaluación." | EV-08-17A y EV-08-17B, cada una con SEL-08 | 1. Intentar registrar un evento con `idEvaluacion` de EV-08-17A e `idEvaluacionSubprueba` de EV-08-17B mediante el componente.<br>2. Intentar lo mismo con un `INSERT` directo. | Paso 1: se rechaza. Paso 2: se anota el resultado; lo que se espera depende de C-4. | Manual (código + SQL) | **BLOQUEADO** |

## Casos — Reconstrucción

### TC-GEN-08-018 — Reconstruir una evaluación completa

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-018 | GEN-08-BE: "Los registros permiten reconstruir las operaciones relevantes realizadas durante una evaluación."<br>§5.1.7: "Registra inicio, respuestas, cierres, recuperación, cálculo y consultas, para poder reproducir un resultado" | ACA-08 y SUJ-08-A; VF-08 | 1. Hacer el flujo completo: E3, E5, dos E7, recarga y E4, cierre, E8 y E9.<br>2. Leer los eventos de esa evaluación ordenados por `ocurridoEn`. | La lista muestra cada paso en el mismo orden en que se hizo, todos con el mismo `idEvaluacion`, y con eso se puede reconstruir el flujo sin mirar otras tablas. | Manual (Postman + SQL); candidato e2e | **BLOQUEADO** |

### TC-GEN-08-019 — Dos evaluaciones no se mezclan

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-019 | RNF-BFA-GEN-08: "deben poder relacionarse con el intento correspondiente." | EV-08-19A de SUJ-08-A y EV-08-19B de SUJ-08-B, en curso al mismo tiempo | 1. Alternar E5 y E7 entre las dos evaluaciones.<br>2. Leer los eventos de cada una. | Cada evento queda con la evaluación y la subprueba en que ocurrió; ninguno de A aparece en B ni al revés. | Manual (Postman + SQL) | **BLOQUEADO** |

## Casos — Inmutabilidad

### TC-GEN-08-020 — No hay forma de editar ni borrar eventos

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-020 | ENT-COM-11: "No editar/borrar mediante flujo ordinario." | Código del PR y contrato | 1. Revisar el contrato y los controladores.<br>2. Revisar el puerto, el repositorio y el servicio de auditoría. | Ninguna ruta, servicio ni método del puerto permite editar o borrar un evento. Que la base también lo impida depende de C-4. | Revisión de código | **BLOQUEADO** |

### TC-GEN-08-021 — Una corrección crea otro evento

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-021 | ENT-COM-11: "Una corrección produce otro evento."<br>CU-BFA-P10: "Corrección posterior: registrar otra revisión y motivo; no sobrescribir la anterior." | La decisión de TC-GEN-08-010 ya registrada | 1. E10 sobre la misma palabra con una decisión distinta y otro motivo.<br>2. Leer los eventos de esa palabra. | Hay dos eventos: el original sigue igual y el nuevo tiene la corrección y su motivo. | Manual (Postman + SQL) | **BLOQUEADO** |

## Casos — La auditoría no altera nada

### TC-GEN-08-022 — Respuestas, puntajes y estados no cambian

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-022 | GEN-08-BE: "La auditoría no altera las respuestas, puntuaciones ni estados originales."<br>CU-GEN-15: "la consulta no modifica respuestas ni puntuaciones y queda registrada." | La evaluación de TC-GEN-08-018, ya finalizada | 1. Guardar una copia de E4 y E9.<br>2. Llamar E4 y E9 tres veces más.<br>3. Comparar con la copia y leer los eventos. | E4 y E9 devuelven lo mismo que en la copia: respuestas, puntajes y estados no cambian. Solo aparecen eventos nuevos de consulta. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-023 — El cierre se registra una sola vez

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-023 | CU-GEN-13: "el cierre ocurre en inicio + D aunque el sujeto recargue o cambie de sesión, y se registra un único evento de cierre."<br>CU-BFA-P07: "Cierre repetido: devolver el mismo cierre y puntaje ya registrado." | EV-08-23 con SEL-08 ACTIVA y duración corta | 1. Dejar que venza.<br>2. Recargar y llamar E4 varias veces, también desde otra sesión.<br>3. Leer los eventos de cierre de SEL-08. | Exactamente un evento de cierre de SEL-08. | Manual (Postman + SQL) | **BLOQUEADO** |

## Casos — Sin datos sensibles

### TC-GEN-08-024 — Sin contraseñas ni tokens

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-024 | GEN-08-BE: "No se almacenan contraseñas, claves de corrección ni datos sensibles innecesarios."<br>ENT-COM-11: `detalle` "Sin secretos o contenido sensible innecesario." | Cuenta ficticia con una contraseña de prueba conocida | 1. E14 (abrir sesión) y E17 (cambiar contraseña con el token de prueba).<br>2. Buscar en todos los eventos la contraseña de prueba, la nueva y el token. | Ninguno de esos textos aparece en ningún evento. Si GEN-08-BE no registra operaciones de cuenta, se revisa igual que no aparezcan y se anota. | Manual (Postman + SQL) | **BLOQUEADO** |

### TC-GEN-08-025 — Sin claves de corrección

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-025 | GEN-08-BE: "No se almacenan contraseñas, claves de corrección ni datos sensibles innecesarios."<br>CA-09: "ninguna respuesta del catálogo/evaluado entrega claves…" | Eventos de TC-GEN-08-003 a 007 y 018; clave ficticia de VF-08 | 1. Revisar el `detalle` de los eventos de respuestas, cierre y calificación. | Ningún evento incluye la clave de corrección ni cuál era la opción correcta. | Manual (SQL) | **BLOQUEADO** |

## Casos — Acceso

### TC-GEN-08-026 — Ninguna ruta expone la auditoría

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-026 | GEN-08-BE: "El acceso a los registros está restringido a componentes y usuarios autorizados."<br>RNF-BFA-GEN-05. | Código del PR y `docs/api/openapi.yaml` | 1. Revisar que ningún controlador ni respuesta de E1 a E31 devuelva eventos de auditoría.<br>2. Si el PR agrega una ruta para leerlos, probarla sin sesión y con SUJ-08-A. | Paso 1: ninguna respuesta incluye eventos. Paso 2: sin sesión, 401 NO_AUTENTICADO; con un Sujeto, 403 SIN_PERMISO. Quién sí puede leerla se revisa contra C-2. | Revisión de código; Postman si hay ruta | **BLOQUEADO** |

### TC-GEN-08-027 — Un acceso denegado queda auditado

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-027 | CU-GEN-19 A3: "Identidad o alcance incorrectos: denegar y auditar."<br>CU-GEN-04: "Devuelve autorización o denegación y registra la operación sensible." | EV-08-27 de SUJ-08-A en curso; SUJ-08-B con sesión | 1. E4 de EV-08-27 como SUJ-08-B.<br>2. Leer los eventos de EV-08-27. | Paso 1: 403 SIN_PERMISO. Paso 2: un evento de la denegación con `actorRef` de SUJ-08-B y `ocurridoEn`; EV-08-27 no cambia. | Manual (Postman + SQL) | **BLOQUEADO** |

## Casos — Pruebas automatizadas y contrato

### TC-GEN-08-028 — Pruebas automatizadas del PR

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-028 | GEN-08-BE: "Las pruebas automatizadas verifican el registro y la integridad de los eventos." | CI del SHA probado | 1. Revisar el job `backend-build` (`mvn verify`).<br>2. Revisar qué prueban las pruebas nuevas de auditoría. | `BUILD SUCCESS` sin fallos, y hay pruebas que verifican tanto el registro de eventos como su integridad. | CI y revisión de código | **BLOQUEADO** |

### TC-GEN-08-029 — El flujo sigue cumpliendo el contrato

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Manual o e2e | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-08-029 | GEN-08-TEST: "Esquemas, códigos HTTP, campos y errores cumplen el contrato aprobado." | Rutas del flujo implementadas en `main` y en el PR | 1. Ejecutar en Postman las rutas del flujo que toque el PR, en éxito y en error.<br>2. Comparar con `docs/api/openapi.yaml`. | Mismos códigos, campos y formato de error que el contrato. Integrar la auditoría no agrega ni quita campos en las respuestas. | Manual (Postman) | **BLOQUEADO** |

## Bloqueos para ejecutar

| Bloqueo | Responsable | Próximo paso |
| --- | --- | --- |
| GEN-08-BE no tiene PR ni rama | Desarrollo (Emilio Carranza y Gessler Herrera) | Probar en `pull/<n>/merge` cuando se abra |
| El flujo E3 a E9 no está en `main` | Desarrollo e Integración (GEN-02 a GEN-05) | Sin esas rutas no hay operaciones que auditar |
| E10 a E12 y E31 sin implementar | BFA-BE y GEN-07-BE | Casos 010 a 013 y 021; alcance según C-5 |
| Catálogo de acciones, lectura de la auditoría e inmutabilidad en base sin definir | Documentación | Consultas C-1 a C-4 |
| DOC-07 en revisión | Documentación | Dependencia de GEN-08-TEST en ClickUp |

## Registro de ejecución

| Fecha | PR/SHA | Tester | Resultado | Evidencia / bug |
| --- | --- | --- | --- | --- |
| — | — | — | Sin ejecutar: 29 BLOQUEADO | — |

### Revisión final

- [x] Cada caso corresponde a un criterio aprobado y a la tarea correcta.
- [ ] Se ejecutó contra el commit vigente del PR.
- [ ] Se registraron resultados reales y evidencia verificable.
- [x] No contiene secretos ni datos personales reales.
- [ ] Se comentó al PR y se informó a Integración.
- [ ] Solo después de aprobar, se colocó o solicitó `testing-ok` cuando corresponde.
