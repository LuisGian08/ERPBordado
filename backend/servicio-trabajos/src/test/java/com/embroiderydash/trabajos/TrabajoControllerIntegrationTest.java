package com.embroiderydash.trabajos;

import com.embroiderydash.trabajos.api.dto.CalcularCostoRequestDto;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrabajoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("OPERADOR y ADMIN pueden simular costo de bordado")
    @WithMockUser(roles = "OPERADOR")
    void operadorPuedeSimularCosto() throws Exception {
        CalcularCostoRequestDto request = new CalcularCostoRequestDto(8000, 30);

        mockMvc.perform(post("/api/v1/trabajos/calcular-costo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadPrendas", is(30)))
                .andExpect(jsonPath("$.puntadasUnitarias", is(8000)))
                .andExpect(jsonPath("$.totalPuntadas", is(240000)));
    }

    @Test
    @DisplayName("OPERADOR y ADMIN pueden listar órdenes de trabajo")
    @WithMockUser(roles = "OPERADOR")
    void operadorPuedeListarTrabajos() throws Exception {
        mockMvc.perform(get("/api/v1/trabajos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("OPERADOR no tiene permisos para liquidar costos reales (HTTP 403)")
    @WithMockUser(roles = "OPERADOR")
    void operadorNoPuedeLiquidarCostoReal() throws Exception {
        mockMvc.perform(post("/api/v1/trabajos/1/liquidar-costo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"costoReal\": 500.00}"))
                .andExpect(status().isForbidden());
    }
}
