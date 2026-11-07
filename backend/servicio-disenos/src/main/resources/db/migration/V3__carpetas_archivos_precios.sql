CREATE TABLE carpetas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    carpeta_padre_id BIGINT REFERENCES carpetas(id) ON DELETE RESTRICT,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creado_por VARCHAR(100) NOT NULL
);
CREATE UNIQUE INDEX uk_carpetas_raiz_nombre ON carpetas (LOWER(nombre)) WHERE carpeta_padre_id IS NULL;
CREATE UNIQUE INDEX uk_carpetas_hija_nombre ON carpetas (carpeta_padre_id, LOWER(nombre)) WHERE carpeta_padre_id IS NOT NULL;
ALTER TABLE disenos ADD COLUMN carpeta_id BIGINT REFERENCES carpetas(id) ON DELETE SET NULL;
ALTER TABLE disenos ADD COLUMN cantidad_colores INTEGER NOT NULL DEFAULT 0 CHECK (cantidad_colores >= 0);
ALTER TABLE disenos ADD COLUMN preview_ruta VARCHAR(500);
ALTER TABLE disenos ADD COLUMN miniatura_ruta VARCHAR(500);
DELETE FROM diseno_archivos a USING diseno_archivos b WHERE a.diseno_id = b.diseno_id AND a.id > b.id;
CREATE UNIQUE INDEX uk_diseno_archivo_principal ON diseno_archivos(diseno_id);
CREATE TABLE diseno_precios (
    diseno_id BIGINT PRIMARY KEY REFERENCES disenos(id) ON DELETE CASCADE,
    precio_unitario NUMERIC(12,2) NOT NULL CHECK (precio_unitario > 0),
    moneda VARCHAR(3) NOT NULL DEFAULT 'PEN',
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_por VARCHAR(100) NOT NULL
);
CREATE TABLE diseno_precios_historial (
    id BIGSERIAL PRIMARY KEY,
    diseno_id BIGINT NOT NULL REFERENCES disenos(id) ON DELETE CASCADE,
    precio_anterior NUMERIC(12,2),
    precio_nuevo NUMERIC(12,2) NOT NULL CHECK (precio_nuevo > 0),
    motivo TEXT,
    cambiado_por VARCHAR(100) NOT NULL,
    cambiado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
