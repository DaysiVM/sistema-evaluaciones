# Plan operativo y reparto de Testing — Sistema de Evaluaciones

> Propuesta de asignación preparada por Scrum (04-11-2026). **Este archivo documenta la distribución, pero no modifica por sí solo las asignaciones en ClickUp.** Mantener las tareas existentes y sus criterios/dependencias; evitar duplicados. Validar con el equipo cuando ClickUp vuelva a permitir actualizaciones.

## 1. Reparto de las 18 tareas existentes

| Tester / usuario ClickUp | Tareas existentes que debe asumir |
| --- | --- |
| Víctor Manuel Gerardo Cabrera Gamez — `vmgcabrera@uamv.edu.ni` (ID `124365169`) | [GEN-01-TEST](https://app.clickup.com/t/86bcw9z30), [GEN-02-TEST](https://app.clickup.com/t/86bcw9z32), [TEST-01](https://app.clickup.com/t/86bcvajae) |
| Rodrigo Dariel Romero Gonzales — `rdromerog@uamv.edu.ni` (ID `124365176`) | [GEN-03-TEST](https://app.clickup.com/t/86bcx2xfn), [GEN-04-TEST](https://app.clickup.com/t/86bcx2z67), [TEST-01-EJEC](https://app.clickup.com/t/86bcvajag) |
| Adrian Enrique Vallecillo Zapata — `aevallecillo@uamv.edu.ni` (ID `124385006`) | [GEN-05-TEST](https://app.clickup.com/t/86bcx2z68), [GEN-06-TEST](https://app.clickup.com/t/86bcx2xfp), [TEST-02-T4](https://app.clickup.com/t/86bcw9z4w) |
| Jose Ariel Zeledon Iglesias — `jzeledoni@uamv.edu.ni` (ID `124365178`) | [GEN-07-TEST](https://app.clickup.com/t/86bcx2z6c), [GEN-08-TEST](https://app.clickup.com/t/86bcx2z6d), [TEST-02-T5](https://app.clickup.com/t/86bcw9z5c) |
| Samuel Benjamin Chavarria Baltodano — `schavarriab@uamv.edu.ni` (ID `124385003`) | [BFA-TEST-01](https://app.clickup.com/t/86bcw9z39) (matriz de 13 subpruebas; coordinar apoyo de pares) |
| Francisco Israel Ferrufino James — `fiferrufino@uamv.edu.ni` (ID `124384993`) | [BFA-TEST-02](https://app.clickup.com/t/86bcw9z3k), [BFA-TEST-03](https://app.clickup.com/t/86bcw9z4d) |
| Jose Francisco Lopez Mayorga — `jlopezm@uamv.edu.ni` (ID `124385000`) | [BFA-TEST-04](https://app.clickup.com/t/86bcw9z62), [TEST-02](https://app.clickup.com/t/86bcvajaj), [TEST-02-EJEC](https://app.clickup.com/t/86bcvajau) |

**Nota de carga:** Samuel cubre más familias funcionales aunque tenga una sola tarea; su responsabilidad es coordinar el inventario de casos y evidencias, no hacer solo las 13 validaciones complejas sin apoyo. Francisco y José Francisco pueden ayudar con revisiones cruzadas, sin perder el responsable único en ClickUp.

## 2. Requisitos, estados y bloqueos a mantener

Estos estados proceden de la última consulta de ClickUp (04-11-2026), no son aprobaciones nuevas:

- [DOC-02 — Requisitos](https://app.clickup.com/t/86bctt9b8): `completed`.
- [DOC-04 — Casos de uso](https://app.clickup.com/t/86bctt9cm): `in review`.
- [DOC-05 — Contratos API](https://app.clickup.com/t/86bctt9cy): `in review`.
- [DOC-07 — Calidad documental](https://app.clickup.com/t/86bctt9dn): `in progress`.
- [DOC-08 — Material ficticio BFA](https://app.clickup.com/t/86bcvaj7m): `Open`.

**Separar preparación de ejecución:**
- Preparar matriz, casos y datos ficticios: puede iniciarse ahora según lo que esté aprobado; no afirmar que pasó.
- Ejecutar pruebas de API/frontend y marcar `testing-ok`: requiere PR comprobable, contrato/requisitos aprobados y ambiente/datos preparados.
- `GEN-xx-TEST` y `BFA-TEST-xx` ya dependen de DOC-07; las BFA también de DOC-08. **No invertir las dependencias**: el integrador espera resultados de Testing y no al revés.
- `TEST-01-EJEC` espera los módulos GEN del flujo común; `TEST-02-EJEC` espera BFA, `TEST-01-EJEC`, seguridad y resiliencia. No ejecutar una suite integrada con funcionalidades que aún no están montadas.

El repositorio [documenta los contratos API como no implementados](https://github.com/Nell16/sistema-evaluaciones/blob/main/docs/api/DOC-05-API.md): las rutas y códigos de negocio deben verificarse contra el contrato aprobado en ClickUp, nunca inventarse tomando la plantilla actual como API funcional.

## 3. Instrucciones específicas para cada tarea (pegar como complemento sin borrar la descripción original)

### GEN-01-TEST — Víctor
- **Desarrollo relacionado:** [GEN-01](https://app.clickup.com/t/86bcvah4t) y sus PR `GEN-01-BE` / `GEN-01-FE`.
- **Casos mínimos:** catálogo con tests disponibles; consulta de detalle; identificador inexistente o entrada inválida; visualización del listado y estado vacío en navegador (si está contemplado en criterios aprobados).
- **Entregable:** `TC-GEN-01-...` en `docs/pruebas/`; colección de API en `tests/api/` si el PR tiene backend; capturas y resultado del PR/SHA.
- **Ejecución bloqueada por:** PR correspondiente y contratos aprobados; mantener dependencias existentes con DOC-07 y `GEN-01-INT`.

### GEN-02-TEST — Víctor
- **Desarrollo:** [GEN-02](https://app.clickup.com/t/86bcvah50), `GEN-02-BE/FE`.
- **Casos:** inicio exitoso, recuperación de intento, no duplicar evaluaciones ni reiniciar tiempo, error de entrada/estado y confirmación del backend antes de avanzar en frontend.
- **Entregable:** `TC-GEN-02-...`, evidencia Postman y navegador por cada PR/SHA.
- **Bloqueo de ejecución:** API/ambiente aprobados y PR disponible; luego se habilita `GEN-02-INT`.

### GEN-03-TEST — Rodrigo
- **Desarrollo:** [GEN-03](https://app.clickup.com/t/86bcvah55), `GEN-03-BE/FE`.
- **Casos:** presentación de pregunta, envío de respuesta, repetición de petición sin duplicado, rechazo de respuesta fuera de tiempo y coherencia del cronómetro con servidor.
- **Entregable:** `TC-GEN-03-...`, evidencias API/frontend y bug reproducible si falla.
- **Bloqueo:** contrato, criterio temporal y PR del módulo, antes de `GEN-03-INT`.

### GEN-04-TEST — Rodrigo
- **Desarrollo:** [GEN-04](https://app.clickup.com/t/86bcvah5d), `GEN-04-BE/FE`.
- **Casos:** finalización válida con subpruebas cerradas, rechazo de cierre incompleto y resultado no disponible presentado claramente como pendiente.
- **Entregable:** `TC-GEN-04-...` con evidencias y estados.
- **Bloqueo:** funciones de inicio/respuestas que use el cierre y PR disponible; reportar a `GEN-04-INT`.

### GEN-05-TEST — Adrián
- **Desarrollo:** [GEN-05](https://app.clickup.com/t/86bcvah5m), `GEN-05-BE/FE`.
- **Casos:** consulta autorizada de resultados; denegación de acceso a otro usuario; estado pendiente; resultados no calculables; interfaz que no presente datos protegidos.
- **Entregable:** casos de permisos y consulta, evidencia sin información personal real.
- **Bloqueo:** reglas de acceso/contratos aprobados y PR de resultados; coordinar con `GEN-06-TEST`.

### GEN-06-TEST — Adrián
- **Desarrollo:** [GEN-06](https://app.clickup.com/t/86bcvaj7e), `GEN-06-BE/FE`.
- **Casos:** sesión válida, operación protegida sin sesión, rol sin permisos, caducidad de sesión si está especificada y mensajes del frontend sin revelar secretos.
- **Entregable:** matriz actor × permiso con evidencia del PR/SHA y casos `TC-GEN-06-...`.
- **Bloqueo:** roles y contratos aprobados; nunca pedir contraseñas reales; antes de `GEN-06-INT`.

### GEN-07-TEST — José Ariel
- **Desarrollo:** [GEN-07](https://app.clickup.com/t/86bcx2z61), `GEN-07-BE`.
- **Casos:** crear nueva versión de formulario, impedir modificación de versión publicada, comprobar que una evaluación conserve su versión, validaciones de entrada.
- **Entregable:** colección Postman con versiones ficticias, casos `TC-GEN-07-...`.
- **Bloqueo:** modelo/contrato de versionado aprobado y PR del backend; luego `GEN-07-INT`.

### GEN-08-TEST — José Ariel
- **Desarrollo:** [GEN-08](https://app.clickup.com/t/86bcx2z62), `GEN-08-BE` (Emilio Carranza entre los responsables).
- **Casos:** auditar inicio, recuperación, respuesta, cierre, calificación y consulta solo en los eventos aprobados; comprobar trazabilidad del actor y que no aparezcan datos sensibles en registros.
- **Entregable:** casos `TC-GEN-08-...` con captura o salida desidentificada; evidencia del PR/SHA.
- **Bloqueo:** formato/eventos de auditoría y permisos aprobados; PR implementado; luego `GEN-08-INT`.

### BFA-TEST-01 — Samuel
- **Desarrollo:** tareas `BFA-BE/FE-01..04` y `BFA-BE/FE-06` cubiertas por la tarea.
- **Casos:** inventario de las **13 subpruebas**, con caso positivo y negativo por subprueba; presentación, formato de respuesta, límites y tiempo cuando aplique.
- **Entregable:** matriz de subpruebas y `TC-BFA-TEST-01-...`, capturas y resultados; pedir una revisión cruzada del lote por Francisco/José Francisco.
- **Bloqueo:** material ficticio DOC-08, criterios DOC-07 y PRs de cada familia. Puede preparar matriz antes de implementar.

### BFA-TEST-02 — Francisco
- **Desarrollo:** `BFA-BE-08`, `BFA-BE-09`, `BFA-BE-10`.
- **Casos:** cálculo independiente de puntuaciones y factores mencionados por la tarea (N1, S2, EIV/EIC/EIG/AE), límites normativos, resultados parciales y ausencia de rango; **sin inventar fórmulas ni interpolaciones**.
- **Entregable:** entradas ficticias y valores esperados calculados a partir de reglas oficialmente aprobadas, tabla `TC-BFA-TEST-02-...`.
- **Bloqueo:** tablas oficiales/criterios aprobados y DOC-08; PRs backend, luego `BFA-INT-02`.

### BFA-TEST-03 — Francisco
- **Desarrollo:** `BFA-BE-07`, `BFA-BE-11`, `BFA-FE-07`, `BFA-FE-09`.
- **Casos:** Fluidez Verbal, letra asignada, orden de entrada, persistencia, máximo de 75 entradas según criterio existente, reenvío sin duplicado y palabras dudosas `PENDIENTE` hasta revisión autorizada.
- **Entregable:** `TC-BFA-TEST-03-...` con casos de límite, duplicados, revisión y evidencia del PR/SHA.
- **Bloqueo:** DOC-08, criterios léxicos aprobados y PRs backend/frontend; luego `BFA-INT-03`.

### BFA-TEST-04 — José Francisco
- **Desarrollo:** `BFA-BE-12`, `BFA-BE-13`, `BFA-FE-08`.
- **Casos:** batería completa y parcial, orden de subpruebas, conservación de respuestas, resultados correctos o explícitamente pendientes y publicación solo cuando corresponda.
- **Entregable:** `TC-BFA-TEST-04-...`, informe corto de resultados y evidencia.
- **Bloqueo:** material DOC-08, cálculos/revisión BFA aprobados y PRs; luego `BFA-INT-04`.

### TEST-02-T4 — Adrián
- **Alcance:** seguridad y aislamiento de datos BFA; **no implementa código**.
- **Casos:** usuario sin permisos no lee claves ni resultados, evaluaciones de distintos usuarios no se mezclan, respuestas de error no exponen datos sensibles.
- **Entregable:** matriz actor × acceso y escenarios para `TEST-02-EJEC`.
- **Bloqueo:** roles y datos de prueba aprobados; se ejecuta cuando `main` integre funciones BFA necesarias.

### TEST-02-T5 — José Ariel
- **Alcance:** recuperación y concurrencia BFA; **no implementa código**.
- **Casos:** pérdida de conexión, reintento, no duplicar intentos, no alterar temporizadores oficiales y simultaneidad controlada con datos ficticios.
- **Entregable:** escenarios reproducibles y resultados para `TEST-02-EJEC`.
- **Bloqueo:** ambientes y umbrales RNF aprobados; no lanzar carga contra servidor compartido sin autorización.

### TEST-01 — Víctor
- **Coordinación del flujo común:** consolidar casos `GEN-01..08-TEST`; comprobar que cada módulo tenga criterio, responsable y resultado.
- **Entregable:** índice con enlaces a casos, estado por módulo, cobertura y lista de bloqueos.
- **Bloqueo:** puede prepararse ahora; cierre requiere casos aprobados y ejecución integrada documentada.

### TEST-01-EJEC — Rodrigo
- **Ejecución común:** usar entorno `main` autorizado cuando las funciones GEN necesarias estén integradas.
- **Casos:** recorrido de catálogo → inicio → preguntas → cierre → consulta, además de autenticación, versiones y auditoría según aprobaciones.
- **Entregable:** fecha, commit desplegado, casos ejecutados, resultados, evidencias y bugs; enlace a `TEST-01`.
- **Bloqueo:** dependencias GEN ya registradas en ClickUp y disponibilidad efectiva del ambiente.

### TEST-02 — José Francisco
- **Coordinación BFA:** juntar casos BFA-TEST-01..04 y TEST-02-T4/T5; revisar cobertura, incidencias y bloqueos.
- **Entregable:** índice de la batería BFA, responsables, criterios, evidencia de cada módulo y estado global.
- **Bloqueo:** diseño puede empezar; cierre solo cuando pruebas integradas y materiales estén verificados.

### TEST-02-EJEC — José Francisco
- **Ejecución BFA:** recorrer batería completa/parcial y resultados, incluir seguridad y recuperación, solo en ambiente `main` aprobado.
- **Entregable:** informe integrado, commit verificado, casos positivos/negativos y defectos enlazados.
- **Bloqueo:** BFA integrado, TEST-01-EJEC terminado, TEST-02-T4/T5 preparados y ambiente listo (relaciones ya existentes).

## 4. Qué hacer en ClickUp (sin duplicar tareas)

1. Abrir cada tarea de la tabla y poner el **único responsable** correspondiente.
2. **Conservar** título, subtarea, descripción, fecha límite, criterios originales y dependencias ya configuradas.
3. En la descripción, añadir la sección de esta guía correspondiente a la tarea. No reemplazar los apartados existentes ni los vínculos a DOC.
4. Mantener `Open` si aún no se inició; usar `in progress` para preparación realmente iniciada, `blocked` cuando no sea posible avanzar y `in review` cuando el resultado espere validación. `completed` solo con evidencias.
5. Evitar añadir dependencia dura desde Testing al cierre de Desarrollo si Testing ya precede Integración en el grafo; anotar el PR que requiere como **precondición de ejecución**, no un bloqueo circular.

## 5. Definición de terminado (DoD) para Testing

- [ ] Cada criterio aplicable tiene un `TC-` identificable.
- [ ] Se identifica el PR y SHA probados o se documenta el bloqueo.
- [ ] API probada con Postman cuando exista backend; frontend probado en navegador cuando exista interfaz.
- [ ] Resultado real, fecha y evidencia adjuntos.
- [ ] Bugs reportados en la lista Bugs y enlazados al PR/tarea.
- [ ] Se deja conformidad por escrito o se rechaza con razones claras.
- [ ] La etiqueta `testing-ok` solo se solicita/aplica tras aprobar el código vigente, cuando el workflow la exige.
- [ ] No hay secretos ni datos personales reales.

**Guías:** [tests/README.md](../../tests/README.md) · [tests/api/README.md](../../tests/api/README.md) · [plantilla de casos](PLANTILLA-CASO-PRUEBA.md) · [Backlog de ClickUp](https://app.clickup.com/90141806675/v/l/li/901422672485).
