package com.embroiderydash.inventario;

import com.embroiderydash.inventario.api.dto.HiloRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HiloControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("OPERADOR y ADMIN pueden consultar catálogo de hilos y alertas")
    @WithMockUser(roles = "OPERADOR")
    void operadorPuedeConsultarHilosYAlertas() throws Exception {
        mockMvc.perform(get("/api/v1/inventario/hilos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/inventario/hilos/alertas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("OPERADOR puede registrar hilos para el taller")
    @WithMockUser(roles = "OPERADOR")
    void operadorNoPuedeCrearHilo() throws Exception {
        HiloRequestDto request = new HiloRequestDto();
        request.setMarca("Madeira");
        request.setTipoMaterial("Poliéster 40wt");
        request.setCodigoColor("00123");
        request.setNombreColor("Rojo");
        request.setReferencia("POLY-TEST");
        request.setStockActualConos(5);
        request.setMetrosPorCono(5000);
        request.setStockMinimoConos(2);
        request.setPrecioCono(new BigDecimal("22.00"));

        mockMvc.perform(post("/api/v1/inventario/hilos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("ADMIN puede crear hilo exitosamente (HTTP 201)")
    @WithMockUser(roles = "ADMIN")
    void adminPuedeCrearHilo() throws Exception {
        HiloRequestDto request = new HiloRequestDto();
        request.setMarca("Madeira");
        request.setTipoMaterial("Poliéster 40wt");
        request.setCodigoColor("00042");
        request.setNombreColor("Verde Esmeralda");
        request.setReferencia("POLY-VERDE-INT");
        request.setStockActualConos(8);
        request.setMetrosPorCono(5000);
        request.setStockMinimoConos(3);
        request.setPrecioCono(new BigDecimal("26.50"));

        var created=mockMvc.perform(post("/api/v1/inventario/hilos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.referencia", is("POLY-VERDE-INT")))
                .andExpect(jsonPath("$.stockActualConos", is(8))).andReturn();
        long hiloId=objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(get("/api/v1/inventario/movimientos/hilo/{hiloId}",hiloId))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].tipoMovimiento",is("ENTRADA")))
                .andExpect(jsonPath("$[0].motivo",is("Stock inicial")));
    }

    @Test
    @DisplayName("Validación sintáctica: Stock negativo devuelve HTTP 400")
    @WithMockUser(roles = "ADMIN")
    void validacionStockNegativoRechazada() throws Exception {
        HiloRequestDto request = new HiloRequestDto();
        request.setMarca("Madeira");
        request.setTipoMaterial("Poliéster 40wt");
        request.setCodigoColor("00000");
        request.setNombreColor("Negro");
        request.setReferencia("POLY-FAIL");
        request.setStockActualConos(-5); // Negativo
        request.setMetrosPorCono(5000);
        request.setStockMinimoConos(2);
        request.setPrecioCono(new BigDecimal("20.00"));

        mockMvc.perform(post("/api/v1/inventario/hilos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("ERROR_VALIDACION_CAMPOS")));
    }

    @Test
    @DisplayName("El código de color solo admite dígitos")
    @WithMockUser(roles = "ADMIN")
    void codigoNoNumericoRechazado() throws Exception {
        HiloRequestDto request = new HiloRequestDto();
        request.setMarca("Madeira"); request.setTipoMaterial("Poliéster 40wt"); request.setCodigoColor("AB12");
        request.setNombreColor("Verde"); request.setReferencia("POLY-COLOR-INVALIDO"); request.setStockActualConos(0);
        request.setMetrosPorCono(5000); request.setStockMinimoConos(1); request.setPrecioCono(new BigDecimal("20.00"));
        mockMvc.perform(post("/api/v1/inventario/hilos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code", is("ERROR_VALIDACION_CAMPOS")));
    }

    @Test
    @DisplayName("La imagen inválida se rechaza después de registrar el hilo")
    @WithMockUser(roles = "ADMIN")
    void imagenCorruptaRechazada() throws Exception {
        HiloRequestDto request = new HiloRequestDto();
        request.setMarca("Madeira"); request.setTipoMaterial("Poliéster 40wt"); request.setCodigoColor("00881");
        request.setNombreColor("Azul de prueba"); request.setReferencia("POLY-IMG-TEST"); request.setStockActualConos(0);
        request.setMetrosPorCono(5000); request.setStockMinimoConos(1); request.setPrecioCono(new BigDecimal("20.00"));
        var result=mockMvc.perform(post("/api/v1/inventario/hilos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn();
        long id=objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        MockMultipartFile badImage=new MockMultipartFile("archivo","image.png","image/png","not an image".getBytes());
        mockMvc.perform(multipart("/api/v1/inventario/hilos/{id}/imagen",id).file(badImage))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code", is("IMAGEN_INVALIDA")));
    }
}
