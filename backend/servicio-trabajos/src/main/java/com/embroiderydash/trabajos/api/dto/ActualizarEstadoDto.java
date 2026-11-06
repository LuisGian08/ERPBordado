package com.embroiderydash.trabajos.api.dto;

import com.embroiderydash.trabajos.domain.model.EstadoTrabajo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para realizar la transición de estado de una orden de trabajo")
public class ActualizarEstadoDto {

    @Schema(description = "Nuevo estado del trabajo", example = "EN_PROCESO")
    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoTrabajo nuevoEstado;

    public ActualizarEstadoDto() {
    }

    public ActualizarEstadoDto(EstadoTrabajo nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }

    public EstadoTrabajo getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(EstadoTrabajo nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }
}
