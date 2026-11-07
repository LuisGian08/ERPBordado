package com.embroiderydash.trabajos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.math.BigDecimal;

/**
 * Propiedades configurables para el cálculo de costos de producción de bordado.
 * Permite adaptar la fórmula de costos sin modificar el código fuente.
 */
@ConfigurationProperties(prefix = "app.costos")
public class CostoProperties {

    /**
     * Costo base fijo de montaje, calibración de máquina y cambio de bastidor por orden de trabajo.
     */
    private BigDecimal costoBaseMontaje = new BigDecimal("10.00");

    /**
     * Costo estimado de hilos de bordado por cada 1,000 puntadas ejecutadas.
     */
    private BigDecimal costoPorMilPuntadas = new BigDecimal("0.85");

    /**
     * Costo de insumos secundarios por prenda (entretela desgarrable/hidrosoluble, agujas, adhesivo en spray).
     */
    private BigDecimal costoEntretelaPorPrenda = new BigDecimal("1.20");

    /**
     * Costo de mano de obra del operador por minuto de trabajo de bordadora.
     */
    private BigDecimal costoMinutoManoObra = new BigDecimal("0.35");

    /**
     * Velocidad promedio de producción en puntadas por minuto (SPM - Stitches Per Minute).
     */
    private Integer velocidadPuntadasPorMinuto = 750;

    public CostoProperties() {
    }

    public BigDecimal getCostoBaseMontaje() {
        return costoBaseMontaje;
    }

    public void setCostoBaseMontaje(BigDecimal costoBaseMontaje) {
        this.costoBaseMontaje = costoBaseMontaje;
    }

    public BigDecimal getCostoPorMilPuntadas() {
        return costoPorMilPuntadas;
    }

    public void setCostoPorMilPuntadas(BigDecimal costoPorMilPuntadas) {
        this.costoPorMilPuntadas = costoPorMilPuntadas;
    }

    public BigDecimal getCostoEntretelaPorPrenda() {
        return costoEntretelaPorPrenda;
    }

    public void setCostoEntretelaPorPrenda(BigDecimal costoEntretelaPorPrenda) {
        this.costoEntretelaPorPrenda = costoEntretelaPorPrenda;
    }

    public BigDecimal getCostoMinutoManoObra() {
        return costoMinutoManoObra;
    }

    public void setCostoMinutoManoObra(BigDecimal costoMinutoManoObra) {
        this.costoMinutoManoObra = costoMinutoManoObra;
    }

    public Integer getVelocidadPuntadasPorMinuto() {
        return velocidadPuntadasPorMinuto;
    }

    public void setVelocidadPuntadasPorMinuto(Integer velocidadPuntadasPorMinuto) {
        this.velocidadPuntadasPorMinuto = velocidadPuntadasPorMinuto;
    }
}
