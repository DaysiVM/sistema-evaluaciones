# GEN-05 — Consulta autorizada de resultados (E9)

`GET /api/v1/evaluaciones/{evaluacionId}/resultados`

Devuelve el resultado de una evaluación **finalizada**, con su **revisión vigente exacta**, solo a usuarios autorizados. Es **común a todos los tests** (BFA, Weill, Thurstone, Allport, Vocacional): no conoce factores ni reglas propias de ningún test.

Fuentes: Documento BFA §5.3 (E9) y CU-BFA-P09; Documento general §4.4, §5.2.1 y §5.2.2; `docs/api/openapi.yaml` (`consultarResultados`, `ResultadoResponse`, `ErrorResponse`).

---

## 1. Qué hace, paso a paso

| Paso | Comprobación | Si falla |
| --- | --- | --- |
| 1 | El ID es un entero positivo | 400 `SOLICITUD_INVALIDA` |
| 2 | Hay sesión y el rol tiene permiso de consultar resultados | 401 `NO_AUTENTICADO` / 403 `SIN_PERMISO` |
| 3 | La evaluación existe | 404 `EVALUACION_NO_ENCONTRADA` |
| 4 | La evaluación está en el alcance del usuario | 403 `SIN_PERMISO` |
| 5 | La evaluación está `FINALIZADA` | 409 `EVALUACION_NO_FINALIZADA` |
| 6 | Se lee el resultado y **solo** su revisión vigente | 500 `ERROR_INTERNO` si los datos son inconsistentes |
| 7 | Se registra la consulta en auditoría | — |

El permiso (paso 2) se comprueba **antes** de buscar la evaluación, para no revelar si existe a quien no tiene permiso.

## 2. Qué se responde según el estado

| Situación | `estadoResultado` | Revisión y versiones | `factores` |
| --- | --- | --- | --- |
| Revisión publicada | el guardado (normalmente `DISPONIBLE`) | `calificacionId`, `revision`, `versionReglas`, `idClave`, `idBaremo` | los de la revisión vigente, en el orden guardado |
| Sin revisión publicada | `PENDIENTE_VALIDACION` o `CALCULANDO` | todos `null` | `[]` |
| Finalizada, pero E8 aún no guardó el resultado | `CALCULANDO` | todos `null`, también `resultadoId` | `[]` |
| `DISPONIBLE` **sin** revisión vigente | — | — | 500 `ERROR_INTERNO`: no se presenta como definitivo algo que no lo es |

## 3. Reglas por factor (no se inventan valores)

`FactorResultado.paraPresentar()` aplica una **lista de permitidos**. No calcula nada: solo decide qué se puede mostrar como dato verificado.

| `estadoCalculo` | `estadoNormativo` | `puntuacionDirecta` | `percentil` / `idRango` |
| --- | --- | --- | --- |
| `CALCULADO` | `CONVERTIDO` con rango exacto | se entrega (un **0 sigue siendo 0**) | se entregan |
| `CALCULADO` | `SIN_COINCIDENCIA`, `SIN_BAREMO_VALIDADO`, `NO_APLICA` | se entrega | `null` |
| `NO_CALCULABLE` | cualquiera | `null` | `null` |
| `PENDIENTE_VALIDACION` | cualquiera | `null` (no es definitiva) | `null` |
| desconocido | cualquiera | `null` | `null` |

Los estados y el `motivo` se entregan siempre tal como están guardados. Nunca se entrega el contenido de la clave: solo su identificador.

## 4. Archivos

| Capa | Archivo | Propósito |
| --- | --- | --- |
| Dominio | `domain/model/resultados/` | `EvaluacionConsultada`, `ContenedorResultado` (ENT-COM-10), `RevisionResultado` (revisión vigente), `FactorResultado` (reglas de la sección 3) |
| Dominio | `domain/port/resultados/` | `AutorizacionResultadoPort`, `EvaluacionConsultaPort`, `ResultadoConsultaPort`, `AuditoriaResultadoPort` |
| Dominio | `domain/exception/resultados/ConsultaResultadoException` | Motivos de rechazo con su código y estado HTTP |
| Aplicación | `application/usecase/resultados/ConsultarResultadosUseCase` | Flujo de la sección 1 |
| Aplicación | `application/usecase/resultados/ResultadoVista` | Lo que devuelve el caso de uso, sin entidades de dominio |
| API | `controller/resultados/ResultadosController` | Endpoint E9 |
| API | `dto/response/resultados/` | `ResultadoResponse` y `ErrorResultadoResponse` (formato común) |
| API | `exception/resultados/ResultadosErrorHandler` | Errores de E9. Solo atiende a este controlador |
| Infraestructura | `infrastructure/security/resultados/AutorizacionResultadoDenegadaAdapter` | **Provisional:** niega toda consulta hasta que exista la sesión (E14) |
| Infraestructura | `infrastructure/persistence/resultados/ConsultaResultadosPendienteAdapter` | **Provisional:** falla de forma explícita hasta que existan las tablas |
| Infraestructura | `infrastructure/persistence/resultados/AuditoriaResultadoRegistroAdapter` | **Provisional:** registra la consulta en el log `auditoria.resultados` |

Se respetan las reglas de `ArquitecturaTest`: el controlador no usa entidades de dominio ni infraestructura, y el dominio no depende de Spring ni de JPA.

## 5. Qué falta para usarlo con datos reales

Los adaptadores provisionales se reemplazan **sin tocar el caso de uso ni el controlador**: se agrega la implementación real y se activa su perfil.

| Puerto | Depende de | Perfil que desactiva el provisional |
| --- | --- | --- |
| `EvaluacionConsultaPort` y `ResultadoConsultaPort` | Tablas de Evaluacion (GEN-02-BE, PR #55) y de Resultado y revisiones (GEN-04-BE, PR #64) | `persistencia-resultados` |
| `AutorizacionResultadoPort` | Sesión (E14) y permisos y alcances de DOC-04 (D05/D11) | `seguridad-integrada` |
| `AuditoriaResultadoPort` | EventoAuditoria (ENT-COM-11, GEN-08) | `auditoria-integrada` |

La lectura real debe tomar la revisión referenciada en `referenciaRevisionVigente`, **nunca la de número mayor** (CU-BFA-P09, CA-08).

## 6. Pruebas

Desde `backend`:

```bash
mvn spotless:apply
mvn verify
```

| Archivo | Qué prueba |
| --- | --- |
| `FactorResultadoTest` | Reglas de la sección 3, incluidos el 0 calculado y los estados desconocidos |
| `ConsultarResultadosUseCaseTest` | Estados, errores 400/404/409, permisos, alcance, datos inconsistentes, orden de comprobaciones y auditoría |
| `ResultadosControllerTest` | Forma del JSON de E9, campos nulos, ausencia de clave y formato común de error (400, 401, 403, 404, 409, 500) |
| `ConsultaResultadosPermisosTest` | Flujo HTTP completo con dobles en memoria: profesional autorizado, sin sesión, rol sin permiso, evaluado y fuera de alcance |
| `AdaptadoresProvisionalesResultadosTest` | Los provisionales niegan el acceso o fallan; nunca inventan datos |

Los valores numéricos de las pruebas son **sintéticos**: no son puntuaciones ni conversiones verificadas del BFA. Los roles de `ConsultaResultadosPermisosTest` solo sirven para probar que se respeta la decisión de autorización; los definitivos los fija DOC-04.
