package com.embroiderydash.disenos;

import com.embroiderydash.disenos.api.dto.ColorHiloDto;
import com.embroiderydash.disenos.api.dto.DisenoRequestDto;
import com.embroiderydash.disenos.api.dto.DisenoResponseDto;
import com.embroiderydash.disenos.application.service.DisenoService;
import com.embroiderydash.disenos.domain.exception.DisenoDuplicadoException;
import com.embroiderydash.disenos.domain.exception.DisenoNotFoundException;
import com.embroiderydash.disenos.domain.model.Diseno;
import com.embroiderydash.disenos.domain.repository.DisenoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisenoServiceTest {

    @Mock
    private DisenoRepository disenoRepository;

    @InjectMocks
    private DisenoService disenoService;

    private Diseno disenoSample;
    private DisenoRequestDto requestDto;

    @BeforeEach
    void setUp() {
        disenoSample = new Diseno();
        disenoSample.setId(1L);
        disenoSample.setNombre("Logo Deportivo");
        disenoSample.setCantidadPuntadas(5000);
        disenoSample.setAnchoMm(new BigDecimal("50.00"));
        disenoSample.setAltoMm(new BigDecimal("50.00"));
        disenoSample.setActivo(true);

        requestDto = new DisenoRequestDto();
        requestDto.setNombre("Logo Deportivo");
        requestDto.setCantidadPuntadas(5000);
        requestDto.setAnchoMm(new BigDecimal("50.00"));
        requestDto.setAltoMm(new BigDecimal("50.00"));
        requestDto.setColores(List.of(new ColorHiloDto(1, "#000000", "Negro", "POLY-1000")));
    }

    @Test
    @DisplayName("Debe registrar un diseño correctamente cuando no existe duplicado")
    void crearDisenoExitoso() {
        when(disenoRepository.existsByNombreIgnoreCase("Logo Deportivo")).thenReturn(false);
        when(disenoRepository.save(any(Diseno.class))).thenAnswer(invocation -> {
            Diseno d = invocation.getArgument(0);
            d.setId(10L);
            return d;
        });

        DisenoResponseDto response = disenoService.crearDiseno(requestDto);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Logo Deportivo", response.getNombre());
        verify(disenoRepository, times(1)).save(any(Diseno.class));
    }

    @Test
    @DisplayName("Debe lanzar DisenoDuplicadoException cuando el nombre ya existe")
    void crearDisenoDuplicadoLanzaExcepcion() {
        when(disenoRepository.existsByNombreIgnoreCase("Logo Deportivo")).thenReturn(true);

        assertThrows(DisenoDuplicadoException.class, () -> disenoService.crearDiseno(requestDto));
        verify(disenoRepository, never()).save(any(Diseno.class));
    }

    @Test
    @DisplayName("Debe lanzar DisenoNotFoundException cuando el ID no existe o está inactivo")
    void obtenerPorIdNoEncontrado() {
        when(disenoRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        assertThrows(DisenoNotFoundException.class, () -> disenoService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Debe realizar baja lógica al eliminar diseño")
    void eliminarDisenoBajaLogica() {
        when(disenoRepository.findById(1L)).thenReturn(Optional.of(disenoSample));

        disenoService.eliminarDiseno(1L);

        assertFalse(disenoSample.getActivo());
        verify(disenoRepository, times(1)).save(disenoSample);
    }
}
