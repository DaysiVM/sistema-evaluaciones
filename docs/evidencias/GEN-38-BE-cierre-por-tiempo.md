# GEN-38-BE — cierre por tiempo

Verificación del 25 de noviembre de 2026 sobre el commit `90791776e9e5f77b436eca8cadfd39e50d64f086`, con main `9096b5b8483c2bdf99b3076e9f05844db310f839` integrado.

- Java 17 y PostgreSQL 17 en una base local desechable: `mvn -f backend/pom.xml verify`, 213 pruebas, 0 fallos, errores u omisiones. Spotless y ArchUnit correctos.
- Frontend: `npm ci`, `npm run lint`, `npm run format:check`, `npm run typecheck`, `npm test` y `npm run build` correctos. La verificación usa el contenido exacto de Git, sin la conversión local a CRLF.
- `CierreSubpruebasIntegrationTest` comprueba E4 con Bearer ficticio, temporizador sin peticiones, concurrencia, repetición del cierre, subprueba con tiempo y evaluación inexistente. El cierre conserva `fecha_cierre = fecha_fin_limite` y una sola solicitud de cálculo.
- El siguiente inicio se comprueba mediante SQL; no acredita E5 ni el recorrido integrado de E5–E7. El temporizador se invoca directamente en la prueba, con la planificación automática desactivada en los contextos de pruebas.

Todos los datos son ficticios. La base y el servidor aislados se detienen al terminar. No se modifican migraciones, reglas de cálculo ni datos de producción.

E8 se trabaja en una rama separada. Su finalización debe esperar a la conexión de resultados y `RegistroAuditoria`; este PR no declara ese endpoint completo. Testing debe repetir la verificación y aplicar `testing-ok` antes de la fusión.
