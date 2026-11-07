package com.embroiderydash.pagos.config;

import com.embroiderydash.pagos.infrastructure.error.ErrorResponse;
import com.embroiderydash.pagos.infrastructure.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.time.OffsetDateTime;
import java.util.List;

@Configuration @EnableWebSecurity @EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtFilter) { this.jwtFilter=jwtFilter; }
    @Bean SecurityFilterChain filterChain(HttpSecurity http,ObjectMapper mapper)throws Exception {
        return http.csrf(c->c.disable()).cors(c->{}).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a->a
                        .requestMatchers("/actuator/**","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/pagos/**").hasAnyRole("ADMIN","ADMIN_EMPRESA")
                        .requestMatchers(HttpMethod.POST,"/api/v1/pagos/*/anular").hasAnyRole("ADMIN","ADMIN_EMPRESA")
                        .anyRequest().denyAll())
                .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->writeError(mapper,res,HttpServletResponse.SC_UNAUTHORIZED,"No autenticado","NO_AUTENTICADO","Se requiere un token Bearer válido",req.getRequestURI()))
                        .accessDeniedHandler((req,res,ex)->writeError(mapper,res,HttpServletResponse.SC_FORBIDDEN,"Acceso denegado","ACCESO_DENEGADO","No cuenta con los permisos necesarios para realizar esta operación",req.getRequestURI())))
                .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class).build();
    }
    private static void writeError(ObjectMapper mapper,HttpServletResponse response,int status,String error,String code,String message,String path)throws java.io.IOException {
        response.setStatus(status);response.setContentType(MediaType.APPLICATION_JSON_VALUE);mapper.writeValue(response.getOutputStream(),new ErrorResponse(status,error,code,message,path));
    }
    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${CORS_ALLOWED_ORIGIN:http://localhost:3000}")String origin) {
        CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of(origin));c.setAllowedMethods(List.of("GET","POST","OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization","Content-Type","Idempotency-Key"));c.setExposedHeaders(List.of("Location"));
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",c);return source;
    }
}
