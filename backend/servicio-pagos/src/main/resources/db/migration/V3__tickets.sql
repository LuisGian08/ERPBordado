CREATE SEQUENCE ticket_codigo_seq START 1;
CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(32) NOT NULL UNIQUE,
    cliente_nombre VARCHAR(160) NOT NULL,
    cliente_contacto VARCHAR(120),
    observaciones TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTO' CHECK (estado IN ('ABIERTO','PAGADO','ANULADO')),
    total NUMERIC(12,2) NOT NULL CHECK (total >= 0),
    creado_por VARCHAR(100) NOT NULL,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE ticket_items (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    diseno_id BIGINT NOT NULL,
    nombre_diseno VARCHAR(150) NOT NULL,
    precio_unitario NUMERIC(12,2) NOT NULL CHECK (precio_unitario > 0),
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    subtotal NUMERIC(12,2) NOT NULL CHECK (subtotal > 0)
);
ALTER TABLE pagos ALTER COLUMN trabajo_id DROP NOT NULL;
ALTER TABLE pagos ADD COLUMN ticket_id BIGINT REFERENCES tickets(id) ON DELETE RESTRICT;
ALTER TABLE pagos ADD CONSTRAINT ck_pago_un_sujeto CHECK ((trabajo_id IS NULL) <> (ticket_id IS NULL));
CREATE INDEX idx_pagos_ticket_id ON pagos(ticket_id);
