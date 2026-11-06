# BFA-TEST-03 — Casos de prueba: Fluidez Verbal

- **Archivo:** `BFA-TEST-03-casos-prueba-v1.md`
- **Versión:** v1, borrador actualizado para revisión; preparado el 2026-11-05.
- **Tarea ClickUp:** [BFA-TEST-03](https://app.clickup.com/t/86bcw9z4d), estado **Open**, vence 2026-11-07 10:00 p. m. (Managua), prioridad no registrada.
- **Responsable:** Francisco Israel ferrufino James.
- **Alcance de esta entrega:** diseño documental únicamente. No se ejecutó ninguna prueba, no se modificó código ni ClickUp, no se publicó en GitHub y no se marcó `testing-ok`.
- **Convención de ejecución:** en todos los casos, resultado obtenido = **PENDIENTE DE EJECUCIÓN** y estado = **NO EJECUTADA**.

## Historial de cambios v0 → v1

- Se conservan todos los IDs y el total de casos de v0; no se ejecutó ningún caso y no se generó evidencia.
- Se actualizaron resultados esperados y permisos con las reglas funcionales de DOC-04, Revisión 2 (3 de noviembre, aprobada funcionalmente en el PDF adjunto) y los cierres de DOC-04/DOC-05/DOC-06 en ClickUp del 5 de noviembre.
- DOC-05 figura Closed; sus comentarios registran el ajuste E3 y la aprobación funcional de E10–E12/permisos. Pero el único PDF adjunto visible sigue titulado v0.6 y un comentario del 5 de noviembre pidió subir los documentos actualizados. Por eso v1 no inventa rutas, DTOs o errores de E10–E12 ni declara la copia del contrato final verificada.
- DOC-04 figura Closed y su PDF adjunto es Revisión 2, con aprobación funcional. Un comentario del 4 de noviembre indicó que el Google Doc BFA §3 aún tenía numeración anterior; no se localizó confirmación posterior de sincronización. Esta diferencia queda señalada para revisión.
- DOC-07 continúa `in progress` y DOC-08 `Open`; siguen siendo bloqueos directos. Este v1 es preparación documental, no autorización ni ejecución de pruebas.

## Aprobación y límites de la fuente

- **DOC-04:** ClickUp Closed (5 nov 17:54); el PDF adjunto es Revisión 2 del 3 nov y declara “aprobado funcionalmente”. Una nota del 4 nov reportó que el Google Doc BFA §3 aún conservaba numeración vieja; no aparece una confirmación posterior de sincronización. El borrador v1 toma como fuente funcional la Revisión 2 adjunta y señala la inconsistencia para revisión.
- **DOC-05:** ClickUp Closed (5 nov 18:26). Los comentarios indican actualización tras DOC-04 R2 y que E10/E11 corresponden a Psicólogo y E12 a Psicólogo o Coordinador; también consta el ajuste E3 para exigir letra asignada. Pero el único PDF adjunto visible se titula v0.6 y el último comentario previo al cierre pidió adjuntar los documentos actualizados. Por eso, v1 no da por verificados los paths/DTO/errores de la versión final que no se ven en ese archivo.
- **DOC-06:** ClickUp Closed (5 nov 17:00); comentario de 3 nov registra D09 alineada: calificación al cerrar cada subprueba, consolidación al finalizar. Requiere conformidad de la fuente vigente antes de revisar arquitectura en PR.
- **DOC-07 sigue `in progress`; DOC-08 sigue `Open`. Las dos tareas BFA-TEST permanecen abiertas y bloqueadas directamente por esas dependencias. No se ejecutó prueba alguna.

## Fuentes y vigencia

- Tareas de Desarrollo cubiertas: [BFA-BE-07](https://app.clickup.com/t/86bcvah7u), [BFA-BE-11](https://app.clickup.com/t/86bcw9yw3), [BFA-FE-07](https://app.clickup.com/t/86bcvah7v) y [BFA-FE-09](https://app.clickup.com/t/86bcwazjx). Todas aparecen **Open**; ClickUp no registra números de PR ni SHA.
- Requisitos completados: [DOC-02: Requisitos funcionales y no funcionales](https://app.clickup.com/t/86bctt9b8) y [DOC-03: Modelo de entidades y relaciones](https://app.clickup.com/t/86bctt9bt). DOC-02 documenta hasta 75 entradas, letra como parámetro, conservación de texto/orden, tiempo FV de 5:00, categorías léxicas confirmadas y cuestiones pendientes.
- Cerradas en ClickUp el 2026-11-05: [DOC-04: Casos de uso](https://app.clickup.com/t/86bctt9cm) (17:54), [DOC-06: Arquitectura técnica](https://app.clickup.com/t/86bctt9db) (17:00) y [DOC-05: Contratos REST](https://app.clickup.com/t/86bctt9cy) (18:26). DOC-04 adjunta Revisión 2 con aprobación funcional; se mantiene la salvedad de sincronización con el Drive indicada más abajo. DOC-05 tiene cierre y comentarios posteriores de actualización, pero el PDF adjunto visible sigue siendo v0.6; rutas/DTO de E10–E12 quedan por cotejar con la copia final. [DOC-07](https://app.clickup.com/t/86bctt9dn) sigue **in progress** y [DOC-08](https://app.clickup.com/t/86bcvaj7m) sigue **Open**. BFA-TEST-03 está bloqueada directamente por DOC-07 y DOC-08.
- Instrucciones consultadas: [Manual de Testing](https://github.com/Nell16/sistema-evaluaciones/blob/main/tests/README.md), [Plan operativo de Testing](https://github.com/Nell16/sistema-evaluaciones/blob/main/docs/pruebas/PLAN-OPERATIVO-TESTING.md) y [Plantilla oficial](https://github.com/Nell16/sistema-evaluaciones/blob/main/docs/pruebas/PLANTILLA-CASO-PRUEBA.md). La preparación de casos puede empezar antes del PR; ejecutar necesita código/PR comprobable, contrato aprobado, entorno y datos ficticios preparados. Una especificación `Closed in ClickUp` no se usa para declarar un resultado válido.
- Referencias funcionales: [Documento BFA](https://docs.google.com/document/d/12BEna1eJiSFJ6_1Re0qVObeJMKXZCOOTQ866DY678PA/edit); [Documento general](https://docs.google.com/document/d/1JQygN5h_5EH8Hf5hKo5GyPWsW4QABz85icf_kzaTtxk/edit); [reglas del repositorio](https://docs.google.com/document/d/1xIMUCVVzZYzav7kGCc671IxY1oadfgrg_r7qAX5U_Ns/edit); [Repositorio del proyecto](https://github.com/Nell16/sistema-evaluaciones).

## Qué puede prepararse y qué sigue bloqueado

**Preparado en este borrador:** estructura de casos para preservar texto/orden/letra, capacidad de 75, entradas vacías/orden, idempotencia técnica, duración de 5:00, guardado/recuperación, estados de revisión y separación de revisar/publicar.

**PENDIENTE antes de cerrar el plan o ejecutar:** aprobación de DOC-07; fixtures ficticias DOC-08; criterios léxicos por ratificar con Psicología/UAM (especialmente palabras derivadas y categorías dudosas); cotejar actores, permisos y P10–P12 con DOC-04 Revisión 2 (Closed en ClickUp); cotejar E7/E10/E12, campos y errores con la copia final de DOC-05 (Closed, PDF visible v0.6); verificar la implementación de arquitectura temporal/UI contra DOC-06 (Closed) y el PR; PR/SHA y ambiente disponibles.

**Datos:** no se incluyen palabras de prueba ni ítems reales. Los marcadores como `texto-ficticio-01` solo sirven para diseñar escenarios de persistencia/estructura y requieren aprobación como fixture. Los casos léxicos quedan sin palabra de ejemplo hasta contar con el material aprobado.

## Casos de prueba

### TC-BFA-TEST-03-001 — Conservación de texto original, orden de entrada y asociación a la respuesta
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-FE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-FV-01; DOC-04 §3.3/3.5; BE07-CA01; FE07-CA06; T03-CA01
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman y navegador, cuando haya contrato aprobado.
- **Preparación / ejecución:** Diseñable ahora para persistencia; fixture/contrato/PR pendientes.

**Objetivo:** Comprobar que la producción original de FV conserva texto, orden, cambios/anulaciones y asociación a la aplicación, sin sobreescribir respuestas anteriores.

**Criterios de aceptación relacionados:** BE07-CA01; FE07-CA06; T03-CA01

**Precondiciones:**
Aplicación de prueba con letra sintética configurada. Usar solo marcadores de respuesta ficticios, no palabras ni ítems reales.

**Datos de entrada:**
Lista de marcadores de captura de prueba no léxicos, por ejemplo `texto-ficticio-01`, `texto-ficticio-02`; esta prueba comprueba persistencia/orden, no validez lingüística. Fixture pendiente de aprobación DOC-08.

**Pasos para reproducir:**
1. Iniciar una aplicación FV de prueba desde la interfaz autorizada.
2. Registrar dos marcadores sintéticos en secuencia.
3. Guardar y recuperar la aplicación por el mecanismo final aprobado.
4. Comparar texto y orden recibidos con lo enviado.

**Resultado esperado:** Se conservan texto original, orden, cambios y anulaciones; las operaciones repetidas conservan la aceptación original sin duplicar la respuesta. Las entradas confirmadas quedan vinculadas al mismo intento y se distinguen de cambios pendientes. No se presupone ruta, DTO ni HTTP de la copia final de DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-05 Closed in ClickUp; DOC-08 Open; PR no disponible.

**Documentación utilizada:** DOC-02 §5.13 (completed); BFA-TEST-03; BFA-BE-07; BFA-FE-07; DOC-05/08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-002 — La letra de aplicación permanece visible, fija y asociada a Fluidez Verbal
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-FE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-FV-03/06; DOC-02 §5.13; BE07-CA02; FE07-CA01
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** E2E / navegador y API / Postman, según contrato.
- **Preparación / ejecución:** DOC-04 §3.5 define letra formal en configuración y exige que sin ella FV no inicie; DOC-08/PR siguen pendientes.

**Objetivo:** Verificar que la letra real configurada para la aplicación aparece en la interfaz, queda registrada y no cambia durante la subprueba.

**Criterios de aceptación relacionados:** BE07-CA02; FE07-CA01; T03-CA01

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Letra asignada desde fixture ficticia aprobada por DOC-08. No asumir la letra del ejemplo del manual ni una letra fija del módulo.

**Pasos para reproducir:**
1. Preparar una aplicación ficticia con letra configurada por el flujo autorizado, sin asumir aleatoriedad ni usar la letra del ejemplo como constante.
2. Abrir la vista de Fluidez Verbal y observar la letra antes de empezar.
3. Avanzar por varias entradas y verificar que la letra continúa visible.
4. Consultar la aplicación recuperada y comparar la letra registrada.

**Resultado esperado:** La letra formal configurada se conserva y permanece fija/visible; sin letra formal, el sistema impide iniciar FV. La práctica está separada de la producción puntuable. La letra/fixture sigue sujeta a DOC-08.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** Falta fixture de DOC-08; DOC-04 y DOC-05 están Closed en ClickUp, pero falta cotejar el mecanismo de asignación con DOC-04 Revisión 2 y la copia final del contrato DOC-05; PR pendiente.

**Documentación utilizada:** DOC-02 §5.13 (completed); BFA-BE-07; BFA-FE-07; DOC-04/05/08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-003 — Límite de 75 entradas y rechazo de la entrada 76
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-FE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-FV-01; hoja BFA consultada; BE07-CA03/04; FE07-CA05; T03-CA01
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman y E2E / navegador.
- **Preparación / ejecución:** Límite de 75 documentado; datos ficticios, contrato y PR pendientes.

**Objetivo:** Verificar el límite de capacidad de la edición: se admiten hasta 75 entradas y no se acepta una 76.ª.

**Criterios de aceptación relacionados:** T03-CA01; BE07-CA03; BE07-CA04; FE07-CA05

**Precondiciones:**
La prueba debe usar una aplicación ficticia autorizada, sin puntuación ni contenido real; el fixture se prepara tras aprobar DOC-08.

**Datos de entrada:**
75 marcadores de captura ficticios generados para prueba (no palabras/ítems reales); intentar una entrada adicional. La lista completa es fixture, pendiente de aprobación.

**Pasos para reproducir:**
1. Configurar una aplicación de prueba con la letra autorizada.
2. Registrar entradas ficticias hasta la posición 74 y comprobar que siguen aceptadas.
3. Registrar la posición 75 y comprobar que se admite.
4. Intentar una posición 76 y observar el comportamiento de API/interfaz.
5. Recuperar y comprobar que no hay una entrada 76 persistida.

**Resultado esperado:** El sistema admite hasta 75 entradas de la edición y no persiste ni presenta la 76 como aceptada. El texto/orden de las primeras 75 se conserva. No se fija código de rechazo ni mensaje hasta DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-08 abierto; contrato en revisión; no hay PR/entorno.

**Documentación utilizada:** DOC-02 §5.13 (completed); BFA-TEST-03; BFA-BE-07; BFA-FE-07; DOC-05/08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-004 — Rechazo de texto vacío y orden no único
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-FV-01; BE07-CA03
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, cuando contrato y PR estén disponibles.
- **Preparación / ejecución:** Regla estructural identificada en BE07; detalles de validación/error pendientes de DOC-05.

**Objetivo:** Comprobar que una entrada vacía o que reutiliza un orden ya ocupado no queda registrada como una entrada válida.

**Criterios de aceptación relacionados:** BE07-CA03

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Fixture con texto vacío y dos operaciones que intentan usar el mismo orden; sin contenido psicológico. La forma del request no se inventa.

**Pasos para reproducir:**
1. Enviar un registro con texto vacío usando el request aprobado.
2. Enviar dos registros de prueba con el mismo orden, siguiendo el contrato final.
3. Consultar el conjunto persistido.
4. Comparar el resultado con la regla de texto no vacío y orden único.

**Resultado esperado:** Las entradas vacías y los órdenes duplicados no se aceptan como válidos; el conjunto de respuestas permanece consistente. Códigos HTTP, claves y texto de error PENDIENTES de DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** Falta cotejar el contrato/API contra la versión final de DOC-05 (Closed en ClickUp); PR pendiente.

**Documentación utilizada:** BFA-BE-07; DOC-02 §5.13; DOC-05 (Closed in ClickUp).

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-005 — Reenvío técnico idempotente sin duplicar palabras
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-BE-11
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** DOC-04 §3.3/3.5; E7 (ruta documentada en adjunto DOC-05 v0.6, verificar copia final); BE07-CA05; BE11-CA07
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, cuando E7/contrato esté aprobado.
- **Preparación / ejecución:** La regla de reenvío/idempotencia está confirmada en DOC-04 §3.3; las reglas de palabra repetida están en §3.5. El PDF API visible es v0.6; verificar la copia final para DTO/códigos.

**Objetivo:** Verificar que repetir técnicamente el mismo envío no crea entradas duplicadas ni decisiones duplicadas.

**Criterios de aceptación relacionados:** T03-CA01; BE07-CA05; BE11-CA07

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Dos subescenarios con fixture ficticia autorizada: (A) reenviar exactamente el mismo cambio técnico; (B) una palabra repetida en otro orden/entrada. No usar palabras reales; fixture lingüística requiere DOC-08.

**Pasos para reproducir:**
1. Enviar una entrada ficticia conforme al request aprobado.
2. Repetir exactamente la misma operación como reintento técnico.
3. Consultar el conjunto de palabras persistidas.
4. Repetir una decisión de revisión por un reintento técnico y comprobar que no se duplica.
5. En una fixture aprobada, comprobar que una palabra repetida no suma dos veces.

**Resultado esperado:** Un reenvío del mismo cambio devuelve/retiene la aceptación original sin duplicar respuesta; una decisión repetida no crea decisión duplicada; la palabra repetida no suma dos veces. No se fija HTTP ni JSON antes de cotejar la copia final de DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-05 Closed in ClickUp; no hay endpoint ni PR confirmado.

**Documentación utilizada:** BFA-BE-07/11; DOC-05; Manual de Testing.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-006 — Cierre por tiempo oficial y rechazo de entradas fuera de tiempo o tras cierre
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-FE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-FV-04; DOC-02 §5.13; BE07-CA06; FE07-CA10/11
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman y E2E / navegador.
- **Preparación / ejecución:** DOC-04 §3.2/P07 y tarea FE07 fijan inicio/vencimiento, cierre una vez y reloj no reiniciado por recarga/reautenticación; duración FV 5:00.

**Objetivo:** Comprobar el límite de cinco minutos, cierre y rechazo de cambios después del vencimiento o cierre.

**Criterios de aceptación relacionados:** BE07-CA06; FE07-CA10; FE07-CA11

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Aplicación de prueba y reloj de entorno controlado; usar datos ficticios. No simular manipulación del reloj del servidor fuera del procedimiento autorizado.

**Pasos para reproducir:**
1. Iniciar la subprueba con el mecanismo aprobado y registrar la hora oficial inicial.
2. Observar la cuenta de cinco minutos según la configuración aprobada.
3. Al vencer el tiempo o después del cierre autorizado, intentar guardar una entrada ficticia.
4. Consultar el estado y las palabras persistidas.

**Resultado esperado:** FV dura 5:00. El inicio fija una vez el vencimiento; recarga/desconexión/reautenticación no lo reinicia. Al vencer, el cierre interno ocurre una sola vez, conserva respuestas aceptadas, bloquea nuevos cambios y dispara la calificación del bloque (P07). Si FV queda sin revisión, conserva PENDIENTE_VALIDACION. No se inventa respuesta HTTP.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-05/06 Closed in ClickUp; ambiente/PR no disponible.

**Documentación utilizada:** DOC-02 §2.6 y §5.13 (completed); BFA-BE-07; BFA-FE-07; DOC-05/06.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-007 — Validación estructural separada de la evaluación lingüística
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-FV-05; DOC-02 §5.13; BE07-CA07
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, según contrato aprobado.
- **Preparación / ejecución:** DOC-04 §3.5 confirma que durante la aplicación no se muestran sugerencias ni validación léxica que ayude a responder; los criterios de revisión se aplican en fase profesional.

**Objetivo:** Verificar que la validación de registro no rechaza automáticamente una entrada por una regla lingüística todavía no aprobada.

**Criterios de aceptación relacionados:** BE07-CA07; BE07-CA08

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Entrada no vacía de fixture ficticia, sin etiqueta lingüística aprobada. No incluir ejemplos de vocabulario real ni atribuir validez.

**Pasos para reproducir:**
1. Enviar una entrada de texto sintética no vacía mediante el contrato aprobado.
2. Verificar que el registro estructural se acepta si cumple formato/capacidad/tiempo.
3. Inspeccionar el resultado de captura para confirmar que no asigna una decisión léxica definitiva no aprobada.

**Resultado esperado:** Durante la captura se comprueban solo estructura/capacidad/tiempo; no se ayuda al Sujeto con validación léxica. La valoración profesional posterior se sujeta a DOC-04 P10 y a las decisiones léxicas aprobadas; no se inventa una clasificación en la aplicación.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-08 sigue Open; DOC-04 y DOC-05 figuran Closed en ClickUp, pero debe confirmarse la sincronización del Google Doc BFA §3 con DOC-04 Revisión 2 y cotejarse la copia final de DOC-05 (el PDF visible está en v0.6); los criterios léxicos detallados siguen pendientes.

**Documentación utilizada:** DOC-02 §5.13 y tabla RF-FV-05; BFA-BE-07; DOC-04/05/08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-008 — Palabra dudosa queda PENDIENTE hasta decisión autorizada
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-BE-11
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** DOC-02 §5.13; T03-CA01; BE07-CA09; BE11-CA04/05
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman y revisión autorizada, cuando se verifique la copia final del contrato DOC-05.
- **Preparación / ejecución:** DOC-04 Revisión 2 define PENDIENTE_VALIDACION y limita REVISAR_FLUIDEZ a Psicólogo con alcance. E10/API exacto se coteja con copia final de DOC-05.

**Objetivo:** Verificar que una respuesta dudosa no se clasifica arbitrariamente ni aporta un puntaje definitivo antes de decisión autorizada.

**Criterios de aceptación relacionados:** T03-CA01; BE07-CA09; BE11-CA04; BE11-CA05

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Aplicación cerrada con producción ficticia autorizada, letra/reglas versionadas y Psicólogo de prueba con REVISAR_FLUIDEZ y alcance. No inventar palabra de prueba; obtener fixture de DOC-08.

**Pasos para reproducir:**
1. Registrar la fixture como respuesta de una aplicación de prueba.
2. Consultar el estado inicial de revisión.
3. Antes de que una persona autorizada decida, inspeccionar el cálculo.
4. Comprobar que la respuesta queda marcada PENDIENTE y no se convierte automáticamente en inválida ni puntúa definitivamente.

**Resultado esperado:** La entrada dudosa queda PENDIENTE_VALIDACION con motivo hasta que Psicólogo autorizado registre una decisión. Una revisión incompleta no produce FV definitiva; sin ella, P08 mantiene pendientes de factores dependientes. La revisión conserva original/orden y genera recálculo trazable al resolverse. Confirmar serialización exacta con DOC-05 final.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** Criterios de palabras derivadas/dudosas requieren ratificación; los permisos funcionales constan en DOC-04 Revisión 2, pero falta cotejar E10 con la versión final DOC-05; DOC-08 sin material; PR pendiente.

**Documentación utilizada:** BFA-TEST-03; DOC-02 §5.13/§8; BFA-BE-07/11; DOC-04/05/08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-009 — Aplicación de reglas léxicas aprobadas sin inventar decisiones para casos abiertos
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-BE-11
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-FV-05/07; DOC-02 §5.13/§8; BE07-CA08/09
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, solo con fixture de clasificación aprobada.
- **Preparación / ejecución:** DOC-04 Revisión 2 explicita: inicial asignada; excluir nombres propios/extranjerismos; ortografía media/final no invalida por sí sola; derivados no auto-rechazados; duplicados no suman dos veces. No elimina la necesidad de fixture ficticia DOC-08.

**Objetivo:** Verificar únicamente las reglas con respaldo documental suficiente y dejar sin decisión automática los casos que siguen abiertos.

**Criterios de aceptación relacionados:** BE07-CA08; BE07-CA09

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
No registrar palabras de ejemplo. Usar solo una fixture léxica ficticia aprobada por DOC-08/Psicología-UAM que cubra categorías documentadas: inicial, nombre propio, extranjerismo, error ortográfico medio/final, derivada/dudosa y repetida. No escribir ni inventar palabras de batería en este documento.

**Pasos para reproducir:**
1. Cargar las categorías y resultados esperados solo desde la fixture autorizada.
2. Solicitar la evaluación/revisión de cada caso por el mecanismo aprobado.
3. Comprobar que los nombres propios y extranjerismos se excluyen; que errores ortográficos en medio/final no invalidan automáticamente.
4. Comprobar que casos derivados/dudosos quedan pendientes hasta decisión; no inferir tratamiento.
5. Revisar que cada palabra válida aporta un punto solo con la decisión aprobada.

**Resultado esperado:** Según DOC-04 Revisión 2, se puntúa un punto por palabra válida tras revisión; se verifica la inicial; nombres propios y extranjerismos se excluyen; una falta ortográfica media/final no invalida por sí sola; derivadas requieren valoración y no se rechazan automáticamente; repetidas no suman dos veces. Una duda se deja PENDIENTE_VALIDACION hasta decisión autorizada. No se asigna clasificación a una palabra sin fixture aprobada.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** Fixture léxica y política de derivados/repeticiones no aprobadas; DOC-07 in progress; DOC-08 open.

**Documentación utilizada:** DOC-02 tabla RF-FV-05 y §8 (completed); BFA-BE-07/11; DOC-07/08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-010 — Captura secuencial por teclado, caracteres españoles y ausencia de autocorrección
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-FE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-FV-03; RNF-FV-01; FE07-CA01/02/03/04
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** E2E / navegador.
- **Preparación / ejecución:** Diseñable desde criterios FE07 y DOC-02; requiere UI/PR y fixture autorizada.

**Objetivo:** Verificar la interacción principal: letra visible, Enter mueve el foco, entrada ordenada, caracteres del español y sin alteraciones por autocorrector/autocompletado.

**Criterios de aceptación relacionados:** FE07-CA01; FE07-CA02; FE07-CA03; FE07-CA04

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Usar una letra de fixture aprobada y texto de prueba sintético con caracteres autorizados, incluyendo tildes y ñ; no usar ítems reales ni palabras de batería.

**Pasos para reproducir:**
1. Abrir la interfaz FV con el perfil ficticio autorizado.
2. Confirmar que la letra permanece visible.
3. Escribir una entrada de fixture y presionar Enter; confirmar que el foco avanza a la siguiente posición.
4. Probar caracteres con tilde y ñ usando la fixture sintética aprobada.
5. Verificar que navegador/interfaz no autocorrige ni autocompleta la entrada.
6. Comprobar orden y texto en la vista recuperada.

**Resultado esperado:** La letra está visible; Enter avanza; los caracteres permitidos se conservan y no hay autocorrección/autocompletado que altere el texto. El resultado no depende de una maqueta no aprobada.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** PR/UI y material ficticio DOC-08 no disponibles.

**Documentación utilizada:** DOC-02 §5.13; BFA-FE-07; Manual de Testing; DOC-08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-011 — Guardado sin interrumpir escritura y objetivo menor a 500 ms
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-FE-07 / BFA-BE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RNF-FV-02; BE07-CA11; FE07-CA07
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** E2E / navegador con medición autorizada.
- **Preparación / ejecución:** El umbral <500 ms está registrado; método de medida, entorno y carga deben aprobarse antes de comparar.

**Objetivo:** Verificar que guardar una palabra no interrumpe la captura y que el tiempo observado cumple el umbral documentado.

**Criterios de aceptación relacionados:** FE07-CA07; BE07-CA11

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Datos sintéticos; entorno local/PR autorizado y reloj de medición acordado. No medir contra servidor compartido sin autorización. Umbral: <500 ms según tarea/Doc.

**Pasos para reproducir:**
1. Registrar una entrada sintética mientras se mantiene el flujo de escritura.
2. Medir el intervalo entre acción de guardado y confirmación observable con el método que apruebe el equipo.
3. Repetir con una muestra de ejecuciones acordada.
4. Registrar valores reales y entorno solamente durante la futura ejecución.

**Resultado esperado:** La escritura no se interrumpe y el guardado cumple <500 ms en las condiciones de medición aprobadas. No declarar el umbral cumplido hasta tener evidencia y método reproducible.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** PR y entorno no disponibles; DOC-07 debe confirmar el criterio de medición/carga; no iniciar carga contra servidor compartido.

**Documentación utilizada:** BFA-FE-07; DOC-02 RNF-FV-02; Plan operativo; DOC-07.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-012 — Estados de guardado visibles y recuperación conserva entradas y tiempo
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-FE-07 / BFA-BE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE07-CA01/05; FE07-CA06/08/09
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** E2E / navegador y API / Postman, según contrato.
- **Preparación / ejecución:** Diseñable ahora; necesita UI, recuperación aprobada, PR y entorno.

**Objetivo:** Comprobar estados visuales pendiente/confirmado/fallido y recuperación de palabras sin reiniciar el tiempo oficial.

**Criterios de aceptación relacionados:** FE07-CA06; FE07-CA08; FE07-CA09; BE07-CA01; BE07-CA05

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Marcadores ficticios no léxicos y sesión controlada. No simular desconexión concurrente o modificar datos compartidos sin plan autorizado.

**Pasos para reproducir:**
1. Iniciar FV en una aplicación de prueba.
2. Observar el estado mientras una entrada está pendiente de guardar.
3. Confirmar que el estado cambia al guardado y, en un fallo controlado autorizado, se distingue del estado fallido.
4. Guardar entradas y registrar el vencimiento oficial.
5. Recuperar la aplicación por el flujo aprobado.
6. Comparar entradas/orden y tiempo restante con lo anterior a la recuperación.

**Resultado esperado:** La recuperación usa el mismo intento, respuestas confirmadas y vencimiento original; cambios sin confirmación se distinguen de los guardados. No detiene ni reinicia el plazo. La UI debe diferenciar cambios pendientes/confirmados/fallidos según FE07. Verificar implementación contra PR y contrato final.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-05 Closed in ClickUp; PR/UI y plan de entorno no disponibles.

**Documentación utilizada:** DOC-04 §3.3; DOC-02 persistencia/recuperación; BFA-BE-07; BFA-FE-07; DOC-05 (cotejar copia final).

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-013 — Cronómetro de 5:00, autoridad temporal y bloqueo al cerrar
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-FE-07
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-FV-04; DOC-02 §2.6/§5.13; BE07-CA06; FE07-CA10/11
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** E2E / navegador y API / Postman.
- **Preparación / ejecución:** DOC-04 §3.2/P07 define inicio/vencimiento único, no reinicio por recarga/reautenticación y cierre idempotente; FE07 también asigna autoridad del cronómetro al backend. DOC-05 attachment final debe cotejarse; PR pendiente.

**Objetivo:** Verificar que el cronómetro FV es de cinco minutos, la UI presenta tiempo restante y después del cierre no permite cambios.

**Criterios de aceptación relacionados:** BE07-CA06; FE07-CA10; FE07-CA11

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Una aplicación ficticia; reloj/temporización del entorno según configuración aprobada. Sin credenciales reales.

**Pasos para reproducir:**
1. Iniciar FV y registrar inicio/fin en la ejecución futura.
2. Comprobar que la configuración indica 5:00.
3. Esperar al cierre controlado o usar mecanismo de simulación autorizado.
4. Intentar una nueva entrada después del cierre.
5. Consultar el estado final y confirmar que no hay cambios aceptados.

**Resultado esperado:** El reloj formal es 5:00; el vencimiento se mantiene con recarga, desconexión o reautenticación. Al cierre no se aceptan nuevas respuestas y el evento es único/idempotente; respuestas confirmadas se conservan. No se altera el reloj desde el cliente ni se inventa código HTTP.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DECISIÓN de arquitectura temporal y contratos en DOC-05/06 Closed in ClickUp; ejecución requiere PR y ambiente.

**Documentación utilizada:** DOC-02 §2.6/§5.13; BFA-BE-07; BFA-FE-07; DOC-05/06.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-014 — La interfaz no da pistas léxicas ni calcula puntuación
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-FE-07 / BFA-FE-09
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** FE07-CA12; FE09-CA03; DOC-02 RF-FV-05/07
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** E2E / navegador.
- **Preparación / ejecución:** Diseñable desde tareas; ejecutar con UI/PR y datos ficticios aprobados.

**Objetivo:** Verificar que la captura no sugiere validez de palabras ni presenta cálculo de puntaje durante la aplicación, y que la pantalla de revisión tampoco calcula puntuaciones.

**Criterios de aceptación relacionados:** FE07-CA12; FE09-CA03

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Entradas de prueba ficticias; no incluir ejemplos reales ni pedir juicio de validez durante la captura.

**Pasos para reproducir:**
1. Abrir la pantalla de captura FV.
2. Introducir texto sintético y observar si aparecen sugerencias, correcciones, validaciones lingüísticas o puntajes.
3. Abrir la pantalla de revisión/publicación solo con perfil de prueba autorizado.
4. Observar si la interfaz calcula el puntaje al cambiar una decisión.

**Resultado esperado:** La interfaz de captura no determina validez lingüística ni calcula puntaje; revisar una palabra no calcula puntuaciones desde el frontend.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** PR/UI y DOC-08 pendientes; pantalla de revisión depende de permisos/contrato en DOC-04/05.

**Documentación utilizada:** BFA-FE-07/09; DOC-02 §5.13; DOC-04/05/08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-015 — Solo revisor autorizado puede decidir y se registra actor, fecha, estado y motivo
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-11
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE11-CA01/02/04; CU-BFA-P10/E10 (DOC-04/05)
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, tras confirmar el permiso conforme a DOC-04 Revisión 2 y cotejar la copia final del contrato DOC-05.
- **Preparación / ejecución:** DOC-04 Revisión 2 aprobada funcionalmente limita REVISAR_FLUIDEZ a Psicólogo con permiso y alcance; Académico, Coordinador, Decano y Sujeto no heredan ese permiso. DOC-05 task Closed, pero la copia API final no está adjunta de forma verificable.

**Objetivo:** Comprobar que solo el perfil autorizado revisa palabras y que cada decisión registra su estado, motivo, fecha y actor responsable.

**Criterios de aceptación relacionados:** BE11-CA01; BE11-CA02; BE11-CA04

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Perfil ficticio de Psicólogo con permiso REVISAR_FLUIDEZ y alcance frente a perfil sin permiso; aplicación FV cerrada con fixture de DOC-08. No crear cuentas reales. Ruta/DTO/error E10 se cotejan con la copia final de DOC-05 antes de ejecutar.

**Pasos para reproducir:**
1. Preparar perfil autorizado y perfil sin permiso según la matriz aprobada.
2. Intentar revisar una palabra con el perfil no autorizado.
3. Repetir con el perfil autorizado y registrar una decisión permitida.
4. Consultar la revisión y comprobar estado, motivo, fecha y actor.

**Resultado esperado:** Solo Psicólogo con REVISAR_FLUIDEZ y alcance revisa; demás perfiles se deniegan. La decisión registra estado/motivo y responsable/fecha conforme a las tareas BE11. No se fija ruta, JSON ni código HTTP sin la copia API final.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-04 Closed/Revisión 2 define el permiso; DOC-08 Open; copy final API y PR BE11 pendientes.

**Documentación utilizada:** BFA-BE-11; DOC-02 §5.13/§8; DOC-04/05 (Closed in ClickUp); DOC-08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-016 — Revisión asociada a la palabra, evaluación y calificación; PENDIENTE no es inválida
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-11
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE11-CA03/05/06; T03-CA01
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, cuando modelo/contrato estén aprobados.
- **Preparación / ejecución:** Diseñable como escenario; relación de entidades y contrato de revisión requieren DOC-03/04/05 y DOC-07.

**Objetivo:** Comprobar integridad de vínculo de la decisión y que una palabra PENDIENTE no se cuente automáticamente como incorrecta ni se cambie el texto original.

**Criterios de aceptación relacionados:** T03-CA01; BE11-CA03; BE11-CA05; BE11-CA06

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Fixture de respuesta/revisión sin contenido léxico real. Identificadores de evaluación, respuesta y calificación ficticios se asignan por el entorno autorizado.

**Pasos para reproducir:**
1. Crear una respuesta sintética dentro de la evaluación aprobada.
2. Solicitar una revisión vinculada a esa respuesta/calificación.
3. Consultar el vínculo y el texto original antes/después.
4. Dejar una decisión PENDIENTE y comprobar que no se trata automáticamente como INVÁLIDA ni genera un puntaje definitivo.

**Resultado esperado:** La revisión se vincula a la producción original, evaluación y revisión de cálculo; no altera las respuestas originales. PENDIENTE_VALIDACION no equivale a INVÁLIDA ni produce FV definitiva. Los nombres de campos/IDs HTTP deben verificarse contra el contrato final.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-04 Closed/Revisión 2 define que originales, orden y decisiones se conservan, y la duda queda PENDIENTE_VALIDACION. DOC-05 Closed; consultar la copia final para los campos exactos de asociación/DTO.

**Documentación utilizada:** DOC-03; DOC-04/05; BFA-BE-11; DOC-07.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-017 — Revisión repetida, auditoría e inmutabilidad de calificación completada
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-11
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE11-CA07/08/09/10
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman y revisión del historial.
- **Preparación / ejecución:** DOC-04 Revisión 2 define que una corrección posterior crea otra revisión con motivo, autor, fecha e insumos/versiones; no sobrescribe revisión previa ni publicación. DOC-05/06 task Closed; verificar serialización API en copia final.

**Objetivo:** Verificar que repetir una decisión no duplica registros, que una calificación completada no se altera directamente y que los cambios exigen un recálculo trazable.

**Criterios de aceptación relacionados:** BE11-CA07; BE11-CA08; BE11-CA09; BE11-CA10

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Usar dos revisiones ficticias y datos de auditoría de prueba; ningún dato personal real. Procedimiento de recálculo PENDIENTE de especificación aprobada.

**Pasos para reproducir:**
1. Registrar una decisión en una palabra sintética.
2. Repetir la misma decisión por reenvío técnico.
3. Consultar el historial y comprobar no duplicación.
4. Sobre una calificación completada, intentar editar directamente la decisión.
5. Usar solo el flujo de recálculo si DOC-04/05 lo aprueba y comprobar la nueva revisión/auditoría.

**Resultado esperado:** Una doble solicitud no duplica decisión. Una revisión completada no se modifica directamente; el Psicólogo con RECALCULAR_BFA, alcance y motivo crea nueva revisión, preserva la anterior y audita cambios. El Sujeto sigue viendo la revisión publicada anterior hasta publicación explícita.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-04/05/06 figuran Closed en ClickUp; antes de ejecutar, confirmar los detalles del flujo de recálculo/publicación y cotejar la copia final de DOC-05; PR no disponible.

**Documentación utilizada:** BFA-BE-11; DOC-04 comentario sobre P10–P12; DOC-05; DOC-06.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-018 — Acceso autorizado a revisión y separación entre revisar y publicar
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-FE-09 / BFA-BE-11
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** FE09-CA01/02/03; CU-BFA-P10/P12 y E10/E12 (pendientes de ratificación)
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** E2E / navegador y API / Postman, según contrato.
- **Preparación / ejecución:** DOC-04 Revisión 2 define REVISAR_FLUIDEZ para Psicólogo con alcance; RECALCULAR_BFA para Psicólogo; PUBLICAR_RESULTADOS para Psicólogo o Coordinador con alcance. Revisar/calcular no publica automáticamente. La copia API actualizada debe cotejarse para rutas/DTO.

**Objetivo:** Comprobar que solo usuarios autorizados pueden usar la interfaz de revisión/publicación, que revisar una palabra no publica resultado y que el frontend no calcula puntajes.

**Criterios de aceptación relacionados:** FE09-CA01; FE09-CA02; FE09-CA03

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Perfiles ficticios: Psicólogo con REVISAR_FLUIDEZ/RECALCULAR_BFA; Coordinador con PUBLICAR_RESULTADOS; perfil sin esos permisos. Sin datos personales reales; usar solo fixture aprobada.

**Pasos para reproducir:**
1. Acceder a la interfaz con perfil sin permiso y comprobar denegación.
2. Acceder con perfil autorizado y revisar una respuesta ficticia.
3. Verificar que el estado de revisión cambia según el flujo aprobado, pero el resultado no se publica automáticamente.
4. Comprobar que la interfaz no muestra cálculo propio ni modifica el puntaje.
5. Ejecutar publicación únicamente como escenario separado si DOC-04/05 define y aprueba el permiso/flujo.

**Resultado esperado:** Solo los permisos explicitados pueden acceder a cada acción. Revisar FV no publica; finalizar, consolidar o recalcular tampoco publica por sí mismo; publicar es acción independiente por Psicólogo o Coordinador autorizado y con salidas válidas. La interfaz no calcula. Rutas/JSON/códigos quedan sujetos al PDF final de DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-04/05/06 Closed; DOC-08 Open; verificar copia API final, diseño/UI-07 y PR FE09 pendientes.

**Documentación utilizada:** BFA-FE-09; BFA-BE-11; DOC-04/05/06; Manual de Testing.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-019 — Evidencia de pruebas automatizadas de registro, revisión, interfaz y límites
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-BE-11 / BFA-FE-07 / BFA-FE-09
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE07-CA11; BE11-CA11; FE07-CA13
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** Revisión manual de PR/CI; Playwright/E2E cuando el PR exista.
- **Preparación / ejecución:** Diseñable ahora; ejecución requiere PR/CI, fixture aprobada y criterio funcional ratificado.

**Objetivo:** Verificar que los PR aportan pruebas automatizadas de los comportamientos que sus criterios requieren y que el resultado del CI corresponde al SHA revisado.

**Criterios de aceptación relacionados:** BE07-CA11; BE11-CA11; FE07-CA13

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Solo revisar tests/evidencia del PR; no crear ni ejecutar suites en esta fase documental.

**Pasos para reproducir:**
1. Abrir cada PR real y registrar SHA.
2. Revisar tests automatizados de registros válidos/invalidos/ambiguos/límite y controles de permisos/revisión.
3. Revisar tests funcionales de captura continua, recuperación, guardado/cierre.
4. Vincular resultado CI con el commit revisado y registrar faltantes como hallazgos.

**Resultado esperado:** El PR contiene y ejecuta pruebas automatizadas que cubren los escenarios correspondientes. CI verde no sustituye las pruebas funcionales ni la evidencia de Testing.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** PR/CI no proporcionados; no se ha ejecutado ningún caso.

**Documentación utilizada:** BFA-BE-07/11; BFA-FE-07/09; Manual de Testing; Plan operativo.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-03-020 — Prueba por PR y registro de conformidad/fallos antes de fusión
- **Tarea de Testing:** BFA-TEST-03
- **Tarea(s) de Desarrollo:** BFA-BE-07 / BFA-BE-11 / BFA-FE-07 / BFA-FE-09
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** T03-CA03; Plan operativo § BFA-TEST-03
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman para backend; E2E/navegador para frontend.
- **Preparación / ejecución:** Solo diseñable; PR/SHA, documentos aprobados, entorno y fixtures faltantes bloquean ejecución.

**Objetivo:** Asegurar que cada PR cubierto se prueba en la combinación propuesta con main antes de fusión y que se registra evidencia o fallo.

**Criterios de aceptación relacionados:** T03-CA03

**Precondiciones:**
Precondición: contar con PR/UI comprobable, contrato aprobado y fixture ficticia autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Números de PR/SHA pendientes; no inventar valores. Usar únicamente fixtures ficticias aprobadas.

**Pasos para reproducir:**
1. Por cada PR real de BE-07, BE-11, FE-07 y FE-09, registrar número y SHA.
2. Ejecutar los casos aplicables contra el PR actual: Postman para backend y navegador para frontend, según contrato aprobado.
3. Si pasa, registrar `Testing OK: TC-…`, PR, SHA y evidencia y solicitar/aplicar `testing-ok` según permiso.
4. Si falla, documentar TC, esperado, obtenido y evidencia en el PR; no asignar `testing-ok`.
5. Verificar el registro antes de que el PR se fusione.

**Resultado esperado:** Cada PR cuenta con pruebas/evidencia y comentario correspondiente antes de fusión; solo casos conformes al SHA revisado pueden recibir `testing-ok`. Este caso tampoco se ejecuta en esta fase.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** Ningún PR/SHA está identificado; DOC-07 sigue in progress y DOC-08 sigue Open. DOC-04/05/06 figuran Closed en ClickUp, pero queda por confirmar la sincronización de DOC-04 Revisión 2 y cotejar la copia final de DOC-05. El entorno y las fixtures siguen pendientes.

**Documentación utilizada:** BFA-TEST-03; BFA-BE-07/11; BFA-FE-07/09; Manual de Testing; Plan operativo.

**Registro de ejecución:** Sin registros.

## Matriz de cobertura: BFA-TEST-03, BFA-BE-07, BFA-BE-11, BFA-FE-07 y BFA-FE-09

| Ref. | Criterio de aceptación registrado (texto íntegro) | Caso(s) | Preparación / estado |
|---|---|---|---|
| `T03-CA01` | Conserva orden, texto y letra; acepta hasta 75 entradas de la edición; las repeticiones técnicas no duplican y las palabras dudosas quedan PENDIENTE hasta una decisión autorizada. | TC-BFA-TEST-03-001 a TC-BFA-TEST-03-009, TC-BFA-TEST-03-012 | Cobertura diseñada; fixture/material, revisión léxica y contrato pendientes. |
| `T03-CA02` | Cada criterio de las tareas cubiertas tiene al menos un caso `TC-`. | Toda la matriz BE07/BE11/FE07/FE09 de abajo | La matriz enumera cada criterio registrado de las cuatro tareas relacionadas. |
| `T03-CA03` | Cada PR cubierto tiene `testing-ok` o sus fallos comentados antes de la fusión. | TC-BFA-TEST-03-020 | PENDIENTE: PR/SHA y ejecución; no aplicar etiqueta en preparación. |
| `BE07-CA01` | El backend registra las palabras y conserva su texto original y orden. | TC-BFA-TEST-03-001, 012 | Diseñable; ejecución pendiente de PR/contrato/fixtures. |
| `BE07-CA02` | La letra asignada se obtiene de la configuración de la aplicación y permanece fija durante la subprueba. | TC-BFA-TEST-03-002 | DOC-04 exige letra formal para iniciar; fixture/material y PR aún pendientes. |
| `BE07-CA03` | Se aceptan únicamente entradas de texto no vacías, con orden único y dentro del máximo configurado. | TC-BFA-TEST-03-003, 004 | Contrato de validación y error pendiente. |
| `BE07-CA04` | La capacidad de la edición documentada permite hasta 75 palabras. | TC-BFA-TEST-03-003 | Capacidad aparece en DOC-02; fixture aprobada pendiente. |
| `BE07-CA05` | El registro utiliza E7 y es idempotente, sin generar duplicados por reenvío. | TC-BFA-TEST-03-005 | E7 está en DOC-05 Closed in ClickUp; ruta/DTO no establecidos como definitivos. |
| `BE07-CA06` | Se rechazan registros fuera del tiempo oficial o cuando la subprueba está cerrada. | TC-BFA-TEST-03-006, 013 | Duración conocida; autoridad temporal y errores exactos pendientes. |
| `BE07-CA07` | La validación estructural no rechaza palabras por criterios lingüísticos todavía no aprobados. | TC-BFA-TEST-03-007 | Diseñable; fixture léxica y reglas completas pendientes. |
| `BE07-CA08` | Las reglas de calificación aplicadas cuentan con respaldo documental. | TC-BFA-TEST-03-009 | Solo reglas confirmadas en DOC-02; palabras derivadas/repeticiones requieren ratificación. |
| `BE07-CA09` | Las respuestas ambiguas permanecen identificadas para revisión y no producen una puntuación definitiva injustificada. | TC-BFA-TEST-03-008, 009 | DOC-04 Revisión 2 define PENDIENTE_VALIDACION y el permiso profesional; cotejar E10 con la copia final de DOC-05, cuyo PDF visible está en v0.6. |
| `BE07-CA10` | Se reutiliza GEN-03 sin duplicar cronómetros ni gestión de estados. | TC-BFA-TEST-03-006, 013, 014, 019 | Revisión de integración/PR; arquitectura y estados de GEN-03 pendientes. |
| `BE07-CA11` | Las pruebas automatizadas verifican los casos válidos, inválidos, ambiguos y los límites de capacidad. | TC-BFA-TEST-03-003 a 009, 019 | PENDIENTE: PR/CI y fixture aprobada. |
| `FE07-CA01` | La letra asignada permanece visible durante toda la subprueba. | TC-BFA-TEST-03-002, 010 | UI/PR y fixture pendientes. |
| `FE07-CA02` | El evaluado puede introducir palabras en orden y avanzar mediante Enter. | TC-BFA-TEST-03-001, 010 | UI/PR y fixture pendientes. |
| `FE07-CA03` | La interfaz admite tildes, ñ y los caracteres permitidos por la configuración. | TC-BFA-TEST-03-010 | Los caracteres permitidos se rigen por configuración aprobada. |
| `FE07-CA04` | Se desactivan autocorrector y autocompletado donde corresponda. | TC-BFA-TEST-03-010 | Criterio funcional respaldado; UI/PR pendiente. |
| `FE07-CA05` | Se respeta la capacidad máxima configurada, hasta 75 entradas en la edición documentada. | TC-BFA-TEST-03-003 | Límite documentado; fixture/PR pendientes. |
| `FE07-CA06` | Las palabras se registran conservando su orden y texto original. | TC-BFA-TEST-03-001, 012 | Diseñable; contrato/UI pendientes. |
| `FE07-CA07` | El guardado no interrumpe la escritura y se verifica el objetivo documentado de menos de 500 ms. | TC-BFA-TEST-03-011 | PENDIENTE: método de medición, entorno y evidencia reproducible. |
| `FE07-CA08` | Se distinguen visualmente las respuestas pendientes de guardar, confirmadas y fallidas. | TC-BFA-TEST-03-012 | Diseñable; UI/PR pendientes. |
| `FE07-CA09` | Al recuperar una aplicación, se presentan las palabras previamente registradas sin reiniciar el tiempo. | TC-BFA-TEST-03-012 | Contrato de recuperación y PR pendientes. |
| `FE07-CA10` | El cronómetro oficial es de 5:00 y su autoridad corresponde al backend. | TC-BFA-TEST-03-006, 013 | 5:00 está documentado; DOC-06 figura Closed en ClickUp. La conformidad de la autoridad temporal se verificará contra el PR y el contrato vigente. |
| `FE07-CA11` | Al cerrarse la subprueba, no se permite continuar modificando respuestas. | TC-BFA-TEST-03-006, 013 | Diseñable; PR/contrato pendientes. |
| `FE07-CA12` | La interfaz no determina la validez lingüística ni calcula puntuaciones. | TC-BFA-TEST-03-007, 014 | Diseñable; UI/PR pendientes. |
| `FE07-CA13` | Las pruebas funcionales verifican escritura continua, recuperación, guardado y cierre. | TC-BFA-TEST-03-010 a 014, 019 | PENDIENTE: PR/UI y CI. |
| `BE11-CA01` | Solo los usuarios autorizados pueden revisar palabras. | TC-BFA-TEST-03-015, 018 | DOC-04 Revisión 2 define los permisos por rol/alcance; cotejar el contrato API con la copia final de DOC-05, cuyo PDF visible está en v0.6. |
| `BE11-CA02` | Cada decisión registra estado, motivo, fecha y actor responsable. | TC-BFA-TEST-03-015 | DOC-05 figura Closed en ClickUp; el esquema/API exacto debe verificarse en la copia final, no cotejada con el PDF v0.6 visible. |
| `BE11-CA03` | Se verifica que la palabra pertenezca a la evaluación y calificación correspondientes. | TC-BFA-TEST-03-016 | Relación funcional y DTO final pendiente. |
| `BE11-CA04` | Las decisiones permitidas son VÁLIDA, INVÁLIDA y PENDIENTE. | TC-BFA-TEST-03-008, 015 | Valores definidos en tarea de desarrollo; contrato Closed in ClickUp. |
| `BE11-CA05` | Las palabras pendientes no se consideran automáticamente incorrectas. | TC-BFA-TEST-03-008, 016 | Diseñable; fixture ambigua y flujo de revisión pendientes. |
| `BE11-CA06` | Las respuestas originales del evaluado permanecen inalteradas. | TC-BFA-TEST-03-001, 016 | Diseñable; historial/contrato pendientes. |
| `BE11-CA07` | Una decisión repetida no genera registros duplicados. | TC-BFA-TEST-03-005, 017 | E10/idempotencia final pendiente. |
| `BE11-CA08` | Las revisiones de calificaciones completadas no pueden modificarse directamente. | TC-BFA-TEST-03-017 | Flujo y estados de calificación en DOC-04/05 Closed in ClickUp. |
| `BE11-CA09` | Las modificaciones que afecten una calificación completada requieren una nueva revisión mediante el proceso de recálculo. | TC-BFA-TEST-03-017 | DOC-04 Revisión 2 describe la nueva revisión; cotejar el endpoint/campos P11 con la copia final de DOC-05 antes de ejecutar. |
| `BE11-CA10` | Las operaciones generan registros de auditoría. | TC-BFA-TEST-03-017 | DOC-05/06 figuran Closed en ClickUp; verificar los campos/eventos contra la copia final de DOC-05 y la implementación del PR. |
| `BE11-CA11` | Las pruebas automatizadas verifican decisiones, conflictos y restricciones de acceso. | TC-BFA-TEST-03-015 a 019 | PENDIENTE: PR/CI y matriz de permisos. |
| `FE09-CA01` | Solo los usuarios autorizados acceden a estas acciones. | TC-BFA-TEST-03-018 | PENDIENTE: permisos/roles y diseño de UI. |
| `FE09-CA02` | Revisar una palabra no publica un resultado. | TC-BFA-TEST-03-018 | Diseñable; P12/flujo de publicación en DOC-04/05 Closed in ClickUp. |
| `FE09-CA03` | La interfaz no calcula puntuaciones. | TC-BFA-TEST-03-014, 018 | Diseñable; UI/PR pendientes. |

**Lectura:** “cubierto” significa que existe un caso diseñado que apunta a ese criterio; no significa que el criterio haya sido ejecutado ni aprobado por Testing. “PENDIENTE” identifica los datos, decisiones o contratos que no deben suponerse.

## Bloqueos y decisiones pendientes

1. **DOC-07 (in progress):** no se ha cerrado la revisión documental, bloqueo directo de la tarea.
2. **DOC-08 (Open, última actualización 30 oct):** no consta material ficticio aprobado. No introducir palabras, reactivos ni datos reales; tampoco inventar una fixture lingüística. Sigue siendo bloqueo directo.
3. **DOC-04 (Closed in ClickUp):** el PDF adjunto Revisión 2 declara aprobación funcional. Un comentario del 4 nov indicó que el Google Doc BFA §3 aún tenía numeración anterior; no hay confirmación posterior de sincronización. Usar la Revisión 2 adjunta como referencia y cotejar la copia de Drive; no queda pendiente el estado de la tarea.
4. **DOC-05 (Closed in ClickUp):** los comentarios registran actualizaciones tras DOC-04 Revisión 2, incluido el ajuste E3 y permisos. El PDF adjunto visible sigue en v0.6 y un comentario del 5 nov pidió subir los documentos actualizados; por eso la copia final no está verificada aquí. Este documento no inventa rutas, DTO ni códigos HTTP de E7/E10/E12.
5. **DOC-06 (Closed in ClickUp):** un comentario de revisión registra D09 alineada: calificar al cerrar cada subprueba y consolidar al finalizar. La aplicación técnica y el uso de servicios comunes se comprobarán contra el PR, que aún no está disponible.
6. **Criterios léxicos:** DOC-02 completado confirma exclusiones y reglas generales, pero identifica como abiertos el tratamiento de palabras derivadas y las claves/decisiones que requieren UAM. La tarea de revisión de palabras también depende de permisos/documentación.
7. **Desarrollo:** BE07, BE11, FE07 y FE09 siguen Open en ClickUp; no se proporcionaron PR/SHA. Ninguna prueba funcional se ejecutó.

### Nota de aprobación

La matriz cubre los criterios de aceptación registrados en BFA-TEST-03 y en las tareas de Desarrollo relacionadas. Esto prueba cobertura en el borrador, no aprobación del plan, ejecución ni conformidad de un PR. Las pruebas léxicas están diseñadas sin palabras reales y no se considerarán listas para ejecutar hasta que se aprueben DOC-07/DOC-08 y la fuente pertinente.
