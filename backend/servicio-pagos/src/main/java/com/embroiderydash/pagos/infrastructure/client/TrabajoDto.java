package com.embroiderydash.pagos.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrabajoDto {
    private Long id;
    private String codigoTrabajo;
    private String clienteNombre;
    private BigDecimal precioVenta;
    private String estado;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoTrabajo() { return codigoTrabajo; }
    public void setCodigoTrabajo(String codigoTrabajo) { this.codigoTrabajo = codigoTrabajo; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
