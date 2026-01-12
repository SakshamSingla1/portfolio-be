package com.portfolio.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    // ================= SIGNING KEY =================
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ================= GENERATE =================
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ================= EXTRACT =================
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ================= VALIDATE =================
    public boolean validateToken(String token, String email) {
        try {
            Claims claims = getClaims(token);
            return email.equals(claims.getSubject())
                    && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false; // ✅ never crash filter
        }
    }

    // ================= CLAIMS =================
    private Claims getClaims(String token) {

        // ✅ absolutely critical
        String cleanToken = token.trim();

        return Jwts.parser()
                .verifyWith(getSigningKey()) // ✅ new API
                .build()
                .parseSignedClaims(cleanToken)
                .getPayload();
    }
}
