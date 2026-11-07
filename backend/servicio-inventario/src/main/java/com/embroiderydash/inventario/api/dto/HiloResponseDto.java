package com.embroiderydash.inventario.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Detalle de hilo en inventario con estado de alerta de stock")
public class HiloResponseDto {

    private Long id;
    private String marca;
    private String tipoMaterial;
    private String codigoColor;
    private Boolean tieneImagen;
    private String nombreColor;
    private String referencia;
    private Integer stockActualConos;
    private Integer metrosPorCono;
    private Integer stockMinimoConos;
    private BigDecimal precioCono;
    private Boolean alertaStockMinimo;
    private Boolean activo;
    private OffsetDateTime creadoEn;
    private OffsetDateTime actualizadoEn;

    public HiloResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getCodigoColor() {
        return codigoColor;
    }

    public void setCodigoColor(String codigoColor) {
        this.codigoColor = codigoColor;
    }

    public Boolean getTieneImagen() { return tieneImagen; }
    public void setTieneImagen(Boolean tieneImagen) { this.tieneImagen = tieneImagen; }

    public String getNombreColor() {
        return nombreColor;
    }

    public void setNombreColor(String nombreColor) {
        this.nombreColor = nombreColor;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getStockActualConos() {
        return stockActualConos;
    }

    public void setStockActualConos(Integer stockActualConos) {
        this.stockActualConos = stockActualConos;
    }

    public Integer getMetrosPorCono() {
        return metrosPorCono;
    }

    public void setMetrosPorCono(Integer metrosPorCono) {
        this.metrosPorCono = metrosPorCono;
    }

    public Integer getStockMinimoConos() {
        return stockMinimoConos;
    }

    public void setStockMinimoConos(Integer stockMinimoConos) {
        this.stockMinimoConos = stockMinimoConos;
    }

    public BigDecimal getPrecioCono() {
        return precioCono;
    }

    public void setPrecioCono(BigDecimal precioCono) {
        this.precioCono = precioCono;
    }

    public Boolean getAlertaStockMinimo() {
        return alertaStockMinimo;
    }

    public void setAlertaStockMinimo(Boolean alertaStockMinimo) {
        this.alertaStockMinimo = alertaStockMinimo;
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
}
