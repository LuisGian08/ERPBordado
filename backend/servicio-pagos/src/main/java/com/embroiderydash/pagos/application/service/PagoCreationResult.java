package com.embroiderydash.pagos.application.service;

import com.embroiderydash.pagos.api.dto.PagoResponseDto;

public record PagoCreationResult(PagoResponseDto pago, boolean creado) {}
