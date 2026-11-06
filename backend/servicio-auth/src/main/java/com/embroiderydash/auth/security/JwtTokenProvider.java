package com.embroiderydash.auth.security;

import com.embroiderydash.auth.domain.UserAccount;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long expirationMs;
    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) throw new IllegalArgumentException("JWT_SECRET debe tener al menos 256 bits (32 bytes)");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expirationMs = expirationMs;
    }
    public String issue(UserAccount user) {
        Date now = new Date();
        return Jwts.builder().subject(user.getUsername()).claim("roles", List.of(user.getRole().name()))
                .issuedAt(now).expiration(new Date(now.getTime() + expirationMs)).signWith(key).compact();
    }
    public boolean isValid(String token) {
        try { Jwts.parser().verifyWith(key).build().parseSignedClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }
    @SuppressWarnings("unchecked")
    public Authentication authentication(String token) {
        Claims claims=Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        List<String> roles=claims.get("roles", List.class);
        Collection<? extends GrantedAuthority> authorities=roles==null?Collections.emptyList():roles.stream()
                .map(r -> r.startsWith("ROLE_")?r:"ROLE_"+r).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }
    public long getExpirationMs() { return expirationMs; }
}
