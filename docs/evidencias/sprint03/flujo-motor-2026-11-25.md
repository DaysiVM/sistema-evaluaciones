# Integración del motor y comprobación de finalización

Preparación y correcciones: Nelson Lazo Rocha, 25 de noviembre de 2026.

## Alcance

Integra sobre #138 las propuestas #132 (E27), #140 (E28), #139 (auditoría E3), #133 (motor E5–E7 y cierre temporal) y el commit `4320668c650f64bd98f6731173ec0faf39dfd051` de auditoría E5. Conserva los historiales originales. No fusionar por separado propuestas ya incorporadas.

Correcciones adicionales:

- E4 autoriza antes de cerrar subpruebas vencidas; una denegación no provoca escritura. El contexto JPA se sincroniza después del cierre JDBC para no devolver estados antiguos.
- E5–E7 tienen permisos explícitos del rol SUJETO y un adaptador real de alcance basado en `cuenta_sujeto_asignado`. La coordinación no responde por el sujeto. No se crean asignaciones generales ni se cambian cuentas del VPS.
- E8 agrega POST `/api/v1/evaluaciones/{id}/finalizar`: permiso del sujeto, asignación, bloqueo de la evaluación, cierre temporal, validación de pendientes, persistencia idempotente del contenedor y auditoría dentro de una transacción.
- Una nueva migración crea `resultado` con unicidad por evaluación, estado y referencia de revisión vigente. La revisión psicométrica y la publicación de puntuaciones no se inventan.

## Resultado observado

Suite local completa con PostgreSQL 17.12: **380 pruebas, 0 fallos, 0 errores, 0 omitidas**. Spotless: **281 archivos limpios**. El runtime local es Java 21 con source/target 17; comprobar también el CI con JDK 17 antes de integrar.

Recorrido contra la aplicación Spring Boot arrancada y PostgreSQL real en localhost:

| Caso | Resultado |
| --- | --- |
| E27 crea pregunta ficticia con coordinación autorizada | 201 |
| E28 guarda opciones y permite reintentar | 200 / 200 |
| E28 con sujeto sin permiso | 403 |
| E28 sobre versión publicada | 409 |
| Colección Newman del motor | 12 solicitudes, 13 aserciones, 0 fallos |
| E3 crea; E4 consulta; E5 inicia y permite reintentar; E6 lee; E7 responde | Códigos y estructura esperados |
| Coordinación intenta E5/E7; otro sujeto intenta E6; llamada sin token | 403 / 403 / 403 / 401 |
| E8 con subprueba activa y tiempo disponible | 409, sin finalizar |
| E8 con subprueba vencida | 200 FINALIZADA; cierre igual al límite; resultado PENDIENTE_VALIDACION |
| Repetir E8 | Mismo resultado y fechas; un solo contenedor y evento |
| Falla ficticia de auditoría durante E8 | 500; rollback de evaluación, resultado y cierre temporal |
| Dos peticiones E8 concurrentes | Ambas 200 con el mismo resultado; un solo contenedor y evento |

El fallo de auditoría se provocó mediante un trigger temporal **solo en la base local desechable**, que se retiró al finalizar el caso. No se ejecutaron estas operaciones en el VPS. Las claves y tokens se generaron y mantuvieron en memoria privada, sin volcarlos en archivos, informes ni exports.

## Reproducción aislada

- Preparar una base PostgreSQL local desechable en `127.0.0.1:55432`, con nombre/usuario en POSTGRES_DB/POSTGRES_USER y contraseña solo en entorno privado.
- Compilar el JAR de backend con JDK 17 y `mvn -f backend/pom.xml verify`.
- Instalar `psycopg[binary]` y Newman desde sus registros oficiales; Node debe estar en PATH.
- Establecer `BFA_BD_AISLADA=SI`; si Newman no está en la resolución normal de Node, NEWMAN_MODULE apunta a su módulo instalado.
- Ejecutar `python tests/api/verificar_motor_local.py`. Arranca el JAR en loopback, crea cuentas y contenido ficticios, ejecuta la colección y prueba rollback y concurrencia. No exportar entornos de Postman ni respuestas de sesión.
- La colección está en `tests/api/GEN-motor-local.postman_collection.json` y rechaza bases distintas de localhost.

## Límites de aceptación

- El contenedor de E8 queda PENDIENTE_VALIDACION. **La consolidación con versiones aprobadas de clave/reglas/baremo sigue pendiente de BFA-BE-38; no se declara ese cálculo implementado, publicado ni aprobado por esta evidencia.**
- La consulta/persistencia de revisiones y el ciclo profesional E9–E12 todavía requieren integración; no contar esas funciones como completadas por existir un contenedor de resultado.
- La migración nueva y la de auditoría requieren revisión del custodio; no se alteran migraciones integradas.
- No se acreditó frontend en navegador contra el backend ni Postman en la URL publicada. No se asigna testing-ok ni se sustituye una revisión independiente.
- ClickUp queda para el registro final según la instrucción de Nelson; este informe no cambia estados, plazos ni responsables.
