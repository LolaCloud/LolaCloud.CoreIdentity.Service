package com.miranda.luna.lci.infrastructure.security;

import com.miranda.luna.lci.domain.model.Operator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationHours;

    public JwtService(
            @Value("${lola.security.jwt.secret:lola-cloud-platform-secret-encryption-key-for-jwt-signing-256-bits-minimum}") String secret,
            @Value("${lola.security.jwt.expiration-hours:24}") long expirationHours
    ) {
        // Ensure key is at least 256 bits (32 bytes)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationHours = expirationHours;
    }

    public String generateToken(Operator operator) {
        return generateToken(operator, null);
    }

    public String generateToken(Operator operator, UUID sessionId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expirationHours, ChronoUnit.HOURS);

        var builder = Jwts.builder()
                .subject(operator.getUsername())
                .claim("operatorId", operator.getId().toString())
                .claim("permissions", operator.getPermissions())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey);

        if (sessionId != null) {
            builder.claim("sessionId", sessionId.toString());
        }

        return builder.compact();
    }

    public UUID extractSessionId(String token) {
        try {
            String sessionId = extractAllClaims(token).get("sessionId", String.class);
            return sessionId != null ? UUID.fromString(sessionId) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public UUID extractOperatorId(String token) {
        try {
            String operatorId = extractAllClaims(token).get("operatorId", String.class);
            return operatorId != null ? UUID.fromString(operatorId) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public long getExpirationHours() {
        return expirationHours;
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        return extractAllClaims(token).get("permissions", List.class);
    }

    public boolean isTokenValid(String token, String username) {
        try {
            Claims claims = extractAllClaims(token);
            boolean usernameMatches = claims.getSubject().equalsIgnoreCase(username);
            boolean notExpired = claims.getExpiration().after(new Date());
            return usernameMatches && notExpired;
        } catch (Exception e) {
            return false;
        }
    }
}
