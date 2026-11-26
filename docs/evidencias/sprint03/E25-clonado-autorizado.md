# e25: clonado autorizado de una versión publicada

base: `8c0014ba91869a5f7659c58ea1826f7c24d63e3c`. corrección: `c39783b`.

el coordinador recibía 403 al ejecutar e25 con una versión publicada. la creación ya estaba autorizada, pero la copia interna exigía además el permiso de consulta directa, denegado por diseño. se retira esa comprobación de la copia; se conservan la autorización de creación y los controles de edición existentes. no se habilita la consulta directa.

## verificación

- antes de corregir: la prueba de clonado por http esperaba 201 y obtuvo 403; las otras dos regresiones pasaron.
- después: `mvn -q -f backend/pom.xml verify`, java 17 y postgresql 16 aislado: 383 pruebas, 0 fallos, 0 errores y 0 omitidas; spotless y empaquetado correctos.
- frontend: lint, formato, typecheck, 137 pruebas y build correctos. no cambia código del frontend.
- tres regresiones nuevas verifican clonado por el coordinador de la matriz vigente, rechazo del sujeto sin permiso y denegación de consulta directa. el origen permanece publicado y conserva sus opciones; el borrador recibe su copia.
- la autorización y persistencia son reales. solo la validación del token se sustituye en estas pruebas; los fixtures son ficticios y las transacciones se revierten.

no se ejecutaron operaciones de negocio en el vps ni se certifica el login emitido por e14, cuentas demo o aceptación pública. quedan pendientes revisión independiente, ci del sha publicado y testing antes de fusionar.
