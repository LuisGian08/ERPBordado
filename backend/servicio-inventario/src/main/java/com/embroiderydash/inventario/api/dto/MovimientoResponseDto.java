package com.embroiderydash.inventario.api.dto;

import com.embroiderydash.inventario.domain.model.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.math.BigDecimal;

@Schema(description = "Registro inmutable del movimiento de almacén realizado")
public class MovimientoResponseDto {

    private Long id;
    private Long hiloId;
    private String referenciaHilo;
    private String nombreColor;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidadConos;
    private Integer stockPrevio;
    private Integer stockNuevo;
    private BigDecimal precioCompraCono;
    private BigDecimal gastoTotal;
    private String motivo;
    private Long referenciaTrabajoId;
    private String operador;
    private OffsetDateTime fechaMovimiento;

    public MovimientoResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHiloId() {
        return hiloId;
    }

    public void setHiloId(Long hiloId) {
        this.hiloId = hiloId;
    }

    public String getReferenciaHilo() {
        return referenciaHilo;
    }

    public void setReferenciaHilo(String referenciaHilo) {
        this.referenciaHilo = referenciaHilo;
    }

    public String getNombreColor() {
        return nombreColor;
    }

    public void setNombreColor(String nombreColor) {
        this.nombreColor = nombreColor;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidadConos() {
        return cantidadConos;
    }

    public void setCantidadConos(Integer cantidadConos) {
        this.cantidadConos = cantidadConos;
    }

    public Integer getStockPrevio() {
        return stockPrevio;
    }

    public void setStockPrevio(Integer stockPrevio) {
        this.stockPrevio = stockPrevio;
    }

    public Integer getStockNuevo() {
        return stockNuevo;
    }

    public void setStockNuevo(Integer stockNuevo) {
        this.stockNuevo = stockNuevo;
    }
    public BigDecimal getPrecioCompraCono() { return precioCompraCono; }
    public void setPrecioCompraCono(BigDecimal precioCompraCono) { this.precioCompraCono = precioCompraCono; }
    public BigDecimal getGastoTotal() { return gastoTotal; }
    public void setGastoTotal(BigDecimal gastoTotal) { this.gastoTotal = gastoTotal; }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getReferenciaTrabajoId() {
        return referenciaTrabajoId;
    }

    public void setReferenciaTrabajoId(Long referenciaTrabajoId) {
        this.referenciaTrabajoId = referenciaTrabajoId;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public OffsetDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(OffsetDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
}
