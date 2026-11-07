package com.embroiderydash.trabajos.domain.exception;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(Long id) { super("No se encontró el cliente seleccionado (ID " + id + ")"); }
}
