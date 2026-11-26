-- Decisión de Nelson 25-nov: permiso más asignación explícita, sin alcance global por rol.
-- No carga asignaciones reales o ficticias automáticamente. Revisar con custodio antes de integrar.
CREATE TABLE cuenta_sujeto_asignado (
    id_cuenta BIGINT NOT NULL REFERENCES cuenta(id_cuenta),
    id_sujeto BIGINT NOT NULL REFERENCES sujeto(id_sujeto),
    PRIMARY KEY (id_cuenta, id_sujeto)
);
CREATE INDEX ix_cuenta_sujeto_asignado_sujeto ON cuenta_sujeto_asignado(id_sujeto);
