// Recorrido de interfaz de GEN-05-TEST (TC-GEN-05-001 a 018): pantalla de resultados (E9).
// Interfaz de main compilada con los mocks por defecto (VITE_USAR_MOCKS sin definir).
// Datos ficticios de services/resultados.mock.ts. No hay datos personales reales.
// Uso: node GEN-05-recorrido-interfaz.js <url-base> <carpeta-capturas>
const { chromium } = require('playwright');
const fs = require('fs');

const BASE = process.argv[2] || 'http://127.0.0.1:5173';
const CAPS = process.argv[3] || 'capturas';
fs.mkdirSync(CAPS, { recursive: true });
const resultados = [];

async function caso(id, titulo, fn) {
  const navegador = await chromium.launch();
  const p = await navegador.newPage({ viewport: { width: 1280, height: 900 } });
  let estado = 'PASÓ';
  let obtenido = '';
  try {
    obtenido = await fn(p);
  } catch (e) {
    estado = 'FALLÓ';
    obtenido = String(e.message || e).split('\n')[0];
  }
  await p.screenshot({ path: `${CAPS}/${id}.jpg`, type: 'jpeg', quality: 70, fullPage: true });
  await navegador.close();
  resultados.push({ id, titulo, estado, obtenido });
  console.log(`${id} | ${estado} | ${titulo} | ${obtenido}`);
}

// El mock tarda ~700 ms en responder.
const esperar = (p, ms = 1200) => p.waitForTimeout(ms);
function exigir(condicion, mensaje) {
  if (!condicion) throw new Error(mensaje);
}
const texto = async (p, sel = '.ev-panel') =>
  (await p.locator(sel).first().innerText()).replace(/\s+/g, ' ');
const campo = (p) => p.getByLabel('ID de la evaluación', { exact: true });
const botonConsultar = (p) => p.getByRole('button', { name: 'Consultar' });
const botonReintentar = (p) => p.getByRole('button', { name: 'Reintentar' });
const vista = (p) => p.locator('.res-vista');
const filas = (p) => p.locator('.res-tabla tbody tr');

async function abrir(p) {
  await p.goto(`${BASE}/resultados`);
  await esperar(p, 800);
}
async function consultar(p, id, ms = 1200) {
  await campo(p).fill(id);
  await botonConsultar(p).click();
  await esperar(p, ms);
}
/** Celdas de la fila de un factor: [factor, puntuación, cálculo, percentil, conversión, motivo]. */
async function fila(p, codigo) {
  const f = p.locator('.res-tabla tbody tr', { has: p.locator('th', { hasText: new RegExp(`^${codigo}$`) }) });
  exigir((await f.count()) === 1, `no hay una fila ${codigo}`);
  const celdas = await f.locator('th, td').allInnerTexts();
  return celdas.map((c) => c.trim());
}
/** Valor de un dato de la revisión (lista dt/dd). */
async function dato(p, nombre) {
  const d = p.locator('.res-datos div', { has: p.locator('dt', { hasText: new RegExp(`^${nombre}$`) }) });
  return (await d.locator('dd').innerText()).trim();
}

(async () => {
  // ---------- A. Búsqueda y validación ----------
  await caso('TC-GEN-05-001', 'Consultar con el ID vacío', async (p) => {
    await abrir(p);
    await botonConsultar(p).click();
    await esperar(p, 400);
    const t = await texto(p);
    exigir(/Ingrese el ID de la evaluación/.test(t), t.slice(0, 200));
    exigir((await vista(p).count()) === 0, 'mostró un resultado');
    exigir((await campo(p).getAttribute('aria-invalid')) === 'true', 'el campo no queda marcado como inválido');
    return '«Ingrese el ID de la evaluación.» bajo el campo (aria-invalid); no consulta';
  });
  await caso('TC-GEN-05-002', 'ID con formato inválido (abc, 0, -5, 1.5)', async (p) => {
    await abrir(p);
    for (const v of ['abc', '0', '-5', '1.5']) {
      await consultar(p, v, 400);
      const t = await texto(p);
      exigir(/debe ser un número entero positivo/.test(t), `${v}: ${t.slice(0, 160)}`);
      exigir((await vista(p).count()) === 0, `${v}: mostró un resultado`);
    }
    return '«El ID de la evaluación debe ser un número entero positivo.» con abc, 0, -5 y 1.5; no consulta';
  });
  await caso('TC-GEN-05-003', 'Ruta directa con ID inválido', async (p) => {
    await p.goto(`${BASE}/evaluaciones/abc/resultados`);
    await esperar(p, 1200);
    const t = await texto(p);
    exigir(/debe ser un número entero positivo/.test(t), t.slice(0, 200));
    exigir((await vista(p).count()) === 0, 'mostró un resultado');
    return '/evaluaciones/abc/resultados: error de formato y ninguna consulta';
  });
  await caso('TC-GEN-05-004', 'Estado de carga mientras responde el servidor', async (p) => {
    await abrir(p);
    await campo(p).fill('16');
    await botonConsultar(p).click();
    await esperar(p, 150);
    const t = await texto(p);
    exigir(/Consultando resultados/.test(t), 'sin indicador de carga');
    exigir(await campo(p).isDisabled(), 'el campo sigue habilitado durante la carga');
    exigir(await botonConsultar(p).isDisabled(), 'el botón sigue habilitado durante la carga');
    exigir((await vista(p).count()) === 0, 'mostró el resultado antes de la respuesta');
    await esperar(p, 1200);
    exigir((await vista(p).count()) === 1, 'no llegó el resultado');
    return '«Consultando resultados…», campo y botón deshabilitados; el resultado aparece solo al responder';
  });
  await caso('TC-GEN-05-005', 'Cambiar el ID limpia el resultado anterior', async (p) => {
    await abrir(p);
    await consultar(p, '16');
    exigir((await vista(p).count()) === 1, 'no mostró el resultado de 16');
    await campo(p).fill('17');
    await esperar(p, 200);
    exigir((await vista(p).count()) === 0, 'sigue mostrando el resultado de 16 con otro ID escrito');
    return 'Al escribir 17 desaparece el resultado de la evaluación 16 hasta volver a consultar';
  });

  // ---------- B. Estados del resultado ----------
  await caso('TC-GEN-05-006', 'Resultado disponible (evaluación 16)', async (p) => {
    await abrir(p);
    await consultar(p, '16');
    const t = await texto(p, '.res-vista');
    exigir(/Resultados de la evaluación 16/.test(t), 'sin título');
    exigir(/Resultado disponible/.test(t), 'sin estado DISPONIBLE');
    exigir(/Datos simulados/.test(t), 'sin aviso de datos simulados');
    exigir((await dato(p, 'Revisión vigente')) === '1', 'revisión distinta de 1');
    exigir((await dato(p, 'Calificación')) === '31', 'calificación distinta de 31');
    exigir((await dato(p, 'Versión de reglas')) === 'BFA-REGLAS-v1', 'versión de reglas distinta');
    const orden = (await filas(p).locator('th').allInnerTexts()).map((s) => s.trim());
    exigir(orden.join(',') === 'VOC1,VOC2,VOCT,R1,CV,FV,EIV', `orden=${orden.join(',')}`);
    return `«Resultado disponible», revisión 1, calificación 31, reglas BFA-REGLAS-v1; 7 factores en el orden recibido (${orden.join(', ')}); aviso de datos simulados`;
  });
  await caso('TC-GEN-05-007', 'Factores convertidos', async (p) => {
    await abrir(p);
    await consultar(p, '16');
    const voc1 = await fila(p, 'VOC1');
    exigir(voc1[1] === '20' && voc1[2] === 'Calculado' && voc1[3] === '50' && voc1[4] === 'Convertido', voc1.join(' | '));
    exigir(voc1[5] === '—', `motivo=${voc1[5]}`);
    const voct = await fila(p, 'VOCT');
    exigir(voct[1] === '38' && voct[3] === '50', voct.join(' | '));
    return `VOC1: ${voc1.join(' | ')}; VOCT: ${voct.slice(1, 5).join(' | ')} (los valores del mock, sin recalcular)`;
  });
  await caso('TC-GEN-05-008', 'Calculado sin conversión y cero recibido', async (p) => {
    await abrir(p);
    await consultar(p, '16');
    const r1 = await fila(p, 'R1');
    exigir(r1[1] === '12' && r1[3] === '—' && /Sin coincidencia/.test(r1[4]) && /rango exacto/.test(r1[5]), r1.join(' | '));
    const cv = await fila(p, 'CV');
    exigir(cv[1] === '0', `CV muestra «${cv[1]}» en vez de 0`);
    exigir(cv[3] === '—' && /Sin baremo validado/.test(cv[4]) && cv[5].length > 1, cv.join(' | '));
    return `R1: ${r1.join(' | ')}; CV: ${cv.join(' | ')}`;
  });
  await caso('TC-GEN-05-009', 'Factores no calculables', async (p) => {
    await abrir(p);
    await consultar(p, '16');
    const fv = await fila(p, 'FV');
    const eiv = await fila(p, 'EIV');
    for (const f of [fv, eiv]) {
      exigir(f[1] === 'No calculable' && f[3] === '—' && f[2] === 'No calculable', f.join(' | '));
      exigir(f[5] !== '—' && f[5].length > 1, `${f[0]} sin motivo`);
      exigir(!/^\d+$/.test(f[1]), `${f[0]} muestra un número`);
    }
    return `FV: ${fv.join(' | ')}; EIV: ${eiv.join(' | ')}`;
  });
  await caso('TC-GEN-05-010', 'Pendiente de validación (evaluación 17)', async (p) => {
    await abrir(p);
    await consultar(p, '17');
    const t = await texto(p, '.res-vista');
    exigir(/Pendiente de validación/.test(t), 'sin estado PENDIENTE_VALIDACION');
    exigir((await filas(p).count()) === 0, 'muestra factores');
    exigir(/No hay una revisión publicada/.test(t), 'sin mensaje de revisión no publicada');
    exigir((await dato(p, 'Revisión vigente')) === '—' && (await dato(p, 'Calificación')) === '—', 'muestra revisión');
    return '«Pendiente de validación», revisión y calificación «—», sin tabla: «No hay una revisión publicada; no se muestran puntuaciones ni percentiles.»';
  });
  await caso('TC-GEN-05-011', 'En cálculo (evaluación 18)', async (p) => {
    await abrir(p);
    await consultar(p, '18');
    const t = await texto(p, '.res-vista');
    exigir(/En cálculo/.test(t), 'sin estado CALCULANDO');
    exigir((await filas(p).count()) === 0, 'muestra factores');
    exigir(/No hay una revisión publicada/.test(t), 'sin mensaje de revisión no publicada');
    return '«En cálculo» («Vuelva a consultar en unos minutos»), sin revisión ni tabla';
  });
  await caso('TC-GEN-05-012', 'Ruta directa con ID válido', async (p) => {
    await p.goto(`${BASE}/evaluaciones/16/resultados`);
    await esperar(p, 1800);
    exigir((await campo(p).inputValue()) === '16', 'el campo no trae el ID de la ruta');
    exigir((await vista(p).count()) === 1, 'no consultó al abrir');
    exigir((await filas(p).count()) === 7, 'no muestra los 7 factores');
    return '/evaluaciones/16/resultados consulta al abrir: campo con 16 y el resultado disponible';
  });

  // ---------- C. Errores ----------
  await caso('TC-GEN-05-013', 'Evaluación no finalizada (409)', async (p) => {
    await abrir(p);
    await consultar(p, '15');
    const t = await texto(p);
    exigir(/aún no ha finalizado/.test(t), t.slice(0, 200));
    exigir((await vista(p).count()) === 0, 'muestra resultados');
    exigir((await botonReintentar(p).count()) === 0, 'ofrece reintentar');
    exigir((await p.locator('.ev-panel .test-notice--warning').count()) === 1, 'no es un aviso (warning)');
    exigir((await p.locator('.ev-panel .test-notice--error').count()) === 0, 'se muestra como error');
    return '«La evaluación aún no ha finalizado. Los resultados se podrán consultar cuando se finalice.» como aviso, sin Reintentar ni datos';
  });
  await caso('TC-GEN-05-014', 'Evaluación inexistente (404)', async (p) => {
    await abrir(p);
    await consultar(p, '99');
    const t = await texto(p);
    exigir(/No encontrado\. No existe una evaluación con ese ID/.test(t), t.slice(0, 200));
    exigir((await vista(p).count()) === 0, 'muestra resultados');
    exigir((await botonReintentar(p).count()) === 0, 'ofrece reintentar');
    return '«No encontrado. No existe una evaluación con ese ID.», sin Reintentar ni datos';
  });
  await caso('TC-GEN-05-015', 'Acceso denegado (403)', async (p) => {
    await abrir(p);
    await consultar(p, '403');
    const t = await texto(p);
    exigir(/Acceso denegado\. No tiene permiso para consultar los resultados de esta evaluación/.test(t), t.slice(0, 200));
    exigir((await vista(p).count()) === 0, 'muestra resultados');
    exigir((await p.locator('.res-datos').count()) === 0, 'muestra datos de la revisión');
    exigir((await botonReintentar(p).count()) === 0, 'ofrece reintentar');
    return '«Acceso denegado. No tiene permiso para consultar los resultados de esta evaluación.»; no muestra ningún dato';
  });
  await caso('TC-GEN-05-016', 'Error del servidor (500) y Reintentar', async (p) => {
    await abrir(p);
    await consultar(p, '500');
    const t = await texto(p);
    exigir(/Error del servidor\. Ocurrió un error en el servidor/.test(t), t.slice(0, 200));
    exigir((await botonReintentar(p).count()) === 1, 'sin Reintentar');
    await botonReintentar(p).click();
    await esperar(p, 150);
    exigir(/Consultando resultados/.test(await texto(p)), 'Reintentar no vuelve a consultar');
    await esperar(p, 1200);
    exigir(/Error del servidor/.test(await texto(p)), 'tras reintentar no vuelve el error');
    return '«Error del servidor. Ocurrió un error en el servidor. Intente de nuevo más tarde.»; Reintentar vuelve a consultar';
  });
  await caso('TC-GEN-05-017', 'Sin conexión (503)', async (p) => {
    await abrir(p);
    await consultar(p, '503');
    const t = await texto(p);
    exigir(/Sin conexión\. No se pudo conectar con el servidor/.test(t), t.slice(0, 200));
    exigir((await botonReintentar(p).count()) === 1, 'sin Reintentar');
    exigir((await vista(p).count()) === 0, 'muestra resultados');
    return '«Sin conexión. No se pudo conectar con el servidor. Revise su conexión e intente de nuevo.», con Reintentar';
  });

  // ---------- D. Datos protegidos ----------
  await caso('TC-GEN-05-018', 'No se presentan datos protegidos', async (p) => {
    const encontrados = [];
    for (const id of ['16', '17', '18']) {
      await p.goto(`${BASE}/evaluaciones/${id}/resultados`);
      await esperar(p, 1800);
      const t = await texto(p, '.ev-pagina');
      for (const patron of [/respuesta\s*correcta/i, /contenido de la clave/i, /@/, /nombre del evaluado/i]) {
        if (patron.test(t)) encontrados.push(`${id}: ${patron}`);
      }
    }
    exigir(encontrados.length === 0, encontrados.join('; '));
    await p.goto(`${BASE}/evaluaciones/16/resultados`);
    await esperar(p, 1800);
    const clave = await dato(p, 'Clave');
    exigir(/^\d+$/.test(clave), `Clave muestra «${clave}», no un identificador`);
    return `En 16, 17 y 18 solo se ven identificadores (Clave = ${clave}); ni contenido de la clave, ni respuestas correctas, ni datos del evaluado`;
  });

  fs.writeFileSync(`${CAPS}/resultados.json`, JSON.stringify(resultados, null, 2));
  const n = (e) => resultados.filter((r) => r.estado === e).length;
  console.log(`TOTAL: ${resultados.length} · PASÓ ${n('PASÓ')} · FALLÓ ${n('FALLÓ')}`);
})();
