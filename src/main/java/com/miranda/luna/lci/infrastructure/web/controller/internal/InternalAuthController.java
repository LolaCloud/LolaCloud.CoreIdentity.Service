package com.miranda.luna.lci.infrastructure.web.controller.internal;

import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.model.OperatorSession;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.domain.repository.OperatorSessionRepositoryPort;
import com.miranda.luna.lci.infrastructure.security.JwtService;
import com.miranda.luna.lci.infrastructure.web.dto.internal.InternalTokenIntrospectionResponse;
import com.miranda.luna.lci.infrastructure.web.dto.internal.InternalValidateTokenRequest;
import com.miranda.luna.lci.infrastructure.web.dto.internal.OperatorSummaryDto;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Internal Identity & Token Introspection Controller.
 * <p>
 * Lola Core Identity (LCI) does NOT act as a BFF proxying traffic for other services.
 * Specialized services (e.g., Lola Relate, Lola Stacker, Lola Runway, Lola Keeper, Lola Store)
 * interact with LCI through this internal API to validate tokens and retrieve operator identities and RBAC permissions.
 * </p>
 */
@RestController
@RequestMapping("/internal/v1/auth")
public class InternalAuthController {

    private final JwtService jwtService;
    private final OperatorRepositoryPort operatorRepository;
    private final OperatorSessionRepositoryPort sessionRepository;

    public InternalAuthController(
            JwtService jwtService,
            OperatorRepositoryPort operatorRepository,
            OperatorSessionRepositoryPort sessionRepository
    ) {
        this.jwtService = jwtService;
        this.operatorRepository = operatorRepository;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/introspect")
    public ResponseEntity<InternalTokenIntrospectionResponse> introspectToken(
            @RequestBody InternalValidateTokenRequest request
    ) {
        if (request == null || request.token() == null || request.token().isBlank()) {
            return ResponseEntity.badRequest().body(InternalTokenIntrospectionResponse.invalid("Token is required"));
        }

        try {
            Claims claims = jwtService.extractAllClaims(request.token());
            if (claims.getExpiration().before(new Date())) {
                return ResponseEntity.ok(InternalTokenIntrospectionResponse.invalid("Token has expired"));
            }

            UUID sessionId = jwtService.extractSessionId(request.token());
            if (sessionId == null) {
                return ResponseEntity.ok(InternalTokenIntrospectionResponse.invalid("Token does not contain a session"));
            }

            Optional<OperatorSession> sessionOpt = sessionRepository.findById(sessionId);
            if (sessionOpt.isEmpty()) {
                return ResponseEntity.ok(InternalTokenIntrospectionResponse.invalid("Session not found"));
            }

            OperatorSession session = sessionOpt.get();
            if (session.isRevoked()) {
                return ResponseEntity.ok(InternalTokenIntrospectionResponse.invalid("Session has been revoked"));
            }

            if (session.isExpired()) {
                return ResponseEntity.ok(InternalTokenIntrospectionResponse.invalid("Session has expired"));
            }

            // Update session activity
            session.touch();
            sessionRepository.save(session);

            String username = claims.getSubject();
            Operator operator = operatorRepository.findByUsername(username)
                    .orElse(null);

            if (operator == null) {
                return ResponseEntity.ok(InternalTokenIntrospectionResponse.invalid("Operator not found"));
            }

            OperatorSummaryDto summary = new OperatorSummaryDto(
                    operator.getId(),
                    operator.getUsername(),
                    operator.getPermissions()
            );

            return ResponseEntity.ok(InternalTokenIntrospectionResponse.success(summary));
        } catch (Exception e) {
            return ResponseEntity.ok(InternalTokenIntrospectionResponse.invalid("Invalid token: " + e.getMessage()));
        }
    }
}
