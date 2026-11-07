package com.embroiderydash.pagos.api.dto;

import com.embroiderydash.pagos.domain.model.TipoPagoOperador;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PagoOperadorRequestDto(
        @NotBlank @Size(max=100) String operador,
        @NotNull LocalDate semanaInicio,
        @NotEmpty List<@NotNull LocalDate> diasAsistencia,
        @NotNull TipoPagoOperador tipoPago,
        BigDecimal tarifaDiaria,
        BigDecimal horasRegulares,
        BigDecimal tarifaHora,
        BigDecimal horasExtras,
        BigDecimal tarifaHoraExtra,
        @NotNull LocalDate fechaPago,
        @Size(max=500) String notas) { }
