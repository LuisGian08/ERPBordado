package com.embroiderydash.trabajos.domain.exception;

public class TrabajoDuplicadoException extends RuntimeException {

    private final String codigoTrabajo;

    public TrabajoDuplicadoException(String codigoTrabajo) {
        super(String.format("Ya existe una orden de trabajo con el código '%s'", codigoTrabajo));
        this.codigoTrabajo = codigoTrabajo;
    }

    public String getCodigoTrabajo() {
        return codigoTrabajo;
    }
}
