ALTER TABLE configuracion_subprueba
    ADD COLUMN opciones_esperadas INTEGER;

ALTER TABLE configuracion_subprueba
    ADD CONSTRAINT ck_configuracion_opciones_esperadas
    CHECK (opciones_esperadas IS NULL OR opciones_esperadas > 0);
