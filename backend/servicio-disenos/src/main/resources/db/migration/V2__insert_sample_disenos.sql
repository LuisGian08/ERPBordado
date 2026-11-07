-- ==============================================================================
-- FLYWAY V2: DATOS DE MUESTRA PARA SERVICIO DE DISEÑOS
-- ==============================================================================

INSERT INTO disenos (id, nombre, descripcion, cantidad_puntadas, ancho_mm, alto_mm, formatos_admitidos, observaciones_tecnicas, activo)
VALUES 
(1, 'Logotipo Corporativo Escudo', 'Bordado frontal para polos y camisas empresariales', 7500, 85.00, 70.00, 'DST,PES', 'Densidad estándar 0.4mm, entretela desgarrable recomendada', true),
(2, 'Insignia Escolar Promocional', 'Escudo circular para uniforme escolar bordado a 4 colores', 12400, 90.00, 90.00, 'DST,PES,EXP', 'Requiere doble refuerzo en los bordes festón', true),
(3, 'Parche Gorra Moto Club', 'Diseño curvo adaptado para bastidor de gorras', 9800, 120.00, 55.00, 'DST', 'Bordar con aguja 75/11 para sarga pesada', true);

SELECT setval('disenos_id_seq', (SELECT MAX(id) FROM disenos));

-- Colores para Logotipo Corporativo Escudo
INSERT INTO diseno_colores (diseno_id, orden_secuencia, codigo_color, nombre_color, referencia_hilo)
VALUES 
(1, 1, '#002B49', 'Azul Marino Corporativo', 'POLY-1042'),
(1, 2, '#D4AF37', 'Dorado Brillante', 'POLY-1088'),
(1, 3, '#FFFFFF', 'Blanco Puro', 'POLY-1001');

-- Colores para Insignia Escolar Promocional
INSERT INTO diseno_colores (diseno_id, orden_secuencia, codigo_color, nombre_color, referencia_hilo)
VALUES 
(2, 1, '#C8102E', 'Rojo Carmesí', 'POLY-1025'),
(2, 2, '#000000', 'Negro', 'POLY-1000'),
(2, 3, '#FFD100', 'Amarillo Oro', 'POLY-1012'),
(2, 4, '#FFFFFF', 'Blanco Puro', 'POLY-1001');

-- Colores para Parche Gorra Moto Club
INSERT INTO diseno_colores (diseno_id, orden_secuencia, codigo_color, nombre_color, referencia_hilo)
VALUES 
(3, 1, '#1A1A1A', 'Negro Carbón', 'POLY-1000'),
(3, 2, '#FF5500', 'Naranja Fuego', 'POLY-1076');

-- Archivos técnicos de muestra
INSERT INTO diseno_archivos (diseno_id, nombre_archivo, formato, ruta_almacenamiento, tamano_bytes, checksum_md5)
VALUES
(1, 'logotipo_corporativo.dst', 'DST', 'disenos/1/logotipo_corporativo.dst', 45120, 'd41d8cd98f00b204e9800998ecf8427e'),
(1, 'logotipo_corporativo.pes', 'PES', 'disenos/1/logotipo_corporativo.pes', 48200, 'b10a8db164e0754105b7a99be72e3fe5'),
(2, 'insignia_escolar.dst', 'DST', 'disenos/2/insignia_escolar.dst', 62300, '7b52009b64fd0a2a49e6d8a939753077');
