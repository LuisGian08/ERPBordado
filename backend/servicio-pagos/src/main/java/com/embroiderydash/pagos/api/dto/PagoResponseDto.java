package com.embroiderydash.pagos.api.dto;

import com.embroiderydash.pagos.domain.model.Pago;
import com.embroiderydash.pagos.domain.model.MetodoPago;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PagoResponseDto(Long id, Long trabajoId, BigDecimal monto, MetodoPago metodoPago,
        String referenciaTransaccion, String estadoResultado, String notas, String operador, OffsetDateTime creadoEn) {
    public static PagoResponseDto from(Pago p) { return new PagoResponseDto(p.getId(),p.getTrabajoId(),p.getMonto(),p.getMetodoPago(),p.getReferenciaTransaccion(),p.getEstadoResultado(),p.getNotas(),p.getOperador(),p.getCreadoEn()); }
}
