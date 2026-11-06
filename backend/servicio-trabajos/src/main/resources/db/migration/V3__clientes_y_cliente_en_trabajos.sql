CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    contacto VARCHAR(100),
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_clientes_nombre ON clientes (LOWER(nombre));

ALTER TABLE trabajos_bordado ADD COLUMN cliente_id BIGINT REFERENCES clientes(id);
ALTER TABLE trabajos_bordado ALTER COLUMN estado SET DEFAULT 'CONFIRMADO';
