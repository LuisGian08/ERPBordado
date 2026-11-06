-- ==============================================================================
-- FLYWAY V1: ESQUEMA DE ÓRDENES DE TRABAJO, COSTOS Y MÁRGENES
-- ==============================================================================

CREATE TABLE trabajos_bordado (
    id BIGSERIAL PRIMARY KEY,
    codigo_trabajo VARCHAR(50) NOT NULL,
    cliente_nombre VARCHAR(150) NOT NULL,
    cliente_contacto VARCHAR(100),
    diseno_id BIGINT NOT NULL,
    nombre_diseno_historico VARCHAR(150) NOT NULL,
    cantidad_prendas INTEGER NOT NULL CHECK (cantidad_prendas > 0),
    puntadas_unitarias INTEGER NOT NULL CHECK (puntadas_unitarias > 0),
    total_puntadas BIGINT NOT NULL CHECK (total_puntadas > 0),
    precio_venta NUMERIC(12, 2) NOT NULL CHECK (precio_venta >= 0),
    costo_estimado NUMERIC(12, 2) NOT NULL CHECK (costo_estimado >= 0),
    costo_real NUMERIC(12, 2) CHECK (costo_real >= 0),
    estado VARCHAR(30) NOT NULL DEFAULT 'COTIZADO' 
        CHECK (estado IN ('COTIZADO', 'CONFIRMADO', 'EN_PROCESO', 'TERMINADO', 'ENTREGADO', 'CANCELADO')),
    fecha_compromiso TIMESTAMP WITH TIME ZONE,
    observaciones TEXT,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_trabajos_codigo ON trabajos_bordado (LOWER(codigo_trabajo));
CREATE INDEX idx_trabajos_cliente ON trabajos_bordado (cliente_nombre);
CREATE INDEX idx_trabajos_estado ON trabajos_bordado (estado);
CREATE INDEX idx_trabajos_diseno ON trabajos_bordado (diseno_id);
