package com.embroiderydash.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.OffsetDateTime;

@Component @Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthenticationGlobalFilter implements GlobalFilter {
    private final JwtVerifier verifier; private final ObjectMapper mapper;
    public JwtAuthenticationGlobalFilter(JwtVerifier verifier,ObjectMapper mapper){this.verifier=verifier;this.mapper=mapper;}
    @Override public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain){
        var request=exchange.getRequest();String path=request.getPath().value();
        boolean publicLogin=request.getMethod()==HttpMethod.POST&&path.equals("/api/v1/auth/login");
        boolean publicHealth=request.getMethod()==HttpMethod.GET&&path.equals("/actuator/health");
        if(request.getMethod()==HttpMethod.OPTIONS||publicLogin||publicHealth)return chain.filter(exchange);
        String header=request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(header!=null&&header.startsWith("Bearer ")&&verifier.valid(header.substring(7)))return chain.filter(exchange);
        return unauthorized(exchange);
    }
    private Mono<Void> unauthorized(ServerWebExchange exchange){
        var response=exchange.getResponse();response.setStatusCode(HttpStatus.UNAUTHORIZED);response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        var body=new GatewayErrorResponse(OffsetDateTime.now(),401,"Unauthorized","NO_AUTENTICADO","Se requiere un token Bearer válido",exchange.getRequest().getPath().value());
        try { byte[] json=mapper.writeValueAsBytes(body);return response.writeWith(Mono.just(response.bufferFactory().wrap(json))); }
        catch(Exception ex){return Mono.error(ex);}
    }
}
