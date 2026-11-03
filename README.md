# Sistema Integrado de Evaluaciones 

> **Alcance actual: solo configuración y estructura.** No incluye endpoints de negocio, cuestionarios, fórmulas, entidades, tablas de evaluación, autenticación ni reportes. La única clase Java es el punto de arranque de Spring Boot; el único componente React es una pantalla de bienvenida.

## Tecnologías solicitadas

- **Backend:** Spring Boot 3.5.16 con Java **17** y Maven.
- **Frontend:** React web (no React Native), TypeScript y Vite como herramientas de arranque.
- **Base de datos:** PostgreSQL 17; **Flyway** preparado para migraciones futuras (todavía no hay tablas de negocio).
- **Colaboración:** un solo repositorio público de GitHub, ramas, pull requests y comprobaciones de compilación.

**Nota terminológica:** Flyweight es un patrón de diseño; Flyway es la herramienta de migraciones de PostgreSQL mencionada en los documentos. Aquí no se implementa el patrón Flyweight.

## Estructura

| Ruta | Propósito | Estado |
| --- | --- | --- |
| `backend/` | Proyecto Maven, arranque de Spring Boot y paquetes separados por capas | Esqueleto |
| `frontend/` | Proyecto React web con un componente de bienvenida y carpetas por módulos | Esqueleto |
| `database/migrations/` | Lugar reservado para SQL versionado con Flyway | Vacío |
| `docs/` | Requisitos, reglas, contratos, modelo, diagramas y traspaso entre equipos | Plantillas y referencias |
| `tests/` | Espacio para futuros casos end-to-end | Vacío |
| `.github/` | GitHub Actions, plantillas de issues y pull requests | Configurado |
| `compose.yaml` | PostgreSQL local reproducible | Configurado |
| `.env.example` | Ejemplo de variables locales, sin datos reales | Configurado |

## Iniciar el esqueleto desde IntelliJ IDEA

1. Clonar el repositorio y abrir la **raíz completa** en IntelliJ IDEA.
2. Tener instalados JDK 17, Node.js 22, npm y Docker (o PostgreSQL 17).
3. Ejecutar `docker compose up -d db` desde la raíz.
4. Ejecutar `mvn -f backend/pom.xml spring-boot:run` para iniciar Spring Boot en `localhost:8080`.
5. Ejecutar `cd frontend`, después `npm ci` y `npm run dev` para abrir React en `localhost:5173`.

El backend **no expone endpoints de evaluación**; solo puede exponer el estado técnico de salud de Spring Boot Actuator. El frontend únicamente muestra que la estructura fue preparada.

## Comprobaciones técnicas

```bash
mvn -f backend/pom.xml verify
cd frontend
npm ci
npm run lint
npm run format:check
npm run typecheck
npm run build
```

GitHub Actions valida la compilación del esqueleto. Cuando se desarrollen funcionalidades, se agregarán pruebas y criterios de aceptación.

## Formato de código

El CI revisa el formato, no las personas: `mvn verify` incluye `spotless:check` (Google Java Format, estilo AOSP) y el CI de frontend corre ESLint y Prettier. Para aplicar el formato antes de subir un PR:

```bash
mvn -f backend/pom.xml spotless:apply
cd frontend && npm run format
```

## Trabajo por ramas y revisión de Pull Requests

**Regla de colaboración:** cada integrante trabaja en **su propia rama de desarrollo**, creada a partir de `main`. **Nadie debe realizar commits ni hacer push directamente a `main`**, incluida la coordinación del proyecto. La rama `main` se reserva para versiones revisadas e integradas.

1. **Crear una rama personal por tarea o issue.** Partir de la versión actual de `main` y usar un nombre descriptivo, por ejemplo `feature/nelson-GEN-01-BE`, `fix/ana-BUG-03` o `docs/luis-DOC-03` (formato `tipo/nombre-<código de tarea>`; el CI lo exige). Si un integrante trabaja en otra tarea, abre otra rama.
2. **Trabajar y publicar solo en esa rama.** Realizar allí los commits y subirlos a GitHub. No trabajar directamente sobre `main`.
3. **Solicitar integración mediante un Pull Request (PR).** Crear un PR desde la rama de trabajo hacia `main`, con el código de la tarea de ClickUp en el título (`[GEN-01-BE] …`), la línea `Tarea ClickUp: <código> · CU-<id>` en la descripción (sin `[estado]` por defecto; ese sufijo modifica el estado de ClickUp) y explicando qué cambió y cómo se verificó.
4. **Revisión exclusiva del proceso de integración.** El **equipo de integración** es responsable de revisar los PRs dirigidos a `main`, pedir correcciones y comprobar la coherencia entre backend, frontend, base de datos y documentación.
5. **Verificar antes de fusionar.** El PR debe obtener la aprobación requerida, resolver los comentarios pendientes y pasar las comprobaciones automáticas disponibles (el check `ci-ok`, que agrupa reglas, backend y frontend). El equipo de integración realiza la fusión cuando todo esté conforme.
6. **Sin acceso directo ni excepciones informales.** No se aceptan pushes directos, force-push ni eliminación de `main`; todo cambio debe ser trazable a un PR revisado.

**Protección técnica (vigente):** la ruleset de `main` exige abrir PR, actualizar la rama frente a `main`, obtener al menos una aprobación válida de CODEOWNERS, resolver las conversaciones y superar el check obligatorio `ci-ok`. Solo está permitida la fusión **Squash and merge**; se prohíben force-pushes y eliminación de `main`. La lista de CODEOWNERS es rotativa: los usuarios pendientes de aceptar su invitación no cuentan como revisores hasta tener permiso de escritura.

Más detalles en [CONTRIBUTING.md](CONTRIBUTING.md) y [flujo de equipos](docs/equipos/FLUJO.md).

## Vinculación GitHub–ClickUp

El repositorio `Nell16/sistema-evaluaciones` se conecta con el espacio `Sistema_tests` de ClickUp. **Cada PR debe incluir el código de tarea y su identificador real de ClickUp**, que puede copiarse desde la tarea. El formato sin cambio de estado es:

```text
Tarea ClickUp: INT-01-CU · CU-86bcvajad
```

Este formato permite enlazar la actividad a la tarea. **No añadir** `[in review]`, `[done]` ni otro sufijo entre corchetes a menos que la coordinación haya autorizado explícitamente cambiar el estado de ClickUp. La integración puede interpretar `CU-ID[estado]` como una instrucción de cambio de estado.

No usar directamente el nombre de rama automático de ClickUp sin comprobarlo: la validación de CI exige `tipo/nombre-CODIGO`, con título de PR `[CODIGO] Descripción`. La vinculación se hace mediante el ID en la descripción; no es necesario incluirlo en la rama.

Consultar la [guía operativa de GitHub y ClickUp](docs/equipos/CONFIGURACION-GITHUB.md). La fusión de un PR a `main` puede desencadenar un **despliegue automático** al VPS: no fusionar PRs de prueba sin acuerdo con Integración y el administrador del servidor.

## Antes de comenzar funcionalidades

1. Revisar requisitos y aprobaciones en `docs/requisitos/` y `docs/casos-de-uso/`.
2. Acordar el modelo de datos en `docs/modelo-datos/` antes de crear migraciones SQL.
3. Acordar los contratos en `docs/api/` antes de programar controladores o consumo de API.
4. Seguir `docs/equipos/FLUJO.md` y `CONTRIBUTING.md` para ramas, issues, revisión y traspasos.
5. No subir datos personales ni pruebas psicológicas reales. Ver `SECURITY.md`.

Los cinco tipos de evaluación citados en el plan son **módulos futuros**, no funcionalidades implementadas. Los documentos y diagramas se preservan como material de referencia.
