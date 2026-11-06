package com.embroiderydash.disenos.domain.exception;

public class DisenoNotFoundException extends RuntimeException {

    private final Long disenoId;

    public DisenoNotFoundException(Long disenoId) {
        super(String.format("El diseño de bordado con ID %d no fue encontrado o está inactivo", disenoId));
        this.disenoId = disenoId;
    }

    public Long getDisenoId() {
        return disenoId;
    }
}
