# Contribución y gobierno del repositorio

## Etapa actual

La rama `main` contiene la configuración inicial de Spring Boot, React, PostgreSQL y la estructura documental. Todavía **no** hay funcionalidades de evaluación ni pruebas funcionales.

## Reglas para los equipos

1. Crear una rama por tarea desde `main` con formato `tipo/nombre-CODIGO`; los tipos válidos son `feature`, `fix`, `docs`, `test` y `chore` (por ejemplo, `docs/nelson-INT-01-CU`).
2. No agregar cuestionarios, endpoints, entidades, SQL ni reglas de puntuación sin requisitos, contratos y modelo aprobados.
3. Abrir PR hacia `main` con título `[CODIGO] Resumen` y referencia `Tarea ClickUp: CODIGO · CU-ID` en la descripción; explicar validaciones y documentación modificada. Agregar `[estado]` al ID de ClickUp solo cuando esté autorizado cambiar el estado de la tarea.
4. Coordinar cualquier cambio de API entre frontend, backend, documentación e integración.
5. Usar Flyway para nuevas migraciones solo después de aprobar DOC-03. No modificar una migración ya aplicada.
6. No subir datos reales, contraseñas, tokens ni archivos `.env`.
7. La revisión y fusión corresponden a los integradores designados; testers y documentadores participan según la rotación. La ruleset activa de `main` exige `ci-ok`, una aprobación de CODEOWNERS, conversaciones resueltas y `Squash and merge`.
8. **Orden PR → Testing → Integración (INT-08):** en un PR de código de una tarea `GEN-*` o `BFA-*`, Testing prueba la rama (backend con la colección Postman de `tests/api/`, frontend en el navegador) y, si pasa, pone la etiqueta `testing-ok` en GitHub; sin esa etiqueta el check `testing-ok` del CI falla y el PR no puede fusionarse. Las tareas `-TEST` e `-INT` y las que no son GEN ni BFA no la necesitan.

## Comprobación técnica inicial

GitHub Actions ejecuta `backend-build` y `frontend-build`: se confirma que los proyectos base compilan. **No equivale a validar funcionalidades que aún no existen.**

Cuando comiencen las historias, agregar pruebas unitarias, de integración y criterios de aceptación. Proteger `main` mediante las reglas del repositorio y exigir PR y las comprobaciones disponibles.

`.github/CODEOWNERS` ya está activo y exige revisión de propietarios del código. Los usuarios invitados que aún no aceptan colaboración no cuentan como revisores hasta obtener permiso de escritura. Ver [configuración operativa](docs/equipos/CONFIGURACION-GITHUB.md). Al fusionar a `main`, el despliegue puede ejecutarse automáticamente; no fusionar pruebas sin autorización del responsable del VPS.
