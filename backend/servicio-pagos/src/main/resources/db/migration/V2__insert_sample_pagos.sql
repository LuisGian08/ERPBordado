-- ==============================================================================
-- FLYWAY V2: DATOS DE MUESTRA PARA SERVICIO DE PAGOS
-- ==============================================================================

-- Trabajo #1 (Total 1500.00): Pago completo en 2 cuotas (1000.00 y 500.00)
INSERT INTO pagos (id, trabajo_id, idempotency_key, monto, metodo_pago, referencia_transaccion, estado_resultado, notas, operador)
VALUES
(1, 1, 'idem-seed-001', 1000.00, 'TRANSFERENCIA', 'BCP-OP-987123', 'REGISTRADO', 'Adelanto 50% de producción', 'admin'),
(2, 1, 'idem-seed-002', 500.00, 'EFECTIVO', 'REC-001-45', 'REGISTRADO', 'Cancelación de saldo contra entrega', 'operador');

-- Trabajo #2 (Total 2200.00): Pago parcial (1100.00 adelanto)
INSERT INTO pagos (id, trabajo_id, idempotency_key, monto, metodo_pago, referencia_transaccion, estado_resultado, notas, operador)
VALUES
(3, 2, 'idem-seed-003', 1100.00, 'TRANSFERENCIA', 'BBVA-TR-554411', 'REGISTRADO', 'Adelanto 50% para inicio de bordado', 'admin');

SELECT setval('pagos_id_seq', (SELECT MAX(id) FROM pagos));
