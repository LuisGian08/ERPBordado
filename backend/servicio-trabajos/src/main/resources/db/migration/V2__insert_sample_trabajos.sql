-- ==============================================================================
-- FLYWAY V2: DATOS DE MUESTRA PARA SERVICIO DE TRABAJOS
-- ==============================================================================

INSERT INTO trabajos_bordado (
    id, codigo_trabajo, cliente_nombre, cliente_contacto, diseno_id, nombre_diseno_historico,
    cantidad_prendas, puntadas_unitarias, total_puntadas, precio_venta, costo_estimado, costo_real,
    estado, fecha_compromiso, observaciones
) VALUES
(
    1, 'TRAB-2026-001', 'Colegio Los Álamos', 'director@losalamos.edu', 2, 'Insignia Escolar Promocional',
    50, 12400, 620000, 1500.00, 895.50, 880.00,
    'TERMINADO', CURRENT_TIMESTAMP + INTERVAL '3 days', '50 camisas escolares talla S a L'
),
(
    2, 'TRAB-2026-002', 'Industrias Metálicas del Norte', 'compras@indmetalicas.com', 1, 'Logotipo Corporativo Escudo',
    100, 7500, 750000, 2200.00, 1150.00, NULL,
    'EN_PROCESO', CURRENT_TIMESTAMP + INTERVAL '5 days', 'Polos de seguridad color azul'
),
(
    3, 'TRAB-2026-003', 'Club Motociclistas Trueno', 'presidencia@mototrueno.com', 3, 'Parche Gorra Moto Club',
    30, 9800, 294000, 750.00, 420.00, NULL,
    'CONFIRMADO', CURRENT_TIMESTAMP + INTERVAL '7 days', 'Gorras negras con visera plana'
);

SELECT setval('trabajos_bordado_id_seq', (SELECT MAX(id) FROM trabajos_bordado));
