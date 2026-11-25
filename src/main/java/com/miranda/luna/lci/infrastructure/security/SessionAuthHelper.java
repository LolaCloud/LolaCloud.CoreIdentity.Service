package com.miranda.luna.lci.infrastructure.security;

import com.miranda.luna.lci.domain.exception.UnauthorizedException;
import com.miranda.luna.lci.domain.model.OperatorSession;
import com.miranda.luna.lci.domain.repository.OperatorSessionRepositoryPort;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SessionAuthHelper {

    private final JwtService jwtService;
    private final OperatorSessionRepositoryPort sessionRepository;

    public SessionAuthHelper(JwtService jwtService, OperatorSessionRepositoryPort sessionRepository) {
        this.jwtService = jwtService;
        this.sessionRepository = sessionRepository;
    }

    public OperatorSession validateAndGetSession(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException("Missing Bearer token");
        }

        UUID sessionId;
        try {
            sessionId = jwtService.extractSessionId(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        if (sessionId == null) {
            throw new UnauthorizedException("Token missing sessionId claim");
        }

        OperatorSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new UnauthorizedException("Session not found"));

        if (session.isRevoked()) {
            throw new UnauthorizedException("Session has been revoked");
        }

        if (session.isExpired()) {
            throw new UnauthorizedException("Session has expired");
        }

        return session;
    }
}
