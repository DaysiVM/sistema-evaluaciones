# GEN-38-BE — preparación de E8

Avance del 25 de noviembre de 2026. No es la entrega completa de E8.

## Aporte de Christopher

`PreparacionFinalizacionService` cierra las subpruebas vencidas, vuelve a leer la evaluación y rechaza las que todavía tienen subpruebas PENDIENTES o ACTIVAS. Conserva una finalización previa y el error de evaluación inexistente. Se agregó `SUBPRUEBAS_PENDIENTES` con respuesta 409 al manejador común.

Las cinco pruebas de preparación pasan. La integración usa la rama de Max `feature/max-GEN-37-BE2`, commit `97a308dcdf6f026f64a42536ca880796a2c5e925`, junto con main y el cierre de #76. E5–E7 pertenecen a Max. Los ajustes de integración conservan los métodos de E3, unifican el DTO de errores, evitan el ciclo entre application y mapper y crean los sujetos y periodos ficticios exigidos por las nuevas claves foráneas.

## Verificación

- Java 17, PostgreSQL 17 local y desechable: `mvn -f backend/pom.xml verify`, 246 pruebas, 0 fallos, errores u omisiones. Spotless y ArchUnit correctos.
- Frontend idéntico al de #76: npm ci, lint, format:check, typecheck, 111 pruebas y build correctos.
- Datos ficticios; servidor aislado detenido al finalizar. Las pruebas locales no conceden testing-ok.

## Para terminar E8

1. Confirmar la entrega vigente de GEN-37-BE2 y probar los cierres con E5–E7.
2. Añadir el controlador POST y la transición persistente a FINALIZADA, serializando por evaluación y manteniendo la repetición idempotente.
3. Conectar la persistencia de resultados de GEN-39-BE para devolver un resultadoId real y su estado. El cálculo BFA se conecta después en BFA-BE-38; no hace falta esperar sus fórmulas para preparar E8.
4. Integrar RegistroAuditoria de GEN-46-BE, con la cuenta autenticada como actor, en la misma operación de finalización.
5. Probar la respuesta completa E8, reintentos y concurrencia, errores 401/403/404/409 y rollback de finalización si falla auditoría o la persistencia del resultado. Después, CI y testing-ok.

Al revisar, GEN-46-BE y GEN-39-BE todavía no tenían una rama o PR disponible. No se agregó una tabla paralela de resultados ni un reemplazo de auditoría. Este avance no expone el endpoint ni cambia la evaluación a FINALIZADA, y no debe fusionarse como si cumpliera el contrato de E8.
