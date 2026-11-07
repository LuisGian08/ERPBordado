package com.embroiderydash.trabajos;

import com.embroiderydash.trabajos.api.dto.ActualizarEstadoDto;
import com.embroiderydash.trabajos.api.dto.CostoEstimadoDto;
import com.embroiderydash.trabajos.api.dto.LiquidarCostoRealDto;
import com.embroiderydash.trabajos.api.dto.TrabajoRequestDto;
import com.embroiderydash.trabajos.api.dto.TrabajoResponseDto;
import com.embroiderydash.trabajos.application.service.CalculadoraCostoService;
import com.embroiderydash.trabajos.application.service.TrabajoService;
import com.embroiderydash.trabajos.domain.exception.EstadoTrabajoInvalidoException;
import com.embroiderydash.trabajos.domain.exception.TrabajoDuplicadoException;
import com.embroiderydash.trabajos.domain.model.EstadoTrabajo;
import com.embroiderydash.trabajos.domain.model.TrabajoBordado;
import com.embroiderydash.trabajos.domain.repository.TrabajoRepository;
import com.embroiderydash.trabajos.infrastructure.client.DisenoDto;
import com.embroiderydash.trabajos.infrastructure.client.DisenosClient;
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
class TrabajoServiceTest {

    @Mock
    private TrabajoRepository trabajoRepository;

    @Mock
    private CalculadoraCostoService calculadoraCostoService;

    @Mock
    private DisenosClient disenosClient;

    @InjectMocks
    private TrabajoService trabajoService;

    private TrabajoRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new TrabajoRequestDto();
        requestDto.setCodigoTrabajo("TRAB-TEST-100");
        requestDto.setClienteNombre("Cliente Prueba");
        requestDto.setDisenoId(1L);
        requestDto.setCantidadPrendas(20);
        requestDto.setPrecioVenta(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Debe crear una orden de trabajo con costo estimado y sin costo real")
    void crearTrabajoExitoso() {
        when(trabajoRepository.existsByCodigoTrabajoIgnoreCase("TRAB-TEST-100")).thenReturn(false);
        when(disenosClient.obtenerDiseno(1L)).thenReturn(Optional.of(new DisenoDto(1L, "Logo Bordado", 8000, true)));

        CostoEstimadoDto costoEstimado = new CostoEstimadoDto();
        costoEstimado.setCostoTotalEstimado(new BigDecimal("280.00"));
        when(calculadoraCostoService.calcularCosto(8000, 20)).thenReturn(costoEstimado);

        when(trabajoRepository.save(any(TrabajoBordado.class))).thenAnswer(i -> {
            TrabajoBordado t = i.getArgument(0);
            t.setId(10L);
            return t;
        });

        TrabajoResponseDto response = trabajoService.crearTrabajo(requestDto);

        assertNotNull(response);
        assertEquals("TRAB-TEST-100", response.getCodigoTrabajo());
        assertEquals(new BigDecimal("280.00"), response.getCostoEstimado());
        assertNull(response.getCostoReal()); // Requisito: no confundir estimación con costo real
        assertEquals(EstadoTrabajo.COTIZADO, response.getEstado());
        verify(trabajoRepository, times(1)).save(any(TrabajoBordado.class));
    }

    @Test
    @DisplayName("Debe lanzar TrabajoDuplicadoException cuando el código ya existe")
    void crearTrabajoCodigoDuplicado() {
        when(trabajoRepository.existsByCodigoTrabajoIgnoreCase("TRAB-TEST-100")).thenReturn(true);

        assertThrows(TrabajoDuplicadoException.class, () -> trabajoService.crearTrabajo(requestDto));
        verify(trabajoRepository, never()).save(any(TrabajoBordado.class));
    }

    @Test
    @DisplayName("Debe rechazar cambios de estado en trabajos cancelados o entregados")
    void transicionEstadoInvalido() {
        TrabajoBordado trabajo = new TrabajoBordado();
        trabajo.setId(5L);
        trabajo.setEstado(EstadoTrabajo.CANCELADO);

        when(trabajoRepository.findById(5L)).thenReturn(Optional.of(trabajo));

        ActualizarEstadoDto request = new ActualizarEstadoDto(EstadoTrabajo.EN_PROCESO);
        assertThrows(EstadoTrabajoInvalidoException.class, () -> trabajoService.actualizarEstado(5L, request));
    }

    @Test
    @DisplayName("Debe asentar el costo real medido en liquidarCostoReal")
    void liquidarCostoRealExitoso() {
        TrabajoBordado trabajo = new TrabajoBordado();
        trabajo.setId(5L);
        trabajo.setCostoEstimado(new BigDecimal("300.00"));
        trabajo.setPrecioVenta(new BigDecimal("600.00"));
        trabajo.setEstado(EstadoTrabajo.TERMINADO);

        when(trabajoRepository.findById(5L)).thenReturn(Optional.of(trabajo));
        when(trabajoRepository.save(any(TrabajoBordado.class))).thenAnswer(i -> i.getArgument(0));

        LiquidarCostoRealDto request = new LiquidarCostoRealDto(new BigDecimal("315.50"), "Mayor consumo de hilo dorado");
        TrabajoResponseDto response = trabajoService.liquidarCostoReal(5L, request);

        assertNotNull(response);
        assertEquals(new BigDecimal("315.50"), response.getCostoReal());
        assertEquals(new BigDecimal("300.00"), response.getCostoEstimado());
    }
}
