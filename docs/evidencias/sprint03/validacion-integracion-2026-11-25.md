# Validación de integración del 25 de noviembre de 2026

Responsable de correcciones y verificación: Nelson Lazo Rocha.

## Código y CI

PR #138 integra las correcciones de #127, #131, #134, #136 y #137, y el acceso React de #111. Conserva sus historiales; evitar fusionar después las propuestas incorporadas por separado.

SHA integrado de backend y frontend: `4dc0b00aa1bb616603049eddee1370e8a5906f1a`.

[CI 45669486065](https://github.com/Nell16/sistema-evaluaciones/actions/runs/45669486065): backend 330 pruebas, cero fallos/errores/omitidas; frontend 137 pruebas en 18 archivos; compilación, formato, lint y reglas aprobados. Las etiquetas testing-ok/ci-ok faltaban y sus checks fallaron por esa condición. No se suman los resultados de los PR individuales.

El ajuste posterior del texto y mínimo de contraseña en recuperación y la colección local deben verificarse en el CI del nuevo SHA; el resultado anterior no se atribuye automáticamente a revisiones posteriores.

## API ejecutada en entorno aislado

Se compiló y arrancó la aplicación completa contra PostgreSQL 17.12 local con migraciones reales, sin modificar el VPS. El runtime local es Java 21 compilado con source/target 17 por ausencia de ct.sym; la compatibilidad con JDK 17 se verifica en el CI citado.

| Caso HTTP real                  | Resultado                                           |
| ------------------------------- | --------------------------------------------------- |
| Salud local                     | 200 UP                                              |
| E1 sin Bearer                   | 401                                                 |
| E13 registro de cuenta ficticia | 201                                                 |
| E14 credenciales válidas        | 200; token presente y expiraEn, rolActivo, permisos |
| Vigencia de sesión              | Aproximadamente dos horas                           |
| E1 con Bearer vigente           | 200                                                 |
| E15 cierre                      | 204                                                 |
| E1 con token cerrado            | 401                                                 |
| E16 sin canal SMTP              | 503 explícito                                       |

Newman ejecutó `tests/api/GEN-acceso-local.postman_collection.json` contra localhost: **7 solicitudes, 8 aserciones, cero fallos**. Incluye credenciales inválidas 401. La colección genera la cuenta y contraseña ficticias en memoria; no exportar sus variables, respuestas ni entornos.

## Recuperación con SMTP local real

Se arrancó un receptor SMTP aislado en loopback y se configuró la aplicación para usarlo. Los tokens se conservaron únicamente en memoria del proceso de pruebas; no se enviaron correos a servicios externos.

- E16 devuelve 202 y entrega un token al receptor local.
- Otra solicitud genera un token diferente; el anterior se rechaza con 400.
- Una contraseña corta se rechaza con 400 sin consumir el token válido.
- E17 cambia la contraseña con 204 y rechaza reutilizar el token con 400.
- La sesión abierta previamente queda revocada (401).
- La contraseña anterior deja de abrir sesión (401) y la nueva funciona (200).
- Un correo inexistente devuelve 202 sin generar correo.

Los casos de caducidad, concurrencia y cuenta inactiva también están cubiertos por las pruebas de integración de backend del CI. No se presentan como pruebas de correo externo.

## Límites y aceptación pendiente

1. La comprobación visual del frontend contra este backend no se completó: el navegador remoto no alcanzó localhost y rechazó observar esa página por su política. Las 137 pruebas de frontend son automatizadas y no sustituyen esa comprobación.
2. No se ha publicado este SHA ni se ha comprobado login demo en main. Las pruebas locales no acreditan Postman en la URL pública, datos móviles ni el equipo del docente.
3. Revisar las nuevas migraciones con el custodio y aprovisionar las asignaciones cuenta–sujeto autorizadas. No se crean asignaciones globales por rol.
4. Configurar SMTP real mediante el entorno seguro antes de aceptar recuperación publicada; sin canal, el 503 es intencional y esa funcionalidad no está entregada.
5. No se otorgó testing-ok ni se fusionó main/release en esta verificación. Mantener la decisión de aceptación pendiente hasta reunir la evidencia correspondiente.

## Tareas relacionadas y evidencia que registrar

| Tarea                     | Corrección / evidencia                                             | Pendiente para aceptación                      |
| ------------------------- | ------------------------------------------------------------------ | ---------------------------------------------- |
| TEST-50-PR127 / GEN-33-BE | Matriz y autorización integradas; CI conjunto                      | Validación funcional del SHA aceptado          |
| TEST-50-PR128 / GEN-42-BE | E26 exige ambos permisos; pruebas HTTP/PostgreSQL                  | Custodio y prueba del escenario publicado      |
| TEST-50-PR129 / GEN-48-N1 | Crear/consultar sujeto con permisos y alcance; error SQL selectivo | Verificación publicada con asignaciones demo   |
| TEST-50-PR130 / GEN-36-BE | E3/E4/E19 y períodos con autorización real y asignación explícita  | Custodio, asignaciones demo y prueba publicada |
| TEST-50-PR93 / GEN-32-BE  | E14–E17, vencimiento y recuperación probados localmente            | SMTP real, integración y evidencia pública     |
| TEST-50-PR111 / GEN-30-FE | Acceso React integrado con contrato de sesión finita; 137 pruebas  | Navegador contra API real y publicada          |
| TEST-50-PR113             | Matriz incorporada a #127/#138                                     | Validación integrada de roles/endpoints        |
| INT-32                    | Arranque/login local comprobados                                   | Despliegue real de main con E14 y cuenta demo  |
| TEST-31                   | Colección local de acceso ejecutada sin fallos                     | Colección BFA de acceso contra URL publicada   |

Estas notas no cambian estados ni asignaciones de ClickUp. Su conector devolvió límite diario de 100 llamadas, con reintento en unas 20 horas y 43 minutos a las 11:50 Nicaragua del 25 de noviembre.
