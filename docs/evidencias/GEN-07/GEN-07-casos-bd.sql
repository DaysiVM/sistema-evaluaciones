-- Casos de prueba de base de datos GEN-07 (PR #23, migración V202611040249)
-- Datos 100 % ficticios: no son ítems reales de ninguna prueba psicológica.
-- Documento: docs/pruebas/GEN-07-casos-prueba-v1.md (TC-GEN-07-001 a 031)
--
-- Uso, sobre una base VACÍA (nunca la compartida ni la de un entorno desplegado):
--   createdb evaluaciones_gen07
--   psql -d evaluaciones_gen07 -f database/migrations/V202611040249__comun_catalogo_versionado.sql
--   psql -d evaluaciones_gen07 -f docs/evidencias/GEN-07/GEN-07-casos-bd.sql
-- Al final imprime cada caso con PASÓ / FALLÓ, lo esperado y lo obtenido.
\set ON_ERROR_STOP 1
SET client_min_messages = warning;

CREATE TEMP TABLE resultado_tc (
  id TEXT PRIMARY KEY, esperado TEXT, obtenido TEXT, estado TEXT
);

-- espera_error: ejecuta la sentencia y comprueba que PostgreSQL la rechace
CREATE FUNCTION pg_temp.espera_error(p_id TEXT, p_sql TEXT, p_fragmento TEXT)
RETURNS VOID LANGUAGE plpgsql AS $$
DECLARE v_aceptado BOOLEAN := false; v_error TEXT;
BEGIN
  BEGIN
    EXECUTE p_sql;
    v_aceptado := true;
    RAISE EXCEPTION 'tc_revertir';   -- deshace la sentencia aceptada
  EXCEPTION WHEN OTHERS THEN
    v_error := SQLERRM;
  END;
  IF v_aceptado THEN
    INSERT INTO resultado_tc VALUES (p_id, 'rechazo: ' || p_fragmento, 'ACEPTADO (sin error)', 'FALLÓ');
  ELSE
    INSERT INTO resultado_tc VALUES (p_id, 'rechazo: ' || p_fragmento, 'rechazo: ' || v_error,
      CASE WHEN position(lower(p_fragmento) IN lower(v_error)) > 0 THEN 'PASÓ' ELSE 'FALLÓ' END);
  END IF;
END $$;

-- espera_ok: la sentencia debe ejecutarse sin error (y sus cambios se conservan)
CREATE FUNCTION pg_temp.espera_ok(p_id TEXT, p_sql TEXT)
RETURNS VOID LANGUAGE plpgsql AS $$
BEGIN
  EXECUTE p_sql;
  INSERT INTO resultado_tc VALUES (p_id, 'aceptado', 'aceptado', 'PASÓ');
EXCEPTION WHEN OTHERS THEN
  INSERT INTO resultado_tc VALUES (p_id, 'aceptado', 'rechazo: ' || SQLERRM, 'FALLÓ');
END $$;

-- ---------------------------------------------------------------
-- Datos ficticios base
-- ---------------------------------------------------------------
INSERT INTO test (id_test, codigo, nombre) VALUES
  (1, 'TEST-FICTICIO', 'Test ficticio de pruebas'),
  (2, 'TEST-OTRO', 'Otro test ficticio');
INSERT INTO subprueba (id_subprueba, id_test, codigo, nombre) VALUES
  (10, 1, 'SP-A', 'Subprueba ficticia A'),
  (11, 1, 'SP-B', 'Subprueba ficticia B'),
  (20, 2, 'SP-X', 'Subprueba de otro test');

-- Versión V1 en borrador con contenido completo
INSERT INTO version_formulario (id_version_formulario, id_test, codigo_version, fuente_ref)
  VALUES (100, 1, 'V1', 'Material ficticio de pruebas');
INSERT INTO configuracion_subprueba (id_configuracion_subprueba, id_version_formulario, id_subprueba,
  orden_aplicacion, tiempo_limite_segundos, tipo_respuesta, instrucciones_ref)
  VALUES (1000, 100, 10, 1, 300, 'SELECCION', 'instr-ficticia-a-v1'),
         (1001, 100, 11, 2, 240, 'TEXTO_LIBRE', 'instr-ficticia-b-v1');
INSERT INTO pregunta (id_pregunta, id_configuracion_subprueba, numero_visible, orden_presentacion, enunciado)
  VALUES (5000, 1000, '1', 1, 'Pregunta ficticia uno'),
         (5001, 1000, '2', 2, 'Pregunta ficticia dos');
INSERT INTO opcion_respuesta (id_opcion, id_pregunta, codigo, orden_presentacion, texto) VALUES
  (9000, 5000, 'A', 1, 'Opción ficticia A'), (9001, 5000, 'B', 2, 'Opción ficticia B'),
  (9002, 5001, 'A', 1, 'Opción ficticia A'), (9003, 5001, 'B', 2, 'Opción ficticia B');

-- ---------------------------------------------------------------
-- Almacenar varias versiones de un mismo test (CA-1 de GEN-07-BE)
-- ---------------------------------------------------------------
SELECT pg_temp.espera_ok('TC-GEN-07-001',
  $q$INSERT INTO version_formulario (id_version_formulario, id_test, codigo_version, fuente_ref)
     VALUES (101, 1, 'V2', 'Material ficticio de pruebas')$q$);
SELECT pg_temp.espera_error('TC-GEN-07-002',
  $q$INSERT INTO version_formulario (id_test, codigo_version, fuente_ref) VALUES (1, 'V1', 'otra')$q$,
  'uq_version_test_codigo');

-- ---------------------------------------------------------------
-- Publicación
-- ---------------------------------------------------------------
INSERT INTO version_formulario (id_version_formulario, id_test, codigo_version, fuente_ref)
  VALUES (102, 1, 'V-VACIA', 'Material ficticio de pruebas');
SELECT pg_temp.espera_error('TC-GEN-07-003',
  $q$UPDATE version_formulario SET estado='PUBLICADA', publicada_en=now(), evidencia_validacion='ev-ficticia'
     WHERE id_version_formulario=102$q$,
  'necesita configuraciones');
SELECT pg_temp.espera_error('TC-GEN-07-004',
  $q$UPDATE version_formulario SET estado='PUBLICADA' WHERE id_version_formulario=100$q$,
  'chk_version_publicacion');
SELECT pg_temp.espera_ok('TC-GEN-07-005',
  $q$UPDATE version_formulario SET estado='PUBLICADA', publicada_en=now(), evidencia_validacion='ev-ficticia-v1'
     WHERE id_version_formulario=100$q$);

-- Huella del contenido de V1 justo después de publicar
CREATE TEMP TABLE huella_v1 AS
SELECT md5(string_agg(fila, '|' ORDER BY fila)) AS h FROM (
  SELECT 'c'||id_configuracion_subprueba||orden_aplicacion||coalesce(tiempo_limite_segundos,0)||tipo_respuesta||instrucciones_ref AS fila
    FROM configuracion_subprueba WHERE id_version_formulario=100
  UNION ALL SELECT 'p'||p.id_pregunta||numero_visible||orden_presentacion||coalesce(enunciado,'')
    FROM pregunta p JOIN configuracion_subprueba c USING (id_configuracion_subprueba) WHERE c.id_version_formulario=100
  UNION ALL SELECT 'o'||o.id_opcion||o.codigo||o.orden_presentacion||coalesce(o.texto,'')
    FROM opcion_respuesta o JOIN pregunta p USING (id_pregunta) JOIN configuracion_subprueba c USING (id_configuracion_subprueba)
    WHERE c.id_version_formulario=100) t;

-- ---------------------------------------------------------------
-- Una versión publicada no se modifica (CA-1 de GEN-07-TEST)
-- ---------------------------------------------------------------
SELECT pg_temp.espera_error('TC-GEN-07-006',
  $q$UPDATE pregunta SET enunciado='Texto cambiado' WHERE id_pregunta=5000$q$, 'publicada o retirada');
SELECT pg_temp.espera_error('TC-GEN-07-007',
  $q$INSERT INTO pregunta (id_configuracion_subprueba, numero_visible, orden_presentacion, enunciado)
     VALUES (1000, '3', 3, 'Pregunta nueva')$q$, 'publicada o retirada');
SELECT pg_temp.espera_error('TC-GEN-07-008',
  $q$DELETE FROM opcion_respuesta WHERE id_opcion=9003$q$, 'publicada o retirada');
SELECT pg_temp.espera_error('TC-GEN-07-009',
  $q$UPDATE configuracion_subprueba SET tiempo_limite_segundos=999 WHERE id_configuracion_subprueba=1000$q$,
  'publicada o retirada');
SELECT pg_temp.espera_error('TC-GEN-07-010',
  $q$UPDATE version_formulario SET fuente_ref='Fuente cambiada' WHERE id_version_formulario=100$q$, 'inmutable');
SELECT pg_temp.espera_error('TC-GEN-07-011',
  $q$DELETE FROM version_formulario WHERE id_version_formulario=100$q$, 'No se puede eliminar');
SELECT pg_temp.espera_error('TC-GEN-07-012',
  $q$UPDATE version_formulario SET estado='BORRADOR', publicada_en=NULL, evidencia_validacion=NULL
     WHERE id_version_formulario=100$q$, 'Transicion de estado no permitida');
SELECT pg_temp.espera_error('TC-GEN-07-013',
  $q$UPDATE subprueba SET nombre='Nombre cambiado' WHERE id_subprueba=10$q$, 'subprueba utilizada');

-- ---------------------------------------------------------------
-- Un cambio crea otra versión y no altera la publicada (CA-2 y CA-4 de GEN-07-BE)
-- ---------------------------------------------------------------
INSERT INTO configuracion_subprueba (id_configuracion_subprueba, id_version_formulario, id_subprueba,
    orden_aplicacion, tiempo_limite_segundos, tipo_respuesta, instrucciones_ref)
    VALUES (1100, 101, 10, 1, 360, 'SELECCION', 'instr-ficticia-a-v2');
INSERT INTO pregunta (id_pregunta, id_configuracion_subprueba, numero_visible, orden_presentacion, enunciado)
    VALUES (5100, 1100, '1', 1, 'Pregunta ficticia uno (texto corregido en V2)');
INSERT INTO opcion_respuesta (id_pregunta, codigo, orden_presentacion, texto)
    VALUES (5100, 'A', 1, 'Opción A v2'), (5100, 'B', 2, 'Opción B v2'), (5100, 'C', 3, 'Opción C v2');
SELECT pg_temp.espera_ok('TC-GEN-07-015',
  $q$UPDATE version_formulario SET estado='PUBLICADA', publicada_en=now(), evidencia_validacion='ev-ficticia-v2'
     WHERE id_version_formulario=101$q$);

INSERT INTO resultado_tc
SELECT 'TC-GEN-07-016', 'contenido de V1 idéntico tras publicar V2',
  CASE WHEN h = (SELECT md5(string_agg(fila, '|' ORDER BY fila)) FROM (
    SELECT 'c'||id_configuracion_subprueba||orden_aplicacion||coalesce(tiempo_limite_segundos,0)||tipo_respuesta||instrucciones_ref AS fila
      FROM configuracion_subprueba WHERE id_version_formulario=100
    UNION ALL SELECT 'p'||p.id_pregunta||numero_visible||orden_presentacion||coalesce(enunciado,'')
      FROM pregunta p JOIN configuracion_subprueba c USING (id_configuracion_subprueba) WHERE c.id_version_formulario=100
    UNION ALL SELECT 'o'||o.id_opcion||o.codigo||o.orden_presentacion||coalesce(o.texto,'')
      FROM opcion_respuesta o JOIN pregunta p USING (id_pregunta) JOIN configuracion_subprueba c USING (id_configuracion_subprueba)
      WHERE c.id_version_formulario=100) t)
  THEN 'idéntico (md5 ' || left(h, 8) || ')' ELSE 'CAMBIÓ' END,
  CASE WHEN h = (SELECT md5(string_agg(fila, '|' ORDER BY fila)) FROM (
    SELECT 'c'||id_configuracion_subprueba||orden_aplicacion||coalesce(tiempo_limite_segundos,0)||tipo_respuesta||instrucciones_ref AS fila
      FROM configuracion_subprueba WHERE id_version_formulario=100
    UNION ALL SELECT 'p'||p.id_pregunta||numero_visible||orden_presentacion||coalesce(enunciado,'')
      FROM pregunta p JOIN configuracion_subprueba c USING (id_configuracion_subprueba) WHERE c.id_version_formulario=100
    UNION ALL SELECT 'o'||o.id_opcion||o.codigo||o.orden_presentacion||coalesce(o.texto,'')
      FROM opcion_respuesta o JOIN pregunta p USING (id_pregunta) JOIN configuracion_subprueba c USING (id_configuracion_subprueba)
      WHERE c.id_version_formulario=100) t)
  THEN 'PASÓ' ELSE 'FALLÓ' END
FROM huella_v1;

-- ---------------------------------------------------------------
-- Retirar
-- ---------------------------------------------------------------
SELECT pg_temp.espera_ok('TC-GEN-07-017',
  $q$UPDATE version_formulario SET estado='RETIRADA' WHERE id_version_formulario=100$q$);
INSERT INTO resultado_tc
SELECT 'TC-GEN-07-018', 'RETIRADA conserva publicada_en y su contenido',
  'publicada_en=' || CASE WHEN publicada_en IS NOT NULL THEN 'conservada' ELSE 'NULA' END
    || ', preguntas=' || (SELECT count(*) FROM pregunta WHERE id_configuracion_subprueba IN (1000,1001)),
  CASE WHEN publicada_en IS NOT NULL
        AND (SELECT count(*) FROM pregunta WHERE id_configuracion_subprueba IN (1000,1001)) = 2
       THEN 'PASÓ' ELSE 'FALLÓ' END
FROM version_formulario WHERE id_version_formulario=100;
SELECT pg_temp.espera_error('TC-GEN-07-019',
  $q$UPDATE version_formulario SET estado='PUBLICADA' WHERE id_version_formulario=100$q$,
  'No se puede modificar una version retirada');

-- ---------------------------------------------------------------
-- Impedir configuraciones o preguntas incompatibles (CA-5 de GEN-07-BE)
-- ---------------------------------------------------------------
INSERT INTO version_formulario (id_version_formulario, id_test, codigo_version, fuente_ref)
  VALUES (103, 1, 'V3', 'Material ficticio de pruebas');
SELECT pg_temp.espera_error('TC-GEN-07-020',
  $q$INSERT INTO configuracion_subprueba (id_version_formulario, id_subprueba, orden_aplicacion, tipo_respuesta, instrucciones_ref)
     VALUES (103, 20, 1, 'SELECCION', 'instr')$q$, 'no pertenece al mismo test');
INSERT INTO configuracion_subprueba (id_configuracion_subprueba, id_version_formulario, id_subprueba,
  orden_aplicacion, tipo_respuesta, instrucciones_ref, maximo_entradas)
  VALUES (1300, 103, 11, 2, 'TEXTO_LIBRE', 'instr-ficticia-b-v3', 75);
INSERT INTO pregunta (id_pregunta, id_configuracion_subprueba, numero_visible, orden_presentacion, enunciado)
  VALUES (5300, 1300, '1', 1, 'Consigna ficticia de texto libre');
SELECT pg_temp.espera_error('TC-GEN-07-021',
  $q$UPDATE configuracion_subprueba SET id_subprueba=20 WHERE id_configuracion_subprueba=1300$q$,
  'no pertenece al mismo test');
SELECT pg_temp.espera_error('TC-GEN-07-014',
  $q$UPDATE pregunta SET id_configuracion_subprueba=1000 WHERE id_pregunta=5300$q$, 'publicada o retirada');
SELECT pg_temp.espera_error('TC-GEN-07-022',
  $q$INSERT INTO opcion_respuesta (id_pregunta, codigo, orden_presentacion, texto) VALUES (5300, 'A', 1, 'x')$q$,
  'solo pertenecen a preguntas de seleccion');
SELECT pg_temp.espera_error('TC-GEN-07-023',
  $q$UPDATE configuracion_subprueba SET id_version_formulario=101 WHERE id_configuracion_subprueba=1300$q$,
  'publicada o retirada');

-- ---------------------------------------------------------------
-- Integridad del modelo aprobado (CA-7 de GEN-07-BE · ENT-COM-07/08/13)
-- ---------------------------------------------------------------
SELECT pg_temp.espera_error('TC-GEN-07-024',
  $q$INSERT INTO configuracion_subprueba (id_version_formulario, id_subprueba, orden_aplicacion, tipo_respuesta, instrucciones_ref)
     VALUES (103, 10, 2, 'SELECCION', 'instr')$q$, 'uq_config_version_orden');
SELECT pg_temp.espera_error('TC-GEN-07-025',
  $q$INSERT INTO configuracion_subprueba (id_version_formulario, id_subprueba, orden_aplicacion, tiempo_limite_segundos, tipo_respuesta, instrucciones_ref)
     VALUES (103, 10, 1, 0, 'SELECCION', 'instr')$q$, 'chk_config_tiempo');
SELECT pg_temp.espera_error('TC-GEN-07-026',
  $q$INSERT INTO configuracion_subprueba (id_version_formulario, id_subprueba, orden_aplicacion, tipo_respuesta, instrucciones_ref)
     VALUES (103, 10, 1, 'DIBUJO', 'instr')$q$, 'chk_config_tipo');
SELECT pg_temp.espera_error('TC-GEN-07-027',
  $q$INSERT INTO configuracion_subprueba (id_version_formulario, id_subprueba, orden_aplicacion, tipo_respuesta, instrucciones_ref, maximo_entradas)
     VALUES (103, 10, 1, 'SELECCION', 'instr', 10)$q$, 'chk_config_entradas_seleccion');
SELECT pg_temp.espera_error('TC-GEN-07-028',
  $q$INSERT INTO pregunta (id_configuracion_subprueba, numero_visible, orden_presentacion, enunciado)
     VALUES (1300, '1', 2, 'Número repetido')$q$, 'uq_pregunta_numero');
SELECT pg_temp.espera_error('TC-GEN-07-029',
  $q$INSERT INTO pregunta (id_configuracion_subprueba, numero_visible, orden_presentacion)
     VALUES (1300, '2', 2)$q$, 'chk_pregunta_contenido');
INSERT INTO configuracion_subprueba (id_configuracion_subprueba, id_version_formulario, id_subprueba,
  orden_aplicacion, tipo_respuesta, instrucciones_ref)
  VALUES (1301, 103, 10, 1, 'SELECCION', 'instr-ficticia-a-v3');
INSERT INTO pregunta (id_pregunta, id_configuracion_subprueba, numero_visible, orden_presentacion, enunciado)
  VALUES (5301, 1301, '1', 1, 'Pregunta ficticia');
INSERT INTO opcion_respuesta (id_pregunta, codigo, orden_presentacion, texto) VALUES (5301, 'A', 1, 'x');
SELECT pg_temp.espera_error('TC-GEN-07-030',
  $q$INSERT INTO opcion_respuesta (id_pregunta, codigo, orden_presentacion, texto) VALUES (5301, 'A', 2, 'y')$q$,
  'uq_opcion_codigo');

-- ---------------------------------------------------------------
-- Publicar sin pasar por el borrador (ENT-COM-12: 1..N configuraciones al publicarse)
-- ---------------------------------------------------------------
SELECT pg_temp.espera_error('TC-GEN-07-031',
  $q$INSERT INTO version_formulario (id_test, codigo_version, fuente_ref, estado, publicada_en, evidencia_validacion)
     VALUES (1, 'V-DIRECTA', 'Material ficticio', 'PUBLICADA', now(), 'ev-ficticia')$q$,
  'necesita configuraciones');

SELECT id, estado, esperado, obtenido FROM resultado_tc ORDER BY id;
SELECT estado, count(*) FROM resultado_tc GROUP BY estado ORDER BY estado;
