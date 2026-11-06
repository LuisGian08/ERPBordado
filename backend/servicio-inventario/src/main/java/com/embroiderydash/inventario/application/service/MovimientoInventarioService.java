package com.embroiderydash.inventario.application.service;

import com.embroiderydash.inventario.api.dto.MovimientoRequestDto;
import com.embroiderydash.inventario.api.dto.MovimientoResponseDto;
import com.embroiderydash.inventario.domain.exception.HiloNotFoundException;
import com.embroiderydash.inventario.domain.exception.StockInsuficienteException;
import com.embroiderydash.inventario.domain.model.Hilo;
import com.embroiderydash.inventario.domain.model.MovimientoInventario;
import com.embroiderydash.inventario.domain.repository.HiloRepository;
import com.embroiderydash.inventario.domain.repository.MovimientoInventarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoInventarioService {

    private final HiloRepository hiloRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public MovimientoInventarioService(HiloRepository hiloRepository, MovimientoInventarioRepository movimientoRepository) {
        this.hiloRepository = hiloRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Registra un movimiento de inventario de forma estrictamente transaccional.
     * Utiliza un Bloqueo Pesimista (SELECT ... FOR UPDATE) sobre el registro del hilo
     * para asegurar que dos transacciones simultáneas no causen condiciones de carrera
     * ni produzcan saldos negativos de conos.
     */
    @Transactional
    public MovimientoResponseDto registrarMovimiento(MovimientoRequestDto request, String operador) {
        // Adquisición de bloqueo pesimista a nivel de fila
        Hilo hilo = hiloRepository.findByIdForUpdate(request.getHiloId())
                .orElseThrow(() -> new HiloNotFoundException(request.getHiloId()));

        int stockPrevio = hilo.getStockActualConos();
        int stockNuevo;

        switch (request.getTipoMovimiento()) {
            case ENTRADA:
                stockNuevo = stockPrevio + request.getCantidadConos();
                break;

            case SALIDA:
                if (stockPrevio < request.getCantidadConos()) {
                    throw new StockInsuficienteException(hilo.getId(), stockPrevio, request.getCantidadConos());
                }
                stockNuevo = stockPrevio - request.getCantidadConos();
                break;

            case AJUSTE:
                // En ajuste de inventario físico, cantidadConos representa el conteo real verificado
                stockNuevo = request.getCantidadConos();
                break;

            default:
                throw new IllegalArgumentException("Tipo de movimiento desconocido: " + request.getTipoMovimiento());
        }

        hilo.setStockActualConos(stockNuevo);
        hiloRepository.save(hilo);

        MovimientoInventario movimiento = new MovimientoInventario(
                hilo,
                request.getTipoMovimiento(),
                request.getCantidadConos(),
                stockPrevio,
                stockNuevo,
                request.getMotivo().trim(),
                request.getReferenciaTrabajoId(),
                operador != null ? operador : "sistema",
                request.getTipoMovimiento() == com.embroiderydash.inventario.domain.model.TipoMovimiento.ENTRADA ? hilo.getPrecioCono() : null
        );

        MovimientoInventario guardado = movimientoRepository.save(movimiento);
        return mapToResponseDto(guardado);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDto> listarMovimientosPorHilo(Long hiloId) {
        if (!hiloRepository.existsById(hiloId)) {
            throw new HiloNotFoundException(hiloId);
        }
        return movimientoRepository.findByHiloIdOrderByFechaMovimientoDesc(hiloId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MovimientoResponseDto> listarTodos(Pageable pageable) {
        return movimientoRepository.findAllByOrderByFechaMovimientoDesc(pageable)
                .map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<MovimientoResponseDto> listarGastos(Pageable pageable) {
        return movimientoRepository.findByTipoMovimientoOrderByFechaMovimientoDesc(
                com.embroiderydash.inventario.domain.model.TipoMovimiento.ENTRADA, pageable).map(this::mapToResponseDto);
    }

    private MovimientoResponseDto mapToResponseDto(MovimientoInventario entity) {
        MovimientoResponseDto dto = new MovimientoResponseDto();
        dto.setId(entity.getId());
        dto.setHiloId(entity.getHilo().getId());
        dto.setReferenciaHilo(entity.getHilo().getReferencia());
        dto.setNombreColor(entity.getHilo().getNombreColor());
        dto.setTipoMovimiento(entity.getTipoMovimiento());
        dto.setCantidadConos(entity.getCantidadConos());
        dto.setStockPrevio(entity.getStockPrevio());
        dto.setStockNuevo(entity.getStockNuevo());
        dto.setPrecioCompraCono(entity.getPrecioCompraCono());
        dto.setGastoTotal(entity.getGastoTotal());
        dto.setMotivo(entity.getMotivo());
        dto.setReferenciaTrabajoId(entity.getReferenciaTrabajoId());
        dto.setOperador(entity.getOperador());
        dto.setFechaMovimiento(entity.getFechaMovimiento());
        return dto;
    }
}
