-- ==============================================================================
-- FLYWAY V1: CREACIÓN DEL ESQUEMA DE DISEÑOS DE BORDADO
-- ==============================================================================

CREATE TABLE disenos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    cantidad_puntadas INTEGER NOT NULL CHECK (cantidad_puntadas > 0),
    ancho_mm NUMERIC(8, 2),
    alto_mm NUMERIC(8, 2),
    formatos_admitidos VARCHAR(100) NOT NULL DEFAULT 'DST,PES',
    observaciones_tecnicas TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Regla de unicidad de nombre de diseño insensible a mayúsculas/minúsculas
CREATE UNIQUE INDEX idx_disenos_nombre_lower ON disenos (LOWER(nombre));

-- Tabla de secuencia de colores de hilo asociados al diseño
CREATE TABLE diseno_colores (
    id BIGSERIAL PRIMARY KEY,
    diseno_id BIGINT NOT NULL,
    orden_secuencia INTEGER NOT NULL,
    codigo_color VARCHAR(50) NOT NULL,
    nombre_color VARCHAR(100) NOT NULL,
    referencia_hilo VARCHAR(50),
    CONSTRAINT fk_diseno_colores_diseno FOREIGN KEY (diseno_id) 
        REFERENCES disenos (id) ON DELETE CASCADE,
    CONSTRAINT uk_diseno_orden UNIQUE (diseno_id, orden_secuencia)
);

-- Tabla de archivos técnicos adjuntos (DST, PES, EXP, JEF)
CREATE TABLE diseno_archivos (
    id BIGSERIAL PRIMARY KEY,
    diseno_id BIGINT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    formato VARCHAR(10) NOT NULL,
    ruta_almacenamiento VARCHAR(500) NOT NULL,
    tamano_bytes BIGINT NOT NULL,
    checksum_md5 VARCHAR(64),
    subido_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_diseno_archivos_diseno FOREIGN KEY (diseno_id) 
        REFERENCES disenos (id) ON DELETE CASCADE
);
