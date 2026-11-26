# Casos de prueba GEN-05 — consulta de resultados (v1)

## Identificación

- **Tarea de Testing:** [GEN-05-TEST](https://app.clickup.com/t/86bcx2z68). Responsable desde el reparto del 19-nov: Francisco Israel Ferrufino, en relevo de Adrián Vallecillo.
- **Historia:** [GEN-05](https://app.clickup.com/t/86bcvah5m) · subtareas `GEN-05-FE` y `GEN-05-BE`.
- **Tester:** Francisco Israel Ferrufino James
- **Versión y fecha:** v1 · 2026-11-19
- **Código probado:** `main` `e7eb264`: pantalla `frontend/src/features/resultados/` y `services/resultados*`, con los mocks por defecto. La pantalla (PR #28, GEN-05-FE) ya está en `main`. El backend (PR #73, GEN-05-BE) está abierto y se prueba aparte.
- **Alcance de esta versión:** la pantalla de resultados, sus estados y sus errores, con datos simulados. **No** valida el backend real ni los permisos reales.
- **Resultado:** **19 PASÓ · 0 FALLÓ · 4 BLOQUEADO**. **No** se pone `testing-ok` en esta versión, porque el código de la pantalla ya está en `main` y no hay un PR de frontend vigente. `testing-ok` se pondrá en el PR #73 cuando se pueda probar.

## Historial de cambios

| Versión | Fecha | Cambios |
| --- | --- | --- |
| v1 | 2026-11-20 | Casos de la pantalla de resultados (estados, errores y datos protegidos) y su ejecución sobre `main` con mocks. Casos de backend bloqueados. |

## Fuentes y estado

| Fuente | Estado | Uso en este documento |
| --- | --- | --- |
| Criterios de GEN-05-TEST, GEN-05-FE y GEN-05-BE (ClickUp) e indicaciones del 19-nov | Publicados en las tareas | Alcance. **Antes de subir el PR, compara la redacción de la columna «CA cubierto» con la de ClickUp.** |
| `docs/pruebas/PLAN-OPERATIVO-TESTING.md` §GEN-05-TEST | En `main` | Casos que pide la tarea: consulta autorizada, acceso denegado a otro usuario, estado pendiente, resultados no calculables y que la interfaz no muestre datos protegidos |
| `docs/api/openapi.yaml`: E9 `consultarResultados` y `ResultadoResponse` (Documento BFA §5.3, CU-BFA-P09) | **Aprobado** (DOC-05) | Estados, campos nulos y códigos de error 401, 403, 404 y 409 |
| Documento general §4.4 (un resultado pendiente no se presenta como definitivo) y §5.2.2 (formato de error) | **Aprobado** | Estados pendientes y categorías de error |
| README de GEN-05-BE en el PR #73 | PR abierto | Orden de comprobaciones del backend y adaptadores provisionales (C-1, C-2) |

Los resultados esperados salen del contrato y de los criterios. Los textos de pantalla en «Obtenido» son lo observado, no un requisito.

## Matriz de cobertura

| Qué pide GEN-05-TEST | Casos | Estado |
| --- | --- | --- |
| Consulta autorizada de resultados | TC-GEN-05-006 a 009, 012 (interfaz); 022 (backend) | PASÓ en interfaz · BLOQUEADO en backend |
| Denegación de acceso a otro usuario | TC-GEN-05-015 (interfaz); 020, 021 (reales) | PASÓ en interfaz · BLOQUEADO en backend |
| Estado pendiente | TC-GEN-05-010, 011 | PASÓ |
| Resultados no calculables | TC-GEN-05-008, 009 | PASÓ |
| La interfaz no presenta datos protegidos | TC-GEN-05-015, 018 | PASÓ |
| Errores del contrato (409, 404, 500, sin conexión) | TC-GEN-05-013, 014, 016, 017 | PASÓ |
| Validación de la entrada y estado de carga | TC-GEN-05-001 a 005 | PASÓ |
| El mock y el cliente real cumplen el mismo contrato | TC-GEN-05-019 | PASÓ |
| Revisión vigente exacta, no la de número mayor (CU-BFA-P09) | TC-GEN-05-023 | BLOQUEADO |

## Consultas pendientes

| ID | Pregunta | Para | Afecta |
| --- | --- | --- | --- |
| C-1 | El README del PR #73 dice que si E8 aún no guardó el resultado se responde `CALCULANDO` con **`resultadoId` nulo**. En `openapi.yaml` `resultadoId` no es `nullable`, y el tipo del frontend lo declara `number`. ¿Se agrega `nullable: true` al contrato, o el backend debe enviar siempre un `resultadoId`? | Documentación (DOC-05) y GEN-05-BE | TC-GEN-05-022; la pantalla muestra «—» y no se rompe, pero el contrato no coincide |
| C-2 | En el PR #73 la autorización es provisional y **niega toda consulta**, y la persistencia es provisional. Con el backend real hoy solo se puede verificar 400, 401 y 403. ¿Cuándo estarán GEN-06 (sesión, E14) y las tablas de GEN-02-BE (#55) y GEN-04-BE (#64)? | GEN-05-BE, Integración (Kisnner) | TC-GEN-05-020 a 023 |

## Datos de prueba (ficticios)

Son los del mock `services/resultados.mock.ts`. Los números son ficticios y no son conversiones de ningún baremo. No hay datos personales reales.

| Evaluación | Respuesta simulada | Usado en |
| --- | --- | --- |
| 16 | `DISPONIBLE`, revisión 1, calificación 31: VOC1, VOC2 y VOCT convertidos; R1 sin coincidencia; CV con puntuación 0 y sin baremo validado; FV y EIV no calculables | 004 a 009, 012, 018 |
| 17 | `PENDIENTE_VALIDACION`, sin revisión publicada, `factores` vacío | 005, 010, 018 |
| 18 | `CALCULANDO`, sin revisión publicada | 011, 018 |
| 15 | `409 EVALUACION_NO_FINALIZADA` | 013 |
| 99 (o cualquier otro) | `404 EVALUACION_NO_ENCONTRADA` | 014 |
| 403, 500, 503 | `SIN_PERMISO`, `ERROR_INTERNO` y fallo de conexión | 015 a 017 |

## Entorno

- **Código:** `main` `e7eb264`. En `frontend/`: `npm ci` y `npm run dev`, sin `.env.local`, así que se usan los mocks (`VITE_USAR_MOCKS` distinto de `false`).
- **Máquina:** Windows, Git Bash, Node 22+, Vite 7.3.7, Playwright con Chromium.
- **Navegador:** recorrido con Chromium (Playwright) usando el script [`GEN-05-recorrido-interfaz.js`](../evidencias/GEN-05/GEN-05-recorrido-interfaz.js). Cada caso abre una página nueva. El mock tarda unos 700 ms en responder.
- **Evidencia:** `docs/evidencias/GEN-05/GEN-05-ejecucion-2026-11-20.txt`, `GEN-05-resultados-2026-11-20.json`, `GEN-05-vitest-2026-11-20.txt` y una captura por caso en `docs/evidencias/GEN-05/capturas/`.

## Casos — Búsqueda y validación

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Obtenido | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-05-001 | GEN-05-FE: errores de validación · E9 `400 SOLICITUD_INVALIDA` | `/resultados`, campo vacío | 1. «Consultar». | Error en el campo y ninguna consulta. | «Ingrese el ID de la evaluación.» bajo el campo, marcado aria-invalid; no consulta. | PASÓ |
| TC-GEN-05-002 | GEN-05-FE: errores de validación | `abc`, `0`, `-5`, `1.5` | 1. Escribir cada valor y «Consultar». | Error de formato (entero positivo) y ninguna consulta. | «El ID de la evaluación debe ser un número entero positivo.» con abc, 0, -5 y 1.5; no consulta. | PASÓ |
| TC-GEN-05-003 | GEN-05-FE: errores de validación | URL `/evaluaciones/abc/resultados` | 1. Abrir la URL. | Error de formato y ninguna consulta. | Error de formato en /evaluaciones/abc/resultados; ninguna consulta. | PASÓ |
| TC-GEN-05-004 | GEN-05-FE: la interfaz solo muestra lo que confirma el backend | Evaluación 16 | 1. «Consultar». 2. Observar antes de 700 ms. | Indicador de carga, formulario deshabilitado y ningún resultado hasta que llega la respuesta. | «Consultando resultados…», campo y botón deshabilitados; el resultado aparece solo al responder. | PASÓ |
| TC-GEN-05-005 | GEN-05-FE: no mezclar resultados de otra evaluación | Resultado de 16 en pantalla | 1. Escribir 17 sin consultar. | El resultado de 16 desaparece. | Al escribir 17 desaparece el resultado de la evaluación 16. | PASÓ |

## Casos — Estados del resultado

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Obtenido | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-05-006 | GEN-05-TEST: consulta autorizada · E9 200 `DISPONIBLE` con revisión vigente | Evaluación 16 | 1. «Consultar». | Estado disponible, revisión 1, calificación 31, versión de reglas; los 7 factores en el orden recibido; aviso de datos simulados. | «Resultado disponible», revisión 1, calificación 31, BFA-REGLAS-v1; 7 factores en orden VOC1, VOC2, VOCT, R1, CV, FV, EIV; aviso de datos simulados. | PASÓ |
| TC-GEN-05-007 | E9: `percentil` solo con un rango exacto de baremo aprobado | Evaluación 16, VOC1 y VOCT | 1. Revisar sus filas. | Puntuación, percentil y «Convertido», tal como los envía el servidor (VOC1: 20 / 50; VOCT: 38 / 50). | VOC1: 20 · Calculado · 50 · Convertido · motivo «—». VOCT: 38 · 50 · Convertido. | PASÓ |
| TC-GEN-05-008 | GEN-05-TEST: resultados no calculables · E9: calculado sin conversión; un 0 es un valor | Evaluación 16, R1 y CV | 1. Revisar sus filas. | R1: 12, percentil «—», sin coincidencia, con motivo. CV: **0** (no «—»), percentil «—», sin baremo validado, con motivo. | R1: 12 · Calculado · «—» · Sin coincidencia en el baremo · «La puntuación no coincide con un rango exacto del baremo.» CV: **0** · Calculado · «—» · Sin baremo validado · con motivo. | PASÓ |
| TC-GEN-05-009 | GEN-05-TEST: resultados no calculables · E9: `puntuacionDirecta` nula si no es calculable | Evaluación 16, FV y EIV | 1. Revisar sus filas. | «No calculable» en lugar de un número, percentil «—» y motivo visible. | FV y EIV: «No calculable» en puntuación y cálculo, percentil «—», «No aplica»; motivos «Subprueba no incluida en la aplicación parcial.» y «Falta un componente: FV.» | PASÓ |
| TC-GEN-05-010 | GEN-05-TEST: estado pendiente · E9: sin revisión publicada · Documento general §4.4 | Evaluación 17 | 1. «Consultar». | «Pendiente de validación», revisión y calificación «—», ninguna puntuación ni percentil. | «Pendiente de validación», revisión y calificación «—», sin tabla: «No hay una revisión publicada; no se muestran puntuaciones ni percentiles.» | PASÓ |
| TC-GEN-05-011 | GEN-05-TEST: estado pendiente · E9 `CALCULANDO` | Evaluación 18 | 1. «Consultar». | «En cálculo», sin revisión ni factores. | «En cálculo», sin revisión ni tabla. | PASÓ |
| TC-GEN-05-012 | GEN-05-FE: consulta directa por ruta | URL `/evaluaciones/16/resultados` | 1. Abrir la URL. | Consulta sola al abrir y muestra el resultado de 16. | Consulta al abrir: campo con 16 y resultado disponible con 7 factores. | PASÓ |

## Casos — Errores

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Obtenido | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-05-013 | E9 `409 EVALUACION_NO_FINALIZADA` | Evaluación 15 | 1. «Consultar». | Aviso de que la evaluación no ha finalizado; ningún dato ni «Reintentar». | Aviso «La evaluación aún no ha finalizado. Los resultados se podrán consultar cuando se finalice.», sin Reintentar ni datos. | PASÓ |
| TC-GEN-05-014 | E9 `404 EVALUACION_NO_ENCONTRADA` | Evaluación 99 | 1. «Consultar». | Aviso de no encontrada; ningún dato. | «No encontrado. No existe una evaluación con ese ID.», sin Reintentar ni datos. | PASÓ |
| TC-GEN-05-015 | GEN-05-TEST: denegación de acceso · E9 `403 SIN_PERMISO` | Evaluación 403 | 1. «Consultar». | «Acceso denegado» y ningún dato de la evaluación ni de la revisión. | «Acceso denegado. No tiene permiso para consultar los resultados de esta evaluación.»; ningún dato. | PASÓ |
| TC-GEN-05-016 | §5.2.2 `500 ERROR_INTERNO` | Evaluación 500 | 1. «Consultar». 2. «Reintentar». | Aviso de error del servidor; «Reintentar» vuelve a consultar. | «Error del servidor. Ocurrió un error en el servidor. Intente de nuevo más tarde.»; Reintentar vuelve a consultar. | PASÓ |
| TC-GEN-05-017 | GEN-05-FE: errores de conexión | Evaluación 503 | 1. «Consultar». | Aviso de conexión con «Reintentar»; ningún dato. | «Sin conexión. No se pudo conectar con el servidor. Revise su conexión e intente de nuevo.», con Reintentar. | PASÓ |

## Casos — Datos protegidos

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Obtenido | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-05-018 | GEN-05-TEST: la interfaz no presenta datos protegidos · E9: «Nunca entrega el contenido de la clave» | Evaluaciones 16, 17 y 18 | 1. Abrir cada una. 2. Revisar el texto de la página. | Solo identificadores (Clave = número). Ni contenido de la clave, ni respuestas correctas, ni datos del evaluado. | En 16, 17 y 18 solo identificadores (Clave = 4); ni contenido de la clave, ni respuestas correctas, ni datos del evaluado. | PASÓ |

## Casos — Pruebas automáticas

| ID | CA cubierto | Datos y precondiciones | Pasos | Resultado esperado | Obtenido | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-GEN-05-019 | GEN-05-FE: la interfaz se desarrolla con mocks de DOC-05 y se integra después con GEN-05-BE | `main` `e7eb264` | 1. En `frontend/`: `npx vitest run src/features/resultados src/services/resultados.mock.test.ts src/services/resultados.test.ts`. | Todas pasan (5 archivos de prueba). | 5 archivos, 28 pruebas: todas pasaron (vitest 4.1.12). | PASÓ |

## Casos bloqueados — Backend real (PR #73)

| ID | CA cubierto | Qué falta | Estado |
| --- | --- | --- | --- |
| TC-GEN-05-020 | E9 `401 NO_AUTENTICADO` | El mock no lo simula. Con el #73 en `pull/73/merge` ya se puede probar sin sesión en Postman; la pantalla real necesita GEN-06. | **BLOQUEADO** (parcial) |
| TC-GEN-05-021 | GEN-05-TEST: denegación de acceso a otro usuario o fuera de alcance | Sesión y permisos reales (GEN-06, E14). Hoy el adaptador provisional del #73 niega a todos, así que un 403 no prueba nada del alcance (C-2). | **BLOQUEADO** |
| TC-GEN-05-022 | GEN-05-BE: E9 200 en `DISPONIBLE`, `PENDIENTE_VALIDACION` y `CALCULANDO` | Persistencia real (tablas de #55 y #64) y autorización real. Ver también C-1. | **BLOQUEADO** |
| TC-GEN-05-023 | CU-BFA-P09: se devuelve la revisión vigente exacta, nunca la de número mayor | Persistencia de revisiones (GEN-04-BE, #64) y publicación E12. | **BLOQUEADO** |

## Observaciones (no bloquean)

| ID | Observación | Dónde |
| --- | --- | --- |
| O-1 | El mock no tiene un factor con `estadoCalculo` `PENDIENTE_VALIDACION` dentro de un resultado `DISPONIBLE`, ni un estado desconocido. En la pantalla no se pueden ver sin cambiar el mock, y Testing no cambia código. Las pruebas unitarias de `presentacion.test.ts` sí cubren el estado desconocido. | `services/resultados.mock.ts` |
| O-2 | La consulta de GEN-05 se apoya en los mensajes de error comunes de GEN-02 (`features/evaluacion/mensajesError.ts`). Un cambio allí afecta esta pantalla; conviene repetir TC-GEN-05-013 a 017 si cambia. | `features/resultados/errores.ts` |

## Registro de ejecución

| Fecha | Código | Tester | Resultado | Evidencia / bug |
| --- | --- | --- | --- | --- |
| 2026-11-20 | `main` `e7eb264`, con mocks | Francisco Ferrufino | 19 PASÓ · 0 FALLÓ · 4 BLOQUEADO | [Salida del recorrido](../evidencias/GEN-05/GEN-05-ejecucion-2026-11-20.txt) · [Resultados JSON](../evidencias/GEN-05/GEN-05-resultados-2026-11-20.json) · [Salida de vitest](../evidencias/GEN-05/GEN-05-vitest-2026-11-20.txt) · [Capturas](../evidencias/GEN-05/capturas/) · [Script](../evidencias/GEN-05/GEN-05-recorrido-interfaz.js) |

### Revisión final

- [x] Cada caso corresponde a un criterio aprobado y a la tarea correcta.
- [x] Se ejecutó contra el commit vigente de `main`.
- [x] Se registraron resultados reales y evidencia verificable.
- [x] No contiene secretos ni datos personales reales.
- [ ] Se informó en GEN-05-TEST.
- [ ] `testing-ok`: no aplica en esta versión; se pondrá en el PR #73 cuando se desbloqueen 020 a 023.
