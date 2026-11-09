import type { ReactNode } from 'react';
import type { TestOption } from '../components/AnswerOption';
import type { OptionsLayout, PromptVariant } from '../components/TestScreen';
import {
  Casilla,
  Fraccion,
  Operacion,
  OperacionVertical,
} from '../features/bfa/components/Operacion';
import { Figura, type FormaFigura } from '../features/bfa/components/Figura';

// Contenido de EJEMPLO para revisar el diseño. No son reactivos del BFA.
// Tiempos, cantidades y tipos de alternativa salen de docs/ «Documentación BFA» (§2.6 y §5).

export type FamiliaSubprueba =
  | 'Vocabulario'
  | 'Espacial'
  | 'Razonamiento'
  | 'Comprensión verbal'
  | 'Numérico'
  | 'Fluidez verbal';

export type ItemEjemplo = {
  prompt: ReactNode;
  options: TestOption[];
  /** Estímulo visual que va sobre las alternativas. */
  recurso?: ReactNode;
};

export type SubpruebaEjemplo = {
  codigo: string;
  nombre: string;
  familia: FamiliaSubprueba;
  tiempoSegundos: number;
  contenido: string;
  respuesta: string;
  requisito: string;
  /** `null` cuando el documento no lo define. */
  volverAtras: boolean | null;
  pendiente?: string;
  /** Texto del contador según el ítem de ejemplo (1, 2, 3...). */
  etiqueta: (n: number) => string;
  promptVariant: PromptVariant;
  optionsLayout: OptionsLayout;
  numeric?: boolean;
  items: ItemEjemplo[];
  fluidez?: { letra: string; maxEntradas: number };
};

const LETRAS = ['a', 'b', 'c', 'd', 'e'];

const textos = (etiquetas: ReactNode[]): TestOption[] =>
  etiquetas.map((label, i) => ({ id: LETRAS[i], label }));

const figuras = (formas: FormaFigura[]): TestOption[] =>
  formas.map((forma, i) => ({
    id: LETRAS[i],
    label: `Alternativa ${LETRAS[i].toUpperCase()}, figura de ejemplo`,
    figure: <Figura forma={forma} alt="" />,
  }));

const verdaderoFalso: TestOption[] = [
  { id: 'a', label: 'Verdadero' },
  { id: 'b', label: 'Falso' },
];

const enunciadoOculto = 'Figura de ejemplo y sus alternativas';
const pendienteFiguras =
  'Material gráfico real, número de alternativas y de ítems: el documento no los fija.';

export const subpruebas: SubpruebaEjemplo[] = [
  {
    codigo: 'VOC1',
    nombre: 'Vocabulario · Forma A',
    familia: 'Vocabulario',
    tiempoSegundos: 300,
    contenido: 'Palabra en mayúsculas (sinónimos), 37 ítems.',
    respuesta: 'Una selección entre 5 alternativas, A a E.',
    requisito: 'RF-VOC-A-01',
    volverAtras: true,
    etiqueta: (n) => `Pregunta ${n} de 37`,
    promptVariant: 'display',
    optionsLayout: 'list',
    items: [
      {
        prompt: 'PALABRA',
        options: textos([
          'Alternativa de ejemplo A',
          'Alternativa de ejemplo B',
          'Alternativa de ejemplo C',
          'Alternativa de ejemplo D',
          'Alternativa de ejemplo E',
        ]),
      },
    ],
  },
  {
    codigo: 'VOC2',
    nombre: 'Vocabulario · Forma B',
    familia: 'Vocabulario',
    tiempoSegundos: 360,
    contenido: 'Bloque de 5 palabras: elegir la que no pertenece, 37 ítems.',
    respuesta: 'Una selección entre las 5 palabras del bloque.',
    requisito: 'RF-VOC-B-01 · RNF-VOC-B-01',
    volverAtras: true,
    pendiente: 'La hoja numera de 41 a 77 y no tiene 38-40: se muestra la numeración de la hoja.',
    etiqueta: (n) => `Pregunta ${40 + n}`,
    promptVariant: 'statement',
    optionsLayout: 'compact',
    items: [
      {
        prompt: 'Marca la palabra que no pertenece al grupo.',
        options: textos([
          'Palabra uno',
          'Palabra dos',
          'Palabra tres',
          'Palabra cuatro',
          'Palabra cinco',
        ]),
      },
    ],
  },
  {
    codigo: 'S1',
    nombre: 'Espacial · Figuras Idénticas',
    familia: 'Espacial',
    tiempoSegundos: 180,
    contenido: 'Figura de estímulo y alternativas gráficas.',
    respuesta: 'Una selección entre alternativas gráficas.',
    requisito: 'RF-ESP-01',
    volverAtras: null,
    pendiente: pendienteFiguras,
    etiqueta: (n) => `Pregunta ${n}`,
    promptVariant: 'hidden',
    optionsLayout: 'figures',
    items: [
      {
        prompt: enunciadoOculto,
        recurso: <Figura rol="estimulo" forma="rombo" alt="Figura de estímulo de ejemplo" />,
        options: figuras(['rombo', 'cuadrado', 'triangulo', 'circulo']),
      },
    ],
  },
  {
    codigo: 'S2',
    nombre: 'Espacial · Desplazamiento',
    familia: 'Espacial',
    tiempoSegundos: 300,
    contenido: 'Configuración con desplazamiento y alternativas gráficas.',
    respuesta: 'Una selección entre alternativas gráficas.',
    requisito: 'RF-ESP-02',
    volverAtras: null,
    pendiente: pendienteFiguras,
    etiqueta: (n) => `Pregunta ${n}`,
    promptVariant: 'hidden',
    optionsLayout: 'figures',
    items: [
      {
        prompt: enunciadoOculto,
        recurso: <Figura rol="estimulo" forma="cruz" alt="Configuración de estímulo de ejemplo" />,
        options: figuras(['cruz', 'cuadrado', 'rombo', 'triangulo']),
      },
    ],
  },
  {
    codigo: 'S1b',
    nombre: 'Espacial · Ladrillos-Cubos',
    familia: 'Espacial',
    tiempoSegundos: 210,
    contenido: 'Configuración de ladrillos/cubos con cinco alternativas.',
    respuesta: 'Una selección entre 5 alternativas, A a E.',
    requisito: 'RF-ESP-03',
    volverAtras: null,
    pendiente: 'Material gráfico real y número de ítems: el documento no los fija.',
    etiqueta: (n) => `Pregunta ${n}`,
    promptVariant: 'hidden',
    optionsLayout: 'figures',
    items: [
      {
        prompt: enunciadoOculto,
        recurso: <Figura rol="estimulo" forma="cubos" alt="Configuración de cubos de ejemplo" />,
        options: figuras(['cubos', 'cuadrado', 'rombo', 'cruz', 'triangulo']),
      },
    ],
  },
  {
    codigo: 'R1',
    nombre: 'Razonamiento · Forma A',
    familia: 'Razonamiento',
    tiempoSegundos: 600,
    contenido: 'Serie numérica incompleta, 20 ítems.',
    respuesta: 'Una selección entre 4 alternativas, A a D; se puede revisar.',
    requisito: 'RF-RAZ-A-01',
    volverAtras: true,
    etiqueta: (n) => `Pregunta ${n} de 20`,
    promptVariant: 'display',
    optionsLayout: 'compact',
    numeric: true,
    items: [
      {
        prompt: (
          <Operacion
            partes={['2,', '4,', '8,', '16,', <Casilla key="c" />]}
            etiqueta="Serie: 2, 4, 8, 16 y un término faltante"
          />
        ),
        options: textos(['20', '24', '32', '64']),
      },
    ],
  },
  {
    codigo: 'R2',
    nombre: 'Razonamiento · Forma B',
    familia: 'Razonamiento',
    tiempoSegundos: 720,
    contenido: 'Series de letras, analogías y relaciones conceptuales.',
    respuesta: 'Una selección entre 4 alternativas, A a D.',
    requisito: 'RF-RAZ-B-01 · RNF-RAZ-B-01',
    volverAtras: null,
    pendiente: 'Sin series de figuras. Letras, palabras y símbolos deben verse sin distorsión.',
    etiqueta: (n) => `Pregunta ${n}`,
    promptVariant: 'display',
    optionsLayout: 'compact',
    items: [
      {
        prompt: (
          <Operacion
            partes={['A,', 'C,', 'E,', 'G,', <Casilla key="c" />]}
            etiqueta="Serie de letras: A, C, E, G y un término faltante"
          />
        ),
        options: textos(['H', 'I', 'J', 'K']),
      },
      {
        prompt: 'Analogía de ejemplo: A es a B como C es a …',
        options: textos(['Opción uno', 'Opción dos', 'Opción tres', 'Opción cuatro']),
      },
    ],
  },
  {
    codigo: 'CV',
    nombre: 'Comprensión Verbal',
    familia: 'Comprensión verbal',
    tiempoSegundos: 900,
    contenido: 'Proverbio o sentencia, 31 ítems.',
    respuesta: 'Una selección entre 3 alternativas, A a C; todo visible sin desplazarse.',
    requisito: 'RF-CV-01 · RNF-CV-01',
    volverAtras: null,
    etiqueta: (n) => `Pregunta ${n} de 31`,
    promptVariant: 'statement',
    optionsLayout: 'list',
    items: [
      {
        prompt:
          'Sentencia de ejemplo con la extensión de un proverbio, que el evaluado debe interpretar para reconocer su significado.',
        options: textos([
          'Alternativa de ejemplo A: un significado expresado con otras palabras, de igual extensión que las demás.',
          'Alternativa de ejemplo B: otro significado posible, redactado en una o dos líneas de texto.',
          'Alternativa de ejemplo C: un tercer significado, que también debe leerse completo.',
        ]),
      },
    ],
  },
  {
    codigo: 'N1-A',
    nombre: 'Numérico · Sumas',
    familia: 'Numérico',
    tiempoSegundos: 240,
    contenido: 'Suma con resultado propuesto.',
    respuesta:
      'Binaria: A = Verdadero, B = Falso. El cambio entre ítems debe tardar menos de 100 ms.',
    requisito: 'RF-NUM-SUM-01 · RNF-NUM-SUM-01',
    volverAtras: false,
    etiqueta: (n) => `Operación ${n}`,
    promptVariant: 'display',
    optionsLayout: 'binary',
    numeric: true,
    items: [
      {
        prompt: (
          <Operacion partes={['128', '+', '57', '=', '185']} etiqueta="128 más 57 igual a 185" />
        ),
        options: verdaderoFalso,
      },
      {
        prompt: (
          <OperacionVertical
            arriba="2 408"
            abajo="395"
            operador="+"
            resultado="2 813"
            etiqueta="2408 más 395 igual a 2813"
          />
        ),
        options: verdaderoFalso,
      },
    ],
  },
  {
    codigo: 'N1-B',
    nombre: 'Numérico · Multiplicaciones',
    familia: 'Numérico',
    tiempoSegundos: 240,
    contenido: 'Multiplicación con resultado propuesto, vertical u horizontal.',
    respuesta: 'Binaria: A = Verdadero, B = Falso.',
    requisito: 'RF-NUM-MUL-01 · RNF-NUM-MUL-01',
    volverAtras: false,
    pendiente: 'Vertical u horizontal según el material adoptado.',
    etiqueta: (n) => `Operación ${n}`,
    promptVariant: 'display',
    optionsLayout: 'binary',
    numeric: true,
    items: [
      {
        prompt: (
          <OperacionVertical
            arriba="47"
            abajo="6"
            operador="×"
            resultado="282"
            etiqueta="47 por 6 igual a 282"
          />
        ),
        options: verdaderoFalso,
      },
      {
        prompt: (
          <Operacion partes={['23', '×', '14', '=', '312']} etiqueta="23 por 14 igual a 312" />
        ),
        options: verdaderoFalso,
      },
    ],
  },
  {
    codigo: 'N2-A',
    nombre: 'Numérico · Operaciones',
    familia: 'Numérico',
    tiempoSegundos: 360,
    contenido: 'Ecuación incompleta con enteros, decimales y fracciones.',
    respuesta: 'Una selección entre 4 alternativas, A a D, que completan la expresión.',
    requisito: 'RF-NUM-OPE-01 · RNF-NUM-OPE-01',
    volverAtras: null,
    pendiente: 'Separador decimal (punto o coma) según el material adoptado.',
    etiqueta: (n) => `Pregunta ${n}`,
    promptVariant: 'display',
    optionsLayout: 'compact',
    numeric: true,
    items: [
      {
        prompt: (
          <Operacion
            partes={['45', '−', <Casilla key="c" />, '=', '18']}
            etiqueta="45 menos un número faltante igual a 18"
          />
        ),
        options: textos(['17', '27', '37', '63']),
      },
      {
        prompt: (
          <Operacion
            partes={['2.5', '+', <Casilla key="c" />, '=', '4.75']}
            etiqueta="2,5 más un número faltante igual a 4,75"
          />
        ),
        options: textos(['1.25', '2.25', '2.75', '7.25']),
      },
      {
        prompt: (
          <Operacion
            partes={[
              <Fraccion key="a" numerador={1} denominador={2} />,
              '+',
              <Casilla key="c" />,
              '=',
              <Fraccion key="b" numerador={3} denominador={4} />,
            ]}
            etiqueta="un medio más un número faltante igual a tres cuartos"
          />
        ),
        options: textos([
          <Fraccion key="1" numerador={1} denominador={4} />,
          <Fraccion key="2" numerador={1} denominador={2} />,
          <Fraccion key="3" numerador={2} denominador={3} />,
          <Fraccion key="4" numerador={3} denominador={8} />,
        ]),
      },
    ],
  },
  {
    codigo: 'N2-B',
    nombre: 'Numérico · Problemas',
    familia: 'Numérico',
    tiempoSegundos: 360,
    contenido: 'Problema aritmético redactado.',
    respuesta: 'Una selección entre 4 alternativas, A a D, siempre visibles.',
    requisito: 'RF-NUM-PRO-01 · RNF-NUM-PRO-01',
    volverAtras: null,
    etiqueta: (n) => `Pregunta ${n}`,
    promptVariant: 'statement',
    optionsLayout: 'list',
    numeric: true,
    items: [
      {
        prompt:
          'Una tienda recibe 48 cajas con 12 lápices cada una. Si durante la semana vende 150 lápices, ¿cuántos lápices le quedan?',
        options: textos(['416 lápices', '426 lápices', '436 lápices', '576 lápices']),
      },
    ],
  },
  {
    codigo: 'FV',
    nombre: 'Fluidez Verbal',
    familia: 'Fluidez verbal',
    tiempoSegundos: 300,
    contenido: 'Producción de palabras con una letra inicial asignada.',
    respuesta: 'Texto libre en grilla de hasta 75 entradas; Enter pasa a la siguiente.',
    requisito: 'RF-FV-01 · RF-FV-03 · RF-FV-06 · RNF-FV-01',
    volverAtras: null,
    pendiente: 'La letra la define la aplicación; en la vista previa es solo un ejemplo.',
    etiqueta: () => 'Hasta 75 palabras',
    promptVariant: 'title',
    optionsLayout: 'list',
    items: [],
    fluidez: { letra: 'M', maxEntradas: 75 },
  },
];
