package com.embroiderydash.pagos.api.dto;

import com.embroiderydash.pagos.domain.model.MetodoPago;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PagoRequestDto(
        @NotNull @Positive Long trabajoId,
        @NotNull @Positive @Digits(integer=10, fraction=2) BigDecimal monto,
        @NotNull MetodoPago metodoPago,
        @Size(max=100) String referenciaTransaccion,
        String notas) { }
