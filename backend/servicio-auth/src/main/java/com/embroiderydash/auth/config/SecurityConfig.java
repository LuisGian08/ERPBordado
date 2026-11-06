package com.embroiderydash.auth.config;

import com.embroiderydash.auth.domain.UserRepository;
import com.embroiderydash.auth.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration @EnableWebSecurity @EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
    @Bean UserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByUsername(username).map(u -> User.withUsername(u.getUsername()).password(u.getPasswordHash())
                .authorities("ROLE_" + u.getRole().name()).disabled(!u.isEnabled()).build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos"));
    }
    @Bean DaoAuthenticationProvider authenticationProvider(UserDetailsService users, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); provider.setUserDetailsService(users); provider.setPasswordEncoder(encoder); return provider;
    }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception { return configuration.getAuthenticationManager(); }
    @Bean SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider provider, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http.csrf(c -> c.disable()).cors(c -> {}).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(provider).authorizeHttpRequests(a -> a
                        .requestMatchers("/api/v1/auth/login", "/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/auth/me").authenticated().anyRequest().denyAll())
                .httpBasic(b -> b.disable()).formLogin(f -> f.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origin}") String origin) {
        CorsConfiguration c = new CorsConfiguration(); c.setAllowedOrigins(List.of(origin)); c.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type")); c.setExposedHeaders(List.of("Location")); c.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**", c); return source;
    }
}
