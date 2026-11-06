package com.embroiderydash.pagos;

import com.embroiderydash.pagos.infrastructure.client.TrabajoDto;
import com.embroiderydash.pagos.infrastructure.client.TrabajosClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class PagoControllerIntegrationTest {
    @Autowired MockMvc mvc;
    @MockBean TrabajosClient trabajos;
    @BeforeEach void setup(){TrabajoDto t=new TrabajoDto();t.setId(3L);t.setCodigoTrabajo("TRAB-3");t.setClienteNombre("Cliente");t.setPrecioVenta(new BigDecimal("500.00"));t.setEstado("CONFIRMADO");when(trabajos.obtenerTrabajo(3L)).thenReturn(Optional.of(t));}
    @Test @DisplayName("Responde 401 al consultar sin token") void unauthorized()throws Exception{mvc.perform(get("/api/v1/pagos/trabajo/3")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("NO_AUTENTICADO"));}
    @Test @DisplayName("OPERADOR no puede anular pagos") @WithMockUser(roles="OPERADOR") void operadorNoAnula()throws Exception{mvc.perform(post("/api/v1/pagos/1/anular")).andExpect(status().isForbidden());}
    @Test @DisplayName("Rechaza pago sin Idempotency-Key") @WithMockUser(roles="ADMIN") void faltaIdempotencyKey()throws Exception{
        mvc.perform(post("/api/v1/pagos").contentType(MediaType.APPLICATION_JSON).content("{\"trabajoId\":3,\"monto\":100,\"metodoPago\":\"EFECTIVO\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("SOLICITUD_INVALIDA"));
    }
    @Test @DisplayName("Registra pago y devuelve 201") @WithMockUser(roles="ADMIN") void paymentCreated()throws Exception{
        mvc.perform(post("/api/v1/pagos").header("Idempotency-Key","integration-001").contentType(MediaType.APPLICATION_JSON)
                .content("{\"trabajoId\":3,\"monto\":100.00,\"metodoPago\":\"EFECTIVO\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.trabajoId").value(3)).andExpect(jsonPath("$.operador").value("user"));
    }
    @Test @DisplayName("Devuelve 409 si el pago excede el saldo") @WithMockUser(roles="ADMIN") void paymentExceeds()throws Exception{
        mvc.perform(post("/api/v1/pagos").header("Idempotency-Key","integration-002").contentType(MediaType.APPLICATION_JSON)
                .content("{\"trabajoId\":3,\"monto\":600.00,\"metodoPago\":\"EFECTIVO\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PAGO_EXCEDE_SALDO"));
    }
}
