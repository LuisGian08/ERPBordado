package com.embroiderydash.disenos;

import com.embroiderydash.disenos.api.dto.ColorHiloDto;
import com.embroiderydash.disenos.api.dto.DisenoRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DisenoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("OPERADOR y ADMIN pueden listar diseños")
    @WithMockUser(roles = "OPERADOR")
    void operadorPuedeListarDisenos() throws Exception {
        mockMvc.perform(get("/api/v1/disenos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("OPERADOR no tiene permisos para crear diseños (HTTP 403)")
    @WithMockUser(roles = "OPERADOR")
    void operadorNoPuedeCrearDiseno() throws Exception {
        DisenoRequestDto request = new DisenoRequestDto();
        request.setNombre("Diseño No Permitido");
        request.setCantidadPuntadas(3000);

        mockMvc.perform(post("/api/v1/disenos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN puede crear un nuevo diseño exitosamente (HTTP 201)")
    @WithMockUser(roles = "ADMIN")
    void adminPuedeCrearDiseno() throws Exception {
        DisenoRequestDto request = new DisenoRequestDto();
        request.setNombre("Diseño Prueba Admin Integration");
        request.setCantidadPuntadas(4500);
        request.setAnchoMm(new BigDecimal("60.00"));
        request.setAltoMm(new BigDecimal("40.00"));
        request.setFormatosAdmitidos("DST,PES");
        request.setColores(List.of(new ColorHiloDto(1, "#FF0000", "Rojo", "POLY-1025")));

        mockMvc.perform(post("/api/v1/disenos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nombre", is("Diseño Prueba Admin Integration")))
                .andExpect(jsonPath("$.cantidadPuntadas", is(4500)))
                .andExpect(jsonPath("$.colores", hasSize(1)));
    }

    @Test
    @DisplayName("Validación sintáctica: Cantidad de puntadas negativa o nula devuelve HTTP 400")
    @WithMockUser(roles = "ADMIN")
    void validacionPuntadasObligatorias() throws Exception {
        DisenoRequestDto request = new DisenoRequestDto();
        request.setNombre("Diseño Inválido");
        request.setCantidadPuntadas(-10); // Inválido

        mockMvc.perform(post("/api/v1/disenos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("ERROR_VALIDACION_CAMPOS")));
    }
}
