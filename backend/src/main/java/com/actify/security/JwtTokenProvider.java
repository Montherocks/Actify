package com.actify.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import java.util.Date;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {
    
    private static final String SECRET_KEY = "ActifySecretKeyForJWTTokenGenerationAndValidation2024PleaseChangeInProduction";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // Generate token for volunteers/users
    public String generateToken(Long userId, String email) {
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("email", email)
            .claim("type", "user")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
    
    // Create token for admins (with email and role)
    public String createToken(String email, String role) {
        return Jwts.builder()
            .setSubject(email)
            .claim("email", email)
            .claim("role", role)
            .claim("type", "admin")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
    
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject());
    }
    
    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Get email from token
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.get("email", String.class);
    }
    
    // Get role from token
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.get("role", String.class);
    }
}
