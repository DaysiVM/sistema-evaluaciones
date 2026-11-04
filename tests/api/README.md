# Colecciones Postman para Testing

Las colecciones de pruebas de API viven aquí. Sirven para revisar Pull Requests de backend antes de que Integración los fusione. **Todavía no deben inventarse endpoints de negocio**: `docs/api/openapi.yaml` debe reflejar contratos aprobados antes de preparar solicitudes reales.

## Inicio rápido

1. Lee la tarea `GEN-xx-TEST` o `BFA-TEST-xx` correspondiente en [el reparto](../../docs/pruebas/PLAN-OPERATIVO-TESTING.md).
2. Identifica el PR de Desarrollo, su número y el SHA que vas a probar. Consulta [la guía general](../README.md) para obtener una versión local del PR.
3. Levanta backend y PostgreSQL según las instrucciones vigentes del proyecto; nunca uses la base de datos compartida de producción para cargar datos de prueba.
4. Abre Postman e importa la colección del módulo si ya existe (por ejemplo, `GEN.postman_collection.json` o `BFA.postman_collection.json`). Si todavía no hay colección, créala **solo con rutas existentes y aprobadas**.
5. Configura un entorno local con `base_url` (URL de tu API de pruebas) y, si corresponde, `token` de una cuenta **ficticia**. No exportes valores secretos a GitHub.
6. Ejecuta los escenarios positivos, negativos y límites aprobados: comprueba HTTP, cuerpo JSON, validaciones, permisos, errores y efectos secundarios.
7. Guarda evidencias de resultado, PR/SHA, fecha, salida observada y fallos en `docs/pruebas/` usando [la plantilla](../../docs/pruebas/PLANTILLA-CASO-PRUEBA.md).

## Convenciones

- Una colección versionada por módulo, con carpetas para las historias/casos de prueba correspondientes.
- Nombres sugeridos: `GEN.postman_collection.json`, `BFA.postman_collection.json`. No crees colecciones vacías solo para aparentar avance.
- Usa variables de entorno Postman: `{{base_url}}`, `{{token}}`, identificadores de pruebas ficticios, etc.
- Nunca subas contraseñas, tokens, datos de evaluados reales ni exports de entornos con secretos.
- Si el contrato de API o el endpoint todavía no existe, documenta **BLOQUEADO** y prepara casos teóricos, sin afirmar que se probaron.
- El endpoint técnico `/actuator/health` solo comprueba salud del servicio; **no** valida la funcionalidad de negocio.
- Si un caso falla, crea la incidencia en la lista **Bugs** de ClickUp y comenta el PR; no coloques `testing-ok`.
- Solo cuando todos los criterios aplicables pasan, comenta la conformidad del SHA probado y coloca o solicita `testing-ok` conforme a los permisos acordados. Si cambia el código, repite las pruebas afectadas.

Consulta también [tests/README.md](../README.md) para el flujo completo Desarrollo → Testing → Integración y los enlaces a tareas reales.
