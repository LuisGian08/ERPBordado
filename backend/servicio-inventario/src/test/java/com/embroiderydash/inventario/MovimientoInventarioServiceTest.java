package com.embroiderydash.inventario;

import com.embroiderydash.inventario.api.dto.MovimientoRequestDto;
import com.embroiderydash.inventario.api.dto.MovimientoResponseDto;
import com.embroiderydash.inventario.application.service.MovimientoInventarioService;
import com.embroiderydash.inventario.domain.exception.StockInsuficienteException;
import com.embroiderydash.inventario.domain.model.Hilo;
import com.embroiderydash.inventario.domain.model.MovimientoInventario;
import com.embroiderydash.inventario.domain.model.TipoMovimiento;
import com.embroiderydash.inventario.domain.repository.HiloRepository;
import com.embroiderydash.inventario.domain.repository.MovimientoInventarioRepository;
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
class MovimientoInventarioServiceTest {

    @Mock
    private HiloRepository hiloRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @InjectMocks
    private MovimientoInventarioService movimientoService;

    private Hilo hiloSample;

    @BeforeEach
    void setUp() {
        hiloSample = new Hilo();
        hiloSample.setId(1L);
        hiloSample.setReferencia("POLY-1000");
        hiloSample.setNombreColor("Negro");
        hiloSample.setStockActualConos(10);
        hiloSample.setStockMinimoConos(3);
        hiloSample.setPrecioCono(new BigDecimal("25.00"));
        hiloSample.setActivo(true);
    }

    @Test
    @DisplayName("Debe incrementar stock en movimiento de ENTRADA")
    void movimientoEntradaIncrementaStock() {
        when(hiloRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hiloSample));
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenAnswer(i -> {
            MovimientoInventario m = i.getArgument(0);
            m.setId(101L);
            return m;
        });

        MovimientoRequestDto request = new MovimientoRequestDto(1L, TipoMovimiento.ENTRADA, 5, "Compra de lote", null);
        MovimientoResponseDto response = movimientoService.registrarMovimiento(request, "operador1");

        assertNotNull(response);
        assertEquals(15, response.getStockNuevo());
        assertEquals(10, response.getStockPrevio());
        assertEquals(15, hiloSample.getStockActualConos());
        verify(hiloRepository, times(1)).save(hiloSample);
    }

    @Test
    @DisplayName("Debe decrementar stock en movimiento de SALIDA exitoso")
    void movimientoSalidaDecrementaStock() {
        when(hiloRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hiloSample));
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenAnswer(i -> {
            MovimientoInventario m = i.getArgument(0);
            m.setId(102L);
            return m;
        });

        MovimientoRequestDto request = new MovimientoRequestDto(1L, TipoMovimiento.SALIDA, 4, "Consumo en trabajo #12", 12L);
        MovimientoResponseDto response = movimientoService.registrarMovimiento(request, "operador1");

        assertNotNull(response);
        assertEquals(6, response.getStockNuevo());
        assertEquals(10, response.getStockPrevio());
        assertEquals(6, hiloSample.getStockActualConos());
        verify(hiloRepository, times(1)).save(hiloSample);
    }

    @Test
    @DisplayName("Debe lanzar StockInsuficienteException cuando la SALIDA excede existencias")
    void movimientoSalidaInsuficienteLanzaExcepcion() {
        when(hiloRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hiloSample));

        // Intento de retirar 15 conos cuando solo hay 10
        MovimientoRequestDto request = new MovimientoRequestDto(1L, TipoMovimiento.SALIDA, 15, "Consumo excesivo", null);

        assertThrows(StockInsuficienteException.class, () -> movimientoService.registrarMovimiento(request, "operador1"));
        assertEquals(10, hiloSample.getStockActualConos()); // Stock intacto
        verify(hiloRepository, never()).save(any(Hilo.class));
        verify(movimientoRepository, never()).save(any(MovimientoInventario.class));
    }
}
