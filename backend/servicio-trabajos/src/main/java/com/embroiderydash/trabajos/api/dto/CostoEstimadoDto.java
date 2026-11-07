package com.embroiderydash.trabajos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Desglose analítico del cálculo de costo estimado de producción")
public class CostoEstimadoDto {

    private Integer cantidadPrendas;
    private Integer puntadasUnitarias;
    private Long totalPuntadas;
    private BigDecimal costoBaseMontaje;
    private BigDecimal costoHilosYPuntadas;
    private BigDecimal costoManoObraTiempo;
    private BigDecimal costoMaterialesSoporte;
    private BigDecimal costoTotalEstimado;
    private BigDecimal costoUnitarioPorPrenda;

    public CostoEstimadoDto() {
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

    public BigDecimal getCostoBaseMontaje() {
        return costoBaseMontaje;
    }

    public void setCostoBaseMontaje(BigDecimal costoBaseMontaje) {
        this.costoBaseMontaje = costoBaseMontaje;
    }

    public BigDecimal getCostoHilosYPuntadas() {
        return costoHilosYPuntadas;
    }

    public void setCostoHilosYPuntadas(BigDecimal costoHilosYPuntadas) {
        this.costoHilosYPuntadas = costoHilosYPuntadas;
    }

    public BigDecimal getCostoManoObraTiempo() {
        return costoManoObraTiempo;
    }

    public void setCostoManoObraTiempo(BigDecimal costoManoObraTiempo) {
        this.costoManoObraTiempo = costoManoObraTiempo;
    }

    public BigDecimal getCostoMaterialesSoporte() {
        return costoMaterialesSoporte;
    }

    public void setCostoMaterialesSoporte(BigDecimal costoMaterialesSoporte) {
        this.costoMaterialesSoporte = costoMaterialesSoporte;
    }

    public BigDecimal getCostoTotalEstimado() {
        return costoTotalEstimado;
    }

    public void setCostoTotalEstimado(BigDecimal costoTotalEstimado) {
        this.costoTotalEstimado = costoTotalEstimado;
    }

    public BigDecimal getCostoUnitarioPorPrenda() {
        return costoUnitarioPorPrenda;
    }

    public void setCostoUnitarioPorPrenda(BigDecimal costoUnitarioPorPrenda) {
        this.costoUnitarioPorPrenda = costoUnitarioPorPrenda;
    }
}
