ALTER TABLE movimientos_inventario
    ADD COLUMN precio_compra_cono NUMERIC(10, 2),
    ADD COLUMN gasto_total NUMERIC(12, 2);

UPDATE movimientos_inventario m
SET precio_compra_cono = h.precio_cono,
    gasto_total = m.cantidad_conos * h.precio_cono
FROM hilos h
WHERE m.hilo_id = h.id
  AND m.tipo_movimiento = 'ENTRADA';

CREATE INDEX idx_movimientos_entrada_fecha
    ON movimientos_inventario (fecha_movimiento DESC)
    WHERE tipo_movimiento = 'ENTRADA';
