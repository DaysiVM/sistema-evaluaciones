/**
 * Catálogo de presentación del perfil BFA (UI-05).
 * Fuente: Documento BFA §2.3 (mapa de factores y resultados) y ENT-BFA-05 (18 códigos).
 *
 * Solo contiene nombres, áreas y el orden de lectura. No calcula nada: las puntuaciones,
 * los compuestos y los percentiles llegan ya resueltos desde E9. La fórmula de los
 * compuestos es un texto informativo (§2.5); el frontend nunca la evalúa.
 * Los nombres quedan sujetos a la validación de DOC-02 (duda D06).
 */

export type AreaBfa = 'verbal' | 'espacial' | 'razonamiento' | 'numerico';

export interface FactorPerfil {
  codigo: string;
  nombre: string;
  /** Los totales (VOCT, ST, RT, NT) se destacan dentro de su área. */
  esTotal?: boolean;
}

export interface CompuestoPerfil extends FactorPerfil {
  /** Fórmula documentada, solo para lectura. */
  formula: string;
}

export const AREAS: { area: AreaBfa; titulo: string; factores: FactorPerfil[] }[] = [
  {
    area: 'verbal',
    titulo: 'Verbal',
    factores: [
      { codigo: 'VOC1', nombre: 'Vocabulario 1' },
      { codigo: 'VOC2', nombre: 'Vocabulario 2' },
      { codigo: 'VOCT', nombre: 'Vocabulario total', esTotal: true },
      { codigo: 'CV', nombre: 'Comprensión Verbal' },
      { codigo: 'FV', nombre: 'Fluidez Verbal' },
    ],
  },
  {
    area: 'espacial',
    titulo: 'Espacial',
    factores: [
      { codigo: 'S1', nombre: 'Espacial perceptivo' },
      { codigo: 'S2', nombre: 'Visualización espacial' },
      { codigo: 'ST', nombre: 'Total espacial', esTotal: true },
    ],
  },
  {
    area: 'razonamiento',
    titulo: 'Razonamiento',
    factores: [
      { codigo: 'R1', nombre: 'Razonamiento 1' },
      { codigo: 'R2', nombre: 'Razonamiento 2' },
      { codigo: 'RT', nombre: 'Razonamiento total', esTotal: true },
    ],
  },
  {
    area: 'numerico',
    titulo: 'Numérico',
    factores: [
      { codigo: 'N1', nombre: 'Numérico 1' },
      { codigo: 'N2', nombre: 'Numérico 2' },
      { codigo: 'NT', nombre: 'Numérico total', esTotal: true },
    ],
  },
];

export const COMPUESTOS: CompuestoPerfil[] = [
  { codigo: 'EIV', nombre: 'Eficiencia Intelectual Verbal', formula: '2·CV + VOCT + FV' },
  { codigo: 'EIC', nombre: 'Eficiencia Intelectual Científica', formula: '2·RT + ST + NT' },
  { codigo: 'EIG', nombre: 'Eficiencia Intelectual Global', formula: 'EIV + EIC' },
  { codigo: 'AE', nombre: 'Aptitud Escolar', formula: '2·RT + CV + VOCT' },
];

/** Los 18 códigos del catálogo inicial (ENT-BFA-05), en orden de lectura. */
export const CODIGOS_PERFIL: string[] = [
  ...AREAS.flatMap((a) => a.factores.map((f) => f.codigo)),
  ...COMPUESTOS.map((c) => c.codigo),
];

const NOMBRES = new Map<string, string>(
  [...AREAS.flatMap((a) => a.factores), ...COMPUESTOS].map((f) => [f.codigo, f.nombre]),
);

/** Nombre visible de un código; si no está en el catálogo se muestra el código recibido. */
export const nombreFactor = (codigo: string): string => NOMBRES.get(codigo) ?? codigo;
