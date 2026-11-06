package com.embroiderydash.inventario.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hilo_id", nullable = false)
    @JsonIgnore
    private Hilo hilo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "cantidad_conos", nullable = false)
    private Integer cantidadConos;

    @Column(name = "stock_previo", nullable = false)
    private Integer stockPrevio;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(name = "precio_compra_cono", precision = 10, scale = 2)
    private BigDecimal precioCompraCono;

    @Column(name = "gasto_total", precision = 12, scale = 2)
    private BigDecimal gastoTotal;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(name = "referencia_trabajo_id")
    private Long referenciaTrabajoId;

    @Column(nullable = false, length = 100)
    private String operador;

    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private OffsetDateTime fechaMovimiento;

    public MovimientoInventario() {
    }

    @PrePersist
    protected void onCreate() {
        this.fechaMovimiento = OffsetDateTime.now();
    }

    public MovimientoInventario(Hilo hilo, TipoMovimiento tipoMovimiento, Integer cantidadConos, Integer stockPrevio, Integer stockNuevo, String motivo, Long referenciaTrabajoId, String operador) {
        this(hilo, tipoMovimiento, cantidadConos, stockPrevio, stockNuevo, motivo, referenciaTrabajoId, operador, null);
    }

    public MovimientoInventario(Hilo hilo, TipoMovimiento tipoMovimiento, Integer cantidadConos, Integer stockPrevio, Integer stockNuevo, String motivo, Long referenciaTrabajoId, String operador, BigDecimal precioCompraCono) {
        this.hilo = hilo;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidadConos = cantidadConos;
        this.stockPrevio = stockPrevio;
        this.stockNuevo = stockNuevo;
        this.motivo = motivo;
        this.referenciaTrabajoId = referenciaTrabajoId;
        this.operador = operador;
        this.precioCompraCono = tipoMovimiento == TipoMovimiento.ENTRADA ? precioCompraCono : null;
        this.gastoTotal = tipoMovimiento == TipoMovimiento.ENTRADA && precioCompraCono != null
                ? precioCompraCono.multiply(BigDecimal.valueOf(cantidadConos)) : null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Hilo getHilo() {
        return hilo;
    }

    public void setHilo(Hilo hilo) {
        this.hilo = hilo;
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
