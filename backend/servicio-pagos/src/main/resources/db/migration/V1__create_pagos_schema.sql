-- ==============================================================================
-- FLYWAY V1: ESQUEMA DE PAGOS, COBRANZAS E IDEMPOTENCIA FINANCIERA
-- ==============================================================================

CREATE TABLE pagos (
    id BIGSERIAL PRIMARY KEY,
    trabajo_id BIGINT NOT NULL,
    idempotency_key VARCHAR(100) NOT NULL,
    monto NUMERIC(12, 2) NOT NULL CHECK (monto > 0),
    metodo_pago VARCHAR(30) NOT NULL 
        CHECK (metodo_pago IN ('EFECTIVO', 'TRANSFERENCIA', 'TARJETA', 'YAPE_PLIN')),
    referencia_transaccion VARCHAR(100),
    estado_resultado VARCHAR(30) NOT NULL DEFAULT 'REGISTRADO' 
        CHECK (estado_resultado IN ('REGISTRADO', 'ANULADO')),
    notas TEXT,
    operador VARCHAR(100) NOT NULL,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Restricción de unicidad para garantizar la idempotencia estricta en operaciones financieras
CREATE UNIQUE INDEX idx_pagos_idempotency ON pagos (idempotency_key);
CREATE INDEX idx_pagos_trabajo_id ON pagos (trabajo_id);
CREATE INDEX idx_pagos_creado_en ON pagos (creado_en DESC);
