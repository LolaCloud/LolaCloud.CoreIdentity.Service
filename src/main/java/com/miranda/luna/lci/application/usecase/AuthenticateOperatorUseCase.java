package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.exception.InvalidCredentialsException;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.model.OperatorSession;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.domain.repository.OperatorSessionRepositoryPort;
import com.miranda.luna.lci.infrastructure.security.JwtService;
import com.miranda.luna.lci.infrastructure.security.UserAgentParser;
import com.miranda.luna.lci.infrastructure.web.dto.LoginResponse;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthenticateOperatorUseCase {

    private final OperatorRepositoryPort operatorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OperatorSessionRepositoryPort sessionRepository;
    private final UserAgentParser userAgentParser;

    public AuthenticateOperatorUseCase(
            OperatorRepositoryPort operatorRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            OperatorSessionRepositoryPort sessionRepository,
            UserAgentParser userAgentParser
    ) {
        this.operatorRepository = operatorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.sessionRepository = sessionRepository;
        this.userAgentParser = userAgentParser;
    }

    public LoginResponse execute(String username, String rawPassword) {
        return execute(username, rawPassword, "127.0.0.1", "Unknown");
    }

    public LoginResponse execute(String username, String rawPassword, String ipAddress, String userAgent) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidCredentialsException("Username and password are required");
        }

        Operator operator = operatorRepository.findByUsername(username.trim())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, operator.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        UserAgentParser.ClientInfo clientInfo = userAgentParser.parse(ipAddress, userAgent);
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtService.getExpirationHours(), ChronoUnit.HOURS);
        UUID sessionId = UUID.randomUUID();

        OperatorSession session = new OperatorSession(
                sessionId,
                operator.getId(),
                clientInfo.ipAddress(),
                clientInfo.deviceType(),
                clientInfo.browser(),
                clientInfo.operatingSystem(),
                clientInfo.userAgent(),
                false,
                now,
                now,
                expiresAt
        );

        sessionRepository.save(session);

        String token = jwtService.generateToken(operator, sessionId);
        OperatorResponse operatorResponse = new OperatorResponse(
                operator.getId(),
                operator.getUsername(),
                operator.getPermissions(),
                operator.getStatus()
        );

        return new LoginResponse(token, operatorResponse, sessionId);
    }
}
