package com.embroiderydash.pagos.application.service;

import com.embroiderydash.pagos.api.dto.PagoRequestDto;
import com.embroiderydash.pagos.api.dto.PagoResponseDto;
import com.embroiderydash.pagos.api.dto.ResumenPagoTrabajoDto;
import com.embroiderydash.pagos.domain.exception.IdempotencyConflictException;
import com.embroiderydash.pagos.domain.exception.PagoExcedeSaldoException;
import com.embroiderydash.pagos.domain.exception.PagoNotFoundException;
import com.embroiderydash.pagos.domain.exception.TrabajoNoDisponibleException;
import com.embroiderydash.pagos.domain.model.EstadoPagoTrabajo;
import com.embroiderydash.pagos.domain.model.Pago;
import com.embroiderydash.pagos.domain.repository.PagoRepository;
import com.embroiderydash.pagos.infrastructure.client.TrabajoDto;
import com.embroiderydash.pagos.infrastructure.client.TrabajosClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class PagoService {
    private final PagoRepository pagos;
    private final TrabajosClient trabajos;
    private final PaymentLock locks;
    private final PagoPersistenceService persistence;

    public PagoService(PagoRepository pagos, TrabajosClient trabajos, PaymentLock locks, PagoPersistenceService persistence) {
        this.pagos=pagos; this.trabajos=trabajos; this.locks=locks; this.persistence=persistence;
    }

    @Transactional
    public PagoCreationResult registrar(PagoRequestDto request, String idempotencyKey, String operador) {
        if (idempotencyKey==null || idempotencyKey.isBlank() || idempotencyKey.length()>100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El header Idempotency-Key es obligatorio y debe tener hasta 100 caracteres");
        locks.lockIdempotencyKey(idempotencyKey);
        var existing=pagos.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) return new PagoCreationResult(verificarRepeticion(existing.get(),request,idempotencyKey),false);

        locks.lockWork(request.trabajoId());
        TrabajoDto trabajo=obtenerTrabajoDisponible(request.trabajoId());
        BigDecimal saldo=trabajo.getPrecioVenta().subtract(totalPagado(request.trabajoId())).max(BigDecimal.ZERO);
        if (request.monto().compareTo(saldo)>0) throw new PagoExcedeSaldoException(request.trabajoId(),request.monto(),saldo);

        Pago nuevo=new Pago(request.trabajoId(),idempotencyKey,request.monto(),request.metodoPago(),request.referenciaTransaccion(),request.notas(),operador);
        try { return new PagoCreationResult(PagoResponseDto.from(persistence.saveAndFlush(nuevo)),true); }
        catch (DataIntegrityViolationException ex) {
            var concurrente=pagos.findByIdempotencyKey(idempotencyKey);
            if (concurrente.isPresent()) return new PagoCreationResult(verificarRepeticion(concurrente.get(),request,idempotencyKey),false);
            throw ex;
        }
    }

    @Transactional(readOnly=true)
    public List<PagoResponseDto> historial(Long trabajoId) {
        return pagos.findByTrabajoIdOrderByCreadoEnDesc(trabajoId).stream().map(PagoResponseDto::from).toList();
    }

    @Transactional(readOnly=true)
    public Page<PagoResponseDto> ingresos(Pageable pageable) {
        return pagos.findByTrabajoIdIsNotNullAndEstadoResultadoOrderByCreadoEnDesc("REGISTRADO", pageable)
                .map(PagoResponseDto::from);
    }

    @Transactional(readOnly=true)
    public ResumenPagoTrabajoDto resumen(Long trabajoId) {
        TrabajoDto trabajo=obtenerTrabajoDisponible(trabajoId);
        BigDecimal total=totalPagado(trabajoId);
        BigDecimal saldo=trabajo.getPrecioVenta().subtract(total).max(BigDecimal.ZERO);
        EstadoPagoTrabajo estado=total.signum()==0
                ? (pagos.existsByTrabajoIdAndEstadoResultado(trabajoId,"ANULADO") ? EstadoPagoTrabajo.ANULADO : EstadoPagoTrabajo.PENDIENTE)
                : total.compareTo(trabajo.getPrecioVenta())>=0?EstadoPagoTrabajo.PAGADO:EstadoPagoTrabajo.PARCIAL;
        return new ResumenPagoTrabajoDto(trabajoId,trabajo.getPrecioVenta(),total,saldo,estado);
    }

    @Transactional
    public PagoResponseDto anular(Long id) {
        Pago pago=pagos.findById(id).orElseThrow(()->new PagoNotFoundException(id));
        if (pago.getTrabajoId()==null) throw new ResponseStatusException(HttpStatus.CONFLICT,"Este registro antiguo no corresponde a un pago de trabajo");
        locks.lockWork(pago.getTrabajoId());
        if (!"ANULADO".equals(pago.getEstadoResultado())) { pago.setEstadoResultado("ANULADO"); pagos.save(pago); }
        return PagoResponseDto.from(pago);
    }

    private TrabajoDto obtenerTrabajoDisponible(Long id) {
        TrabajoDto trabajo=trabajos.obtenerTrabajo(id).orElseThrow(()->new TrabajoNoDisponibleException(id,"no existe"));
        if ("CANCELADO".equalsIgnoreCase(trabajo.getEstado())) throw new TrabajoNoDisponibleException(id,"está cancelado");
        if (trabajo.getPrecioVenta()==null || trabajo.getPrecioVenta().signum()<=0) throw new TrabajoNoDisponibleException(id,"no tiene un precio de venta válido");
        return trabajo;
    }
    private BigDecimal totalPagado(Long trabajoId) {
        BigDecimal total=pagos.calcularTotalPagadoPorTrabajo(trabajoId);
        return total==null?BigDecimal.ZERO:total;
    }
    private PagoResponseDto verificarRepeticion(Pago pago,PagoRequestDto request,String key) {
        if (!Objects.equals(pago.getTrabajoId(),request.trabajoId()) || pago.getMonto().compareTo(request.monto())!=0 || pago.getMetodoPago()!=request.metodoPago())
            throw new IdempotencyConflictException(key);
        return PagoResponseDto.from(pago);
    }
}
