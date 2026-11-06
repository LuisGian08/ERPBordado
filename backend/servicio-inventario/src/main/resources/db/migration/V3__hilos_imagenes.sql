ALTER TABLE hilos ADD COLUMN imagen_ruta VARCHAR(500);
ALTER TABLE hilos ADD COLUMN imagen_content_type VARCHAR(50);
UPDATE hilos SET codigo_color = COALESCE(NULLIF(left(regexp_replace(codigo_color, '[^0-9]', '', 'g'), 20), ''), '0');
ALTER TABLE hilos ADD CONSTRAINT ck_hilos_codigo_color_numerico CHECK (codigo_color ~ '^[0-9]{1,20}$');
