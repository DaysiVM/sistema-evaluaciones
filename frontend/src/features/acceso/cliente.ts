import { http } from '../../services/http';
import type { ClienteAcceso, CuentaResponse, SesionResponse } from './tipos';

/** E13–E17. El acceso usa la API real; los servicios simulados se integran en GEN-31-FE. */
export const clienteAcceso: ClienteAcceso = {
  registrar: (solicitud) =>
    http<CuentaResponse>('/auth/registro', {
      method: 'POST',
      body: JSON.stringify(solicitud),
    }),
  iniciarSesion: (solicitud) =>
    http<SesionResponse>('/auth/sesion', {
      method: 'POST',
      body: JSON.stringify(solicitud),
    }),
  cerrarSesion: () => http<void>('/auth/sesion', { method: 'DELETE' }),
  pedirRecuperacion: (solicitud) =>
    http<void>('/auth/recuperacion', {
      method: 'POST',
      body: JSON.stringify(solicitud),
    }),
  confirmarRecuperacion: (solicitud) =>
    http<void>('/auth/recuperacion/confirmar', {
      method: 'POST',
      body: JSON.stringify(solicitud),
    }),
};
