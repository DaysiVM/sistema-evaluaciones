import { describe, expect, it } from 'vitest';
import { enlacesPorRol } from './navegacion';
import { ROLES } from './tipos';

const textos = (rol: Parameters<typeof enlacesPorRol>[0]) => enlacesPorRol(rol).map((e) => e.texto);

describe('navegación por rol', () => {
  it('sin sesión y el Sujeto solo ven el catálogo', () => {
    expect(textos(null)).toEqual(['Catálogo']);
    expect(textos('SUJETO')).toEqual(['Catálogo']);
  });

  it('los profesionales ven panel, gestión y resultados', () => {
    for (const rol of ['PSICOLOGO', 'ACADEMICO', 'DECANO'] as const)
      expect(textos(rol)).toEqual(['Catálogo', 'Panel', 'Sujetos y periodos', 'Resultados']);
  });

  it('solo el Coordinador ve Administración', () => {
    expect(textos('COORDINADOR')).toContain('Administración');
    expect(ROLES.filter((r) => textos(r).includes('Administración'))).toEqual(['COORDINADOR']);
  });
});
