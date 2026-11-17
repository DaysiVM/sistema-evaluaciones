import { describe, expect, it } from 'vitest';
import type { ResultadoFactor } from '../../resultados/tipos';
import { CODIGOS_PERFIL } from './catalogo';
import {
  percentilVisible,
  resumirPerfil,
  textoPercentil,
  textoPuntuacionDirecta,
} from './presentacionPerfil';

const factor = (cambios: Partial<ResultadoFactor> = {}): ResultadoFactor => ({
  codigo: 'VOC1',
  puntuacionDirecta: 20,
  estadoCalculo: 'CALCULADO',
  percentil: 60,
  idRango: 1,
  estadoNormativo: 'CONVERTIDO',
  motivo: null,
  ...cambios,
});

describe('presentación del perfil BFA', () => {
  it('el catálogo tiene los 18 códigos de ENT-BFA-05, sin repetir', () => {
    expect(CODIGOS_PERFIL).toHaveLength(18);
    expect(new Set(CODIGOS_PERFIL).size).toBe(18);
  });

  it('muestra un 0 recibido como 0 y un nulo como «—»', () => {
    expect(textoPuntuacionDirecta(factor({ puntuacionDirecta: 0 }))).toBe('0');
    expect(textoPuntuacionDirecta(factor({ puntuacionDirecta: null }))).toBe('—');
  });

  it('no presenta como definitiva la PD de un factor pendiente o no calculable', () => {
    const pendiente = factor({ estadoCalculo: 'PENDIENTE_VALIDACION', puntuacionDirecta: 28 });
    expect(textoPuntuacionDirecta(pendiente)).toBe('Pendiente');
    expect(textoPuntuacionDirecta(factor({ estadoCalculo: 'NO_CALCULABLE' }))).toBe(
      'No calculable',
    );
  });

  it('el percentil se marca con «Pc» y nunca con «%»', () => {
    expect(textoPercentil(factor())).toBe('Pc 60');
    expect(textoPercentil(factor())).not.toContain('%');
  });

  it.each(['SIN_COINCIDENCIA', 'SIN_BAREMO_VALIDADO', 'NO_APLICA'] as const)(
    'sin CONVERTIDO (%s) no hay percentil aunque llegue un número',
    (estadoNormativo) => {
      const f = factor({ estadoNormativo, percentil: 40 });
      expect(percentilVisible(f)).toBeNull();
      expect(textoPercentil(f)).toBe('—');
    },
  );

  it('cuenta los estados recibidos y marca el perfil parcial', () => {
    const completos = CODIGOS_PERFIL.map((codigo) => factor({ codigo }));
    expect(resumirPerfil(completos)).toEqual({
      total: 18,
      calculados: 18,
      conPercentil: 18,
      parcial: false,
    });
    const parcial = completos.map((f) =>
      f.codigo === 'S1' ? factor({ codigo: 'S1', estadoCalculo: 'NO_CALCULABLE' }) : f,
    );
    expect(resumirPerfil(parcial)).toMatchObject({ calculados: 17, parcial: true });
  });
});
