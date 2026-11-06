package com.embroiderydash.disenos.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PrecioRequest(@NotNull @DecimalMin("0.01") @Digits(integer=10, fraction=2) BigDecimal precioUnitario,
                            @Size(max=500) String motivo) { }
