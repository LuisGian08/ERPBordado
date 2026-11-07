package com.embroiderydash.trabajos.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trabajos_bordado")
public class TrabajoBordado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_trabajo", nullable = false, length = 50, unique = true)
    private String codigoTrabajo;

    @Column(name = "cliente_nombre", nullable = false, length = 150)
    private String clienteNombre;

    @Column(name = "cliente_contacto", length = 100)
    private String clienteContacto;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "diseno_id", nullable = false)
    private Long disenoId;

    @Column(name = "nombre_diseno_historico", nullable = false, length = 150)
    private String nombreDisenoHistorico;

    @Column(name = "cantidad_prendas", nullable = false)
    private Integer cantidadPrendas;

    @Column(name = "puntadas_unitarias", nullable = false)
    private Integer puntadasUnitarias;

    @Column(name = "total_puntadas", nullable = false)
    private Long totalPuntadas;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "costo_estimado", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoEstimado;

    @Column(name = "costo_real", precision = 12, scale = 2)
    private BigDecimal costoReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoTrabajo estado = EstadoTrabajo.CONFIRMADO;

    @Column(name = "fecha_compromiso")
    private OffsetDateTime fechaCompromiso;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    public TrabajoBordado() {
    }

    @PrePersist
    protected void onCreate() {
        this.creadoEn = OffsetDateTime.now();
        this.actualizadoEn = this.creadoEn;
        if (this.estado == null) {
            this.estado = EstadoTrabajo.CONFIRMADO;
        }
        if (this.cantidadPrendas != null && this.puntadasUnitarias != null) {
            this.totalPuntadas = (long) this.cantidadPrendas * this.puntadasUnitarias;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = OffsetDateTime.now();
        if (this.cantidadPrendas != null && this.puntadasUnitarias != null) {
            this.totalPuntadas = (long) this.cantidadPrendas * this.puntadasUnitarias;
        }
    }

    public BigDecimal calcularMargenEstimado() {
        if (precioVenta == null || costoEstimado == null) {
            return BigDecimal.ZERO;
        }
        return precioVenta.subtract(costoEstimado);
    }

    public BigDecimal calcularPorcentajeMargenEstimado() {
        if (precioVenta == null || precioVenta.compareTo(BigDecimal.ZERO) == 0 || costoEstimado == null) {
            return BigDecimal.ZERO;
        }
        return calcularMargenEstimado()
                .multiply(BigDecimal.valueOf(100))
                .divide(precioVenta, 2, RoundingMode.HALF_UP);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoTrabajo() {
        return codigoTrabajo;
    }

    public void setCodigoTrabajo(String codigoTrabajo) {
        this.codigoTrabajo = codigoTrabajo;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteContacto() {
        return clienteContacto;
    }

    public void setClienteContacto(String clienteContacto) {
        this.clienteContacto = clienteContacto;
    }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getDisenoId() {
        return disenoId;
    }

    public void setDisenoId(Long disenoId) {
        this.disenoId = disenoId;
    }

    public String getNombreDisenoHistorico() {
        return nombreDisenoHistorico;
    }

    public void setNombreDisenoHistorico(String nombreDisenoHistorico) {
        this.nombreDisenoHistorico = nombreDisenoHistorico;
    }

    public Integer getCantidadPrendas() {
        return cantidadPrendas;
    }

    public void setCantidadPrendas(Integer cantidadPrendas) {
        this.cantidadPrendas = cantidadPrendas;
    }

    public Integer getPuntadasUnitarias() {
        return puntadasUnitarias;
    }

    public void setPuntadasUnitarias(Integer puntadasUnitarias) {
        this.puntadasUnitarias = puntadasUnitarias;
    }

    public Long getTotalPuntadas() {
        return totalPuntadas;
    }

    public void setTotalPuntadas(Long totalPuntadas) {
        this.totalPuntadas = totalPuntadas;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getCostoEstimado() {
        return costoEstimado;
    }

    public void setCostoEstimado(BigDecimal costoEstimado) {
        this.costoEstimado = costoEstimado;
    }

    public BigDecimal getCostoReal() {
        return costoReal;
    }

    public void setCostoReal(BigDecimal costoReal) {
        this.costoReal = costoReal;
    }

    public EstadoTrabajo getEstado() {
        return estado;
    }

    public void setEstado(EstadoTrabajo estado) {
        this.estado = estado;
    }

    public OffsetDateTime getFechaCompromiso() {
        return fechaCompromiso;
    }

    public void setFechaCompromiso(OffsetDateTime fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
