# Traspaso semanal: sprint 03 (BFA)

Formato de `TRASPASO.md`. Redactado el 26 de noviembre de 2026 por Integración con lo verificable en `main` y en GitHub a esa hora; lo que no se pudo comprobar se marca como tal. Los datos son ficticios, sin credenciales ni ítems reales.

- **Semana y módulo:** sprint 03, prueba BFA. Motor común de evaluación (backend) y primeras pantallas del frontend.
- **Grupo que entrega / rol:** Integración.
- **Grupo que recibe / rol:** Integración entrante.
- **Versión estable de `main` (hash de commit):** `12138f1` ([GEN-32-FE] login real, #151), con CI y despliegue en éxito. Se vuelve a anotar el hash al fusionar este documento.

## Requisitos y contratos aprobados

`docs/api/openapi.yaml` define 31 operaciones (E1 a E31). Según un análisis estático de los controladores de `main` contra ese contrato, **21 están implementadas y 10 faltan**. Es un cruce de rutas y verbos, no una prueba de comportamiento.

| Estado | Operaciones |
|---|---|
| Implementadas en `main` | E1 a E8, E13 a E19, E21, E23, E25 a E28 |
| Faltan | E9, E10, E11, E12, E20, E22, E24, E29, E30, E31 |

PR abiertos que cubren parte de lo que falta: E20 (#154), E22 (#152), E29 (#155, que reemplaza al #95), E30 (#135) y E9 (#73, el más antiguo). Para E10, E11, E12, E24 y E31 no se identificó un PR.

## Funcionalidades integradas y enlace a demo

Ver `docs/arquitectura/inventario-motor-evaluacion.md` para tablas, capas, endpoints y puntos de extensión. La API publicada y su Swagger dependen del despliegue de `main` hacia el VPS; no se comprobó desde este documento que la URL responda.

## Pruebas ejecutadas y evidencia

- CI de `main` en verde (`reglas`, `backend-build`, `frontend-build`, `ci-ok`) y despliegue en éxito en los últimos commits.
- Evidencias de pruebas en `docs/evidencias/` y casos en `docs/pruebas/`.
- **No** se ejecutó Postman ni el login E14 contra la URL publicada como parte de este traspaso.

## Issues y PR abiertos

47 PR abiertos al redactar. Agrupados:

- **Reemplazados por el #142 (ya fusionado), candidatos a cierre con nota a cada autor:** #127, #128, #129, #131, #133, #136, #137, #138 y #139. Verificar uno por uno antes de cerrar y conservar la autoría en la nota.
- **Esperando `testing-ok`:** la mayoría de los PR de GEN y BFA con CI verde. La etiqueta la pone Testing tras probar; no se pone por cuenta propia.
- **Con trabajo vigente y cambios pedidos:** #135, #154 y #146, cada uno con comentarios de integración en el PR.
- **Borradores:** #153, #139, #130, #119 y otros.

## Cambios de base de datos

Orden en `main`, todas con versión única y creciente:

1. `V202611040249__comun_catalogo_versionado`
2. `V202611111300__comun_bloquear_estado_inicial_invalido`
3. `V202611170600__comun_agregar_factor_subprueba`
4. `V202611240156__comun_cuenta_y_sesion`
5. `V202611240800__comun_motor_evaluacion`
6. `V202611240830__comun_sujeto_periodo`
7. `V202611251100__comun_recuperacion_cuenta`
8. `V202611251130__comun_cuenta_sujeto_asignado`
9. `V202611251204__comun_evento_auditoria`
10. `V202611251400__comun_resultado_pendiente`
11. `V202611251500__comun_configuracion_opciones_esperadas`

Pendientes, con veredicto del custodio:

- `V202611251600__bfa_claves_versionadas` (#155): válida. Reemplaza a la del #95, que no debe fusionarse junto con ella.
- `V202611251620__bfa_resultado_factores` (#153): válida. Entra después de la anterior.
- `V202611251021__bfa_baremos` (#123 y #135): **debe renombrarse**, es anterior a la última de `main`. Propuesta de nombre: `V202611251640__bfa_baremos`. Esos dos PR también traen `V202611241102__bfa_claves_versionadas`, duplicada con la del #155.
- `V202611251400__comun_resultado` (#143) choca con la versión ya fusionada `V202611251400__comun_resultado_pendiente`; debe renombrarse o integrarse.

Regla vigente: cada migración lleva una versión posterior a la última de `main` y a las que se fusionan antes; no se edita una migración ya fusionada; Flyway tiene `out-of-order` activo, pero el custodio mantiene el orden.

## Riesgos, errores conocidos y decisiones pendientes

- **`testing-ok` independiente.** La etiqueta la ponen sobre todo dos personas de Testing, lo que genera esperas de horas. Quien escribió commits en un PR no debería ponerla en ese mismo PR. Si no hay nadie disponible, la excepción se registra por escrito con la aprobación del Scrum Master y queda anotada en el PR.
- **Condiciones del #142 aún sin cerrar:** repetir la evidencia de `docs/evidencias/sprint03/` por una persona sin commits en ese PR y la evidencia de E14 desplegado.
- **Matriz de permisos.** Al principio estaba vacía en `main`; la integración de GEN-33-BE la incorporó con el #142. Comprobar que cada endpoint exige el permiso del contrato, con pruebas que usen el servicio de autorización real y no solo mocks.
- **Pruebas de controlador e integración.** Desde que el filtro Bearer está en `main`, los tests de controlador deben simular `AutenticacionService` y mandar un Bearer ficticio, como `CatalogoTestsControllerTest`.
- **PR duplicados.** Varios PR cubren la misma tarea; fusionar solo uno por tarea y cerrar los demás.
- **Trabajo grande sin partir.** El #142 reunió unas 9 700 líneas; los #144 y #145 (frontend) son de unas 14 800 y 6 900 líneas. Revisarlos por partes sensibles (dependencias, autenticación, migraciones, archivos de despliegue).

## Próximas tres tareas prioritarias

1. Cerrar los PR reemplazados con una nota que enlace el PR vigente y reconozca a cada autor.
2. Fusionar en orden los PR con migraciones pendientes (#155, luego #153), tras corregir los nombres de las demás.
3. Completar las operaciones que faltan: E20, E22, E29, E30, E31, E9 a E12 y E24.

## Aprobación del integrador, tester y Scrum Master

- Integrador: pendiente.
- Tester: pendiente.
- Scrum Master: pendiente.

## Cómo trabaja Integración (guía rápida para quien recibe)

1. Mirar los PR abiertos y el estado de `main` antes de decidir el orden.
2. Un PR de un fork pide aprobación para ejecutar los workflows: antes de aprobar, leer que no toque `.github`, `pom.xml`, `package.json`, `Dockerfile`, `deploy/`, scripts ni archivos de propiedades.
3. Fusionar solo con `ci-ok`, rama al día, aprobación de integración y, en GEN y BFA, `testing-ok` puesto por Testing. Fusionar con **squash**, que es el único método permitido.
4. Tras cada fusión, los demás PR quedan atrás de `main`: traer `main` (`update-branch`), esperar su CI y fusionar el siguiente.
5. Si falla el CI de un PR tras otra fusión, leer el log antes de culpar al autor (por ejemplo, un test de controlador sin `AutenticacionService`).
6. Revisar migraciones: versión posterior a la última de `main`, única y sin duplicar tablas.
7. Comunicar con comentarios breves en el PR: qué se revisó, qué falta y dónde mirar.
