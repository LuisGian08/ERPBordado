package com.embroiderydash.pagos.domain.exception;

import java.math.BigDecimal;

public class PagoExcedeSaldoException extends RuntimeException {

    private final Long trabajoId;
    private final BigDecimal montoPago;
    private final BigDecimal saldoPendiente;

    public PagoExcedeSaldoException(Long trabajoId, BigDecimal montoPago, BigDecimal saldoPendiente) {
        super(String.format("El pago de %.2f excede el saldo pendiente de %.2f para la orden de trabajo ID %d",
                montoPago, saldoPendiente, trabajoId));
        this.trabajoId = trabajoId;
        this.montoPago = montoPago;
        this.saldoPendiente = saldoPendiente;
    }

    public Long getTrabajoId() {
        return trabajoId;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }
}
