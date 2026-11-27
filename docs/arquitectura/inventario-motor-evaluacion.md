# Inventario del motor de evaluación

Qué existe en `main` al 26 de noviembre de 2026 y cómo se reutiliza para otras pruebas (Weill, Thurstone, Allport, Orientación vocacional). Es un inventario de lo verificable en el código y en las migraciones; lo propuesto y todavía no implementado se marca como tal. Los datos son ficticios.

## Capas del backend

Paquete base `com.universidad.tests`, con reglas de dependencias de ArchUnit en `ArquitecturaTest`:

| Capa | Contenido |
|---|---|
| `controller` | REST, filtro Bearer (`FiltroSesionBearer`) y manejo de errores de entrada |
| `application/service`, `application/usecase` | Casos de uso y autorización (`AutorizacionService`, `AutorizacionContenidoService`, `AutorizacionSujetoService`, `AutorizacionEvaluacionService`) |
| `application/evaluator` | Contrato `TestEvaluator`, `TestEvaluatorRegistry` y los evaluadores por prueba (`bfa`) |
| `domain/model`, `domain/port`, `domain/repository`, `domain/exception` | Entidades de dominio sin dependencias de infraestructura, puertos y repositorios |
| `infrastructure/persistence`, `infrastructure/security` | Adaptadores JPA y JDBC, hash de contraseñas y tokens de sesión |
| `dto`, `mapper`, `exception`, `config` | Contratos HTTP, mapeos, manejadores de errores y configuración |

## Tablas en `main` (migraciones)

| Migración | Tablas |
|---|---|
| `V202611040249__comun_catalogo_versionado` | `test`, `subprueba`, `version_formulario`, `configuracion_subprueba`, `pregunta`, `opcion_respuesta` |
| `V202611240156__comun_cuenta_y_sesion` | `cuenta`, `cuenta_rol`, `sesion` |
| `V202611240800__comun_motor_evaluacion` | `evaluacion`, `evaluacion_subprueba`, `respuesta_evaluacion`, `solicitud_calculo_subprueba` |
| `V202611240830__comun_sujeto_periodo` | `sujeto`, `periodo` |
| `V202611251100__comun_recuperacion_cuenta` | `recuperacion_cuenta` |
| `V202611251130__comun_cuenta_sujeto_asignado` | `cuenta_sujeto_asignado` |
| `V202611251204__comun_evento_auditoria` | `evento_auditoria` |
| `V202611251400__comun_resultado_pendiente` | `resultado` |

Otras tres migraciones ajustan tablas existentes: `V202611111300` (bloquea estados iniciales inválidos), `V202611170600` (agrega el factor a la subprueba) y `V202611251500` (opciones esperadas en la configuración).

## Endpoints: contrato frente a `main`

Implementadas (21): E1, E2, E3, E4, E5, E6, E7, E8, E13, E14, E15, E16, E17, E18, E19, E21, E23, E25, E26, E27, E28.

Faltan (10): E9 consultar resultados, E10 revisión de palabras de Fluidez Verbal, E11 recálculo, E12 publicar resultado vigente, E20 crear período, E22 listar evaluaciones, E24 instrucciones de la subprueba, E29 clave de respuestas, E30 baremos, E31 publicar versión.

El cruce se hizo por ruta y verbo contra `docs/api/openapi.yaml`; no demuestra que cada operación se comporte como el contrato.

## Puntos de extensión para una prueba nueva

Lo que ya está pensado para que cada prueba aporte lo suyo sin tocar el flujo común:

1. **Contenido de la prueba como datos.** Test, subpruebas, versión de formulario, configuración, preguntas y opciones se crean con E25 a E28 sobre las mismas tablas. Según `docs/modelo-datos/aplicacion-respuestas.md`, Weill cabe en ese núcleo sin tablas nuevas.
2. **`TestEvaluator`.** Cada prueba implementa `getCodigoTest()` y `consolidar(...)`, y se registra en `TestEvaluatorRegistry` desde `config/gen/EvaluatorConfiguration`. El motor común no incorpora fórmulas de ninguna prueba. BFA ya tiene `BfaTestEvaluator` y `ConsolidadorCompuestosBfa`.
3. **`ReglaInicioTest`.** Punto de extensión del inicio de la evaluación (E3): `aplicaA`, `validar` y `registrar`. BFA aporta `ReglaInicioBfa`.
4. **Autorización por permisos.** Los permisos por rol viven en `MatrizPermisos`; cada servicio de autorización exige el permiso que dice el contrato.
5. **Tablas propias solo si hacen falta.** BFA tiene `version_clave_bfa`, `entrada_clave_bfa`, `baremo_bfa` y `rango_baremo_bfa` (en PR abiertos, todavía no en `main`). Para el cálculo común, `docs/modelo-datos/calificacion-baremos.md` propone `escala`, `regla_calificacion`, `revision_resultado`, `puntaje_subprueba`, `resultado_escala` y `conversion_resultado_escala`. Es propuesta, no está en migraciones.

## Pasos sugeridos para añadir una prueba

1. Fijar en la documentación el núcleo que reutiliza y las tablas propias que necesita, con la misma convención de nombres.
2. Cargar el contenido con los endpoints de configuración, con permisos reales y datos ficticios.
3. Implementar y registrar el `TestEvaluator` de la prueba, con sus reglas de puntuación aprobadas.
4. Añadir `ReglaInicioTest` si E3 necesita comprobaciones propias.
5. Declarar los permisos de sus endpoints en `MatrizPermisos`, con pruebas HTTP que usen el servicio de autorización real: éxito con el permiso, 403 sin él.
6. Pedir al custodio el nombre de cada migración nueva: versión posterior a la última de `main`, única y sin duplicar tablas.

## Lo que no se sabe todavía

- Cómo se comportan en producción las operaciones implementadas: falta evidencia de punta a punta contra la URL publicada.
- Qué baremos y claves oficiales usarán Weill, Thurstone y Allport. Siguen como incógnitas en `docs/modelo-datos/`.
- Si el cálculo de resultados (E8 a E12) se integra con las tablas propuestas de calificación o con las de los PR abiertos.
