package com.embroiderydash.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtVerifier {
    private final SecretKey key;
    public JwtVerifier(@Value("${app.jwt.secret}") String secret) {
        byte[] bytes=secret.getBytes(StandardCharsets.UTF_8);
        if(bytes.length<32)throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes UTF-8");
        this.key=Keys.hmacShaKeyFor(bytes);
    }
    public boolean valid(String token) {
        try { Jwts.parser().verifyWith(key).build().parseSignedClaims(token);return true; }
        catch(JwtException|IllegalArgumentException ex){return false;}
    }
}
