package com.embroiderydash.trabajos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos para simular y calcular el costo estimado de producción de un bordado")
public class CalcularCostoRequestDto {

    @Schema(description = "Cantidad de puntadas unitarias del diseño", example = "8500")
    @NotNull(message = "La cantidad de puntadas es obligatoria")
    @Positive(message = "La cantidad de puntadas debe ser mayor a 0")
    private Integer cantidadPuntadas;

    @Schema(description = "Número total de prendas a bordar en la orden", example = "50")
    @NotNull(message = "La cantidad de prendas es obligatoria")
    @Positive(message = "La cantidad de prendas debe ser mayor a 0")
    private Integer cantidadPrendas;

    public CalcularCostoRequestDto() {
    }

    public CalcularCostoRequestDto(Integer cantidadPuntadas, Integer cantidadPrendas) {
        this.cantidadPuntadas = cantidadPuntadas;
        this.cantidadPrendas = cantidadPrendas;
    }

    public Integer getCantidadPuntadas() {
        return cantidadPuntadas;
    }

    public void setCantidadPuntadas(Integer cantidadPuntadas) {
        this.cantidadPuntadas = cantidadPuntadas;
    }

    public Integer getCantidadPrendas() {
        return cantidadPrendas;
    }

    public void setCantidadPrendas(Integer cantidadPrendas) {
        this.cantidadPrendas = cantidadPrendas;
    }
}
