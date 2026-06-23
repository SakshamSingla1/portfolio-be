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
    public String generateAccessToken(String email, String userId) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 5-minute pending token issued mid-login when 2FA is required.
    // JwtAuthFilter rejects it for normal API calls (type != null).
    public String generatePendingToken(String email, Long userId) {
        long fiveMinutes = 5 * 60 * 1000L;
        return Jwts.builder()
                .subject(email)
                .claim("userId", String.valueOf(userId))
                .claim("type", "PENDING_2FA")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + fiveMinutes))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isPendingToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "PENDING_2FA".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    // ================= EXTRACT =================
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        Claims claims = getClaims(token);
        Object userId = claims.get("userId");
        if (userId == null) return null;
        return userId.toString();
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
