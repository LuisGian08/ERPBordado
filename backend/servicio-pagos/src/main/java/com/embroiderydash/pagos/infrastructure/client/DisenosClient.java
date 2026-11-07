package com.embroiderydash.pagos.infrastructure.client;

import com.embroiderydash.pagos.infrastructure.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.util.List;

@Component
public class DisenosClient {
    private final RestClient client;
    private final JwtTokenProvider tokens;
    public DisenosClient(@Value("${app.clients.disenos-url:http://localhost:8096}") String url, JwtTokenProvider tokens) {
        this.client=RestClient.builder().baseUrl(url).build(); this.tokens=tokens;
    }
    public DisenoPrecio obtenerPrecio(Long id) {
        String token=tokens.generateToken("servicio-pagos-internal", List.of("ROLE_ADMIN"));
        try { return client.get().uri("/api/v1/precios/{id}",id).header("Authorization","Bearer "+token).retrieve().body(DisenoPrecio.class); }
        catch (org.springframework.web.client.HttpClientErrorException.NotFound ex) { return null; }
    }
    public DisenoInfo obtenerDiseno(Long id) {
        String token=tokens.generateToken("servicio-pagos-internal", List.of("ROLE_ADMIN"));
        try { return client.get().uri("/api/v1/disenos/{id}",id).header("Authorization","Bearer "+token).retrieve().body(DisenoInfo.class); }
        catch (org.springframework.web.client.HttpClientErrorException.NotFound ex) { return null; }
    }
    public record DisenoPrecio(Long disenoId,BigDecimal precioUnitario,String moneda,Boolean sinPrecio) { }
    public record DisenoInfo(Long id,String nombre) { }
}
