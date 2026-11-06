package com.embroiderydash.inventario.application.service;

import com.embroiderydash.inventario.api.dto.AlertaStockDto;
import com.embroiderydash.inventario.api.dto.HiloRequestDto;
import com.embroiderydash.inventario.api.dto.HiloResponseDto;
import com.embroiderydash.inventario.domain.exception.HiloDuplicadoException;
import com.embroiderydash.inventario.domain.exception.HiloNotFoundException;
import com.embroiderydash.inventario.domain.model.Hilo;
import com.embroiderydash.inventario.domain.repository.HiloRepository;
import com.embroiderydash.inventario.domain.repository.MovimientoInventarioRepository;
import com.embroiderydash.inventario.domain.model.MovimientoInventario;
import com.embroiderydash.inventario.domain.model.TipoMovimiento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;
import java.util.stream.Collectors;

@Service
public class HiloService {

    private final HiloRepository hiloRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public HiloService(HiloRepository hiloRepository, MovimientoInventarioRepository movimientoRepository) {
        this.hiloRepository = hiloRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public List<HiloResponseDto> listarTodos() {
        return hiloRepository.findAllActivos().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HiloResponseDto obtenerPorId(Long id) {
        Hilo hilo = hiloRepository.findActivoById(id)
                .orElseThrow(() -> new HiloNotFoundException(id));
        return mapToResponseDto(hilo);
    }

    @Transactional(readOnly = true)
    public HiloResponseDto obtenerPorReferencia(String referencia) {
        Hilo hilo = hiloRepository.findByReferenciaIgnoreCase(referencia.trim())
                .orElseThrow(() -> new HiloNotFoundException(referencia.trim()));
        return mapToResponseDto(hilo);
    }

    @Transactional
    public HiloResponseDto crearHilo(HiloRequestDto request) {
        String codigoColor = request.getCodigoColor().trim();
        String referenciaTrim = request.getReferencia() == null || request.getReferencia().isBlank()
                ? codigoColor : request.getReferencia().trim();
        if (hiloRepository.existsByReferenciaIgnoreCase(referenciaTrim)) {
            throw new HiloDuplicadoException(referenciaTrim);
        }

        Hilo hilo = new Hilo();
        hilo.setMarca(request.getMarca() == null || request.getMarca().isBlank() ? "Taller" : request.getMarca().trim());
        hilo.setTipoMaterial(request.getTipoMaterial() == null || request.getTipoMaterial().isBlank() ? "Hilo de bordado" : request.getTipoMaterial().trim());
        hilo.setCodigoColor(codigoColor);
        hilo.setNombreColor(request.getNombreColor().trim());
        hilo.setReferencia(referenciaTrim);
        hilo.setStockActualConos(request.getStockActualConos());
        hilo.setMetrosPorCono(request.getMetrosPorCono() == null ? 5000 : request.getMetrosPorCono());
        hilo.setStockMinimoConos(request.getStockMinimoConos() == null ? 5 : request.getStockMinimoConos());
        hilo.setPrecioCono(request.getPrecioCono());
        hilo.setActivo(true);

        Hilo guardado = hiloRepository.save(hilo);
        if (guardado.getStockActualConos() > 0) {
            movimientoRepository.save(new MovimientoInventario(guardado, TipoMovimiento.ENTRADA,
                    guardado.getStockActualConos(), 0, guardado.getStockActualConos(), "Stock inicial", null, "sistema", guardado.getPrecioCono()));
        }
        return mapToResponseDto(guardado);
    }

    @Transactional
    public HiloResponseDto actualizarHilo(Long id, HiloRequestDto request) {
        Hilo hilo = hiloRepository.findActivoById(id)
                .orElseThrow(() -> new HiloNotFoundException(id));

        String refTrim = request.getReferencia() == null || request.getReferencia().isBlank()
                ? hilo.getReferencia() : request.getReferencia().trim();
        if (hiloRepository.existsByReferenciaIgnoreCaseAndIdNot(refTrim, id)) {
            throw new HiloDuplicadoException(refTrim);
        }

        hilo.setMarca(request.getMarca() == null || request.getMarca().isBlank() ? hilo.getMarca() : request.getMarca().trim());
        hilo.setTipoMaterial(request.getTipoMaterial() == null || request.getTipoMaterial().isBlank() ? hilo.getTipoMaterial() : request.getTipoMaterial().trim());
        hilo.setCodigoColor(request.getCodigoColor().trim());
        hilo.setNombreColor(request.getNombreColor().trim());
        hilo.setReferencia(refTrim);
        if (request.getMetrosPorCono() != null) hilo.setMetrosPorCono(request.getMetrosPorCono());
        if (request.getStockMinimoConos() != null) hilo.setStockMinimoConos(request.getStockMinimoConos());
        hilo.setPrecioCono(request.getPrecioCono());

        Hilo actualizado = hiloRepository.save(hilo);
        return mapToResponseDto(actualizado);
    }

    @Transactional
    public void eliminarHilo(Long id) {
        Hilo hilo = hiloRepository.findActivoById(id)
                .orElseThrow(() -> new HiloNotFoundException(id));
        hilo.setActivo(false);
        hiloRepository.save(hilo);
    }

    @Transactional(readOnly = true)
    public List<AlertaStockDto> obtenerAlertasStockMinimo() {
        return hiloRepository.findHilosBajoStockMinimo().stream()
                .map(h -> new AlertaStockDto(
                        h.getId(),
                        h.getMarca(),
                        h.getReferencia(),
                        h.getCodigoColor(),
                        h.getNombreColor(),
                        h.getStockActualConos(),
                        h.getStockMinimoConos()
                ))
                .collect(Collectors.toList());
    }

    public HiloResponseDto mapToResponseDto(Hilo hilo) {
        HiloResponseDto dto = new HiloResponseDto();
        dto.setId(hilo.getId());
        dto.setMarca(hilo.getMarca());
        dto.setTipoMaterial(hilo.getTipoMaterial());
        dto.setCodigoColor(hilo.getCodigoColor());
        dto.setTieneImagen(tieneImagenDisponible(hilo));
        dto.setNombreColor(hilo.getNombreColor());
        dto.setReferencia(hilo.getReferencia());
        dto.setStockActualConos(hilo.getStockActualConos());
        dto.setMetrosPorCono(hilo.getMetrosPorCono());
        dto.setStockMinimoConos(hilo.getStockMinimoConos());
        dto.setPrecioCono(hilo.getPrecioCono());
        dto.setAlertaStockMinimo(hilo.tieneAlertaStockMinimo());
        dto.setActivo(hilo.getActivo());
        dto.setCreadoEn(hilo.getCreadoEn());
        dto.setActualizadoEn(hilo.getActualizadoEn());
        return dto;
    }

    private boolean tieneImagenDisponible(Hilo hilo) {
        String ruta = hilo.getImagenRuta();
        if (ruta == null || ruta.isBlank()) return false;
        try { return Files.isRegularFile(Path.of(ruta)); }
        catch (InvalidPathException ex) { return false; }
    }
}
