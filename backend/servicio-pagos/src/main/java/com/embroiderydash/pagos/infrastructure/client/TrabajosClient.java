package com.embroiderydash.pagos.infrastructure.client;

import com.embroiderydash.pagos.domain.exception.TrabajosServiceUnavailableException;
import com.embroiderydash.pagos.infrastructure.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.List;
import java.util.Optional;

@Component
public class TrabajosClient {
    private static final Logger log=LoggerFactory.getLogger(TrabajosClient.class);
    private final RestClient client; private final JwtTokenProvider tokens;
    public TrabajosClient(@Value("${app.clients.trabajos-url:http://localhost:8098}") String url, JwtTokenProvider tokens) {
        this.client=RestClient.builder().baseUrl(url).build(); this.tokens=tokens;
    }
    public Optional<TrabajoDto> obtenerTrabajo(Long id) {
        String token=tokens.generateToken("servicio-pagos-internal", List.of("ROLE_ADMIN"));
        try { return Optional.ofNullable(client.get().uri("/api/v1/trabajos/{id}", id).header("Authorization","Bearer "+token).retrieve().body(TrabajoDto.class)); }
        catch (HttpClientErrorException.NotFound e) { return Optional.empty(); }
        catch (RestClientException e) {
            log.error("No fue posible consultar el trabajo {} en servicio-trabajos: {}", id, e.getMessage());
            throw new TrabajosServiceUnavailableException("El servicio de trabajos no está disponible temporalmente", e);
        }
    }
}
