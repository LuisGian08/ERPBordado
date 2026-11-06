package com.embroiderydash.inventario.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Alerta temprana de inventario crítico para reposición de hilos")
public class AlertaStockDto {

    private Long hiloId;
    private String marca;
    private String referencia;
    private String codigoColor;
    private String nombreColor;
    private Integer stockActualConos;
    private Integer stockMinimoConos;
    private Integer deficitConos;

    public AlertaStockDto() {
    }

    public AlertaStockDto(Long hiloId, String marca, String referencia, String codigoColor, String nombreColor, Integer stockActualConos, Integer stockMinimoConos) {
        this.hiloId = hiloId;
        this.marca = marca;
        this.referencia = referencia;
        this.codigoColor = codigoColor;
        this.nombreColor = nombreColor;
        this.stockActualConos = stockActualConos;
        this.stockMinimoConos = stockMinimoConos;
        this.deficitConos = Math.max(0, stockMinimoConos - stockActualConos);
    }

    public Long getHiloId() {
        return hiloId;
    }

    public void setHiloId(Long hiloId) {
        this.hiloId = hiloId;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCodigoColor() {
        return codigoColor;
    }

    public void setCodigoColor(String codigoColor) {
        this.codigoColor = codigoColor;
    }

    public String getNombreColor() {
        return nombreColor;
    }

    public void setNombreColor(String nombreColor) {
        this.nombreColor = nombreColor;
    }

    public Integer getStockActualConos() {
        return stockActualConos;
    }

    public void setStockActualConos(Integer stockActualConos) {
        this.stockActualConos = stockActualConos;
    }

    public Integer getStockMinimoConos() {
        return stockMinimoConos;
    }

    public void setStockMinimoConos(Integer stockMinimoConos) {
        this.stockMinimoConos = stockMinimoConos;
    }

    public Integer getDeficitConos() {
        return deficitConos;
    }

    public void setDeficitConos(Integer deficitConos) {
        this.deficitConos = deficitConos;
    }
}
