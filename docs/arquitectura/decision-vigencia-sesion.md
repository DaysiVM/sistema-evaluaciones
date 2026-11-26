# Vigencia de sesiones E14

Decisión de Nelson Lazo Rocha, 25-nov-2026: sesiones de dos horas, configurables.

La propiedad `seguridad.sesion.duracion` acepta una duración ISO-8601 positiva; el valor predeterminado es `PT2H`. E14 persiste y devuelve la misma fecha `expiraEn`. Bearer deja de aceptar el token al llegar a esa fecha; E15 puede revocarlo antes. Cambiar la duración afecta las nuevas sesiones.

Esta decisión no define la recuperación de contraseña E16/E17 ni concede roles o permisos. Esas partes conservan sus pendientes. No se incluyen cuentas o credenciales de demo.

## Recuperación E16/E17

Nelson autorizó tokens de recuperación de 15 minutos, un solo uso, invalidación al pedir otro y contraseña nueva de al menos 12 caracteres. Las cuentas inhabilitadas no se recuperan. El consumo y cambio de hash se serializan por cuenta dentro de una transacción; cambiar la contraseña revoca sus sesiones abiertas. El token se guarda únicamente como resumen, en una tabla separada y no puede autenticar Bearer.

El canal real se configura con las propiedades estándar `spring.mail.*` y `seguridad.recuperacion.remitente`. Sin canal configurado, E16 devuelve 503 para cualquier correo y no simula envío; con el canal disponible, devuelve 202 sin cuerpo para cuenta activa, inhabilitada o inexistente. No se imprimen tokens o contraseñas en logs, tampoco con perfil demo. Las pruebas sustituyen únicamente el transporte para capturar el token en memoria privada. La configuración del canal real y la revisión de la nueva migración por el custodio siguen pendientes antes de certificar la publicación.

Los límites de nombres/apellidos de registro se alinean a VARCHAR(120) de la base ya integrada para responder 400 antes de persistir; OpenAPI documenta ese máximo. El registro público sigue limitado a SUJETO; no se inventa concesión de roles privilegiados.
