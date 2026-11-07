package com.embroiderydash.pagos.domain.exception;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException(String key) { super("La clave de idempotencia ya fue utilizada para una solicitud diferente: " + key); }
}
