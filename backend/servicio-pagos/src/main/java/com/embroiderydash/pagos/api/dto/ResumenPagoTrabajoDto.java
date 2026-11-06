package com.embroiderydash.pagos.api.dto;

import com.embroiderydash.pagos.domain.model.EstadoPagoTrabajo;
import java.math.BigDecimal;

public record ResumenPagoTrabajoDto(Long trabajoId, BigDecimal precioVenta, BigDecimal totalPagado,
        BigDecimal saldoPendiente, EstadoPagoTrabajo estado) {}
