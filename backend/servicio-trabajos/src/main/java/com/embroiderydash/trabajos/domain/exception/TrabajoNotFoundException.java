package com.embroiderydash.trabajos.domain.exception;

public class TrabajoNotFoundException extends RuntimeException {

    private final Long trabajoId;

    public TrabajoNotFoundException(Long trabajoId) {
        super(String.format("La orden de trabajo con ID %d no fue encontrada", trabajoId));
        this.trabajoId = trabajoId;
    }

    public TrabajoNotFoundException(String codigoTrabajo) {
        super(String.format("No se encontró ninguna orden de trabajo con el código '%s'", codigoTrabajo));
        this.trabajoId = null;
    }

    public Long getTrabajoId() {
        return trabajoId;
    }
}
