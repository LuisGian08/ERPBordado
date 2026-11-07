package com.embroiderydash.pagos.api.dto;

import com.embroiderydash.pagos.domain.model.PagoOperador;
import com.embroiderydash.pagos.domain.model.TipoPagoOperador;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record PagoOperadorResponseDto(Long id, String operador, LocalDate semanaInicio, LocalDate semanaFin,
        List<LocalDate> diasAsistencia, TipoPagoOperador tipoPago, BigDecimal tarifaDiaria,
        BigDecimal horasRegulares, BigDecimal tarifaHora, BigDecimal montoBase, BigDecimal horasExtras,
        BigDecimal tarifaHoraExtra, BigDecimal montoExtras, BigDecimal montoTotal, LocalDate fechaPago,
        String estado, String notas, String registradoPor, OffsetDateTime creadoEn) {
    public static PagoOperadorResponseDto from(PagoOperador p) {
        List<LocalDate> dias=p.getDiasAsistencia().isBlank()?List.of():java.util.Arrays.stream(p.getDiasAsistencia().split(","))
                .map(LocalDate::parse).toList();
        return new PagoOperadorResponseDto(p.getId(),p.getOperador(),p.getSemanaInicio(),p.getSemanaFin(),dias,
                p.getTipoPago(),p.getTarifaDiaria(),p.getHorasRegulares(),p.getTarifaHora(),p.getMontoBase(),
                p.getHorasExtras(),p.getTarifaHoraExtra(),p.getMontoExtras(),p.getMontoTotal(),p.getFechaPago(),
                p.getEstado(),p.getNotas(),p.getRegistradoPor(),p.getCreadoEn());
    }
}
