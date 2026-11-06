package com.embroiderydash.pagos.domain.exception;

public class PagoNotFoundException extends RuntimeException {
    public PagoNotFoundException(Long id) { super("No se encontró el pago con ID " + id); }
}
