package com.embroiderydash.trabajos.api.dto;

import com.embroiderydash.trabajos.domain.model.EstadoTrabajo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Detalle completo de orden de trabajo con desglose financiero de costos y margen")
public class TrabajoResponseDto {

    private Long id;
    private String codigoTrabajo;
    private String clienteNombre;
    private String clienteContacto;
    private Long clienteId;
    private Long disenoId;
    private String nombreDisenoHistorico;
    private Integer cantidadPrendas;
    private Integer puntadasUnitarias;
    private Long totalPuntadas;
    private BigDecimal precioVenta;
    private BigDecimal costoEstimado;
    private BigDecimal costoReal;
    private BigDecimal margenEstimado;
    private BigDecimal porcentajeMargenEstimado;
    private EstadoTrabajo estado;
    private OffsetDateTime fechaCompromiso;
    private String observaciones;
    private OffsetDateTime creadoEn;
    private OffsetDateTime actualizadoEn;

    public TrabajoResponseDto() {
    }

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

    public BigDecimal getMargenEstimado() {
        return margenEstimado;
    }

    public void setMargenEstimado(BigDecimal margenEstimado) {
        this.margenEstimado = margenEstimado;
    }

    public BigDecimal getPorcentajeMargenEstimado() {
        return porcentajeMargenEstimado;
    }

    public void setPorcentajeMargenEstimado(BigDecimal porcentajeMargenEstimado) {
        this.porcentajeMargenEstimado = porcentajeMargenEstimado;
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
