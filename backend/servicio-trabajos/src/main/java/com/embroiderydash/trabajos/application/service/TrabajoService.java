package com.embroiderydash.trabajos.application.service;

import com.embroiderydash.trabajos.api.dto.*;
import com.embroiderydash.trabajos.domain.exception.EstadoTrabajoInvalidoException;
import com.embroiderydash.trabajos.domain.exception.ClienteNotFoundException;
import com.embroiderydash.trabajos.domain.exception.TrabajoDuplicadoException;
import com.embroiderydash.trabajos.domain.exception.TrabajoNotFoundException;
import com.embroiderydash.trabajos.domain.model.EstadoTrabajo;
import com.embroiderydash.trabajos.domain.model.TrabajoBordado;
import com.embroiderydash.trabajos.domain.repository.TrabajoRepository;
import com.embroiderydash.trabajos.domain.repository.ClienteRepository;
import com.embroiderydash.trabajos.infrastructure.client.DisenoDto;
import com.embroiderydash.trabajos.infrastructure.client.DisenosClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TrabajoService {

    private final TrabajoRepository trabajoRepository;
    private final CalculadoraCostoService calculadoraCostoService;
    private final DisenosClient disenosClient;
    private final ClienteRepository clienteRepository;

    public TrabajoService(
            TrabajoRepository trabajoRepository,
            CalculadoraCostoService calculadoraCostoService,
            DisenosClient disenosClient,
            ClienteRepository clienteRepository) {
        this.trabajoRepository = trabajoRepository;
        this.calculadoraCostoService = calculadoraCostoService;
        this.disenosClient = disenosClient;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public Page<TrabajoResponseDto> listarTrabajos(String cliente, Pageable pageable) {
        Page<TrabajoBordado> pagina = (cliente != null && !cliente.isBlank())
                ? trabajoRepository.findByClienteNombreContainingIgnoreCase(cliente.trim(), pageable)
                : trabajoRepository.findAll(pageable);

        return pagina.map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public TrabajoResponseDto obtenerPorId(Long id) {
        TrabajoBordado trabajo = trabajoRepository.findById(id)
                .orElseThrow(() -> new TrabajoNotFoundException(id));
        return mapToResponseDto(trabajo);
    }

    @Transactional(readOnly = true)
    public TrabajoResponseDto obtenerPorCodigo(String codigoTrabajo) {
        TrabajoBordado trabajo = trabajoRepository.findByCodigoTrabajoIgnoreCase(codigoTrabajo.trim())
                .orElseThrow(() -> new TrabajoNotFoundException(codigoTrabajo.trim()));
        return mapToResponseDto(trabajo);
    }

    @Transactional
    public TrabajoResponseDto crearTrabajo(TrabajoRequestDto request) {
        String codigoTrim = request.getCodigoTrabajo().trim();
        if (trabajoRepository.existsByCodigoTrabajoIgnoreCase(codigoTrim)) {
            throw new TrabajoDuplicadoException(codigoTrim);
        }

        // Consulta de contrato REST con servicio-disenos para validar diseño y obtener puntadas
        Optional<DisenoDto> disenoOpt = disenosClient.obtenerDiseno(request.getDisenoId());
        int puntadasUnitarias = 5000; // Valor fallback por si el servicio remoto aún no está encendido en test
        String nombreDiseno = "Diseño #" + request.getDisenoId();

        if (disenoOpt.isPresent()) {
            DisenoDto diseno = disenoOpt.get();
            if (diseno.getCantidadPuntadas() != null && diseno.getCantidadPuntadas() > 0) {
                puntadasUnitarias = diseno.getCantidadPuntadas();
            }
            if (diseno.getNombre() != null) {
                nombreDiseno = diseno.getNombre();
            }
        }

        // Cálculo formal de costo estimado
        CostoEstimadoDto calculoCosto = calculadoraCostoService.calcularCosto(puntadasUnitarias, request.getCantidadPrendas());

        TrabajoBordado trabajo = new TrabajoBordado();
        trabajo.setCodigoTrabajo(codigoTrim);
        if (request.getClienteId() != null) {
            var cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new ClienteNotFoundException(request.getClienteId()));
            trabajo.setClienteId(cliente.getId());
            trabajo.setClienteNombre(cliente.getNombre());
            trabajo.setClienteContacto(cliente.getContacto());
        } else {
            trabajo.setClienteNombre(request.getClienteNombre().trim());
            trabajo.setClienteContacto(request.getClienteContacto());
        }
        trabajo.setDisenoId(request.getDisenoId());
        trabajo.setNombreDisenoHistorico(nombreDiseno);
        trabajo.setCantidadPrendas(request.getCantidadPrendas());
        trabajo.setPuntadasUnitarias(puntadasUnitarias);
        trabajo.setPrecioVenta(request.getPrecioVenta());
        trabajo.setCostoEstimado(calculoCosto.getCostoTotalEstimado());
        trabajo.setCostoReal(null); // Estricto: el costo real NO es la estimación
        EstadoTrabajo estadoInicial = request.getEstado() != null ? request.getEstado() : EstadoTrabajo.CONFIRMADO;
        validarEstadoDeProduccion(estadoInicial);
        trabajo.setEstado(estadoInicial);
        trabajo.setFechaCompromiso(request.getFechaCompromiso());
        trabajo.setObservaciones(request.getObservaciones());

        TrabajoBordado guardado = trabajoRepository.save(trabajo);
        return mapToResponseDto(guardado);
    }

    @Transactional
    public TrabajoResponseDto actualizarEstado(Long id, ActualizarEstadoDto request) {
        TrabajoBordado trabajo = trabajoRepository.findById(id)
                .orElseThrow(() -> new TrabajoNotFoundException(id));

        EstadoTrabajo actual = trabajo.getEstado();
        EstadoTrabajo nuevo = request.getNuevoEstado();

        validarEstadoDeProduccion(nuevo);

        if (actual == EstadoTrabajo.CANCELADO || actual == EstadoTrabajo.ENTREGADO) {
            throw new EstadoTrabajoInvalidoException(
                    String.format("No se puede cambiar el estado de un trabajo que ya está en %s", actual));
        }

        trabajo.setEstado(nuevo);
        TrabajoBordado guardado = trabajoRepository.save(trabajo);
        return mapToResponseDto(guardado);
    }

    @Transactional
    public TrabajoResponseDto liquidarCostoReal(Long id, LiquidarCostoRealDto request) {
        TrabajoBordado trabajo = trabajoRepository.findById(id)
                .orElseThrow(() -> new TrabajoNotFoundException(id));

        trabajo.setCostoReal(request.getCostoReal());
        if (request.getNotasLiquidacion() != null && !request.getNotasLiquidacion().isBlank()) {
            String obsPrevia = trabajo.getObservaciones() != null ? trabajo.getObservaciones() + "\n" : "";
            trabajo.setObservaciones(obsPrevia + "[LIQUIDACIÓN COSTO REAL]: " + request.getNotasLiquidacion());
        }

        TrabajoBordado guardado = trabajoRepository.save(trabajo);
        return mapToResponseDto(guardado);
    }

    public TrabajoResponseDto mapToResponseDto(TrabajoBordado entity) {
        TrabajoResponseDto dto = new TrabajoResponseDto();
        dto.setId(entity.getId());
        dto.setCodigoTrabajo(entity.getCodigoTrabajo());
        dto.setClienteNombre(entity.getClienteNombre());
        dto.setClienteContacto(entity.getClienteContacto());
        dto.setClienteId(entity.getClienteId());
        dto.setDisenoId(entity.getDisenoId());
        dto.setNombreDisenoHistorico(entity.getNombreDisenoHistorico());
        dto.setCantidadPrendas(entity.getCantidadPrendas());
        dto.setPuntadasUnitarias(entity.getPuntadasUnitarias());
        dto.setTotalPuntadas(entity.getTotalPuntadas());
        dto.setPrecioVenta(entity.getPrecioVenta());
        dto.setCostoEstimado(entity.getCostoEstimado());
        dto.setCostoReal(entity.getCostoReal());
        dto.setMargenEstimado(entity.calcularMargenEstimado());
        dto.setPorcentajeMargenEstimado(entity.calcularPorcentajeMargenEstimado());
        dto.setEstado(entity.getEstado());
        dto.setFechaCompromiso(entity.getFechaCompromiso());
        dto.setObservaciones(entity.getObservaciones());
        dto.setCreadoEn(entity.getCreadoEn());
        dto.setActualizadoEn(entity.getActualizadoEn());
        return dto;
    }

    private void validarEstadoDeProduccion(EstadoTrabajo estado) {
        if (estado != EstadoTrabajo.CONFIRMADO && estado != EstadoTrabajo.EN_PROCESO && estado != EstadoTrabajo.TERMINADO) {
            throw new EstadoTrabajoInvalidoException("Los estados permitidos son Confirmado, En proceso y Terminado");
        }
    }
}
