package com.embroiderydash.trabajos.infrastructure.client;

import com.embroiderydash.trabajos.infrastructure.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class DisenosClient {

    private static final Logger log = LoggerFactory.getLogger(DisenosClient.class);

    private final RestClient restClient;
    private final JwtTokenProvider jwtTokenProvider;

    public DisenosClient(
            @Value("${app.clients.disenos-url:http://localhost:8096}") String disenosUrl,
            JwtTokenProvider jwtTokenProvider) {
        this.restClient = RestClient.builder()
                .baseUrl(disenosUrl)
                .build();
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Optional<DisenoDto> obtenerDiseno(Long disenoId) {
        // Genera token JWT de servicio con rol ADMIN para comunicación inter-microservicio segura
        String serviceToken = jwtTokenProvider.generateToken("servicio-trabajos-internal", List.of("ROLE_ADMIN"));

        try {
            DisenoDto diseno = restClient.get()
                    .uri("/api/v1/disenos/{id}", disenoId)
                    .header("Authorization", "Bearer " + serviceToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.warn("Servicio de diseños respondió 4xx para disenoId {}: {}", disenoId, res.getStatusCode());
                    })
                    .body(DisenoDto.class);

            return Optional.ofNullable(diseno);
        } catch (Exception ex) {
            log.error("Fallo al comunicarse con servicio-disenos para ID {}: {}", disenoId, ex.getMessage());
            return Optional.empty();
        }
    }
}
