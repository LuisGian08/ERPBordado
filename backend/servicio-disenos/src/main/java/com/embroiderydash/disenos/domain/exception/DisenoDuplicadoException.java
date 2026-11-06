package com.embroiderydash.disenos.domain.exception;

public class DisenoDuplicadoException extends RuntimeException {

    private final String nombre;

    public DisenoDuplicadoException(String nombre) {
        super(String.format("Ya existe un diseño registrado con el nombre '%s'", nombre));
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
