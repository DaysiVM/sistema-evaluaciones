// Tipos de GEN-06 según DOC-05 (E13–E17). Los nombres siguen el contrato.

export const ROLES = ['SUJETO', 'PSICOLOGO', 'COORDINADOR', 'ACADEMICO', 'DECANO'] as const;
export type Rol = (typeof ROLES)[number];

export const ETIQUETAS_ROL: Record<Rol, string> = {
  SUJETO: 'Sujeto',
  PSICOLOGO: 'Psicólogo',
  COORDINADOR: 'Coordinador',
  ACADEMICO: 'Académico',
  DECANO: 'Decano',
};

export interface RegistroRequest {
  correo: string;
  contrasena: string;
  nombres: string;
  apellidos: string;
  rol: Rol;
}

export interface CuentaResponse {
  id: number;
  correo: string;
  rol: Rol;
  estado: string;
}

export interface InicioSesionRequest {
  correo: string;
  contrasena: string;
}

export interface SesionResponse {
  token: string;
  expiraEn: string;
  rolActivo: Rol;
  permisos: string[];
}

export interface RecuperacionRequest {
  correo: string;
}

export interface ConfirmarRecuperacionRequest {
  token: string;
  nuevaContrasena: string;
}

/** Sesión en el cliente: lo que devuelve E14 más el correo con que se abrió. */
export interface Sesion extends SesionResponse {
  correo: string;
}

export interface ClienteAcceso {
  /** E13 */
  registrar(solicitud: RegistroRequest): Promise<CuentaResponse>;
  /** E14 */
  iniciarSesion(solicitud: InicioSesionRequest): Promise<SesionResponse>;
  /** E15 */
  cerrarSesion(): Promise<void>;
  /** E16 */
  pedirRecuperacion(solicitud: RecuperacionRequest): Promise<void>;
  /** E17 */
  confirmarRecuperacion(solicitud: ConfirmarRecuperacionRequest): Promise<void>;
}
