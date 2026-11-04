# Guía de Testing — Sistema Integrado de Evaluaciones

Esta guía complementa las tareas `GEN-xx-TEST`, `BFA-TEST-xx`, `TEST-01` y `TEST-02` de ClickUp. **No sustituye** los requisitos ni contratos aprobados. Si una especificación sigue `in review`, pide aclaración antes de declarar un resultado como válido.

## 1. Responsabilidades y orden

1. **Desarrollo** abre un Pull Request (PR) con código de tarea de ClickUp, explicación de cambios y forma de probarlo.
2. **Testing** prepara casos con datos ficticios, revisa el PR y ejecuta pruebas contra el código del PR (no basta con probar una versión antigua de `main`).
3. Si falla, el tester comenta en el PR y registra un defecto en la lista **Bugs** de ClickUp. No aplica `testing-ok`.
4. Si cumple los criterios documentados, deja un comentario de conformidad con evidencia, número de PR y SHA del commit verificado. Un tester con permiso para gestionar etiquetas agrega `testing-ok` cuando el CI la requiera. Si no tiene permiso, pide a Integración que la aplique tras revisar la evidencia.
5. **Integración / CODEOWNERS** comprueba CI, evidencia, revisión y conversaciones antes de autorizar el `Squash and merge`. Testing no fusiona PR.

**Importante:** el workflow comprueba que exista `testing-ok`, pero no verifica quién la aplicó ni que Postman se haya ejecutado. La evidencia escrita es obligatoria. Si el desarrollador modifica el PR después de la aprobación de Testing, hay que repetir las pruebas afectadas y actualizar la conformidad antes de fusionar.

## 2. Qué preparar antes de que Desarrollo termine

- Lee tu tarea de ClickUp y sus criterios de aceptación.
- Revisa los requisitos, casos de uso y contratos API **aprobados** de DOC-02, DOC-04 y DOC-05; DOC-07 coordina la revisión. Para BFA, DOC-08 aporta material **ficticio**.
- Crea una tabla de casos `TC-...` usando [la plantilla](../docs/pruebas/PLANTILLA-CASO-PRUEBA.md) y cubre al menos un caso positivo, uno negativo y los límites relevantes.
- Incluye qué tarea o PR valida, entrada, resultado esperado y evidencia.
- Si falta contrato, dato de prueba o implementación, registra **BLOQUEADO** con enlace al impedimento; no inventes resultados.

Se pueden diseñar casos antes de que exista el PR; **ejecutarlos** requiere una versión comprobable. Las tareas `GEN-xx-TEST` y `BFA-TEST-xx` ya incluyen dependencias documentales en ClickUp. Las tareas de integración `GEN-xx-INT` y `BFA-INT-xx` esperan el resultado de Testing; no crees una dependencia inversa que forme un ciclo.

## 3. Probar un PR concreto

Anota el **número de PR**, su **SHA** y qué documentos/versiones aplican. Para pruebas locales contra la combinación propuesta del PR con `main`, consulta el procedimiento de la sección 3.5 del manual del proyecto.

Si dispones de Git y tienes acceso al repositorio, un ejemplo es:

```bash
git fetch origin pull/NUMERO_PR/merge:pr-test-NUMERO_PR
git switch pr-test-NUMERO_PR
```

Sustituye `NUMERO_PR` por el número real. Esta referencia puede no existir cuando el PR tiene conflictos; en ese caso solicita al desarrollador que lo actualice. No hagas push a `main`.

**Backend / API:** prepara una base de datos local de pruebas conforme al README de backend; usa Postman con una colección bajo `tests/api/`. Comprueba método, ruta, permisos, entrada, estado HTTP, JSON de salida y errores según el contrato **aprobado**. Usa variables como `{{base_url}}` y `{{token}}`, sin subir tokens ni credenciales reales.

**Frontend:** desde `frontend/`, ejecuta `npm ci` y `npm run dev` para revisar la interfaz en el navegador. Verifica navegación, validaciones, estados de carga, errores, teclado y respuesta observable. Ejecuta `npm run lint`, `npm run format:check`, `npm run typecheck`, `npm test` y `npm run build` cuando corresponda. El CI en verde no equivale a aprobación funcional.

**Ejecución integrada:** solo cuando las tareas bloqueantes estén listas y el entorno de integración autorizado, ejecuta `TEST-01-EJEC` o `TEST-02-EJEC` sobre `main`. No uses `release` como entorno de pruebas, no restablezcas bases compartidas ni publiques datos reales.

## 4. Archivos, convenciones y entregables

- `docs/pruebas/`: matrices de casos y resultados en Markdown. Nombra los casos `TC-GEN-01-001`, `TC-BFA-TEST-02-001`, etc.
- `tests/api/`: colecciones Postman versionadas; usa nombres como `GEN.postman_collection.json` y `BFA.postman_collection.json`. Exporta sin secretos ni datos reales.
- Capturas o logs: adjúntalos en ClickUp/PR cuando ayuden a reproducir un resultado; oculta nombres, contraseñas y tokens.
- En cada caso registra: **ID, tarea, PR/SHA, precondiciones, pasos, esperado, obtenido, estado (PASÓ / FALLÓ / BLOQUEADO), evidencia y fecha**.
- Si reportas un bug en **Bugs**: incluye título, severidad, pasos reproducibles, esperado, real, entorno/PR/SHA, evidencia y tarea de origen. Enlázalo desde el PR.
- Para pruebas de un cambio nuevo, usa una rama propia `test/<usuario>-<CODIGO>`, título PR `[<CODIGO>] descripción` y la referencia `Tarea ClickUp: <CODIGO> · CU-<id real>`. No inventes IDs.

## 5. Criterio para aprobar, rechazar o bloquear

| Estado | Cuándo usarlo | Acción |
| --- | --- | --- |
| PASÓ | Criterios verificados con evidencia contra el PR actual | Comentar conformidad; agregar/solicitar `testing-ok` cuando aplique |
| FALLÓ | El comportamiento contradice el criterio aprobado | Reportar bug; comentar el PR; no dar `testing-ok` |
| BLOQUEADO | Falta una especificación aprobada, ambiente, datos ficticios o código probables | Registrar bloqueo y responsable; no afirmar que pasó |
| NO APLICA | Caso fuera del alcance del PR | Justificarlo por escrito |

No apruebes por intuición ni solo porque `ci-ok` esté verde. Nunca alteres datos de usuarios reales para probar.

Consulta el **[reparto nominal y los casos por tarea](../docs/pruebas/PLAN-OPERATIVO-TESTING.md)** para las 18 tareas existentes, sus enlaces y precondiciones de ejecución.

## 6. Coordinación de tareas

- `GEN-01..08-TEST`: validan las tareas `GEN-01..08` y sus PR backend/frontend. Se reportan antes de `GEN-xx-INT`.
- `BFA-TEST-01..04`: validan subpruebas, cálculos, Fluidez Verbal y evaluación integral, respectivamente; se reportan antes de la integración BFA.
- `TEST-02-T4` y `TEST-02-T5`: seguridad/datos y recuperación/concurrencia; alimentan `TEST-02-EJEC`.
- `TEST-01` y `TEST-02` coordinan suites; `TEST-01-EJEC` y `TEST-02-EJEC` guardan la evidencia integrada final.

La tarea puede empezar con **diseño de casos** aunque la ejecución siga esperando la entrega de Desarrollo. Si hay un bloqueo, coméntalo en ClickUp y avisa a Scrum; evita marcar una tarea `completed` sin resultados verificables.

## 7. Formato breve de comentario al PR

```text
Testing: TC-GEN-XX-001, TC-GEN-XX-002 ejecutados contra PR #N, SHA abc123.
Resultado: PASÓ / FALLÓ / BLOQUEADO.
Evidencia: enlace a ClickUp o archivo de pruebas.
Incidencias: ninguna / enlace al bug.
Conformidad: aprobada para este SHA / pendiente de corrección.
```

**Referencia de tareas:** [Backlog de ClickUp](https://app.clickup.com/90141806675/v/l/li/901422672485) · [Guía Postman](api/README.md) · [Plantilla de casos](../docs/pruebas/PLANTILLA-CASO-PRUEBA.md).
