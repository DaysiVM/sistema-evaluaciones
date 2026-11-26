# Nota Allport — estructura para la BD

DOC-38 · 24 de noviembre de 2026 · Responsable: Davide Patiño. Pendiente de aceptación de Iván.

[Documento entregado en Drive](https://docs.google.com/document/d/1n0HlwlahyGj7FsjEJPNKBhUzdOF7GHsOlfJvHih-RgE/edit).

## Fuentes y alcance

- **F1:** [Estudio de Valores (Corregido).pdf](https://drive.google.com/file/d/1N75SDXr2rjuTNSj_7DsykigkgRSImoWb/view), 11 páginas. Se cita la página del PDF, coincidente con la numeración impresa donde aparece.
- **F2:** [formulario_3.pdf](https://drive.google.com/file/d/1aMXMCno4ocdgyIRXmxQcphhBkz_ArLY1/view), p. 1. Se identifica como cuestionario de Wilson et al. (2002); no respalda la estructura de Allport. ALL-05.

Solo estructura necesaria para la BD. Los modelos EA previos son antecedentes; no se usan como autoridad de puntuación. No se reproducen enunciados, opciones reales ni claves por ítem. Las implicaciones de persistencia son propuestas para revisión, no tablas ni endpoints aprobados.

## 1. Partes

**Dos partes.** La primera contiene 30 ítems (F1, pp. 1–5). La segunda tiene instrucciones propias (p. 6) y situaciones en pp. 7–9. El cuadro de calificación espera 60 + 50 + 40 = 150 puntos en esta parte; a 10 puntos por situación implica 15 situaciones efectivas. Es una deducción del cuadro, no una numeración limpia: la p. 9 mezcla una variante por sexo con rótulos y referencias inconsistentes. Registrar **ALL-01**, sin fijar 16 ítems independientes. Las partes no equivalen a las seis escalas.

## 2. Tipo de ítem y respuesta

**Parte I:** reparto de tres puntos entre dos alternativas; pares permitidos (3,0), (0,3), (2,1) y (1,2). Se conserva el puntaje asignado a cada alternativa, no solo una opción seleccionada (F1, p. 1).

**Parte II:** ordenar cuatro alternativas, asignando 4 a la preferida y después 3, 2 y 1; cada valor se usa una vez y la suma por situación es 10 (F1, p. 6). No es una escala Likert de valoraciones independientes. La BD necesita representar alternativa y valor/orden dentro de la respuesta. No se añaden tipos al contrato en esta entrega. Omisiones: ALL-02.

## 3. Tiempo límite

F1, p. 1, declara que no hay límite de tiempo. La p. 6 no introduce un límite para la segunda parte. No se configura un límite numérico ni se convierte una duración sugerida en restricción. La representación técnica de ausencia de límite corresponde al modelo común.

## 4. Escalas que produce

Seis: **teorético, económico, estético, social, político y religioso** (F1, cuadro de p. 10 y descripciones de p. 11). El cuadro usa «teorética» y la descripción «teórico»; se conserva la referencia a la misma escala. Cada escala agrega aportes de ambas partes; no se crea una escala por parte.

## 5. Cálculo: entradas y salida

**Entradas:** puntos por alternativa, distribución de casillas en columnas R/S/T/X/Y/Z por página y correspondencia de esas columnas con las seis escalas. La correspondencia cambia por página; no se debe asumir que una letra siempre representa la misma escala (F1, pp. 2–5, 7–10).

**Proceso:** sumar columnas de cada página, trasladar cada subtotal a su escala según el cuadro de p. 10, sumar por escala y aplicar las correcciones: teorético +3, económico −1, estético +4, social −3, político +2 y religioso −5. **Salida:** seis puntajes finales corregidos (F1, p. 10).

**Control:** páginas de la primera parte: 24, 24, 21 y 21 puntos (90); segunda parte: 60, 50 y 40 (150). La suma de las seis escalas finales debe ser 240; las correcciones suman cero. Es un control de integridad, no una séptima escala ni una medida global de valor. Las reglas deben vincularse a la edición del formulario. No se transcribe la clave por ítem.

**Pendientes:** resolver numeración/variante (ALL-01) y omisiones (ALL-02). Las instrucciones permiten omitir por imposibilidad de decidir (pp. 1 y 6), pero la corrección pide todas las respuestas (p. 10). No hay respaldo para imputar cero, prorratear o corregir automáticamente ese caso.

## 6. Baremos

**No documentados en las fuentes revisadas.** F1, pp. 1–11, ofrece instrucciones, formulario, suma/correcciones y descripciones; no incluye baremos, percentiles ni rangos por edad, sexo o escolaridad. No equivale a afirmar que Allport carece de normas. Se registra **ALL-03** para obtener el manual/baremo de la edición aplicable. La variante por sexo de p. 9 no demuestra normas diferenciadas. F2 no suple este faltante.

## 7. Resultado final que necesita conservar la BD

**Respaldado por el instrumento:** seis valores finales, uno por escala, y la comprobación de suma 240 cuando la aplicación está completa (F1, p. 10). La p. 11 describe las escalas, sin umbrales ni una clasificación automática.

**Propuesta de persistencia para revisión:** vincular el resultado con la evaluación y la versión de formulario/regla; conservar respuestas puntuadas, subtotales por página/escala, corrección aplicada y puntaje final para reproducir el cálculo. Distinguir resultado incompleto de cero y conservar la causa. Sin norma validada, no añadir percentiles, categorías, diagnóstico ni recomendación vocacional automática. El formato digital y la publicación requieren decisión del modelo común (**ALL-04**); no se inventan entidades, campos API ni migraciones.

## Registro de incógnitas

[Registro de incógnitas DOC-38 — Allport](https://docs.google.com/spreadsheets/d/11uwCtLcol-GVEJDItfXU_a1A8kAR7yNjCyPRIS97_48/edit). Hoja nueva dentro de Documentación; no se localizó una hoja común accesible. IDs ALL-01 a ALL-05, pendientes de revisión.

Todas las siguientes incógnitas quedan abiertas para revisión de Iván y, cuando corresponda, del docente. La nota documenta la evidencia disponible; no declara aceptado el instrumento ni sus reglas digitales.

| ID | Punto | Incógnita / evidencia | Impacto BD / decisión pendiente | Estado |
|---|---|---|---|---|
| ALL-01 | 1, 2, 5 | F1 p. 9: referencia a 14A, rótulos 15 A y 16; F1 p. 10 espera 40 puntos para esa página. Confirmar numeración canónica y alternativa por sexo. | No contar ambas variantes como dos respuestas; confirmar 15 situaciones efectivas en la segunda parte antes de publicar formulario. | Abierta; revisión Iván / docente |
| ALL-02 | 5 | F1 pp. 1 y 6 permiten omitir si no se puede decidir; p. 10 exige todas contestadas. No hay regla de imputación ni corrección de incompletos. | Conservar ausencia distinta de cero; no publicar un puntaje completo ni prorratear sin regla aprobada. | Abierta; revisión Iván / docente |
| ALL-03 | 6 | F1 pp. 1–11 no incluyen tablas normativas ni selección por edad, sexo o escolaridad. Solicitar manual y baremo aplicable a esta edición y población. | No inventar percentiles, categorías ni conversiones. Sexo en variante de ítem no demuestra baremo por sexo. | Abierta; revisión Iván / docente |
| ALL-04 | 7 | F1 pp. 10–11 presentan seis puntajes finales y descripciones, sin formato digital, umbrales ni reglas de informe. | Confirmar persistencia y publicación de resultado en el modelo común; distinguir propuesta técnica de requisito del test. | Abierta; revisión Iván |
| ALL-05 | 1–7 | F2 p. 1 identifica Cuestionario de valores de Wilson et al. (2002), distinto de F1. | Confirmar edición autorizada de Allport; excluir F2 de la configuración y calificación de Allport. | Abierta; revisión Iván / docente |
