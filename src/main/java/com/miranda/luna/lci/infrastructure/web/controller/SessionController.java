package com.miranda.luna.lci.infrastructure.web.controller;

import com.miranda.luna.lci.domain.exception.InvalidOperationException;
import com.miranda.luna.lci.domain.exception.ResourceNotFoundException;
import com.miranda.luna.lci.domain.model.OperatorSession;
import com.miranda.luna.lci.domain.repository.OperatorSessionRepositoryPort;
import com.miranda.luna.lci.infrastructure.security.SessionAuthHelper;
import com.miranda.luna.lci.infrastructure.web.dto.SessionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionAuthHelper authHelper;
    private final OperatorSessionRepositoryPort sessionRepository;

    public SessionController(SessionAuthHelper authHelper, OperatorSessionRepositoryPort sessionRepository) {
        this.authHelper = authHelper;
        this.sessionRepository = sessionRepository;
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> listActiveSessions(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        OperatorSession currentSession = authHelper.validateAndGetSession(authHeader);

        // Update current session activity
        currentSession.touch();
        sessionRepository.save(currentSession);

        List<OperatorSession> activeSessions = sessionRepository.findActiveByOperatorId(currentSession.getOperatorId());

        List<SessionResponse> response = activeSessions.stream()
                .map(session -> new SessionResponse(
                        session.getId(),
                        session.getDeviceType(),
                        session.getBrowser(),
                        session.getOperatingSystem(),
                        session.getIpAddress(),
                        session.getCreatedAt(),
                        session.getLastActiveAt(),
                        session.getExpiresAt(),
                        session.getId().equals(currentSession.getId())
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> revokeSession(
            @PathVariable("id") UUID id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        OperatorSession currentSession = authHelper.validateAndGetSession(authHeader);

        if (id.equals(currentSession.getId())) {
            throw new InvalidOperationException("Cannot disconnect the current active session. Use sign out instead.");
        }

        OperatorSession targetSession = sessionRepository.findById(id)
                .filter(session -> session.getOperatorId().equals(currentSession.getOperatorId()))
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        targetSession.revoke();
        sessionRepository.save(targetSession);

        return ResponseEntity.ok(Map.of(
                "message", "Session revoked successfully"
        ));
    }
}
