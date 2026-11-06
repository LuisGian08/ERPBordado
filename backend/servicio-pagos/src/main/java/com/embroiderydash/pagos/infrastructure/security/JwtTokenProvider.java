package com.embroiderydash.pagos.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private static final Logger log=LoggerFactory.getLogger(JwtTokenProvider.class);
    private final SecretKey key; private final long expirationMs;
    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expirationMs=expirationMs;
    }
    public String generateToken(String username,List<String> roles) {
        Date now=new Date(); return Jwts.builder().subject(username).claim("roles",roles).issuedAt(now)
                .expiration(new Date(now.getTime()+expirationMs)).signWith(key).compact();
    }
    public boolean validateToken(String token) {
        try { Jwts.parser().verifyWith(key).build().parseSignedClaims(token); return true; }
        catch (JwtException|IllegalArgumentException e) { log.warn("Token JWT invÃ¡lido o expirado en servicio-pagos: {}",e.getMessage()); return false; }
    }
    public Authentication getAuthentication(String token) {
        Claims claims=Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        List<?> roles=claims.get("roles",List.class);
        Collection<? extends GrantedAuthority> authorities=roles==null?Collections.emptyList():roles.stream().map(String::valueOf)
                .map(r->r.startsWith("ROLE_")?r:"ROLE_"+r).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(claims.getSubject(),null,authorities);
    }
}
