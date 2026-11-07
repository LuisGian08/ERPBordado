package com.embroiderydash.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokens;
    public JwtAuthenticationFilter(JwtTokenProvider tokens) { this.tokens=tokens; }
    @Override protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {
        String header=request.getHeader("Authorization");
        if (header!=null && header.startsWith("Bearer ")) {
            String token=header.substring(7);
            if (tokens.isValid(token)) { Authentication authentication=tokens.authentication(token); SecurityContextHolder.getContext().setAuthentication(authentication); }
        }
        chain.doFilter(request,response);
    }
}
