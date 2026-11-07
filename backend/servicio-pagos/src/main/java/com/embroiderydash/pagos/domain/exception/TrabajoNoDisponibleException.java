package com.embroiderydash.pagos.domain.exception;

public class TrabajoNoDisponibleException extends RuntimeException {
    public TrabajoNoDisponibleException(Long id, String motivo) { super("El trabajo " + id + " no está disponible para registrar pagos: " + motivo); }
}
