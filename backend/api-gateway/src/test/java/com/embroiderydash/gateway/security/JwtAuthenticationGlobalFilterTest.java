package com.embroiderydash.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationGlobalFilterTest {
    private static final String SECRET="test-secret-with-at-least-32-bytes-for-hmac";
    private JwtAuthenticationGlobalFilter filter;
    @BeforeEach void setup(){filter=new JwtAuthenticationGlobalFilter(new JwtVerifier(SECRET),new ObjectMapper());}
    private String token(Instant expiresAt){var key=Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));Instant now=Instant.now();return Jwts.builder().subject("admin").claim("roles",List.of("ADMIN"))
            .issuedAt(Date.from(now.minusSeconds(2))).expiration(Date.from(expiresAt)).signWith(key).compact();}
    private AtomicBoolean pass(MockServerWebExchange exchange){AtomicBoolean called=new AtomicBoolean();GatewayFilterChain chain=e->{called.set(true);return Mono.empty();};StepVerifier.create(filter.filter(exchange,chain)).verifyComplete();return called;}
    @Test void sinTokenResponde401Json(){var exchange=MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/disenos").build());assertFalse(pass(exchange).get());assertEquals(HttpStatus.UNAUTHORIZED,exchange.getResponse().getStatusCode());StepVerifier.create(exchange.getResponse().getBodyAsString()).expectNextMatches(s->s.contains("NO_AUTENTICADO")&&s.contains("\"status\":401")).verifyComplete();}
    @Test void tokenInvalidoResponde401(){var exchange=MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/trabajos").header("Authorization","Bearer invalid").build());assertFalse(pass(exchange).get());assertEquals(HttpStatus.UNAUTHORIZED,exchange.getResponse().getStatusCode());}
    @Test void tokenExpiradoResponde401(){var exchange=MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/pagos").header("Authorization","Bearer "+token(Instant.now().minusSeconds(5))).build());assertFalse(pass(exchange).get());assertEquals(HttpStatus.UNAUTHORIZED,exchange.getResponse().getStatusCode());}
    @Test void tokenValidoContinuaYReenviaBearer(){String jwt=token(Instant.now().plusSeconds(3600));var exchange=MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/inventario/hilos").header("Authorization","Bearer "+jwt).build());assertTrue(pass(exchange).get());assertEquals("Bearer "+jwt,exchange.getRequest().getHeaders().getFirst("Authorization"));}
    @Test void loginEsPublico(){var exchange=MockServerWebExchange.from(MockServerHttpRequest.method(HttpMethod.POST,"/api/v1/auth/login").build());assertTrue(pass(exchange).get());}
    @Test void optionsEsPublico(){var exchange=MockServerWebExchange.from(MockServerHttpRequest.options("/api/v1/disenos").build());assertTrue(pass(exchange).get());}
}
