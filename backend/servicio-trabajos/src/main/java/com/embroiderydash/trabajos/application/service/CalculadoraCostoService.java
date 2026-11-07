package com.embroiderydash.trabajos.application.service;

import com.embroiderydash.trabajos.api.dto.CalcularCostoRequestDto;
import com.embroiderydash.trabajos.api.dto.CostoEstimadoDto;
import com.embroiderydash.trabajos.config.CostoProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Servicio encargado del cálculo de costos estimados de producción.
 *
 * Fórmula Inicial Paramétrica:
 * Costo Estimado = Costo Montaje Base (fijo por orden)
 *                + Costo Hilo y Aguja (puntadas / 1,000 * costoPorMilPuntadas * prendas)
 *                + Costo Mano de Obra ( (puntadas / velocidadSPM) * costoMinuto * prendas )
 *                + Costo Insumos Soporte (costoEntretela * prendas)
 *
 * NOTA DE NEGOCIO: Esta fórmula es preliminar y configurable vía application.yml.
 * Decisiones pendientes a confirmar por el taller:
 * 1. Si el costo de montaje varía por tipo de prenda (gorras vs camisas).
 * 2. Si se aplica un recargo por roturas frecuentes de hilo o cambios de color superiores a 5.
 */
@Service
public class CalculadoraCostoService {

    private final CostoProperties properties;

    public CalculadoraCostoService(CostoProperties properties) {
        this.properties = properties;
    }

    public CostoEstimadoDto calcularCosto(CalcularCostoRequestDto request) {
        return calcularCosto(request.getCantidadPuntadas(), request.getCantidadPrendas());
    }

    public CostoEstimadoDto calcularCosto(int puntadasUnitarias, int cantidadPrendas) {
        long totalPuntadas = (long) puntadasUnitarias * cantidadPrendas;

        // 1. Costo Base de Montaje y Calibración
        BigDecimal costoMontaje = properties.getCostoBaseMontaje();

        // 2. Costo de Hilos por cada 1,000 puntadas
        BigDecimal milesPuntadas = BigDecimal.valueOf(totalPuntadas)
                .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
        BigDecimal costoHilos = milesPuntadas.multiply(properties.getCostoPorMilPuntadas())
                .setScale(2, RoundingMode.HALF_UP);

        // 3. Costo de Mano de Obra / Tiempo de Máquina
        BigDecimal minutosTotales = BigDecimal.valueOf(totalPuntadas)
                .divide(BigDecimal.valueOf(properties.getVelocidadPuntadasPorMinuto()), 4, RoundingMode.HALF_UP);
        BigDecimal costoManoObra = minutosTotales.multiply(properties.getCostoMinutoManoObra())
                .setScale(2, RoundingMode.HALF_UP);

        // 4. Costo de Entretelas y Soporte
        BigDecimal costoSoporte = properties.getCostoEntretelaPorPrenda()
                .multiply(BigDecimal.valueOf(cantidadPrendas))
                .setScale(2, RoundingMode.HALF_UP);

        // 5. Costo Total Estimado
        BigDecimal costoTotal = costoMontaje
                .add(costoHilos)
                .add(costoManoObra)
                .add(costoSoporte)
                .setScale(2, RoundingMode.HALF_UP);

        // 6. Costo Unitario por Prenda
        BigDecimal costoUnitario = costoTotal.divide(BigDecimal.valueOf(cantidadPrendas), 2, RoundingMode.HALF_UP);

        CostoEstimadoDto dto = new CostoEstimadoDto();
        dto.setCantidadPrendas(cantidadPrendas);
        dto.setPuntadasUnitarias(puntadasUnitarias);
        dto.setTotalPuntadas(totalPuntadas);
        dto.setCostoBaseMontaje(costoMontaje);
        dto.setCostoHilosYPuntadas(costoHilos);
        dto.setCostoManoObraTiempo(costoManoObra);
        dto.setCostoMaterialesSoporte(costoSoporte);
        dto.setCostoTotalEstimado(costoTotal);
        dto.setCostoUnitarioPorPrenda(costoUnitario);

        return dto;
    }
}
