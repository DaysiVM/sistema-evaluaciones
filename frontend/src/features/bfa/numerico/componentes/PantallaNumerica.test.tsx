import { renderToStaticMarkup } from 'react-dom/server';
import { describe, expect, it } from 'vitest';
import type { PreguntaSubprueba } from '../../../../services/subpruebas';
import { crearClienteSubpruebasMock } from '../../../../services/subpruebas.mock';
import type { CodigoNumerico } from '../subpruebas';
import { PantallaNumerica, type PantallaNumericaProps } from './PantallaNumerica';

const nada = () => undefined;

async function preguntasDe(codigo: CodigoNumerico) {
  const cliente = crearClienteSubpruebasMock({ retardoMs: 0, validarOrden: false });
  await cliente.iniciar(1, codigo);
  return (await cliente.obtenerPreguntas(1, codigo)).preguntas;
}

function html(
  codigo: CodigoNumerico,
  pregunta: PreguntaSubprueba | undefined,
  extra: Partial<PantallaNumericaProps> = {},
) {
  return renderToStaticMarkup(
    <PantallaNumerica
      codigo={codigo}
      fase="activa"
      pregunta={pregunta}
      indice={0}
      total={10}
      seleccion={null}
      estadoGuardado="saved"
      segundosRestantes={240}
      onIniciar={nada}
      onSeleccionar={nada}
      {...extra}
    />,
  );
}

const letras = (marcado: string) =>
  [...marcado.matchAll(/type="radio"[^>]*value="([A-Z])"/g)].map((m) => m[1]);

describe('PantallaNumerica', () => {
  it.each(['N1-A', 'N1-B'] as const)(
    '%s presenta la operación con A = Verdadero y B = Falso',
    async (codigo) => {
      const [p] = await preguntasDe(codigo);
      const marcado = html(codigo, p);
      expect(letras(marcado)).toEqual(['A', 'B']);
      expect(marcado).toContain('Verdadero');
      expect(marcado).toContain('Falso');
      expect(marcado).toContain('options--binary');
      p.enunciado.split(' ').forEach((parte) => expect(marcado).toContain(`>${parte}<`));
    },
  );

  it('dibuja Sumas en vertical cuando el material lo pide', async () => {
    const [p] = await preguntasDe('N1-A');
    expect(html('N1-A', p, { formato: 'vertical' })).toContain('operacion-vertical');
    expect(html('N1-A', p)).not.toContain('operacion-vertical');
  });

  it('Operaciones muestra la expresión incompleta con cuatro alternativas A-D', async () => {
    const preguntas = await preguntasDe('N2-A');
    const conFraccion = preguntas.find((p) => p.enunciado.includes('/'))!;
    const marcado = html('N2-A', conFraccion);
    expect(letras(marcado)).toEqual(['A', 'B', 'C', 'D']);
    expect(marcado).toContain('class="casilla"');
    expect(marcado).toContain('class="fraccion"');
    expect(marcado).toContain('1 sobre 2 más término faltante igual a 3 sobre 4');
  });

  it('Operaciones conserva decimales y negativos tal como llegan', async () => {
    const preguntas = await preguntasDe('N2-A');
    const decimal = preguntas.find((p) => p.enunciado.includes('2.5'))!;
    const negativo = preguntas.find((p) => p.enunciado.startsWith('−'))!;
    expect(html('N2-A', decimal)).toContain('>4.75<');
    expect(html('N2-A', decimal)).toContain('1.25');
    expect(html('N2-A', negativo)).toContain('>−3<');
  });

  it('Problemas muestra el enunciado completo y las cuatro alternativas', async () => {
    const [p] = await preguntasDe('N2-B');
    const marcado = html('N2-B', p);
    expect(marcado).toContain(p.enunciado);
    expect(marcado).toContain('question-prompt--statement');
    expect(letras(marcado)).toEqual(['A', 'B', 'C', 'D']);
    p.opciones.forEach((o) => expect(marcado).toContain(o.texto));
  });

  it('no muestra respuestas correctas ni puntuaciones', async () => {
    for (const codigo of ['N1-A', 'N1-B', 'N2-A', 'N2-B'] as const) {
      const [p] = await preguntasDe(codigo);
      expect(html(codigo, p, { seleccion: 'A' })).not.toMatch(
        /correct|incorrect|acierto|puntuaci/i,
      );
    }
  });

  it('marca la selección actual y muestra el estado de guardado', async () => {
    const [p] = await preguntasDe('N2-A');
    const marcado = html('N2-A', p, { seleccion: 'C', estadoGuardado: 'saving' });
    expect(marcado).toMatch(/checked="" value="C"/);
    expect(marcado).toContain('Guardando');
    expect(html('N2-A', p, { estadoGuardado: 'error' })).toContain('No se pudo guardar');
  });

  it('solo N2 ofrece volver a la pregunta anterior', async () => {
    const [s] = await preguntasDe('N1-A');
    const [o] = await preguntasDe('N2-A');
    expect(html('N1-A', s, { indice: 3, onAnterior: nada })).not.toContain('Anterior');
    expect(html('N2-A', o, { indice: 3, onAnterior: nada })).toContain('Anterior');
    expect(html('N2-A', o, { indice: 0, onAnterior: nada })).not.toContain('Anterior');
  });

  it('cerrada: sin alternativas, sin volver y sin modificaciones', async () => {
    const [p] = await preguntasDe('N2-B');
    const marcado = html('N2-B', p, { fase: 'cerrada', cerradaPorTiempo: true, onAnterior: nada });
    expect(marcado).not.toContain('radiogroup');
    expect(marcado).not.toContain('Anterior');
    expect(marcado).toContain('El tiempo oficial terminó');
    expect(marcado).toContain('ya no admite cambios');
    expect(marcado).toContain('Continuar');
  });

  it('instrucciones: sin preguntas ni reloj oficial hasta E5', () => {
    const marcado = html('N1-A', undefined, { fase: 'instrucciones', segundosRestantes: null });
    expect(marcado).not.toContain('radiogroup');
    expect(marcado).toContain('>—<');
    expect(marcado).toContain('4 minutos');
    expect(marcado).toContain('Comenzar');
  });

  it('el reloj muestra el tiempo informado por el backend', async () => {
    const [p] = await preguntasDe('N2-A');
    expect(html('N2-A', p, { segundosRestantes: 359 })).toContain('>05:59<');
  });
});

describe('fin de la subprueba y espera', () => {
  it('en la última pregunta el botón es «Terminé»', async () => {
    const [p] = await preguntasDe('N2-A');
    expect(html('N2-A', p, { indice: 5, total: 6 })).toContain('>Terminé<');
    expect(html('N2-A', p, { indice: 2, total: 6 })).toContain('>Siguiente<');
  });

  it('la espera muestra el reloj y lo respondido, sin alternativas', () => {
    const marcado = html('N1-A', undefined, {
      fase: 'espera',
      segundosRestantes: 95,
      respondidas: 38,
      total: 40,
    });
    expect(marcado).toContain('En espera');
    expect(marcado).toContain('>01:35<');
    expect(marcado).toContain('Respondiste 38 de 40 operaciones');
    expect(marcado).toContain('No se puede pasar a la siguiente antes del tiempo');
    expect(marcado).not.toContain('radiogroup');
  });

  it('solo N2 permite volver a revisar desde la espera', () => {
    const conRevision = { fase: 'espera' as const, onRevisar: nada };
    expect(html('N1-B', undefined, conRevision)).not.toContain('Volver a revisar');
    expect(html('N2-B', undefined, conRevision)).toContain('Volver a revisar');
  });
});

describe('rendimiento del cambio entre operaciones (RNF-NUM-SUM-01)', () => {
  it('dibujar la operación siguiente tarda menos de 100 ms', async () => {
    const preguntas = await preguntasDe('N1-A');
    html('N1-A', preguntas[0]); // primera carga fuera de la medición
    const tiempos = preguntas.map((p, i) => {
      const inicio = performance.now();
      html('N1-A', p, { indice: i, total: preguntas.length, seleccion: null });
      return performance.now() - inicio;
    });
    expect(Math.max(...tiempos)).toBeLessThan(100);
  });
});
