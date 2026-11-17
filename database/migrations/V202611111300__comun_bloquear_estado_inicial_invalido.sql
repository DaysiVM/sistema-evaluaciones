
-- BUG-04 / TC-GEN-07-031
-- Impedir crear versiones directamente como PUBLICADA o RETIRADA.

CREATE FUNCTION validar_estado_inicial_version()
  RETURNS TRIGGER
  LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.estado <> 'BORRADOR' THEN
        RAISE EXCEPTION
            'Una version debe crearse en estado BORRADOR';
END IF;

RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_estado_inicial_version
  BEFORE INSERT ON version_formulario
  FOR EACH ROW
  EXECUTE FUNCTION validar_estado_inicial_version();
