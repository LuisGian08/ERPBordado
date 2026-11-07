-- ==============================================================================
-- FLYWAY V2: DATOS DE MUESTRA PARA SERVICIO DE INVENTARIO
-- ==============================================================================

INSERT INTO hilos (id, marca, tipo_material, codigo_color, nombre_color, referencia, stock_actual_conos, metros_por_cono, stock_minimo_conos, precio_cono, activo)
VALUES
(1, 'Madeira', 'Poliéster 40wt', '#000000', 'Negro Clásico', 'POLY-1000', 18, 5000, 5, 24.50, true),
(2, 'Madeira', 'Poliéster 40wt', '#FFFFFF', 'Blanco Puro', 'POLY-1001', 25, 5000, 8, 24.50, true),
(3, 'Madeira', 'Poliéster 40wt', '#C8102E', 'Rojo Carmesí', 'POLY-1025', 4, 5000, 6, 26.00, true), -- En alerta de stock
(4, 'Gunold', 'Poliéster 40wt', '#002B49', 'Azul Marino', 'POLY-1042', 12, 5000, 5, 25.00, true),
(5, 'Isacord', 'Metálico Brillante', '#D4AF37', 'Dorado Festón', 'POLY-1088', 2, 3000, 3, 38.00, true); -- En alerta de stock

SELECT setval('hilos_id_seq', (SELECT MAX(id) FROM hilos));

INSERT INTO movimientos_inventario (hilo_id, tipo_movimiento, cantidad_conos, stock_previo, stock_nuevo, motivo, operador)
VALUES
(1, 'ENTRADA', 20, 0, 20, 'Compra inicial a distribuidor mayorista', 'admin'),
(1, 'SALIDA', 2, 20, 18, 'Consumo en orden de bordado #101', 'operador'),
(2, 'ENTRADA', 25, 0, 25, 'Compra inicial a distribuidor mayorista', 'admin'),
(3, 'ENTRADA', 10, 0, 10, 'Compra inicial de lote', 'admin'),
(3, 'SALIDA', 6, 10, 4, 'Bordado de escudos escolares', 'operador');
