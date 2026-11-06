package com.embroiderydash.disenos.config;

import com.embroiderydash.disenos.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Actuator y documentación OpenAPI públicos
                .requestMatchers("/actuator/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/v1/disenos/*/precio").hasAnyRole("ADMIN", "ADMIN_EMPRESA")
                .requestMatchers(HttpMethod.GET, "/api/v1/disenos/**", "/api/v1/carpetas/**", "/api/v1/precios/**").hasAnyRole("ADMIN", "ADMIN_EMPRESA", "DISENADOR", "OPERADOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/disenos/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/disenos/*/carpeta").hasAnyRole("ADMIN", "DISENADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/disenos/*").hasAnyRole("ADMIN", "DISENADOR")
                .requestMatchers(HttpMethod.POST, "/api/v1/disenos/**", "/api/v1/carpetas/**").hasAnyRole("ADMIN", "DISENADOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/carpetas/**").hasAnyRole("ADMIN", "DISENADOR")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/carpetas/**").hasAnyRole("ADMIN", "DISENADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/carpetas/**").hasAnyRole("ADMIN", "DISENADOR")
                // Creación, modificación y borrado reservado exclusivamente a ADMIN
                .anyRequest().denyAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${CORS_ALLOWED_ORIGIN:http://localhost:3000}") String origin) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Idempotency-Key"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
