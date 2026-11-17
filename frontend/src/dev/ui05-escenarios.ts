import type { ResultadoFactor, ResultadoResponse } from '../features/resultados/tipos';

/**
 * Escenarios de la vista previa de UI-05 (perfil de resultados BFA).
 * SOLO PARA REVISAR EL DISEÑO: las puntuaciones y percentiles son de ejemplo y no son
 * conversiones verificadas contra el baremo. Siguen el contrato de E9 (Documento BFA §5.3).
 */

const convertido = (
  codigo: string,
  pd: number,
  percentil: number,
  idRango: number,
): ResultadoFactor => ({
  codigo,
  puntuacionDirecta: pd,
  estadoCalculo: 'CALCULADO',
  percentil,
  idRango,
  estadoNormativo: 'CONVERTIDO',
  motivo: null,
});

const sinCoincidencia = (codigo: string, pd: number): ResultadoFactor => ({
  codigo,
  puntuacionDirecta: pd,
  estadoCalculo: 'CALCULADO',
  percentil: null,
  idRango: null,
  estadoNormativo: 'SIN_COINCIDENCIA',
  motivo: 'La puntuación no coincide con ningún rango del baremo; no se interpola.',
});

const sinBaremo = (codigo: string, pd: number): ResultadoFactor => ({
  codigo,
  puntuacionDirecta: pd,
  estadoCalculo: 'CALCULADO',
  percentil: null,
  idRango: null,
  estadoNormativo: 'SIN_BAREMO_VALIDADO',
  motivo: 'No hay un baremo validado para la edición aplicada.',
});

const noCalculable = (codigo: string, motivo: string): ResultadoFactor => ({
  codigo,
  puntuacionDirecta: null,
  estadoCalculo: 'NO_CALCULABLE',
  percentil: null,
  idRango: null,
  estadoNormativo: 'NO_APLICA',
  motivo,
});

const base = (evaluacionId: number): Omit<ResultadoResponse, 'factores'> => ({
  evaluacionId,
  resultadoId: 500 + evaluacionId,
  estadoResultado: 'DISPONIBLE',
  versionFormularioId: 1,
  calificacionId: 900 + evaluacionId,
  revision: 1,
  versionReglas: 'BFA-R1',
  idClave: 3,
  idBaremo: 1,
});

const verbal = [
  convertido('VOC1', 20, 60, 101),
  convertido('VOC2', 18, 55, 102),
  convertido('VOCT', 38, 60, 103),
  convertido('CV', 15, 50, 104),
  convertido('FV', 30, 45, 105),
];
const espacial = [
  convertido('S1', 40, 70, 106),
  convertido('S2', 25, 65, 107),
  convertido('ST', 65, 70, 108),
];
const razonamiento = [
  convertido('R1', 12, 50, 109),
  convertido('R2', 14, 55, 110),
  convertido('RT', 26, 50, 111),
];
const numerico = [
  convertido('N1', 18, 50, 112),
  convertido('N2', 4, 45, 113),
  convertido('NT', 22, 50, 114),
];

export interface EscenarioUi05 {
  id: string;
  titulo: string;
  descripcion: string;
  resultado: ResultadoResponse;
}

const motivoNoAplicada = 'Subprueba no aplicada (aplicación parcial).';

export const ESCENARIOS_UI05: EscenarioUi05[] = [
  {
    id: 'completo',
    titulo: 'Completo',
    descripcion:
      'Los 18 factores calculados. EIG muestra «Sin coincidencia en el baremo»: tiene PD, pero no percentil.',
    resultado: {
      ...base(16),
      factores: [
        ...verbal,
        ...espacial,
        ...razonamiento,
        ...numerico,
        convertido('EIV', 98, 80, 115),
        convertido('EIC', 139, 97, 116),
        sinCoincidencia('EIG', 237),
        convertido('AE', 105, 99, 117),
      ],
    },
  },
  {
    id: 'parcial',
    titulo: 'Parcial',
    descripcion:
      'Aplicación parcial sin las subpruebas espaciales ni numéricas: esos factores y EIC/EIG quedan «No calculable».',
    resultado: {
      ...base(19),
      factores: [
        ...verbal,
        noCalculable('S1', motivoNoAplicada),
        noCalculable('S2', motivoNoAplicada),
        noCalculable('ST', 'Faltan componentes: S1 y S2.'),
        ...razonamiento,
        noCalculable('N1', motivoNoAplicada),
        noCalculable('N2', motivoNoAplicada),
        noCalculable('NT', 'Faltan componentes: N1 y N2.'),
        convertido('EIV', 98, 80, 115),
        noCalculable('EIC', 'Faltan componentes: ST y NT.'),
        noCalculable('EIG', 'Falta un componente: EIC.'),
        convertido('AE', 105, 99, 117),
      ],
    },
  },
  {
    id: 'fv-pendiente',
    titulo: 'FV pendiente',
    descripcion:
      'FV con palabras por revisar: no se muestra su PD como definitiva y EIV/EIG quedan sin calcular.',
    resultado: {
      ...base(20),
      factores: [
        ...verbal.filter((f) => f.codigo !== 'FV'),
        {
          codigo: 'FV',
          // El backend podría enviar un valor provisional; la vista no lo presenta como definitivo.
          puntuacionDirecta: 28,
          estadoCalculo: 'PENDIENTE_VALIDACION',
          percentil: null,
          idRango: null,
          estadoNormativo: 'NO_APLICA',
          motivo: 'Hay palabras pendientes de revisión.',
        },
        ...espacial,
        ...razonamiento,
        ...numerico,
        noCalculable('EIV', 'Depende de FV, pendiente de validación.'),
        convertido('EIC', 139, 97, 116),
        noCalculable('EIG', 'Falta un componente: EIV.'),
        convertido('AE', 105, 99, 117),
      ],
    },
  },
  {
    id: 'sin-baremo',
    titulo: 'Sin baremo validado',
    descripcion:
      'Puntuaciones calculadas sin baremo aprobado: se ven las PD y ningún percentil. S2 = 0 es un cero real.',
    resultado: {
      ...base(21),
      idBaremo: null,
      factores: [
        sinBaremo('VOC1', 20),
        sinBaremo('VOC2', 18),
        sinBaremo('VOCT', 38),
        sinBaremo('CV', 15),
        sinBaremo('FV', 30),
        sinBaremo('S1', 40),
        sinBaremo('S2', 0),
        sinBaremo('ST', 40),
        sinBaremo('R1', 12),
        sinBaremo('R2', 14),
        sinBaremo('RT', 26),
        sinBaremo('N1', 18),
        sinBaremo('N2', 4),
        sinBaremo('NT', 22),
        sinBaremo('EIV', 98),
        sinBaremo('EIC', 114),
        sinBaremo('EIG', 212),
        sinBaremo('AE', 105),
      ],
    },
  },
  {
    id: 'pendiente-validacion',
    titulo: 'Pendiente de validación',
    descripcion: 'Resultado sin revisión publicada: no se muestran puntuaciones ni percentiles.',
    resultado: {
      ...base(17),
      estadoResultado: 'PENDIENTE_VALIDACION',
      calificacionId: null,
      revision: null,
      factores: [],
    },
  },
  {
    id: 'calculando',
    titulo: 'En cálculo',
    descripcion: 'El servidor aún calcula: no hay revisión ni valores.',
    resultado: {
      ...base(18),
      estadoResultado: 'CALCULANDO',
      calificacionId: null,
      revision: null,
      factores: [],
    },
  },
];
