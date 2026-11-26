import type { Rol } from './tipos';

export interface EnlaceNav {
  a: string;
  texto: string;
  /** NavLink sin coincidencia parcial (para «/»). */
  exacto?: boolean;
}

const CATALOGO: EnlaceNav = { a: '/', texto: 'Catálogo', exacto: true };

const PROFESIONAL: EnlaceNav[] = [
  CATALOGO,
  { a: '/panel', texto: 'Panel' },
  { a: '/gestion', texto: 'Sujetos y periodos' },
  { a: '/resultados', texto: 'Resultados' },
];

/** Enlaces de la navegación principal según el rol activo (sin sesión: solo el catálogo). */
export function enlacesPorRol(rol: Rol | null): EnlaceNav[] {
  switch (rol) {
    case null:
    case 'SUJETO':
      return [CATALOGO];
    case 'COORDINADOR':
      return [...PROFESIONAL, { a: '/administracion', texto: 'Administración' }];
    default:
      return PROFESIONAL;
  }
}
