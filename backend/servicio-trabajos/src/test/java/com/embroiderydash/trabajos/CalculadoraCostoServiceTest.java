package com.embroiderydash.trabajos;

import com.embroiderydash.trabajos.api.dto.CostoEstimadoDto;
import com.embroiderydash.trabajos.application.service.CalculadoraCostoService;
import com.embroiderydash.trabajos.config.CostoProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraCostoServiceTest {

    private CalculadoraCostoService calculadoraService;

    @BeforeEach
    void setUp() {
        CostoProperties properties = new CostoProperties();
        properties.setCostoBaseMontaje(new BigDecimal("10.00"));
        properties.setCostoPorMilPuntadas(new BigDecimal("0.85"));
        properties.setCostoEntretelaPorPrenda(new BigDecimal("1.20"));
        properties.setCostoMinutoManoObra(new BigDecimal("0.35"));
        properties.setVelocidadPuntadasPorMinuto(750);

        calculadoraService = new CalculadoraCostoService(properties);
    }

    @Test
    @DisplayName("Debe calcular correctamente el desglose de costo estimado usando BigDecimal")
    void calcularCostoEstimadoCorrecto() {
        // Orden de 50 prendas, con un diseño de 10,000 puntadas cada una
        // Total puntadas = 500,000
        // Montaje = 10.00
        // Hilos = (500,000 / 1000) * 0.85 = 425.00
        // Mano de obra = (500,000 / 750) * 0.35 = 666.6667 * 0.35 = 233.33
        // Entretela = 50 * 1.20 = 60.00
        // Total estimado = 10.00 + 425.00 + 233.33 + 60.00 = 728.33
        // Costo unitario = 728.33 / 50 = 14.57

        CostoEstimadoDto resultado = calculadoraService.calcularCosto(10000, 50);

        assertNotNull(resultado);
        assertEquals(50, resultado.getCantidadPrendas());
        assertEquals(10000, resultado.getPuntadasUnitarias());
        assertEquals(500000L, resultado.getTotalPuntadas());
        assertEquals(new BigDecimal("10.00"), resultado.getCostoBaseMontaje());
        assertEquals(new BigDecimal("425.00"), resultado.getCostoHilosYPuntadas());
        assertEquals(new BigDecimal("233.33"), resultado.getCostoManoObraTiempo());
        assertEquals(new BigDecimal("60.00"), resultado.getCostoMaterialesSoporte());
        assertEquals(new BigDecimal("728.33"), resultado.getCostoTotalEstimado());
        assertEquals(new BigDecimal("14.57"), resultado.getCostoUnitarioPorPrenda());
    }
}
