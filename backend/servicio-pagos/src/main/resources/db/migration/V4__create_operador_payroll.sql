CREATE TABLE pagos_operadores (
    id BIGSERIAL PRIMARY KEY,
    operador VARCHAR(100) NOT NULL,
    semana_inicio DATE NOT NULL,
    semana_fin DATE NOT NULL,
    dias_asistencia TEXT NOT NULL,
    tipo_pago VARCHAR(20) NOT NULL CHECK (tipo_pago IN ('DIA_COMPLETO','POR_HORAS')),
    tarifa_diaria NUMERIC(12,2),
    horas_regulares NUMERIC(8,2),
    tarifa_hora NUMERIC(12,2),
    monto_base NUMERIC(12,2) NOT NULL CHECK (monto_base >= 0),
    horas_extras NUMERIC(8,2) NOT NULL DEFAULT 0 CHECK (horas_extras >= 0),
    tarifa_hora_extra NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (tarifa_hora_extra >= 0),
    monto_extras NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (monto_extras >= 0),
    monto_total NUMERIC(12,2) NOT NULL CHECK (monto_total >= 0),
    fecha_pago DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PAGADO' CHECK (estado IN ('PAGADO')),
    notas VARCHAR(500),
    registrado_por VARCHAR(100) NOT NULL,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_pago_operador_semana CHECK (semana_fin = semana_inicio + 6),
    CONSTRAINT ck_pago_operador_tipo_importe CHECK (
        (tipo_pago = 'DIA_COMPLETO' AND tarifa_diaria IS NOT NULL AND tarifa_diaria > 0)
        OR (tipo_pago = 'POR_HORAS' AND horas_regulares IS NOT NULL AND horas_regulares > 0 AND tarifa_hora IS NOT NULL AND tarifa_hora > 0)
    )
);
CREATE UNIQUE INDEX uq_pago_operador_semana ON pagos_operadores (LOWER(operador), semana_inicio);
CREATE INDEX idx_pago_operador_fecha_pago ON pagos_operadores (fecha_pago DESC);
