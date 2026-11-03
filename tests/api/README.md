# Colecciones Postman

Colecciones de la API REST (`docs/api/openapi.yaml`) que usa Testing para aprobar el backend de un PR de GEN o BFA antes de que Integración lo fusione (el frontend se prueba en el navegador) (orden PR → Testing → Integración, INT-08).

- Una colección por módulo (`GEN.postman_collection.json`, `BFA.postman_collection.json`, …).
- Sin datos reales ni credenciales: variables de entorno (`{{base_url}}`, etc.) para lo sensible.
- Testing marca el PR con la etiqueta `testing-ok` en GitHub solo después de probar la rama del PR.
