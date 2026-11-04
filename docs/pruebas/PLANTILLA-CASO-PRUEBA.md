# Plantilla de caso de prueba — Testing

Copia esta plantilla para cada caso nuevo. Usa **datos inventados**, requisitos aprobados y resultados observables. Los ejemplos son orientativos: no sustituyen el contrato del módulo.

## Identificación

- **ID del caso:** TC-GEN-01-001
- **Tarea de Testing:** GEN-01-TEST
- **Tarea de Desarrollo:** GEN-01-BE / GEN-01-FE (según aplique)
- **PR y SHA revisado:** #___ / _______
- **Requisito / CU / contrato aprobado:** _______
- **Tester:** _______
- **Fecha y entorno:** _______
- **Tipo:** API (Postman) / Frontend (navegador) / Integración

## Escenario

**Objetivo:** ¿qué comportamiento del requisito se valida?

**Precondiciones:** cuentas ficticias y estado previo necesario.

**Datos de entrada:** parámetros / JSON / interacción, sin información personal real.

**Pasos para reproducir:**
1. _______
2. _______
3. _______

**Resultado esperado:** especificar campos, códigos HTTP, restricciones o pantalla según el documento aprobado.

**Resultado obtenido:** _______

**Estado:** PASÓ / FALLÓ / BLOQUEADO / NO APLICA

**Evidencia:** enlace a captura, log sin secretos o exportación Postman.

**Defecto asociado (si falla):** enlace a la tarea en la lista Bugs.

**Bloqueo (si no puede ejecutarse):** documento, PR o ambiente faltante; responsable; próximo paso.

## Registro de ejecución

| Fecha | PR/SHA | Tester | Resultado | Evidencia / bug |
| --- | --- | --- | --- | --- |
| AAAA-MM-DD | #___ / ______ | ______ | PASÓ / FALLÓ / BLOQUEADO | ______ |

### Revisión final

- [ ] El caso corresponde a un criterio aprobado y a la tarea correcta.
- [ ] Se ejecutó contra el commit vigente del PR.
- [ ] Se registraron resultados reales y evidencia verificable.
- [ ] No contiene secretos ni datos personales reales.
- [ ] Se comentó al PR y se informó a Integración.
- [ ] Solo después de aprobar, se colocó o solicitó `testing-ok` cuando corresponde.
