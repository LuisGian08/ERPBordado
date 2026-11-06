package com.embroiderydash.pagos;

import com.embroiderydash.pagos.api.dto.PagoRequestDto;
import com.embroiderydash.pagos.application.service.*;
import com.embroiderydash.pagos.domain.exception.*;
import com.embroiderydash.pagos.domain.model.*;
import com.embroiderydash.pagos.domain.repository.PagoRepository;
import com.embroiderydash.pagos.infrastructure.client.TrabajoDto;
import com.embroiderydash.pagos.infrastructure.client.TrabajosClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {
    @Mock PagoRepository pagos;
    @Mock TrabajosClient trabajos;
    @Mock PaymentLock locks;
    @Mock PagoPersistenceService persistence;
    @InjectMocks PagoService service;
    private PagoRequestDto request;
    @BeforeEach void setup(){request=new PagoRequestDto(3L,new BigDecimal("100.00"),MetodoPago.EFECTIVO,null,null);}
    private TrabajoDto trabajo(String estado,String precio){TrabajoDto t=new TrabajoDto();t.setId(3L);t.setCodigoTrabajo("TRAB-3");t.setPrecioVenta(new BigDecimal(precio));t.setEstado(estado);return t;}
    @Test @DisplayName("Registra pago dentro del saldo") void pagoOk(){
        when(pagos.findByIdempotencyKey("key-1")).thenReturn(Optional.empty());when(trabajos.obtenerTrabajo(3L)).thenReturn(Optional.of(trabajo("CONFIRMADO","250.00")));
        when(pagos.calcularTotalPagadoPorTrabajo(3L)).thenReturn(new BigDecimal("50.00"));when(persistence.saveAndFlush(any(Pago.class))).thenAnswer(i->i.getArgument(0));
        var result=service.registrar(request,"key-1","operador");assertTrue(result.creado());assertEquals(3L,result.pago().trabajoId());assertEquals("operador",result.pago().operador());verify(locks).lockWork(3L);
    }
    @Test @DisplayName("Rechaza un pago mayor que el saldo pendiente") void excedeSaldo(){
        when(pagos.findByIdempotencyKey("key-1")).thenReturn(Optional.empty());when(trabajos.obtenerTrabajo(3L)).thenReturn(Optional.of(trabajo("CONFIRMADO","100.00")));
        when(pagos.calcularTotalPagadoPorTrabajo(3L)).thenReturn(BigDecimal.ZERO);assertThrows(PagoExcedeSaldoException.class,()->service.registrar(request,"key-1","op"));verify(persistence,never()).saveAndFlush(any());
    }
    @Test @DisplayName("Devuelve el pago anterior para un reintento idéntico") void idemRepetida(){
        Pago old=new Pago(3L,"key-1",request.monto(),request.metodoPago(),null,null,"op");old.setId(12L);when(pagos.findByIdempotencyKey("key-1")).thenReturn(Optional.of(old));
        var result=service.registrar(request,"key-1","otro");assertFalse(result.creado());assertEquals(12L,result.pago().id());verify(trabajos,never()).obtenerTrabajo(any());
    }
    @Test @DisplayName("Rechaza una clave repetida con contenido distinto") void idemConflicto(){
        Pago old=new Pago(3L,"key-1",request.monto(),MetodoPago.EFECTIVO,null,null,"op");when(pagos.findByIdempotencyKey("key-1")).thenReturn(Optional.of(old));
        PagoRequestDto changed=new PagoRequestDto(3L,request.monto(),MetodoPago.YAPE_PLIN,null,null);assertThrows(IdempotencyConflictException.class,()->service.registrar(changed,"key-1","op"));
    }
    @Test @DisplayName("Rechaza trabajos cancelados o inexistentes") void trabajoNoDisponible(){
        when(pagos.findByIdempotencyKey(any())).thenReturn(Optional.empty());when(trabajos.obtenerTrabajo(3L)).thenReturn(Optional.of(trabajo("CANCELADO","250.00")));
        assertThrows(TrabajoNoDisponibleException.class,()->service.registrar(request,"key-1","op"));
        when(trabajos.obtenerTrabajo(3L)).thenReturn(Optional.empty());assertThrows(TrabajoNoDisponibleException.class,()->service.registrar(request,"key-2","op"));
    }
    @Test @DisplayName("Anula un pago sin eliminar su registro") void anulacion(){
        Pago pago=new Pago(3L,"key-1",request.monto(),MetodoPago.EFECTIVO,null,null,"op");pago.setId(12L);when(pagos.findById(12L)).thenReturn(Optional.of(pago));
        assertEquals("ANULADO",service.anular(12L).estadoResultado());verify(pagos).save(pago);
    }
}
