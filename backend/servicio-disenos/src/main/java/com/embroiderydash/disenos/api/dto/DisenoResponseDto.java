package com.embroiderydash.disenos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Respuesta detallada con información del diseño de bordado")
public class DisenoResponseDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer cantidadPuntadas;
    private BigDecimal anchoMm;
    private BigDecimal altoMm;
    private String formatosAdmitidos;
    private String observacionesTecnicas;
    private Boolean activo;
    private OffsetDateTime creadoEn;
    private OffsetDateTime actualizadoEn;
    private List<ColorHiloDto> colores = new ArrayList<>();
    private List<ArchivoTecnicoDto> archivos = new ArrayList<>();

    public DisenoResponseDto() {
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidadPuntadas() {
        return cantidadPuntadas;
    }

    public void setCantidadPuntadas(Integer cantidadPuntadas) {
        this.cantidadPuntadas = cantidadPuntadas;
    }

    public BigDecimal getAnchoMm() {
        return anchoMm;
    }

    public void setAnchoMm(BigDecimal anchoMm) {
        this.anchoMm = anchoMm;
    }

    public BigDecimal getAltoMm() {
        return altoMm;
    }

    public void setAltoMm(BigDecimal altoMm) {
        this.altoMm = altoMm;
    }

    public String getFormatosAdmitidos() {
        return formatosAdmitidos;
    }

    public void setFormatosAdmitidos(String formatosAdmitidos) {
        this.formatosAdmitidos = formatosAdmitidos;
    }

    public String getObservacionesTecnicas() {
        return observacionesTecnicas;
    }

    public void setObservacionesTecnicas(String observacionesTecnicas) {
        this.observacionesTecnicas = observacionesTecnicas;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public OffsetDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(OffsetDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public List<ColorHiloDto> getColores() {
        return colores;
    }

    public void setColores(List<ColorHiloDto> colores) {
        this.colores = colores;
    }

    public List<ArchivoTecnicoDto> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<ArchivoTecnicoDto> archivos) {
        this.archivos = archivos;
    }
}
