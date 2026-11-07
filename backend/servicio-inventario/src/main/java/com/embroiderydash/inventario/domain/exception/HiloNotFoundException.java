package com.embroiderydash.inventario.domain.exception;

public class HiloNotFoundException extends RuntimeException {

    private final Long hiloId;

    public HiloNotFoundException(Long hiloId) {
        super(String.format("El hilo con ID %d no existe o está inactivo", hiloId));
        this.hiloId = hiloId;
    }

    public HiloNotFoundException(String referencia) {
        super(String.format("No se encontró ningún hilo con la referencia '%s'", referencia));
        this.hiloId = null;
    }

    public Long getHiloId() {
        return hiloId;
    }
}
