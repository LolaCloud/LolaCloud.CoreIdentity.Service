package com.miranda.luna.lci.infrastructure.web.controller;

import com.miranda.luna.lci.application.usecase.AuthenticateOperatorUseCase;
import com.miranda.luna.lci.domain.model.OperatorSession;
import com.miranda.luna.lci.domain.repository.OperatorSessionRepositoryPort;
import com.miranda.luna.lci.infrastructure.security.JwtService;
import com.miranda.luna.lci.infrastructure.security.UserAgentParser;
import com.miranda.luna.lci.infrastructure.web.dto.ApiResponse;
import com.miranda.luna.lci.infrastructure.web.dto.LoginRequest;
import com.miranda.luna.lci.infrastructure.web.dto.LoginResponse;
import com.miranda.luna.lci.infrastructure.web.filter.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping
public class AuthController {

    private final AuthenticateOperatorUseCase authenticateOperatorUseCase;
    private final JwtService jwtService;
    private final OperatorSessionRepositoryPort operatorSessionRepository;

    public AuthController(
            AuthenticateOperatorUseCase authenticateOperatorUseCase,
            JwtService jwtService,
            OperatorSessionRepositoryPort operatorSessionRepository
    ) {
        this.authenticateOperatorUseCase = authenticateOperatorUseCase;
        this.jwtService = jwtService;
        this.operatorSessionRepository = operatorSessionRepository;
    }

    @PostMapping({"/api/v1/auth/login", "/auth/login"})
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest servletRequest
    ) {
        String ipAddress = UserAgentParser.extractClientIp(servletRequest);
        String userAgent = servletRequest != null ? servletRequest.getHeader("User-Agent") : null;
        LoginResponse response = authenticateOperatorUseCase.execute(
                request.username(),
                request.password(),
                ipAddress,
                userAgent
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/auth/logout", "/auth/logout"})
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest servletRequest
    ) {
        String requestId = null;
        if (servletRequest != null) {
            Object reqIdAttr = servletRequest.getAttribute(RequestIdFilter.REQUEST_ID_ATTRIBUTE);
            if (reqIdAttr != null) {
                requestId = reqIdAttr.toString();
            }
        }
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.ok(ApiResponse.of(Map.of("message", "Logged out"), requestId));
        }

        String token = authHeader.trim();
        if (token.regionMatches(true, 0, "Bearer ", 0, 7)) {
            token = token.substring(7).trim();
        }

        if (token.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.of(Map.of("message", "Logged out"), requestId));
        }

        try {
            UUID sessionId = jwtService.extractSessionId(token);
            if (sessionId != null) {
                Optional<OperatorSession> sessionOpt = operatorSessionRepository.findById(sessionId);
                if (sessionOpt.isPresent()) {
                    OperatorSession session = sessionOpt.get();
                    session.revoke();
                    operatorSessionRepository.save(session);
                    return ResponseEntity.ok(ApiResponse.of(Map.of("message", "Logged out successfully"), requestId));
                }
            }
        } catch (Exception ignored) {
            // Graceful handling for invalid/expired tokens
        }

        return ResponseEntity.ok(ApiResponse.of(Map.of("message", "Logged out"), requestId));
    }
}
