# PostgreSQL y Flyway — estructura inicial

PostgreSQL 17 está configurado en `compose.yaml`. Spring Boot incorpora el controlador JDBC y Flyway para futuras migraciones.

**Actualmente no hay tablas, datos ni migraciones del negocio.** La carpeta `migrations/` está reservada.

Cuando DOC-03 apruebe el modelo, el equipo agregará migraciones con el formato
`V<AAAAMMDDHHmm>__<modulo>_<descripcion>.sql` (ej. `V202611031530__bfa_crear_tablas.sql`).
La fecha y hora (en UTC) evita que dos ramas usen la misma versión. `<modulo>` es el módulo en minúsculas
(`bfa`) o `comun` para las tablas generales. Spring Boot las empaqueta desde esta carpeta (ver `backend/pom.xml`).

- Cada tarea crea en su PR solo las tablas que usa. Las tablas comunes (catálogo de tests) se fusionan antes que las que dependen de ellas.
- Si una migración usa tablas de otra con fecha posterior, antes de fusionar se renombra con la fecha y hora actuales.
- No editar ni borrar migraciones ya fusionadas: el CI lo rechaza. Los errores se corrigen con otra migración compatible.
- Las migraciones no cargan datos; las pruebas crean sus propios datos ficticios.

Reglas completas: Documento general §5.1.8.

**Flyway ≠ Flyweight:** Flyway gestiona migraciones de base de datos; Flyweight es un patrón de diseño. Se usa Flyway según el contexto de los documentos del curso.
