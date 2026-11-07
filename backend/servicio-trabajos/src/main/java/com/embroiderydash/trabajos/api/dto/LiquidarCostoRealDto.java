package com.embroiderydash.trabajos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Datos para asentar el costo real incurrido tras la producción física del bordado")
public class LiquidarCostoRealDto {

    @Schema(description = "Costo real medido tras la producción (hilos reales consumidos + tiempo real + mermas)", example = "880.00")
    @NotNull(message = "El costo real es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo real no puede ser negativo")
    private BigDecimal costoReal;

    @Schema(description = "Observaciones o justificación de desviaciones respecto al costo estimado")
    private String notasLiquidacion;

    public LiquidarCostoRealDto() {
    }

    public LiquidarCostoRealDto(BigDecimal costoReal, String notasLiquidacion) {
        this.costoReal = costoReal;
        this.notasLiquidacion = notasLiquidacion;
    }

    public BigDecimal getCostoReal() {
        return costoReal;
    }

    public void setCostoReal(BigDecimal costoReal) {
        this.costoReal = costoReal;
    }

    public String getNotasLiquidacion() {
        return notasLiquidacion;
    }

    public void setNotasLiquidacion(String notasLiquidacion) {
        this.notasLiquidacion = notasLiquidacion;
    }
}
