# GEN-02-BE — Iniciar una evaluación (E3)

**Rama:** `feature/daysi-GEN-02-BE` · **Base:** `main` `f4c78ba` · **Autora:** Daysi · **Coordinación de la subtarea:** Juan Carlos · **Tarea:** [86bcvah52](https://app.clickup.com/t/86bcvah52)

Backend para **crear una evaluación (E3)**, más las consultas que necesita la pantalla de inicio: **evaluado (E19)** y **períodos disponibles (E21)**. El detalle del test (E2) lo aporta GEN-01, que ya está en `main`.

## 0. Alcance según las decisiones del Scrum Master (23/11/2026, PR #41)

| Decisión | Cómo queda en esta rama |
| --- | --- |
| El dueño del modelo `Evaluacion` y sus tablas es el **#41**, con ids `Long` | Las tablas `evaluacion`, `evaluacion_subprueba` y `aplicacion_fluidez_bfa` van en una migración aparte, **marcada como provisional** y ofrecida como base del paso 1 del #41 (sección 6.1) |
| GEN-02-BE se rehace desde `main` **solo con E3** | Rama nueva desde `main`. Sin tablas `gen02_*`, sin autenticación Basic y sin cambios en `openapi.yaml` |
| **E4 (GET) se queda en el #41** | Se retiró de esta rama |
| Nada de manejadores globales nuevos | `ManejadorErroresInicioEvaluacion` está limitado a los 3 controladores de esta rama (`assignableTypes`) |
| Orden: #51 → piezas del #41 → GEN-02-BE y #64 | Esta rama se fusiona **después** del paso 1 del #41. Hasta entonces, el PR queda en borrador |

E19 y E21 no se mencionan en las decisiones. Se incluyen porque la pantalla de inicio los usa y no los tiene otra tarea.

---

## 1. Qué incluye

| Pieza | Estado |
| --- | --- |
| Interfaz de iniciar y retomar (GEN-02-FE) | Ya estaba en `main` (#32). Testing la aprobó con mocks: 24 de 24 casos ([ejecución](GEN-02-ejecucion-2026-11-18.txt)) |
| **E3** `POST /api/v1/evaluaciones` | Nuevo |
| **E19** `GET /api/v1/sujetos/{id}` y **E21** `GET /api/v1/periodos?disponible=true` | Nuevo |
| Tablas `sujeto` y `periodo` | Nuevo (`V202611240213__comun_sujeto_periodo.sql`) |
| Tablas `evaluacion`, `evaluacion_subprueba` y `aplicacion_fluidez_bfa` | **Provisional** (`V202611240214__comun_evaluacion_provisional.sql`), ver 6.1 |
| Pruebas de integración con PostgreSQL, de dominio y de E3 | Nuevo |

**Origen:** el backend se escribió el 6/11 (commit local `f1dab9d`) sobre la rama de GEN-07-BE. Para esta entrega se trasladó al `main` actual y se ajustó:

- **Sin E2 propio.** Se usa el de GEN-01, que devuelve la misma estructura, incluido `versionFormularioId`. Desaparece `VERSION_AMBIGUA`.
- **Sin E4.** Se retiró `ConsultarEvaluacionUseCase` y el GET. El controlador pasó a llamarse `InicioEvaluacionController`, para no compartir ruta de archivo ni nombre de bean con el `EvaluacionController` del #41.
- **`ErrorResponse`:** se conserva el de GEN-01 y se le agrega `detalles` (errores por campo, §5.2.2). Un constructor de 5 parámetros mantiene intacto `ManejadorErroresApi`.
- **`Subprueba`** ahora recibe `factor` (`V202611170600`). Se ajustaron las pruebas unitarias.
- **Migraciones** con fecha y hora actuales (UTC), para que se ejecuten después de todas las de `main`.

---

## 2. Cómo comprobarlo

Requisitos: **Java 17**, Maven, Docker Desktop y **Node 22.12 o superior**.

```bash
docker compose up -d db                  # PostgreSQL 17
mvn -f backend/pom.xml spotless:apply
mvn -f backend/pom.xml clean verify      # «clean» evita usar migraciones viejas que hayan quedado en target/
mvn -f backend/pom.xml spring-boot:run   # http://localhost:8080
```

> Si en Windows ya hay un PostgreSQL usando el puerto 5432: `docker compose down`, `$env:POSTGRES_PORT = "5433"`, `docker compose up -d db` y repetir `mvn verify`.

Frontend con el backend real (`frontend/.env.local` con `VITE_USAR_MOCKS=false`):

```bash
cd frontend
npm ci
npm run lint && npm run format:check && npm test && npm run build
npm run dev   # http://localhost:5173/evaluaciones/inicio?testId=<id>
```

Con el backend real funciona la pestaña **Iniciar nueva**. La pestaña **Retomar** necesita E4, que llegará con el #41. Mientras tanto, la recuperación se prueba con los mocks.

Las migraciones no cargan datos. Para probar en el navegador hay que registrar antes un test con versión publicada, un evaluado y un período.

---

## 3. Endpoints

| Endpoint | Qué hace | Respuestas |
| --- | --- | --- |
| **E3** `POST /api/v1/evaluaciones` | Crea la evaluación EN_CURSO, completa o parcial, con sus subpruebas PENDIENTES en el orden oficial | 201 + `Location`; 400, 404, 409, 422 |
| **E19** `GET /api/v1/sujetos/{id}` | Datos del evaluado | 200; 404 |
| **E21** `GET /api/v1/periodos?disponible=true` | Períodos que admiten nuevas evaluaciones | 200 |

Errores con el formato común del Documento general §5.2.2.

---

## 4. Criterios de aceptación de GEN-02-BE

| Criterio | Estado | Cómo se cumple |
| --- | --- | --- |
| Cumplir E3; crear evaluaciones en la base de datos | ✅ | E3 valida test, versión publicada, evaluado, período, subpruebas y letra de FV. Guarda la evaluación y sus subpruebas con llaves a `sujeto`, `periodo`, `test`, `version_formulario` y `configuracion_subprueba` |
| Cumplir E4; recuperar evaluaciones | ➡️ #41 | Decisión del SM |
| Rechazar datos o configuraciones inválidas | ✅ | 400 con detalles por campo; 404, 409 y 422 según el caso (sección 5) |
| Rechazar accesos no autorizados | ⏳ GEN-06 | `AutorizacionEvaluacionPort` existe, pero su implementación es **provisional y permite todo** |
| No duplicar intentos | ✅ | Comprobación previa e índice único `uq_evaluacion_en_curso` (evaluado + test + período abiertos), que también protege frente a solicitudes simultáneas |
| Ejecutar las pruebas automatizadas | ✅ | `mvn clean verify`: 54 de 54 (sección 5) |

---

## 5. Pruebas

| Conjunto | Resultado |
| --- | --- |
| Migraciones aplicadas en orden sobre PostgreSQL (las 5) | ✅ 23/11 |
| Índice anti-duplicado (2.ª evaluación abierta rechazada; se permite otra tras finalizar) | ✅ 23/11, en SQL |
| Frontend: `lint`, `format:check`, `test`, `build` | ✅ 116 de 116 pruebas, 23/11 |
| Backend `mvn clean verify` (Java 17, PostgreSQL 17 en Docker) | ✅ **54 de 54**, 23/11 |

Escenarios de integración (`EvaluacionApiIntegrationTest`, 9 pruebas):

- Crear una evaluación completa y una parcial (en orden oficial).
- Segundo intento → 409 `EVALUACION_EN_CURSO`.
- Cuerpo inválido → 400 con detalles.
- Evaluado o período inexistente → 404.
- Subprueba ajena → 422.
- Período inactivo → 422.
- Versión en borrador → 409.
- FV sin letra → 409; con letra `m` → se guarda `M`.
- E2 de GEN-01 entrega la versión que usa E3.
- E19 y E21.
- Inserción directa de una 2.ª evaluación abierta → la base la rechaza.

---

## 6. Pendientes

### 6.1 Modelo de evaluación (PR #41)

La migración `V202611240214__comun_evaluacion_provisional.sql` y las clases `Evaluacion`, `EvaluacionSubprueba`, sus entidades JPA y sus adaptadores existen **solo para que E3 funcione y se pueda probar** mientras el #41 prepara su modelo con ids `Long`.

Se ofrecen como **base del paso 1 del #41**, porque ya cumplen lo que pidió el SM:

- Ids `BIGINT` con identidad.
- Llaves foráneas a sujeto, período, test, versión y configuración.
- Índice anti-duplicado.

Cuando el paso 1 del #41 esté en `main`:

1. Rebasar esta rama sobre `main`.
2. Borrar la migración provisional y las clases de evaluación que el #41 ya traiga.
3. Adaptar `IniciarEvaluacionUseCase` y los adaptadores al modelo del #41.
4. Repetir `mvn verify`.

**Diferencias con el #41 que hay que conciliar** (la versión del #41 revisada es `a94175e`):

| Tema | Esta rama | #41 |
| --- | --- | --- |
| Ids | `BIGINT` con identidad | `VARCHAR(50)`; pasa a `Long` por decisión del SM |
| Evaluado, período, test y versión | Llaves foráneas | Columnas de texto sin llave |
| Subprueba | Llave a `configuracion_subprueba` | Llave más copia de código, nombre, orden y duración |
| Estado inicial | `EN_CURSO` | `PENDIENTE` |
| Letra de FV | Tabla `aplicacion_fluidez_bfa` (ENT-BFA-11) | Columna `letra_inicial` en `evaluacion_subprueba` |
| Anti-duplicado | Índice único parcial | No tiene |

### 6.2 Autenticación y permisos (GEN-06)

`AutorizacionEvaluacionProvisionalAdapter` **permite todo**. GEN-06 debe aportar la implementación real (401/403). **No usar con datos reales.** La interfaz ya muestra «Acceso denegado» cuando recibe 403.

### 6.3 Contrato

- `PERIODO_NO_DISPONIBLE` (422, período inactivo) **no está en `openapi.yaml`**. Hay que agregarlo con el responsable de DOC-05 o reemplazarlo por un código aprobado.
- Supuestos por validar:
  - Un período está «disponible» si `activo = true`; no se revisan las fechas.
  - Si se envía letra y la evaluación no incluye FV, la letra se ignora.

### 6.4 Otras tareas

- **E4 (recuperar):** #41. La pestaña **Retomar** de la interfaz lo usará cuando esté listo.
- **Alta de evaluados y períodos (E18 y E20, CU-GEN-20):** sin asignar.
- **Testing:** colección Postman de E3, E19 y E21, y casos contra el backend real.

### 6.5 PR relacionados

- **#55 (Juan Carlos):** borrador con tablas `gen02_*` y autenticación Basic. El SM pidió rehacerlo desde `main`; esta rama es esa reconstrucción, acordada con Juan Carlos.
- **#58 (Nelson):** revisión documental del #55. No trae implementación; sus observaciones (E4 duplicado, bean `EvaluacionController`, tablas paralelas, contrato) quedan resueltas aquí.
- **#41 (Javier):** dueño del modelo de evaluación y de E4.

---

## 7. Archivos

| Capa | Archivos |
| --- | --- |
| Migraciones | `V202611240213__comun_sujeto_periodo.sql`; `V202611240214__comun_evaluacion_provisional.sql` (provisional) |
| Dominio | `Sujeto`, `Periodo` y sus repositorios; `AutorizacionEvaluacionPort`, `ErrorNegocioException`, `ErroresEvaluacion`; provisional: `Evaluacion`, `EvaluacionSubprueba`, estados, `EvaluacionRepository` |
| Aplicación | `IniciarEvaluacionUseCase`, `ConsultarSujetoUseCase`, `ListarPeriodosUseCase`, `ReglaInicioTest`, `bfa/ReglaInicioBfa` |
| Infraestructura | Entidades JPA, repositorios y adaptadores; `persistence/bfa/…`; `AutorizacionEvaluacionProvisionalAdapter` |
| API | `InicioEvaluacionController`, `SujetoController`, `PeriodoController`, DTOs, `EvaluacionMapper`, `ManejadorErroresInicioEvaluacion`, `RelojConfig` |
| Frontend | Ajustes en `features/evaluacion` (tipos, mensajes de error, progreso) y sus pruebas |

Las reglas propias de BFA están en subpaquetes `bfa`, y el flujo común no depende de ellas. Se respetan las reglas de capas de ArchUnit.
