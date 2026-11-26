# BFA-TEST-02 — Casos de prueba: cálculo de puntuaciones y baremos

- **Archivo:** `BFA-TEST-02-casos-prueba-v2.md`
- **Versión:** v2, 2026-11-21 (actualizada el 23-nov con el PR #64): matriz del alcance del PR #50 (claves) y casos nuevos TC-017 a TC-027. v1 (2026-11-05) se conserva tal cual más abajo.
- **Tarea ClickUp:** [BFA-TEST-02](https://app.clickup.com/t/86bcw9z3k), estado **Open**, vence 2026-11-29, prioridad alta.
- **Responsable:** Francisco Israel ferrufino James.
- **Alcance de esta entrega:** diseño documental de casos únicamente. No se ejecutaron pruebas, no se modificó código ni ClickUp, no se publicó nada en GitHub y no se asignó `testing-ok`.
- **Convención de ejecución:** en todos los casos, resultado obtenido = **PENDIENTE DE EJECUCIÓN** y estado = **NO EJECUTADA**.

## Historial de cambios v1 → v2

- 23-nov: se agrega la sección «Revisión de fórmulas del PR #64 (BFA-BE-08)», que es la prioridad indicada por Nelson: comparación de fórmulas, ejecución de TC-001, 003 y 004 sobre el #64 con un archivo de verificación de Testing, y consultas C-1 a C-5.
- Se agrega la sección «Alcance actual del PR #50 (v2)», que es el primer entregable pedido el 17-nov: qué trae el PR #50 (HEAD `36e2b19`), una matriz de BE09-CA01 a CA08 frente al código y las pruebas del PR, los casos nuevos TC-017 a TC-027 y los bloqueos B-1 a B-12.
- Se actualizan las filas BE09 de la matriz de cobertura con los casos nuevos.
- No cambia ningún caso de v1 (TC-001 a 016): conservan su redacción, su autoría y su estado PENDIENTE DE EJECUCIÓN.
- Nada fue ejecutado. La revisión del PR #50 es estática (lectura del código).

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

- Esta tarea cubre [BFA-BE-08](https://app.clickup.com/t/86bcvah7x), [BFA-BE-09](https://app.clickup.com/t/86bcw9yw1) y [BFA-BE-10](https://app.clickup.com/t/86bcw9yw2), actualmente **Open** en ClickUp. No constan números de PR ni SHA.
- Requisitos completados en ClickUp: [DOC-02: Requisitos funcionales y no funcionales](https://app.clickup.com/t/86bctt9b8) y [DOC-03: Modelo de entidades y relaciones](https://app.clickup.com/t/86bctt9bt). DOC-02 contiene las fórmulas directas/compuestas y las tablas normativas empleadas para los cálculos independientes de este borrador.
- Cerradas en ClickUp el 2026-11-05: [DOC-04: Casos de uso](https://app.clickup.com/t/86bctt9cm) (17:54), [DOC-06: Arquitectura técnica](https://app.clickup.com/t/86bctt9db) (17:00) y [DOC-05: Contratos REST](https://app.clickup.com/t/86bctt9cy) (18:26). DOC-04 adjunta Revisión 2 (3 nov), que indica aprobación funcional. DOC-05 tiene cierre y comentarios posteriores de actualización, pero el PDF adjunto visible sigue siendo v0.6; las rutas/DTO de E10–E12 quedan por cotejar con la copia final. DOC-07 permanece *in progress* y DOC-08 *Open*. Las tareas BFA-TEST-02 y sus dependencias indican usar solo DOC en *completed*.
- Instrucciones consultadas: [Manual de Testing](https://github.com/Nell16/sistema-evaluaciones/blob/main/tests/README.md), [Plan operativo de Testing](https://github.com/Nell16/sistema-evaluaciones/blob/main/docs/pruebas/PLAN-OPERATIVO-TESTING.md) y [Plantilla oficial](https://github.com/Nell16/sistema-evaluaciones/blob/main/docs/pruebas/PLANTILLA-CASO-PRUEBA.md). El plan permite diseñar casos antes del PR; ejecutar exige contrato aprobado, ambiente, datos y PR comprobable. Si un documento sigue `Closed in ClickUp`, no declarar un resultado válido.
- Documentos de referencia: [Documento general](https://docs.google.com/document/d/1JQygN5h_5EH8Hf5hKo5GyPWsW4QABz85icf_kzaTtxk/edit); [Documento BFA](https://docs.google.com/document/d/12BEna1eJiSFJ6_1Re0qVObeJMKXZCOOTQ866DY678PA/edit); [reglas del repositorio](https://docs.google.com/document/d/1xIMUCVVzZYzav7kGCc671IxY1oadfgrg_r7qAX5U_Ns/edit); [Repositorio del proyecto](https://github.com/Nell16/sistema-evaluaciones).

## Qué puede prepararse y qué sigue bloqueado

**Preparado en este borrador:** cálculos manuales de N1, S2 y factores compuestos con entradas agregadas artificiales; chequeo de un mapeo N1 que aparece en la tabla normativa completada; escenarios para valor ausente, versiones, trazabilidad, seguridad, claves y baremos.

**PENDIENTE antes de cerrar el plan o ejecutar:** revisión/aprobación de DOC-07; material/fixtures ficticios DOC-08; confirmación de compatibilidad de la edición UAM con la norma 1992; confirmar que el Google Doc BFA §3 está sincronizado con DOC-04 Revisión 2 (DOC-04 figura Closed en ClickUp y el PDF adjunto declara aprobación funcional); cotejar la copia final del contrato DOC-05, porque el PDF visible está en v0.6 pese a que DOC-05 figura Closed en ClickUp; PR/SHA y ambiente de prueba. DOC-06 figura Closed en ClickUp y su comentario de revisión registra D09/ciclo de cálculo; su implementación se verificará contra el PR. Los casos que requieran esas piezas se mantienen como borradores con resultado esperado conceptual, sin inventar endpoints ni respuestas HTTP.

**Datos:** los números usados en TC-001 a TC-003 son exclusivamente valores agregados sintéticos para comprobar aritmética, no respuestas a reactivos ni material real. Las fixtures de claves y baremos sintéticos deben ser autorizadas antes de ejecutarse. No se incluyen claves ni contenidos psicológicos reales.

## Casos de prueba

### TC-BFA-TEST-02-001 — N1 positivo y aplicación del mínimo cero
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-BFA-CAL-02; DOC-04 CU-BFA-P07/P08; BE08-CA03; T02-CA01
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, cuando se coteje el contrato de cálculo con la copia final de DOC-05.
- **Preparación / ejecución:** Diseñable ahora con conteos sintéticos; ejecución bloqueada por contrato, PR y aprobación del fixture.

**Objetivo:** Comprobar que N1 combina aciertos y errores de Sumas y Multiplicaciones antes de aplicar el mínimo cero.

**Criterios de aceptación relacionados:** T02-CA01; BE08-CA02; BE08-CA03

**Precondiciones:**
Debe existir una versión aprobada de reglas; el componente de cálculo del PR debe estar disponible. No se inventará endpoint ni JSON: usar solo la interfaz finalmente aprobada en DOC-05.

**Datos de entrada:**
Fixture agregado artificial, no son respuestas ni ítems: Caso A (fixture aritmética sintética): Sumas 9 aciertos/2 errores; Multiplicaciones 6 aciertos/3 errores; esperado 10. Caso B: Sumas 2/5; Multiplicaciones 1/2; esperado 0. Caso C (ejemplo funcional explícito de DOC-04 §3.4): Sumas 6/9 y Multiplicaciones 9/2; N1 = max(0, 15 − 11) = 4. Son conteos agregados, no reactivos ni respuestas reales. Los valores sintéticos A/B siguen sujetos a aprobación de fixture.

**Pasos para reproducir:**
1. Cargar el fixture sintético en la interfaz aprobada del cálculo.
2. Solicitar el cálculo de N1 para Caso A.
3. Comparar el resultado con 10.
4. Repetir con Caso B y comparar con 0.
5. Repetir con el caso C documentado en DOC-04 §3.4 y comparar N1 con 4.
6. Comprobar que el límite cero se aplica después de consolidar ambos bloques, no por separado.

**Resultado esperado:** N1 combina los aciertos y errores de ambos bloques y aplica max(0,…) una sola vez durante la consolidación; A=10, B=0 y el ejemplo funcional C=4. En el ciclo aprobado, los conteos de bloque se guardan al cierre de cada subprueba (CU-BFA-P07) y N1 se deriva de ambos en consolidación (CU-BFA-P08). No se fijan nombres de campos ni error HTTP: confirmar contra el documento/API final antes de ejecutar.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-05 está Closed in ClickUp; PR/endpoint no registrado; fixture sintético pendiente de aprobación.

**Documentación utilizada:** DOC-02 §2.4 y §4.2 (completed); BFA-TEST-02; BFA-BE-08; DOC-05 (Closed in ClickUp).

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-002 — S2 positivo y aplicación del mínimo cero
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-BFA-CAL-02; criterio T02-CA01; BE08-CA02
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, cuando se coteje el contrato de cálculo con la copia final de DOC-05.
- **Preparación / ejecución:** Diseñable ahora con conteos sintéticos; ejecución bloqueada por contrato, PR y aprobación del fixture.

**Objetivo:** Verificar la regla oficial de S2 con un caso positivo y otro cuyo resultado bruto sea menor que cero.

**Criterios de aceptación relacionados:** T02-CA01; BE08-CA02

**Precondiciones:**
Regla disponible en DOC-02: S2=max(0, aciertos−errores). El cálculo debe estar accesible por contrato aprobado.

**Datos de entrada:**
Fixture agregado artificial, no incluye reactivos: Caso A: 8 aciertos y 2 errores, esperado 6. Caso B: 2 aciertos y 4 errores, esperado 0. Datos propuestos, no psicométricos.

**Pasos para reproducir:**
1. Ingresar el fixture A en la interfaz documentada del cálculo.
2. Solicitar S2 y cotejar con 6.
3. Repetir con el fixture B y cotejar con 0.
4. Verificar que una puntuación negativa se limita a cero, sin inventar valores adicionales.

**Resultado esperado:** S2=6 en A y S2=0 en B; nunca un valor negativo.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-05 figura Closed en ClickUp, pero la copia final del contrato no está cotejada con el PDF v0.6 disponible; PR/endpoint pendiente y fixture sintético pendiente de revisión.

**Documentación utilizada:** DOC-02 §2.4; BFA-TEST-02; BFA-BE-08; DOC-05 (Closed in ClickUp).

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-003 — Factores intermedios y fórmulas compuestas con cálculo independiente
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** RF-BFA-CAL-02/03; DOC-04 CU-BFA-P07/P08; DOC-05 E8/E9 (contrato adjunto v0.6; confirmar copia final); T02-CA01; BE08-CA01/02
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, cuando se coteje la copia final del contrato DOC-05; cotejo manual independiente.
- **Preparación / ejecución:** Las fórmulas están respaldadas por DOC-02 y DOC-04. DOC-04 define que subpruebas puntúan al cierre y que E8 consolida lo ya persistido. Los estados funcionales están definidos en DOC-04; confirmar cómo los campos se serializan en la copia final de DOC-05.

**Objetivo:** Comprobar, usando entradas sintéticas de puntuación directa, las sumas intermedias y las fórmulas VOCT, ST, RT, NT, EIV, EIC, EIG y AE.

**Criterios de aceptación relacionados:** T02-CA01; BE08-CA01; BE08-CA02

**Precondiciones:**
Precondiciones: DOC-02 §2.4-2.5 es la fuente para las fórmulas; no usar reactivos, claves reales ni baremos. La salida debe reflejar los estados funcionales de DOC-04 (CALIFICADA, SOLO_CONTEOS, PENDIENTE_VALIDACION, NO_CALCULABLE y, para percentil no disponible, SIN_BAREMO_VALIDADO); el nombre del campo y JSON exacto se cotejan con el contrato final.

**Datos de entrada:**
Fixture artificial de puntuaciones, no material de test: CV=10; VOC1=7; VOC2=5; FV=4; Figuras Idénticas=6; Ladrillos-Cubos=4; S2 aciertos=8/errores=2; R1=5; R2=7; Sumas=9/2; Multiplicaciones=6/3; Operaciones=8; Problemas=9. Cálculo independiente: VOCT=12; S1=10; S2=6; ST=16; RT=12; N1=10; N2=17; NT=27; EIV=36; EIC=67; EIG=103; AE=46. Fixture artificial sujeto a aprobación.

**Pasos para reproducir:**
1. Ingresar las puntuaciones sintéticas y sus conteos en el mecanismo aprobado.
2. Solicitar cálculo de factores y resultados.
3. Comparar cada valor con el cálculo independiente documentado en la entrada.
4. Verificar estado funcional de cada factor/resultado, usando los estados definidos por DOC-04; verificar el JSON/enum exacto solo contra el contrato final adjunto/aprobado.
5. Confirmar que FV aporta una vez a EIV y no a EIC/AE, conforme a las fórmulas.

**Resultado esperado:** Los valores coinciden exactamente con: VOCT=12, S1=10, S2=6, ST=16, RT=12, N1=10, N2=17, NT=27, EIV=36, EIC=67, EIG=103 y AE=46. DOC-04 define los estados de cálculo y conversión; la respuesta API debe representar esos estados conforme a la versión final de DOC-05, cuya copia actualizada no está adjunta en ClickUp. El estado/JSON HTTP no se infiere de esta prueba.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** PR de cálculo no disponible; falta cotejar los estados y campos con la copia final de DOC-05 (Closed en ClickUp); fixture requiere validación.

**Documentación utilizada:** DOC-02 §2.3-2.5 y §4.2 (completed); BFA-TEST-02; BFA-BE-08; DOC-05 y DOC-07.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-004 — Aplicación parcial y resultados no calculables sin convertir faltantes en cero
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** T02-CA01; DOC-04 CU-BFA-P08; BE08-CA04/05/07/08
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, solo con contrato y estados aprobados.
- **Preparación / ejecución:** DOC-04 §3.4 y CU-BFA-P08 ya definen la regla parcial y los estados funcionales. La combinación de subpruebas concreta sigue sujeta a una configuración publicada compatible; cotejar JSON con DOC-05 final.

**Objetivo:** Verificar que una evaluación parcial calcula únicamente factores con todos los componentes disponibles y válidos, y representa los demás como no calculables o pendientes, no como cero.

**Criterios de aceptación relacionados:** T02-CA01; BE08-CA04; BE08-CA05; BE08-CA07; BE08-CA08

**Precondiciones:**
Usar solo una configuración parcial publicada y compatible que aparezca en el contrato/documento aprobado. No elegir una combinación ni dependencias no registradas.

**Datos de entrada:**
Usar una aplicación parcial sintética con una subprueba/componente deliberadamente omitido, elegida solo después de aprobar la matriz de dependencias. El valor omitido es AUSENTE, nunca 0. Los datos de aplicación quedan PENDIENTES.

**Pasos para reproducir:**
1. Configurar la combinación parcial permitida, según el caso de uso aprobado.
2. Completar y cerrar únicamente componentes disponibles.
3. Solicitar cálculo/consolidación.
4. Inspeccionar factores calculables y no calculables, estados y motivos según contrato aprobado.
5. Comprobar que ningún dato AUSENTE se sustituyó por cero y que no se generó una puntuación definitiva para entradas pendientes.

**Resultado esperado:** Solo se calculan los factores con dependencias completas y válidas. Los componentes no aplicados quedan NO_CALCULABLE; faltas de clave/regla o revisión profesional quedan PENDIENTE_VALIDACION; no se sustituyen por cero. La evaluación puede finalizarse si las subpruebas incluidas cerraron, aunque FV quede pendiente; los factores dependientes no se presentan como definitivos. Un percentil sin baremo compatible conserva la puntuación directa con estado SIN_BAREMO_VALIDADO.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-04 y DOC-05 están Closed in ClickUp; matriz de dependencias/estados definitiva pendiente; fixture parcial no aprobado.

**Documentación utilizada:** DOC-02 §2.1, §4.2; BFA-TEST-02; BFA-BE-08; DOC-04/05.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-005 — Conversión por coincidencia normativa exacta conocida
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-10 / BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** Normas BFA 1992, tabla N1; BE10-CA04/05/10
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, cuando se apruebe el contrato de conversión.
- **Preparación / ejecución:** Diseñable ahora desde el dato publicado en DOC-02; ejecución bloqueada por confirmación de compatibilidad con la edición UAM y DOC-05.

**Objetivo:** Verificar una conversión conocida por el rango exacto del factor, sin usar fórmula universal.

**Criterios de aceptación relacionados:** T02-CA01; BE10-CA04; BE10-CA05; BE10-CA10

**Precondiciones:**
El baremo de prueba debe ser versión 1992 de Nicaragua y estar marcado aprobado/compatible para la edición de la prueba. La compatibilidad de edición se mantiene pendiente.

**Datos de entrada:**
Dato fuente aprobado en DOC-02, anexo normativo: para N1, puntuación directa 18 corresponde al percentil 50. No incorporar otro valor ni transformar la tabla en regla universal.

**Pasos para reproducir:**
1. Cargar o seleccionar el baremo aprobado y compatible, según el contrato final.
2. Consultar la conversión del factor N1 con puntuación directa 18.
3. Comparar el percentil con la fila N1 del percentil 50 en el anexo normativo DOC-02.
4. Comprobar que la conversión identifica el factor y la versión de baremo aplicados.

**Resultado esperado:** La conversión devuelve N1/percentil 50 para puntuación 18 usando exclusivamente la tabla 1992 aprobada y compatible; se conserva la versión normativa. Si la compatibilidad no está confirmada, no se declara una conversión válida y el caso queda bloqueado.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-02 documenta la tabla; la compatibilidad con la edición UAM está abierta; DOC-05 Closed in ClickUp y no hay PR.

**Documentación utilizada:** DOC-02 §2.7 y anexo normativo (completed); BFA-BE-10; DOC-05 (Closed in ClickUp).

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-006 — Puntuación sin rango: resultado nulo, con motivo, sin interpolación ni extrapolación
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-10 / BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** T02-CA01; BE08-CA07; BE10-CA06/07/10
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, cuando el contrato de conversión esté aprobado.
- **Preparación / ejecución:** Regla funcional y estado de salida definidos por DOC-04; la compatibilidad del baremo/edición debe estar aprobada y la copia final de DOC-05 debe confirmar el campo/API.

**Objetivo:** Verificar que una puntuación que no coincide con ningún rango normativo no recibe percentil inventado.

**Criterios de aceptación relacionados:** T02-CA01; BE08-CA07; BE10-CA06; BE10-CA07; BE10-CA10

**Precondiciones:**
Fuente DOC-02: N1 máximo documentado en la fila 99 es 39–43; para fixture sintético N1=44 no hay rango mostrado en la tabla. La compatibilidad del conjunto normativo debe aprobarse antes de ejecutar.

**Datos de entrada:**
Dato sintético de puntuación: N1=44. No se genera material de aplicación ni se supone una tabla adicional.

**Pasos para reproducir:**
1. Preparar el baremo normativo de prueba desde la tabla aprobada y compatible.
2. Solicitar conversión de N1=44.
3. Revisar el valor normativo, estado y motivo de indisponibilidad.
4. Confirmar que el sistema no interpola, extrapola, usa otra columna ni sustituye el percentil por 0.

**Resultado esperado:** Si la puntuación no tiene coincidencia normativa válida, se conserva el puntaje directo y el percentil queda nulo con estado SIN_BAREMO_VALIDADO y motivo; no se interpola, extrapola ni sustituye por cero. La conversión usa solo baremo compatible y versionado. No se inventa el campo JSON/código de error.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** Compatibilidad y contratos pendientes; no existe PR desplegable.

**Documentación utilizada:** DOC-02 §2.7 y anexo normativo; BFA-TEST-02; BFA-BE-10; DOC-05.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-007 — Carga/versionado de baremo conserva valores y celdas sin información
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-10
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE10-CA01/02
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman o revisión de carga del PR, según interfaz aprobada.
- **Preparación / ejecución:** Diseñable ahora con la tabla adjunta a DOC-02; DOC-05 figura Closed en ClickUp, pero el PDF visible es v0.6; confirmar interfaz/campos contra la copia final antes de ejecutar.

**Objetivo:** Comprobar que los datos normativos se conservan por versión, factor y rango, y que una celda vacía permanece vacía, no se transforma en cero.

**Criterios de aceptación relacionados:** BE10-CA01; BE10-CA02

**Precondiciones:**
Usar copia controlada de la tabla aprobada de DOC-02. No alterar la tabla normativa de origen.

**Datos de entrada:**
Celda fuente para comparar: FV en percentil 97 aparece como “-”; conservarla como ausencia de dato.

**Pasos para reproducir:**
1. Cargar o preparar una importación de prueba de la tabla normativa autorizada.
2. Comparar cantidad/valores de filas y columnas con el documento fuente.
3. Revisar la celda FV/percentil 97.
4. Verificar que la celda sin valor se conserva como ausencia de dato y que se identifican versión y factor.

**Resultado esperado:** Los rangos se conservan por versión/factor; una celda sin valor no genera percentil, interpolación ni cero. La consulta distingue puntuación directa, percentil y motivo de indisponibilidad; el JSON/campo exacto se coteja contra la copia final de DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** Falta cotejar la interfaz de carga y el contrato con la copia final de DOC-05 (Closed en ClickUp); PR de BFA-BE-10 no disponible.

**Documentación utilizada:** DOC-02 anexo normativo (completed); BFA-BE-10; DOC-05.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-008 — Detección de rango inválido o coincidencia ambigua
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-10
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE10-CA03/10
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman o verificación de carga del PR, según contrato aprobado.
- **Preparación / ejecución:** Diseñable ahora con fixture estructural artificial; requiere contrato y PR.

**Objetivo:** Verificar que la carga/validación rechaza rangos malformados o que producen más de una coincidencia para el mismo factor y puntuación.

**Criterios de aceptación relacionados:** BE10-CA03; BE10-CA10

**Precondiciones:**
Precondición: contar con PR comprobable, contrato aprobado y fixture de prueba autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Fixture exclusivamente sintético para validar estructura, no es un baremo psicométrico: dos rangos solapados del mismo factor (10–15 y 15–20), más un rango invertido (8–4). No desplegar ni presentar como norma real.

**Pasos para reproducir:**
1. Cargar el fixture estructural en el mecanismo de prueba autorizado.
2. Solicitar validación del baremo antes de aprobarlo.
3. Comprobar el resultado para la configuración inválida y para el valor compartido 15.
4. Revisar que el baremo no quede disponible para conversiones oficiales.

**Resultado esperado:** El sistema detecta la invalidez y la ambigüedad; no aprueba ni utiliza el fixture como baremo oficial. No se fija un código HTTP/error sin DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-05 Closed en ClickUp, pero el PDF adjunto v0.6 no refleja con certeza la última revisión; PR pendiente y fixture sintética requiere aprobación.

**Documentación utilizada:** BFA-BE-10; DOC-02 §2.7; DOC-05 (Closed in ClickUp).

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-009 — Uso exclusivo de baremos aprobados y compatibles con la edición
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-10 / BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE08-CA06; BE10-CA04/10
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, cuando se apruebe el contrato de selección de baremo.
- **Preparación / ejecución:** DOC-04 §3.4/P08 define el uso exclusivo de baremo compatible y versionado; DOC-07 sigue en progreso. Confirmar el DTO/API con la copia final de DOC-05.

**Objetivo:** Verificar que solo un baremo aprobado y compatible puede producir una conversión normativa oficial.

**Criterios de aceptación relacionados:** BE08-CA06; BE10-CA04; BE10-CA10

**Precondiciones:**
Precondición: contar con PR comprobable, contrato aprobado y fixture de prueba autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Fixtures sin valores normativos reales adicionales: una versión test aprobada/compatible y dos versiones de prueba no aprobada o incompatible. Identificadores y estados finales se tomarán del contrato aprobado.

**Pasos para reproducir:**
1. Preparar las tres versiones de fixture según el modelo aprobado.
2. Solicitar la conversión para la misma puntuación bajo cada versión.
3. Verificar que solo la versión aprobada/compatible puede calcular percentil.
4. Comprobar que las otras versiones no se aplican silenciosamente y dejan constancia del motivo.

**Resultado esperado:** Solo el baremo aprobado y compatible con el factor/edición se usa para percentil. Una versión incompatible/no aprobada no produce percentil normativo definitivo; registrar el estado y motivo según contrato final. No sustituir ni interpolar.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-05 Closed en ClickUp; compatibilidad exacta con edición UAM sigue pendiente en DOC-02; no hay PR.

**Documentación utilizada:** DOC-02 §2.7; BFA-BE-08/10; DOC-05/07.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-010 — Trazabilidad histórica de entradas, reglas, clave y baremo
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-08 / BFA-BE-09 / BFA-BE-10
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE08-CA09/10; BE09-CA07; BE10-CA08
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman más revisión del registro resultante.
- **Preparación / ejecución:** DOC-04 P11/P12 define nuevas revisiones con insumos/versiones trazables, preservación de resultados históricos y publicación separada. DOC-07 sigue pendiente; revisar los identificadores del JSON en la copia final de DOC-05.

**Objetivo:** Comprobar que cada cálculo guarda referencias suficientes para identificar insumos y versiones utilizadas, y que un cálculo posterior no sustituye el historial.

**Criterios de aceptación relacionados:** BE08-CA09; BE08-CA10; BE09-CA07; BE10-CA08

**Precondiciones:**
Precondición: contar con PR comprobable, contrato aprobado y fixture de prueba autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Usar dos revisiones de reglas/clave/baremo solo de prueba, con identificadores de fixture; no usar claves oficiales ni resultados reales.

**Pasos para reproducir:**
1. Crear una calificación de prueba con versión A de reglas/clave/baremo.
2. Conservar el resultado y sus referencias.
3. Crear una nueva revisión con versión B, usando el proceso aprobado.
4. Consultar ambas revisiones y comparar que cada una mantiene sus referencias originales.

**Resultado esperado:** Cada revisión nueva conserva las respuestas originales, entradas, versiones y resultados históricos; recalcular no modifica la publicación previa y requiere un acto explícito posterior de publicación. Validar la serialización/IDs exactos con la copia final de DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-03 completed; DOC-04/05/06 Closed en ClickUp. Verificar copia API final/IDs; PR y DOC-07 siguen pendientes.

**Documentación utilizada:** DOC-02 §4.2/7; DOC-03 (completed, comentarios con dependencias abiertas); DOC-05; BFA-BE-08/09/10.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-011 — Clave sintética aprobada y vinculada a la edición permite calificación de prueba
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-09 / BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE09-CA01/04/08; BE08-CA08
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, solo mediante la interfaz interna aprobada.
- **Preparación / ejecución:** Diseñable ahora a nivel de comportamiento; no ejecutable hasta disponer de clave ficticia aprobada, contrato e implementación.

**Objetivo:** Comprobar que una clave de prueba aprobada, versionada y compatible permite verificar respuestas sintéticas para la edición vinculada.

**Criterios de aceptación relacionados:** BE09-CA01; BE09-CA04; BE09-CA08; BE08-CA08

**Precondiciones:**
Precondición: contar con PR comprobable, contrato aprobado y fixture de prueba autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
No incluir ítems ni claves reales. Fixture de clave ficticia y respuestas sintéticas: PENDIENTE de DOC-08 y del responsable del material autorizado.

**Pasos para reproducir:**
1. Preparar clave y respuestas solo de prueba después de su aprobación.
2. Vincular la clave a una edición/version de formulario de prueba.
3. Solicitar el cálculo por el componente interno autorizado.
4. Comparar el resultado con el expected de la fixture sintética aprobada.
5. Verificar que versión y edición quedan identificadas.

**Resultado esperado:** La clave aprobada y compatible verifica las respuestas de la fixture sintética; la clave conserva edición/versión. No se usa ningún contenido real ni se documenta un endpoint no aprobado.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-08 no está completada; contrato/API y PR pendientes; claves oficiales de producción dependen de autorización UAM.

**Documentación utilizada:** DOC-02 §7; BFA-BE-09; BFA-BE-08; DOC-05/08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-012 — Clave inválida o decisión de Fluidez Verbal pendiente bloquea calificación definitiva
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-09 / BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE08-CA08; BE09-CA02/03/08; DOC-04 CU-BFA-P07/P08/P10
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman, solo con interfaz de test aprobada.
- **Preparación / ejecución:** Los estados funcionales PENDIENTE_VALIDACION y el tratamiento de FV pendiente se definen en DOC-04; fixtures y verificación de E10/JSON requieren DOC-08 y la copia final de DOC-05.

**Objetivo:** Verificar que las claves inválidas o no aprobadas quedan bloqueadas para generar puntuaciones oficiales.

**Criterios de aceptación relacionados:** BE08-CA08; BE09-CA02; BE09-CA03; BE09-CA08

**Precondiciones:**
Precondición: contar con PR comprobable, contrato aprobado y fixture de prueba autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Fixtures de prueba autorizadas: claves sintéticas incompleta, incompatible o no aprobada; evaluación con FV cerrada pero revisión incompleta. No incluir claves reales, reactivos reales ni palabras reales.

**Pasos para reproducir:**
1. Cargar cada fixture mediante la herramienta de prueba autorizada.
2. Solicitar una calificación de control para cada clave.
3. Solicitar consolidación de la evaluación con el componente FV todavía PENDIENTE.
4. Inspeccionar resultado, estado y motivo según contrato aprobado.
5. Confirmar que la puntuación afectada no se presenta como definitiva/oficial hasta aprobar la clave y resolver la revisión FV.

**Resultado esperado:** La subprueba cerrada puede persistir conteos; si falta clave queda SOLO_CONTEOS/PENDIENTE_VALIDACION según insumo. Si FV no tiene revisión completa, queda PENDIENTE_VALIDACION y no produce FV definitiva; P08 puede finalizar la evaluación sin fabricar FV ni compuestos dependientes. Una clave no aprobada no se usa para calificación oficial. Confirmar código/campo en copia final de DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-07 sigue en proceso y DOC-08 abierta; DOC-05 figura Closed, pero falta cotejar E10 y sus permisos con la copia final del contrato; no hay interfaz ni PR comprobable.

**Documentación utilizada:** DOC-02 §7; BFA-BE-09/08; BFA-TEST-03; DOC-05/07/08.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-013 — Claves y resultados protegidos frente a consumidores no autorizados
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-09 / BFA-BE-08
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE08-CA01; BE09-CA05/06
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman; matriz de permisos pendiente de aprobación.
- **Preparación / ejecución:** DOC-04 Revisión 2 define autorización por sesión, rol activo, permiso explícito y alcance; DOC-05 task Closed comenta los permisos. La copia API final aún debe cotejarse; DOC-07 sigue en progreso.

**Objetivo:** Verificar que respuestas correctas/claves no se exponen por interfaces públicas y que usuarios sin permiso no pueden consultar o modificar claves; resultados solo a consumidores autorizados.

**Criterios de aceptación relacionados:** BE08-CA01; BE09-CA05; BE09-CA06

**Precondiciones:**
Precondición: contar con PR comprobable, contrato aprobado y fixture de prueba autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Perfiles de prueba ficticios. Para revisión FV, usar Psicólogo con REVISAR_FLUIDEZ y alcance; el sujeto no recibe claves ni respuestas protegidas. Los endpoints/códigos se copian solo de la versión final de DOC-05; no registrar tokens ni credenciales.

**Pasos para reproducir:**
1. Crear o recibir perfiles ficticios conforme a la matriz de permisos aprobada.
2. Consultar los recursos permitidos y no permitidos usando solo rutas autorizadas en DOC-05.
3. Intentar consultar la clave por la interfaz pública y modificarla sin permiso.
4. Revisar respuestas sin guardar tokens/secretos.

**Resultado esperado:** Las claves/respuestas correctas no se exponen a la interfaz pública; cada operación sensible exige permiso y alcance. La revisión FV solo corresponde a Psicólogo autorizado con alcance; no se usan códigos HTTP/campos que no estén en la copia final de DOC-05.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-04/05/06 Closed en ClickUp; copia final del contrato debe verificarse; PR y perfiles de prueba pendientes.

**Documentación utilizada:** DOC-02 §4.2/4.3; DOC-04/05/06; BFA-BE-08/09.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-014 — Responsabilidad de cálculo en backend y reutilización del evaluador común
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-08 / BFA-BE-10
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE08-CA11; BE10-CA09
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** Revisión manual del PR/diff, no ejecución funcional.
- **Preparación / ejecución:** Puede definirse como revisión de PR; DOC-06 figura Closed en ClickUp y la conformidad de la implementación se comprobará contra el PR.

**Objetivo:** Verificar en el PR que las reglas normativas/cálculos estén en backend y que se reutilice el evaluador/servicios comunes sin duplicar responsabilidad en BFA o frontend.

**Criterios de aceptación relacionados:** BE08-CA11; BE10-CA09

**Precondiciones:**
Precondición: contar con PR comprobable, contrato aprobado y fixture de prueba autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Se requiere PR/commit y arquitectura aprobada. No se modifica código ni se afirma conformidad por inspección parcial.

**Pasos para reproducir:**
1. Abrir el PR y registrar SHA.
2. Comparar el diff con la arquitectura aprobada de DOC-06.
3. Inspeccionar ubicación de reglas de cálculo/baremación y comprobar que el frontend no calcula percentiles.
4. Registrar evidencia textual del archivo/capa revisada sin proponer cambios como si estuvieran implementados.

**Resultado esperado:** La lógica normativa permanece en backend y los servicios del evaluador común se reutilizan sin duplicar responsabilidades, según arquitectura finalmente aprobada.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** DOC-06 Closed en ClickUp; PR/commit no disponible para revisar.

**Documentación utilizada:** DOC-06; BFA-BE-08/10; Manual de Testing y Plan operativo.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-015 — Evidencia de pruebas automatizadas de cálculo, claves y baremos en el PR
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-08 / BFA-BE-09 / BFA-BE-10
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** BE08-CA12; BE09-CA08; BE10-CA10
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** Revisión manual de PR/CI y resultados automatizados.
- **Preparación / ejecución:** Diseñable ahora; requiere PR con suite ejecutada y fixtures autorizadas.

**Objetivo:** Confirmar que el PR incorpora y ejecuta pruebas automatizadas para escenarios completos/parciales/pendientes, fórmulas, límites, baremos y claves.

**Criterios de aceptación relacionados:** BE08-CA12; BE09-CA08; BE10-CA10

**Precondiciones:**
Precondición: contar con PR comprobable, contrato aprobado y fixture de prueba autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
No crear ni modificar pruebas de producción. Revisar únicamente evidencia publicada por Desarrollo/CI en el PR.

**Pasos para reproducir:**
1. Abrir cada PR del grupo y registrar PR/SHA.
2. Revisar los resultados del CI y los tests existentes relacionados con la matriz de este documento.
3. Comprobar que los escenarios indicados tienen resultado automatizado verificable.
4. Registrar ausencias como hallazgo; no inferir éxito a partir de CI verde únicamente.

**Resultado esperado:** Hay pruebas automatizadas ejecutadas que cubren los escenarios citados; la evidencia corresponde al SHA revisado. CI verde por sí solo no constituye `testing-ok`.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** PRs BFA-BE-08/09/10 no están identificados ni disponibles; fixtures aprobadas pendientes.

**Documentación utilizada:** BFA-BE-08/09/10; Manual de Testing; Plan operativo.

**Registro de ejecución:** Sin registros.

### TC-BFA-TEST-02-016 — Prueba por PR y registro de conformidad/fallos antes de fusión
- **Tarea de Testing:** BFA-TEST-02
- **Tarea(s) de Desarrollo:** BFA-BE-08 / BFA-BE-09 / BFA-BE-10
- **PR y SHA revisado:** PENDIENTE; no están registrados y no se ha accedido a un PR.
- **Requisito / CU / contrato relacionado:** T02-CA03; Plan operativo § BFA-TEST-02
- **Tester:** Francisco Israel ferrufino James.
- **Fecha y entorno:** diseño v0 preparado el 2026-11-04, v1 actualizado el 2026-11-05; entorno de ejecución PENDIENTE.
- **Tipo:** API / Postman por PR backend; revisión manual del registro.
- **Preparación / ejecución:** Solo diseñable; bloqueado hasta PR identificados, contratos/documentación aprobados, entorno y datos preparados.

**Objetivo:** Asegurar que cada PR incluido se prueba contra su combinación propuesta con main y recibe registro con evidencia antes de fusionarse.

**Criterios de aceptación relacionados:** T02-CA03

**Precondiciones:**
Precondición: contar con PR comprobable, contrato aprobado y fixture de prueba autorizada; resolver los bloqueos de este caso antes de ejecutar.

**Datos de entrada:**
Los números de PR/SHA son PENDIENTES; no completar con valores inventados. Cada PR debe estar disponible y actualizado con main.

**Pasos para reproducir:**
1. Para cada PR real de BFA-BE-08, BFA-BE-09 y BFA-BE-10, registrar número y SHA.
2. Ejecutar únicamente los casos aplicables de esta matriz en el entorno permitido, usando Postman contra el contrato aprobado.
3. Si pasa, registrar comentario con `Testing OK: TC-…`, PR, SHA y evidencia, y solicitar/aplicar `testing-ok` según permisos.
4. Si falla, documentar TC, esperado/obtenido/evidencia en el PR; no etiquetar `testing-ok`.
5. Confirmar que cada resultado se registró antes de la fusión.

**Resultado esperado:** Cada PR cubierto cuenta con pruebas y comentario/evidencia; cada aprobado tiene `testing-ok` según el proceso. Los fallos están documentados antes de fusión y no reciben la etiqueta. Esta prueba no se ejecuta en este borrador.

**Resultado obtenido:** PENDIENTE DE EJECUCIÓN.

**Estado:** NO EJECUTADA.

**Evidencia:** Ninguna generada; no se ejecutó la prueba.

**Defecto asociado:** No aplica; no se ejecutó.

**Bloqueo / dependencia:** No se localizaron números/SHAs de PR; DOC-07/08 y contratos pendientes; no ejecutar ahora.

**Documentación utilizada:** BFA-TEST-02; BFA-BE-08/09/10; Manual de Testing; Plan operativo.

**Registro de ejecución:** Sin registros.

## Revisión de fórmulas del PR #64 (BFA-BE-08) — prioridad del 23-nov

> Lo pidió Nelson el 23-nov: priorizar las fórmulas del PR #64, ejecutar los casos aplicables de N1 y su piso cero, compuestos, parciales y datos faltantes con resultados calculados aparte, y dar conformidad **solo sobre lo probado**. Baremos, persistencia e integración completa quedan para sus propias entregas, y la tarea no se cierra por este PR.

### Identificación

| Dato | Valor |
| --- | --- |
| PR | [#64](https://github.com/Nell16/sistema-evaluaciones/pull/64), BFA-BE-08 «Implementar contrato TestEvaluator y evaluador psicométrico BFA» |
| HEAD | `9b1ad73d40f369239d3ae37cbd761eb777ae426f` (20-nov, ya sincronizado con `main` `ef0317e`) |
| SHA combinado probado (`pull/64/merge`) | `32e5a41` (merge de `9b1ad73` sobre `main` `ef69e2e`), 23-nov. Pendiente de confirmar con Kisnner. |
| Alcance del PR | 9 archivos y 532 líneas: `TestEvaluator`, `TestEvaluatorRegistry`, `ResultadoEvaluacion`, `FactorCalculado`, `BfaTestEvaluator`, `ConsolidadorCompuestosBfa`, `EvaluatorConfiguration` y 2 clases de prueba (7 pruebas). Sin endpoints, sin base de datos, sin baremos. |
| Método | Pruebas del dominio y del evaluador. Testing agrega un archivo propio, [`PR64VerificacionTestingTest.java`](../evidencias/BFA-TEST-02/PR64VerificacionTestingTest.java), con 14 verificaciones y valores calculados a mano. Se copia **solo en una copia aparte** de `pull/64/merge`; no forma parte del PR (mismo método que BFA-TEST-01 con el PR #45). |

### Fórmulas: documento frente a código

Cálculo independiente con el fixture de TC-003: CV=10, VOC1=7, VOC2=5, FV=4, S1=6+4=10, S2=8−2=6, R1=5, R2=7, N1=10, N2=8+9=17.

| Resultado | Fórmula (DOC-02 §2.4-2.5) | Código del PR #64 | Esperado | ¿Coincide la fórmula? |
| --- | --- | --- | --- | --- |
| VOCT | VOC1 + VOC2 | `agregar(VOCT, VOC1, VOC2)` | 12 | Sí |
| ST | S1 + S2 | `agregar(ST, S1, S2)` | 16 | Sí |
| RT | R1 + R2 | `agregar(RT, R1, R2)` | 12 | Sí |
| NT | N1 + N2 | `agregar(NT, N1, N2)` | 27 | Sí |
| EIV | 2·CV + VOCT + FV | `agregarPonderado(EIV, CV·2, VOCT·1, FV·1)` | 36 | Sí |
| EIC | 2·RT + ST + NT | `agregarPonderado(EIC, RT·2, ST·1, NT·1)` | 67 | Sí |
| EIG | EIV + EIC | `agregar(EIG, EIV, EIC)` | 103 | Sí |
| AE | 2·RT + CV + VOCT | `agregarPonderado(AE, RT·2, CV·1, VOCT·1)` | 46 | Sí |
| N1 | max(0, (aciertos A + B) − (errores A + B)) | `calcularN1(aA, eA, aB, eB)` | A=10, B=0, C=4 | Sí |
| S2 | max(0, aciertos − errores) | **No está en el PR**: S2 llega ya calculado | 6 | No aplica (C-2) |
| S1, N2 | Suma de sus dos sub-bloques | **No está en el PR**: llegan ya sumados | 10, 17 | No aplica (C-2) |

### Ejecución sobre el PR #64

| ID | Qué se verifica | Verificaciones en el archivo de Testing | Resultado esperado | Obtenido | Estado |
| --- | --- | --- | --- | --- | --- |
| TC-BFA-TEST-02-001 (PR #64) | N1 y su piso cero | TC-001 A a E | A=10, B=0, C (DOC-04 §3.4)=4. D: con bloques (2,5) y (9,1) da **5**; si el piso se aplicara por bloque daría 8. E: rechaza un conteo negativo en cualquiera de las 4 posiciones. | Las 5 verificaciones dieron lo esperado: A=10, B=0, C=4, D=5, y E lanza `IllegalArgumentException` en las 4 posiciones. | **PASÓ** |
| TC-BFA-TEST-02-002 (PR #64) | S2 con piso cero | — | El PR no calcula S2. | No cubierto por el #64 | **BLOQUEADO** (C-2) |
| TC-BFA-TEST-02-003 (PR #64) | Los 8 compuestos, los pesos y dónde entra FV | TC-003 A a D | Los valores de la tabla de arriba. FV solo cambia EIV y EIG. CV pesa 2 en EIV y 1 en AE; RT pesa 2 en EIC y en AE. El evaluador devuelve `PENDIENTE_VALIDACION`, `resultadoId` nulo, 18 factores y ningún percentil. | Las 4 verificaciones dieron lo esperado: VOCT=12, ST=16, RT=12, NT=27, EIV=36, EIC=67, EIG=103 y AE=46; pesos y FV correctos; `PENDIENTE_VALIDACION`, `resultadoId` nulo, 18 factores sin percentil. | **PASÓ** |
| TC-BFA-TEST-02-004 (PR #64) | Parciales y faltantes sin cero | TC-004 A a E | FV=0 es un valor (EIV=32). Sin R2: pendientes RT, EIC, EIG y AE, y se calculan VOCT, ST, NT y EIV. Sin CV: pendientes EIV, EIG y AE, y EIC=67. FV nulo: pendiente, sin valor. Sin datos: los 8 pendientes y ninguno vale 0. | Las 5 verificaciones dieron lo esperado. Ningún faltante se convirtió en 0. El estado de los pendientes es `PENDIENTE` (ver C-1). | **PASÓ** |
| Pruebas del PR | Regresión | `mvn verify` completo | `BUILD SUCCESS`; incluye las 7 pruebas del #64 y el formato Spotless. | `Tests run: 40, Failures: 0, Errors: 0`; Spotless sin cambios; `BUILD SUCCESS`. | **PASÓ** |

**Ejecución oficial (23-nov, 13:14 y 13:28, America/Managua):** Windows 11, Java 17.0.21 (Microsoft), Maven 3.9.9, PostgreSQL 17 en Docker (puerto local 55432), copia aparte `git worktree` de `pull/64/merge` `32e5a41`.

1. Verificación de Testing: `mvn test -Dtest=PR64VerificacionTestingTest,ConsolidadorCompuestosBfaTest,BfaTestEvaluatorTest`, con el archivo de Testing copiado solo en la copia aparte. Resultado: `Tests run: 21, Failures: 0` (14 de Testing + 7 del PR), `BUILD SUCCESS`. Evidencia: [`PR64-testing-2026-11-23.txt`](../evidencias/BFA-TEST-02/PR64-testing-2026-11-23.txt).
2. Regresión: `mvn verify` sobre la copia sin cambios (`git status` vacío). Resultado: `Tests run: 40, Failures: 0, Errors: 0`, Spotless limpio, `BUILD SUCCESS`. Evidencia: [`PR64-verify-2026-11-23.txt`](../evidencias/BFA-TEST-02/PR64-verify-2026-11-23.txt).

Un intento anterior de `mvn verify` falló por causas del entorno (puerto 5432 bloqueado, carpeta equivocada y el archivo de Testing presente en la copia). Esas salidas no se usan como evidencia.

**Revisión previa (no es la ejecución oficial):** el 23-nov se compilaron las 5 clases del evaluador del PR #64 (`9b1ad73`) junto con el archivo de Testing, usando un JUnit mínimo en un entorno sin Maven. Las 14 verificaciones dieron el resultado esperado. La ejecución oficial y su evidencia se hacen con Maven sobre `pull/64/merge`.

### Consultas para el PR #64

| ID | Consulta | Para | Afecta |
| --- | --- | --- | --- |
| C-1 | Un factor incompleto queda con `estadoCalculo` **`PENDIENTE`** y motivo `FALTA_PUNTUACION_X`. DOC-04 define `NO_CALCULABLE` (componente no aplicado) y `PENDIENTE_VALIDACION` (falta clave o revisión), y la pantalla de resultados de GEN-05 solo reconoce `CALCULADO`, `NO_CALCULABLE` y `PENDIENTE_VALIDACION`: `PENDIENTE` se mostraría como código crudo. ¿Se traduce al guardar o en E9, o se cambia el evaluador? | Max / BFA-BE-08, Documentación (DOC-04/05) | TC-004, GEN-05 |
| C-2 | El PR no calcula S2 = max(0, aciertos − errores) ni las sumas de sub-bloques de S1 y N2: los recibe ya calculados. ¿En qué componente se calculan? | Max / BFA-BE-08 | TC-002, TC-003 |
| C-3 | `consolidar` acepta puntuaciones negativas sin avisar (VOC1=−3 da VOCT=2), aunque N1 sí rechaza conteos negativos. ¿Se validan antes de llegar al evaluador? | Max / BFA-BE-08 | TC-004 |
| C-4 | En los compuestos en cadena, el motivo indica el compuesto intermedio y no el dato que falta (sin R2, EIG dice `FALTA_PUNTUACION_EIC`). ¿Es suficiente para DOC-04, o debe nombrar la puntuación base que falta? | Documentación | TC-004 |
| C-5 | `calcularN1` existe, pero `consolidar` recibe N1 ya calculado y no lo usa. ¿Quién llama a `calcularN1` en el flujo de E8? | Max / BFA-BE-08 | TC-001 |

**Conformidad:** TC-001, 003 y 004 **PASÓ** en `pull/64/merge` `32e5a41`. La conformidad se emite solo sobre esos casos y ese SHA; no cubre TC-002 (S2), baremos, persistencia ni integración con E8. C-1 a C-5 se comentan en el PR. `testing-ok` solo si los casos pasan y Integración acepta que C-1 a C-5 no bloquean esta parte.

## Alcance actual del PR #50 (v2)

> **Primer entregable pedido el 17-nov:** matriz del alcance actual del PR #50 y bloqueos que necesitan respuesta de Max o de Integración. Nada de esta sección está ejecutado. Antes de una ejecución final hay que acordar con Integración cómo se divide y valida el alcance (B-11).

### Identificación del PR revisado

| Dato | Valor |
| --- | --- |
| PR | [#50](https://github.com/Nell16/sistema-evaluaciones/pull/50), borrador, de BFA-BE-09 (Max Martínez, apoyo de Carlos Ávalos). BFA-BE-09 está en *in review* en ClickUp. |
| HEAD revisado | `36e2b19f9c0602136ba028f8b76da723bff36105` (17-nov). Es el mismo que indica la tarea. |
| Base del PR | `d33f6a8`. `main` avanzó 9 commits, hasta `ef0317e`. Una fusión de prueba (`git merge-tree`) **no da conflictos** al 21-nov. |
| Tamaño | 33 archivos y 6075 líneas añadidas, sin borrar nada. Son solo de backend: ningún archivo de frontend. |
| Revisión | Estática, leyendo el código del PR (dominio, servicios, adaptadores, migraciones y nombres de las pruebas). **No se compiló ni se ejecutó.** |

### Qué contiene el PR #50

| Pieza | Archivos | Qué hace |
| --- | --- | --- |
| Dominio | `VersionClaveBFA`, `EntradaClaveBFA`, `EstadoVersionClaveBFA` | La clave pertenece a un formulario (`idVersionFormulario`) y tiene `version` y `referenciaFuente` obligatorias. Estados: BORRADOR → APROBADA (con evidencia) → RETIRADA (conserva la evidencia). Solo una APROBADA sirve para calificaciones nuevas. |
| Servicios | `GestionClaveBFAService`, `AprobacionClaveBFAService`, `ValidacionCoberturaClaveBFAService`, `ValidacionClaveBFAParaCalificacionService` | Crear clave; registrar entradas una por una o por lote (atómico y sin preguntas repetidas); consultar; aprobar si la cobertura está completa; retirar; validar la cobertura exacta de las preguntas de **selección**; entregar las entradas solo de una clave APROBADA, completa y del mismo formulario. |
| Autorización | `AutorizacionClaveBFAPort` y `AutorizacionClaveBFADenegadaAdapter` (perfil `!seguridad-integrada`) | Un permiso por operación: crear, agregar, consultar, aprobar, retirar y calificar. El adaptador provisional **niega todo**. El permiso de calificar es distinto del de consultar y, por defecto, también se niega. |
| Persistencia | Entidades JPA, repositorios y 2 adaptadores | Comprueba que la pregunta sea del formulario de la clave y que la opción sea de la pregunta. Rechaza entradas duplicadas y versiones duplicadas por formulario. Aprobar y retirar vuelven a exigir el permiso. |
| Base de datos | `V202611121500`, `V202611141830` y `V202611141900` | Tablas `version_clave_bfa` y `entrada_clave_bfa` con restricciones UNIQUE y CHECK. Disparadores que: solo permiten crear en BORRADOR; impiden cambiar identidad, versión o fuente; solo permiten BORRADOR→APROBADA y APROBADA→RETIRADA; impiden modificar entradas y agregarlas a claves no BORRADOR; impiden borrar claves o entradas APROBADAS o RETIRADAS. |
| Pruebas del PR | 11 clases: 2 de dominio, 4 de servicio con dobles y 5 de integración con PostgreSQL | Usan la base `evaluaciones_bfa_test`, con `flyway.out-of-order: true` solo en pruebas. |
| CI | `.github/workflows/ci.yml` | Agrega el paso «Crear base de pruebas BFA» y líneas en blanco. **Cambia un archivo común de CI** (B-12). |

**No contiene:** endpoints REST ni controladores (0 archivos), un perfil `seguridad-integrada` con permisos reales, la integración con el cálculo de BFA-BE-08 (no hay registro de qué clave usó cada calificación), baremos (BFA-BE-10) ni fórmulas.

### Matriz de alcance: criterios de BFA-BE-09 frente al PR #50

| Criterio de BFA-BE-09 (texto de ClickUp) | Qué hay en el PR #50 | Pruebas del PR (por nombre) | Caso de Testing | Cobertura |
| --- | --- | --- | --- | --- |
| BE09-CA01 Cada clave queda vinculada a una edición y versión identificable del formulario. | FK a `version_formulario`, `version` única por formulario, identidad inmutable | `debeRechazarVersionClaveDuplicadaEnMismoFormulario`, `debeImpedirCambiarLaVersionDeClave` | TC-017 (y TC-011 de v1) | **Parcial:** falta confirmar que «edición» = `version_formulario` (B-6). |
| BE09-CA02 El sistema detecta claves incompletas, incompatibles o inconsistentes. | Cobertura exacta; opción de otra pregunta; pregunta de otro formulario; duplicados | `debeRechazarOpcionDeOtraPregunta`, `debeRechazarPreguntaDeOtroFormulario`, `debeRechazarClaveConCoberturaIncompleta`, `debeRechazarClaveConPreguntaAdicional` | TC-018, TC-019, TC-020 | **En el PR.** |
| BE09-CA03 Una clave no aprobada no puede utilizarse para generar calificaciones oficiales. | `obtenerClaveAprobada` rechaza BORRADOR, RETIRADA y otro formulario | `debeRechazarClaveEnBorradorDesdePostgreSQL`, `debeRechazarClaveRetiradaDesdePostgreSQL` | TC-024 (y TC-012 de v1) | **Parcial:** no hay calificación real que la consuma (BFA-BE-08, B-9). |
| BE09-CA04 Las claves aprobadas permiten comprobar las respuestas de las preguntas correspondientes. | Entrega las entradas de una clave aprobada y completa | `debeEntregarEntradasDeClaveCompletaYAutorizada` | TC-024 | **Parcial:** entrega las entradas, pero ninguna respuesta se corrige todavía (B-9). |
| BE09-CA05 Las respuestas correctas no se exponen mediante los endpoints públicos del sistema. | No hay endpoints | — | TC-026 | **Se cumple porque no hay API.** Se repite cuando haya endpoints de claves. |
| BE09-CA06 Los usuarios sin autorización no pueden consultar ni modificar claves. | Puerto de permisos; adaptador provisional que niega todo | `debeDenegarAprobacionSinPermisoDeAprobar`, `debeRechazarConsultaDeClaveSinAutorizacion`, `debeDenegarEntregaSinPermisoDeCalificacion` | TC-025 (y TC-013 de v1) | **Parcial:** solo «nadie puede». Los roles reales de DOC-05 no existen (B-3). |
| BE09-CA07 Las claves utilizadas en cálculos históricos permanecen identificables y no se sustituyen silenciosamente. | Disparadores de inmutabilidad y de no borrar; el retiro conserva la evidencia | `debeImpedirModificarUnaEntradaYaRegistrada`, `debeImpedirEliminarClaveAprobada`, `debeImpedirReactivarClaveRetirada` | TC-022, TC-023 (y TC-010 de v1) | **Parcial:** la clave no se puede alterar, pero no hay registro de qué clave usó cada cálculo (B-5). |
| BE09-CA08 Las pruebas automatizadas verifican claves válidas, incompletas, incompatibles y no autorizadas. | 11 clases de prueba | Todas las anteriores | TC-027 (y TC-015 de v1) | **En el PR:** hay que ejecutarlas en `pull/50/merge`. |
| Pedido el 17-nov: aprobación y retiro | `AprobacionClaveBFAService` | `debeAprobarClaveCompletaYGuardarEvidenciaEnPostgreSQL`, `debeRetirarClaveAprobadaYConservarEvidencia` | TC-021, TC-022 | **En el PR.** |
| Pedido el 17-nov: carga y compatibilidad | Carga individual y por lote, atómica | `debePersistirTodaLaCargaMultipleValida`, `debeRevertirCargaCompletaSiFallaLaSegundaEntrada` | TC-018, TC-019 | **En el PR.** |
| Entregable de BFA-BE-09: «Integración con las reglas de calificación BFA» | No está | — | TC-012 de v1 | **Fuera del PR** (BFA-BE-08). |

**Fuera del PR #50, sigue pendiente:** BE08-CA01 a CA12 y BE10-CA01 a CA10 (TC-001 a TC-010 y TC-014 de v1). Según la tarea, no se emite conformidad final ni se cierra BFA-TEST-02 por aprobar solo las claves.

### Casos nuevos de claves (v2)

Se aplica la misma convención que en v1: **resultado obtenido = PENDIENTE DE EJECUCIÓN** y **estado = NO EJECUTADA**. Se ejecutan en local sobre `pull/50/merge`, con PostgreSQL de `docker compose` y la base de pruebas, siguiendo las instrucciones de Max (B-2). Es una ejecución de servicios y base de datos: **no se usa Postman** porque no hay endpoints. Todos los datos son sintéticos: formulario, preguntas y opciones ficticias, sin claves ni ítems reales.

Las verificaciones independientes de base de datos (marcadas «SQL») se hacen con `psql` sobre la base de pruebas, después de `mvn verify`, con filas ficticias creadas por Testing. No reemplazan las pruebas del PR; las confirman por fuera.

| ID | Criterio | Datos y precondiciones | Pasos | Resultado esperado | Obtenido | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-BFA-TEST-02-017 — Vínculo clave–formulario y versión | BE09-CA01 | Formulario sintético F1 con una subprueba de selección | 1. Crear clave «K-v1» para F1. 2. Crear otra «K-v1» para F1. 3. Crear clave para un formulario inexistente. 4. SQL: `UPDATE` de `version` o `id_version_formulario`. | 1: BORRADOR, sin evidencia. 2: rechazo por versión duplicada. 3: rechazo por formulario inexistente. 4: el disparador rechaza el cambio de identidad o versión. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-018 — Carga de entradas compatibles | BE09-CA02, BE09-CA04 | K-v1 en BORRADOR; preguntas P1 y P2 de F1 con sus opciones; P9 de otro formulario | 1. Registrar P1 con su opción. 2. P2 con una opción de P1. 3. P9. 4. P1 otra vez. | 1: guardada. 2: «la opción no pertenece a la pregunta». 3: «la pregunta no pertenece al formulario». 4: rechazo por duplicado. En 2 a 4 no se guarda nada. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-019 — Carga por lote atómica | BE09-CA02 | K-v1 en BORRADOR | 1. Lote válido de 2 entradas. 2. Lote con la 2.ª entrada inválida. 3. Lote con una pregunta repetida, vacío o con entradas de otra clave. | 1: guarda las 2. 2: no queda **ninguna** entrada del lote. 3: rechazo antes de guardar. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-020 — Cobertura exacta de preguntas de selección | BE09-CA02 | F1 con 2 preguntas de selección y una subprueba de texto libre (tipo FV) | 1. Validar cobertura con 1 de 2 entradas. 2. Con 2 de 2. 3. Formulario sin preguntas de selección. | 1: «no cubre exactamente todas las preguntas». 2: válida, y las preguntas de texto libre no se exigen. 3: rechazo. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-021 — Aprobación | Pedido 17-nov; BE09-CA03 | K-v1 incompleta y K-v2 completa, ambas en BORRADOR | 1. Aprobar K-v1 con evidencia. 2. Aprobar K-v2 sin evidencia o con evidencia en blanco. 3. Aprobar K-v2 con evidencia. 4. Aprobar K-v2 otra vez. | 1: rechazo y K-v1 sigue en BORRADOR. 2: rechazo. 3: APROBADA y la evidencia queda guardada. 4: rechazo («solo BORRADOR»). | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-022 — Retiro conserva evidencia | Pedido 17-nov; BE09-CA07 | K-v2 APROBADA y K-v3 en BORRADOR | 1. Retirar K-v3. 2. Retirar K-v2. 3. Retirar K-v2 otra vez. 4. SQL: pasar K-v2 a APROBADA o a BORRADOR. | 1: rechazo. 2: RETIRADA, con la **misma** evidencia. 3: rechazo. 4: el disparador rechaza la transición. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-023 — Inmutabilidad histórica en la base (SQL) | BE09-CA07 | K-v2 APROBADA o RETIRADA, con entradas | SQL: 1. `UPDATE` de una entrada. 2. `INSERT` de una entrada nueva. 3. `DELETE` de una entrada. 4. `DELETE` de la clave. 5. Cambiar `evidencia_aprobacion`. 6. `INSERT` de una clave directamente en APROBADA. | Todos rechazados por los disparadores, con su mensaje, y los datos quedan iguales. Control: en una clave BORRADOR, el `DELETE` sí se permite. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-024 — Uso para calificación | BE09-CA03, BE09-CA04 | K-v2 APROBADA y completa; K-v1 en BORRADOR; K-v4 RETIRADA; F2 otro formulario. Permiso de calificar concedido con un doble de prueba | 1. Pedir entradas de K-v2 con F1. 2. K-v2 con F2. 3. K-v1. 4. K-v4. 5. Una clave inexistente. | 1: entrega todas las entradas y la lista no se puede modificar. 2 a 5: rechazo, sin entregar ninguna entrada. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-025 — Acceso denegado | BE09-CA06 | Perfil por defecto (adaptador que niega todo) | 1. Llamar crear, agregar, consultar, aprobar, retirar y calificar. 2. Con un doble que solo concede calificar: consultar la clave. | 1: todas rechazadas con `SecurityException` **antes** de leer datos (no revelan si la clave existe). 2: calificar funciona y consultar se rechaza; son permisos separados. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-026 — Sin exposición por API | BE09-CA05 | `pull/50/merge` | 1. Buscar controladores o rutas que devuelvan claves o entradas. 2. Revisar `openapi.yaml`. 3. Con el backend arriba, revisar Swagger UI. | No hay ninguna ruta de claves. Se repite cuando existan endpoints de claves. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |
| TC-BFA-TEST-02-027 — Pruebas automáticas del PR | BE09-CA08 | `pull/50/merge`, PostgreSQL 17 local y base `evaluaciones_bfa_test` creada | 1. `mvn -f backend/pom.xml verify`. 2. Anotar las clases y el conteo de pruebas. | `BUILD SUCCESS`, y las 11 clases de claves se ejecutan y pasan. Se guarda la salida como evidencia. | PENDIENTE DE EJECUCIÓN | NO EJECUTADA |

**Evidencia prevista:** `docs/evidencias/BFA-TEST-02/` con la salida de `mvn verify`, la salida de los `psql` (TC-022 y 023) y el SHA combinado probado.

## Matriz de cobertura: BFA-TEST-02, BFA-BE-08, BFA-BE-09 y BFA-BE-10

| Ref. | Criterio de aceptación registrado (texto íntegro) | Caso(s) | Preparación / estado |
|---|---|---|---|
| `T02-CA01` | Se verifican N1, S2, factores compuestos, EIV/EIC/EIG/AE, parciales y ausencia de rango, sin interpolar ni inventar ceros. | TC-BFA-TEST-02-001 a TC-BFA-TEST-02-006 | Cobertura diseñada; parcialidad/estados y ejecución pendientes. |
| `T02-CA02` | Cada criterio de las tareas cubiertas tiene al menos un caso `TC-`. | Toda la matriz BE08/BE09/BE10 de abajo | La matriz enumera todos los criterios de aceptación cargados para las tareas de desarrollo cubiertas. |
| `T02-CA03` | Cada PR cubierto tiene `testing-ok` o sus fallos comentados antes de la fusión. | TC-BFA-TEST-02-016 | PENDIENTE: PR/SHA y ejecución; no aplicar etiqueta en esta preparación. |
| `BE08-CA01` | Los 18 factores y resultados definidos pueden representarse con su estado de cálculo. | TC-BFA-TEST-02-003, 004, 010 | DOC-04 ya define estados funcionales; cotejar serialización con el documento final de DOC-05 antes de ejecutar. |
| `BE08-CA02` | Las puntuaciones intermedias y compuestas coinciden con las fórmulas documentadas y los casos de prueba aprobados. | TC-BFA-TEST-02-001, 002, 003 | Aritmética diseñada con fixture sintética; aprobar fixture y ejecutar. |
| `BE08-CA03` | N1 aplica el mínimo de cero después de consolidar los aciertos y errores de ambos bloques numéricos. | TC-BFA-TEST-02-001 | Diseñable desde DOC-02 completed. |
| `BE08-CA04` | Las aplicaciones parciales calculan únicamente los resultados cuyos componentes están disponibles y validados. | TC-BFA-TEST-02-004 | Regla de parcialidad definida en DOC-04 §3.4/P08; combinación concreta depende de configuración publicada. |
| `BE08-CA05` | Los factores no calculables conservan puntuación nula y su motivo correspondiente; nunca se sustituyen datos faltantes por cero. | TC-BFA-TEST-02-004 | Estados NO_CALCULABLE/PENDIENTE_VALIDACION definidos en DOC-04; JSON API por cotejar. |
| `BE08-CA06` | La conversión a percentiles utiliza únicamente baremos aprobados, compatibles y versionados. | TC-BFA-TEST-02-009 | PENDIENTE: compatibilidad de edición y estados de aprobación. |
| `BE08-CA07` | Si no existe un rango normativo válido, el percentil permanece nulo con su estado y motivo, sin interpolaciones. | TC-BFA-TEST-02-006 | Estado funcional SIN_BAREMO_VALIDADO definido en DOC-04; campo JSON por cotejar. |
| `BE08-CA08` | La ausencia de claves oficiales o decisiones pendientes de Fluidez Verbal impide presentar como definitivas las puntuaciones afectadas. | TC-BFA-TEST-02-012 | Estado PENDIENTE_VALIDACION y finalización sin puntuación definitiva de FV definidos por DOC-04; fixture DOC-08 sigue pendiente. |
| `BE08-CA09` | Se conserva la trazabilidad de versiones, entradas y resultados calculados. | TC-BFA-TEST-02-010 | DOC-05 figura Closed en ClickUp; los campos de historial deben cotejarse con la copia final, pues el PDF visible está en v0.6. |
| `BE08-CA10` | No se modifican respuestas originales ni revisiones históricas completadas. | TC-BFA-TEST-02-010 | PENDIENTE: interfaz de consulta/revisión final. |
| `BE08-CA11` | La implementación utiliza el evaluador BFA y los servicios del motor común sin duplicar responsabilidades. | TC-BFA-TEST-02-014 | DOC-06 figura Closed en ClickUp; verificar la conformidad arquitectónica durante la revisión del PR, aún no disponible. |
| `BE08-CA12` | Las pruebas automatizadas verifican cálculos completos, parciales, pendientes y casos límite. | TC-BFA-TEST-02-015 | PENDIENTE: PR/CI y fixtures. |
| `BE09-CA01` | Cada clave queda vinculada a una edición y versión identificable del formulario. | TC-BFA-TEST-02-011, 017 | v2: PR #50 (`36e2b19`) en matriz de alcance; PENDIENTE: fixture sintética y contrato. |
| `BE09-CA02` | El sistema detecta claves incompletas, incompatibles o inconsistentes. | TC-BFA-TEST-02-012, 018, 019, 020 | v2: PR #50 (`36e2b19`) en matriz de alcance; PENDIENTE: fixture sintética y estados/errores contractuales. |
| `BE09-CA03` | Una clave no aprobada no puede utilizarse para generar calificaciones oficiales. | TC-BFA-TEST-02-012, 021, 024 | v2: PR #50 (`36e2b19`) en matriz de alcance; PENDIENTE: estado/flujo de aprobación. |
| `BE09-CA04` | Las claves aprobadas permiten comprobar las respuestas de las preguntas correspondientes. | TC-BFA-TEST-02-011, 018, 024 | v2: PR #50 (`36e2b19`) en matriz de alcance; PENDIENTE: clave/respuestas de prueba aprobadas; ninguna clave real incluida. |
| `BE09-CA05` | Las respuestas correctas no se exponen mediante los endpoints públicos del sistema. | TC-BFA-TEST-02-013, 026 | v2: PR #50 (`36e2b19`) en matriz de alcance; PENDIENTE: endpoints y política de autorización final. |
| `BE09-CA06` | Los usuarios sin autorización no pueden consultar ni modificar claves. | TC-BFA-TEST-02-013, 025 | v2: PR #50 (`36e2b19`) en matriz de alcance; DOC-04 define permisos por rol/alcance; endpoint/código final requiere cotejo con copia actualizada DOC-05. |
| `BE09-CA07` | Las claves utilizadas en cálculos históricos permanecen identificables y no se sustituyen silenciosamente. | TC-BFA-TEST-02-010, 022, 023 | v2: PR #50 (`36e2b19`) en matriz de alcance; Reglas de nuevas revisiones e historial definidas por DOC-04 P11; IDs/campos de contrato final por verificar. |
| `BE09-CA08` | Las pruebas automatizadas verifican claves válidas, incompletas, incompatibles y no autorizadas. | TC-BFA-TEST-02-011, 012, 015, 027 | v2: PR #50 (`36e2b19`) en matriz de alcance; PENDIENTE: PR/CI y datos de prueba autorizados. |
| `BE10-CA01` | Los baremos se almacenan por versión, factor y rango normativo. | TC-BFA-TEST-02-007 | Cotejar los detalles de la interfaz de carga con la copia final de DOC-05; el PDF visible está en v0.6. |
| `BE10-CA02` | La carga conserva fielmente los valores y las celdas sin información del material aprobado. | TC-BFA-TEST-02-007 | Dato vacío FV/percentil 97 documentado; PR pendiente. |
| `BE10-CA03` | El sistema detecta rangos inválidos o coincidencias ambiguas. | TC-BFA-TEST-02-008 | Fixture estructural artificial; PR/contrato pendiente. |
| `BE10-CA04` | La conversión utiliza únicamente baremos aprobados y compatibles con la edición correspondiente. | TC-BFA-TEST-02-005, 009 | PENDIENTE: compatibilidad de la edición UAM. |
| `BE10-CA05` | Cada puntuación se convierte utilizando el rango exacto aplicable al factor. | TC-BFA-TEST-02-005 | N1=18 → percentil 50, según anexo DOC-02; compatible/PR pendiente. |
| `BE10-CA06` | Las puntuaciones sin correspondencia normativa conservan percentil nulo con su estado y motivo. | TC-BFA-TEST-02-006 | Diseñable; cotejar el campo/estado con la copia final de DOC-05, no verificada en el PDF v0.6 disponible. |
| `BE10-CA07` | No se aplican interpolaciones, extrapolaciones ni fórmulas universales. | TC-BFA-TEST-02-006 | Diseñable desde la regla; ejecución pendiente. |
| `BE10-CA08` | Los baremos utilizados en resultados históricos permanecen identificables. | TC-BFA-TEST-02-010 | PENDIENTE: esquema final de historial. |
| `BE10-CA09` | La lógica normativa se mantiene en backend y no se incorpora al frontend. | TC-BFA-TEST-02-014 | Revisión manual; DOC-06 Closed in ClickUp. |
| `BE10-CA10` | Las pruebas automatizadas verifican conversiones conocidas, límites, ausencia de rangos y compatibilidad de versiones. | TC-BFA-TEST-02-005 a TC-BFA-TEST-02-009, 015 | PENDIENTE: PR/CI, fixture aprobada y compatibilidad. |

**Lectura:** “cubierto” significa que existe un caso diseñado que apunta a ese criterio; no significa que el criterio haya sido ejecutado ni aprobado por Testing. “PENDIENTE” identifica los datos, decisiones o contratos que no deben suponerse.

## Bloqueos y decisiones pendientes

1. **DOC-07 (in progress, actualizado 5 nov 2:15 a. m.):** falta cerrar revisión/aprobación; sigue siendo bloqueo directo.
2. **DOC-08 (Open):** todavía no consta como completado el material ficticio. Impide disponer de fixtures de ejercicios/claves y evidencias sintéticas aprobadas.
3. **DOC-04 (Closed, 5 nov 5:54 p. m.):** Revisión 2 adjunta dice “aprobado funcionalmente”; ya define P07/P08, reglas de parcialidad y estados. Nota de consistencia: comentario del 4 nov informó que el Google Doc BFA §3 seguía con numeración antigua; no hay confirmación posterior de Drive sincronizado.
4. **DOC-05 (Closed, 5 nov 6:26 p. m.):** comentarios registran el ajuste de E3 y permisos/actualización con DOC-04 R2. Sin embargo, el único PDF adjunto visible es v0.6 y un comentario del 5 nov 4:27 p. m. pidió subir los documentos actualizados. Este v1 no inventa ni certifica rutas, payloads o errores de E10–E12; cotejarlos contra la copia final antes de ejecutar.
5. **DOC-06 (Closed, 5 nov 5:00 p. m.):** D09 y el ciclo de cálculo constan corregidos en el comentario de revisión; inspección de implementación requiere PR/commit.
6. **Compatibilidad normativa:** DOC-02 documenta Normas BFA 1992 (Nicaragua), pero la compatibilidad exacta con la edición usada por UAM queda como punto abierto. Hasta su resolución, TC-005/006/009 no debe producir conformidad normativa.
7. **Desarrollo:** BE-08/09/10 siguen Open, sin PR/SHA; ningún caso puede ejecutarse contra versión concreta. DOC-08 (Open) también continúa como bloqueo directo.

### Bloqueos nuevos de v2 (PR #50) y a quién se le pregunta

| ID | Bloqueo o pregunta | Para | Afecta |
| --- | --- | --- | --- |
| B-1 | Estado documental al 21-nov: DOC-07 sigue *in progress* y DOC-08 sigue *Open*. Toda ejecución será parcial y no da conformidad final. | Documentación (Gabriel Pérez, Geovanny Ruiz) | Todos |
| B-2 | Instrucciones de Max para correr las pruebas en local: crear la base `evaluaciones_bfa_test` (en CI se hace con `CREATE DATABASE … OWNER evaluaciones`), variables y comando. ¿Hay que correr algo más que `mvn verify`? | Max | TC-017 a 027 |
| B-3 | No existe una implementación del perfil `seguridad-integrada`: toda operación se niega. ¿Cuándo y en qué tarea se implementan los roles de DOC-05 (Psicólogo crea/carga/aprueba, permiso de calificación interno)? | Max, GEN-06 | TC-025, BE09-CA06 |
| B-4 | No hay ninguna restricción que impida tener **dos claves APROBADAS a la vez** para el mismo formulario. ¿Se permite? Si se permite, ¿cuál usa la calificación? | Max, Documentación (DOC-03 y DOC-04) | TC-021, TC-024 |
| B-5 | Ninguna tabla registra qué clave usó cada calificación. ¿Lo hace BFA-BE-08? Sin eso, BE09-CA07 solo queda cubierto a medias. | Max, BFA-BE-08 | TC-010, TC-023 |
| B-6 | ¿La «edición» del criterio CA01 es `version_formulario`? Hay que confirmarlo con ENT-BFA-02 y 03 de DOC-03. | Documentación | TC-017 |
| B-7 | DOC-08 está *Open*. ¿Testing puede usar datos sintéticos propios (formulario, preguntas y opciones inventadas) para TC-017 a 024? | Documentación, Integración | TC-017 a 024 |
| B-8 | La cobertura solo exige preguntas de **selección**; las de texto libre (FV) quedan fuera. Hay que confirmar que es lo esperado y que FV se trata en BFA-TEST-03. | Max | TC-020, TC-012 |
| B-9 | BFA-BE-08 (cálculo) y BFA-BE-10 (baremos) no tienen PR. Siguen bloqueados TC-001 a 010 y 014, y la integración de la clave con la calificación. | Desarrollo BFA | v1 |
| B-10 | El PR parte de `d33f6a8` y `main` va en `ef0317e`. Hoy fusiona sin conflictos. En las pruebas `out-of-order: true`; en ejecución normal no. Si otro PR fusiona antes migraciones con una versión mayor, puede fallar Flyway. | Integración (Kisnner) | TC-027 |
| B-11 | El PR sigue en borrador con 33 archivos. ¿Se divide (por ejemplo, migraciones y dominio por un lado, servicios por otro) o se valida entero? Hay que decidirlo antes de la ejecución final. | Integración, Max | Todos |
| B-12 | El PR modifica `.github/workflows/ci.yml`, que es común para todos. ¿Integración aprueba ese cambio dentro de un PR de BFA? | Integración | Fusión |

### Nota de aprobación

Los criterios de BFA-TEST-02 y las tareas de Desarrollo están registrados en ClickUp, pero la tarea de Testing está Open y no muestra un campo separado de aprobación. Esta matriz prueba cobertura documental de criterios registrados, no reemplaza la aprobación de casos que indique el equipo ni el visto bueno de DOC-07. El documento debe revisarse antes de pasarlo al encargado.
