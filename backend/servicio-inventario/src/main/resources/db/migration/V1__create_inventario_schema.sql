-- ==============================================================================
-- FLYWAY V1: ESQUEMA DE INVENTARIO DE HILOS Y TRAZABILIDAD DE MOVIMIENTOS
-- ==============================================================================

CREATE TABLE hilos (
    id BIGSERIAL PRIMARY KEY,
    marca VARCHAR(100) NOT NULL,
    tipo_material VARCHAR(50) NOT NULL DEFAULT 'Poliéster 40wt',
    codigo_color VARCHAR(50) NOT NULL,
    nombre_color VARCHAR(100) NOT NULL,
    referencia VARCHAR(50) NOT NULL,
    stock_actual_conos INTEGER NOT NULL CHECK (stock_actual_conos >= 0),
    metros_por_cono INTEGER NOT NULL CHECK (metros_por_cono > 0),
    stock_minimo_conos INTEGER NOT NULL DEFAULT 5 CHECK (stock_minimo_conos >= 0),
    precio_cono NUMERIC(10, 2) NOT NULL CHECK (precio_cono >= 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Regla de unicidad por referencia de hilo
CREATE UNIQUE INDEX idx_hilos_referencia ON hilos (LOWER(referencia));

-- Tabla de trazabilidad inmutable de movimientos de almacén
CREATE TABLE movimientos_inventario (
    id BIGSERIAL PRIMARY KEY,
    hilo_id BIGINT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA', 'AJUSTE')),
    cantidad_conos INTEGER NOT NULL CHECK (cantidad_conos > 0),
    stock_previo INTEGER NOT NULL,
    stock_nuevo INTEGER NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    referencia_trabajo_id BIGINT,
    operador VARCHAR(100) NOT NULL,
    fecha_movimiento TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimientos_hilo FOREIGN KEY (hilo_id) 
        REFERENCES hilos (id) ON DELETE RESTRICT
);

CREATE INDEX idx_movimientos_hilo_fecha ON movimientos_inventario (hilo_id, fecha_movimiento DESC);
