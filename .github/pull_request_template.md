## Tarea y alcance

- Tarea ClickUp: <código> · CU-<id interno>   (copiar ID real; añadir `[estado]` solo para un cambio de estado autorizado)
- Módulo:
- Resumen del cambio:
- Nota: `CU-ID[estado]` puede cambiar el estado de ClickUp; no usar el sufijo en PRs de prueba.

## Trazabilidad

- RF / RNF: 
- Caso(s) de uso: 
- Contrato API / migración impactada: 

## Validaciones

- [ ] `docker compose up -d db` y `mvn -f backend/pom.xml verify`
- [ ] `npm run typecheck`, `npm test` y `npm run build` desde `frontend`
- [ ] Evidencia o captura sin información sensible
- [ ] Contratos y documentación actualizados
- [ ] Sin regresiones en módulos anteriores
- [ ] No hay secretos ni datos personales reales
- [ ] Si es código de una tarea GEN o BFA: Testing lo probó (backend con `tests/api/`, frontend en el navegador) y puso la etiqueta `testing-ok`

## Revisión del equipo

- Integrador responsable:
- Tester y criterio de aceptación:
- Documentador si cambió contrato/modelo:
- Riesgos y tareas pendientes:
