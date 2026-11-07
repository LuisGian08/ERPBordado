package com.embroiderydash.trabajos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import com.embroiderydash.trabajos.domain.model.EstadoTrabajo;

@Schema(description = "Datos requeridos para registrar una nueva orden de trabajo de bordado")
public class TrabajoRequestDto {

    @Schema(description = "Código único o identificador de orden de trabajo", example = "TRAB-2026-004")
    @NotBlank(message = "El código de trabajo es obligatorio")
    @Size(max = 50, message = "El código de trabajo no puede exceder 50 caracteres")
    private String codigoTrabajo;

    @Schema(description = "Nombre o razón social del cliente", example = "Restaurante La Casona")
    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 150, message = "El nombre del cliente no puede exceder 150 caracteres")
    private String clienteNombre;

    @Schema(description = "Teléfono o correo de contacto", example = "+51 987 654 321")
    @Size(max = 100, message = "El contacto no puede exceder 100 caracteres")
    private String clienteContacto;

    private Long clienteId;

    @Schema(description = "ID del diseño de bordado registrado previamente", example = "1")
    @NotNull(message = "El ID del diseño es obligatorio")
    private Long disenoId;

    @Schema(description = "Cantidad total de prendas a bordar", example = "25")
    @NotNull(message = "La cantidad de prendas es obligatoria")
    @Positive(message = "La cantidad de prendas debe ser mayor a 0")
    private Integer cantidadPrendas;

    @Schema(description = "Precio total de venta pactado con el cliente", example = "650.00")
    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor a 0")
    private BigDecimal precioVenta;

    @Schema(description = "Estado inicial de producción", example = "TERMINADO")
    private EstadoTrabajo estado;

    @Schema(description = "Fecha y hora límite de entrega o compromiso")
    private OffsetDateTime fechaCompromiso;

    @Schema(description = "Notas técnicas u observaciones del pedido")
    private String observaciones;

    public TrabajoRequestDto() {
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

    public Integer getCantidadPrendas() {
        return cantidadPrendas;
    }

    public void setCantidadPrendas(Integer cantidadPrendas) {
        this.cantidadPrendas = cantidadPrendas;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
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
}
