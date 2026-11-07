package com.embroiderydash.trabajos.infrastructure.client;

public class DisenoDto {

    private Long id;
    private String nombre;
    private Integer cantidadPuntadas;
    private Boolean activo;

    public DisenoDto() {
    }

    public DisenoDto(Long id, String nombre, Integer cantidadPuntadas, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.cantidadPuntadas = cantidadPuntadas;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidadPuntadas() {
        return cantidadPuntadas;
    }

    public void setCantidadPuntadas(Integer cantidadPuntadas) {
        this.cantidadPuntadas = cantidadPuntadas;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
