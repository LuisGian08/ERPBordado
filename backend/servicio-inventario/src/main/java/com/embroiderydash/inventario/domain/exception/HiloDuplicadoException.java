package com.embroiderydash.inventario.domain.exception;

public class HiloDuplicadoException extends RuntimeException {

    private final String referencia;

    public HiloDuplicadoException(String referencia) {
        super(String.format("Ya existe un hilo registrado con la referencia '%s'", referencia));
        this.referencia = referencia;
    }

    public String getReferencia() {
        return referencia;
    }
}
