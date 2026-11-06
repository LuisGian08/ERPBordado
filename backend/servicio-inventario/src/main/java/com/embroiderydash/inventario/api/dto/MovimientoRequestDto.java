package com.embroiderydash.inventario.api.dto;

import com.embroiderydash.inventario.domain.model.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para registrar un movimiento físico de entrada, salida o ajuste de hilos")
public class MovimientoRequestDto {

    @Schema(description = "ID del hilo a afectar", example = "1")
    @NotNull(message = "El ID del hilo es obligatorio")
    private Long hiloId;

    @Schema(description = "Tipo de movimiento de almacén", example = "SALIDA")
    @NotNull(message = "El tipo de movimiento es obligatorio (ENTRADA, SALIDA, AJUSTE)")
    private TipoMovimiento tipoMovimiento;

    @Schema(description = "Cantidad de conos a sumar o restar", example = "2")
    @NotNull(message = "La cantidad de conos es obligatoria")
    @Positive(message = "La cantidad de conos debe ser mayor a 0")
    private Integer cantidadConos;

    @Schema(description = "Motivo o justificación del movimiento", example = "Consumo en producción de trabajo #204")
    @NotBlank(message = "El motivo del movimiento es obligatorio")
    @Size(max = 255, message = "El motivo no puede exceder 255 caracteres")
    private String motivo;

    @Schema(description = "ID del trabajo de bordado relacionado (opcional)", example = "204")
    private Long referenciaTrabajoId;

    public MovimientoRequestDto() {
    }

    public MovimientoRequestDto(Long hiloId, TipoMovimiento tipoMovimiento, Integer cantidadConos, String motivo, Long referenciaTrabajoId) {
        this.hiloId = hiloId;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidadConos = cantidadConos;
        this.motivo = motivo;
        this.referenciaTrabajoId = referenciaTrabajoId;
    }

    public Long getHiloId() {
        return hiloId;
    }

    public void setHiloId(Long hiloId) {
        this.hiloId = hiloId;
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
}
