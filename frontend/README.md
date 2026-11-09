# Frontend

## Formato de código

El CI exige `npm run lint` (ESLint) y `npm run format:check` (Prettier) en verde. Antes de subir un PR:

```bash
npm run format
npm run lint
```

`npm run format` aplica Prettier sobre todo el proyecto; `npm run lint` revisa ESLint (hooks de React, imports, etc.) sin modificar archivos.

## Preview del sistema visual UI-01

La preview local de `UI-01` permite revisar la interfaz común sin API, scoring ni datos reales. Al abrirla, usa el
panel de controles “Variaciones del test” para cambiar los estados mediante botones; la URL solo conserva una forma
opcional de iniciar una combinación concreta.

Inicia el frontend desde este directorio:

```bash
npm run dev -- --host 0.0.0.0
```

Abre la vista base:

```text
http://localhost:5173/dev/ui-01
```

### Variaciones disponibles

| URL                             | Qué muestra                              |
| ------------------------------- | ---------------------------------------- |
| `/dev/ui-01`                    | Pregunta de selección con cinco opciones |
| `/dev/ui-01?response=text`      | Campo de respuesta libre                 |
| `/dev/ui-01?phase=instructions` | Pantalla de instrucciones                |
| `/dev/ui-01?phase=practice`     | Pantalla de práctica                     |
| `/dev/ui-01?phase=closed`       | Subprueba cerrada                        |
| `/dev/ui-01?notice=warning`     | Aviso textual de tiempo                  |
| `/dev/ui-01?notice=error`       | Error textual                            |
| `/dev/ui-01?save=saving`        | Guardado en progreso                     |
| `/dev/ui-01?save=error`         | Error de guardado                        |
| `/dev/ui-01?resource=visual`    | Recurso visual de ejemplo                |

Los parámetros se pueden combinar. Por ejemplo:

```text
http://localhost:5173/dev/ui-01?save=error&resource=visual
```

La preview usa contenido demostrativo y sirve para validar la biblioteca de componentes, los estados y la semántica accesible antes de conectarla al motor de evaluación.

## Preview de variantes BFA (UI-04)

`UI-04` extiende los componentes comunes de `UI-01` para los tipos de pregunta del BFA. Usa contenido de **ejemplo**
(no son reactivos reales) y marcadores en lugar de las figuras oficiales. Con `npm run dev`, abre:

```text
http://localhost:5173/dev/ui-04
http://localhost:5173/dev/ui-04?subprueba=N1-A
```

Cada subprueba (`VOC1`, `VOC2`, `S1`, `S2`, `S1b`, `R1`, `R2`, `CV`, `N1-A`, `N1-B`, `N2-A`, `N2-B`, `FV`) muestra su
ficha con tiempo oficial, tipo de respuesta y requisito. También acepta `phase`, `save` y `notice` como en UI-01.

| Tipo de contenido             | Cómo se compone                                                              |
| ----------------------------- | ---------------------------------------------------------------------------- |
| Selección (3, 4 o 5 opciones) | `optionsLayout="list"` o `"compact"` (rejilla de dos columnas)               |
| Binaria (Verdadero / Falso)   | `optionsLayout="binary"`                                                     |
| Figuras                       | `questionResource` con `<Figura rol="estimulo" />` y `figure` en cada opción |
| Operaciones                   | `<Operacion>`, `<OperacionVertical>`, `<Fraccion>`, `<Casilla>` y `numeric`  |
| Problemas y proverbios        | `promptVariant="statement"`                                                  |
| Texto (Fluidez Verbal)        | `responseArea={<FluidezVerbal />}`                                           |

Para usar figuras reales basta pasar `src` (el `recursoUrl` de la API) a `<Figura>`: sin `src` dibuja el marcador.
Los componentes comunes siguen funcionando igual: todas las props nuevas de `TestScreen` son opcionales.
