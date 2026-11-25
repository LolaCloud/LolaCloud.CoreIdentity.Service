package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.exception.InvalidOperationException;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.infrastructure.web.dto.CreateOperatorRequest;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CreateOperatorUseCase {

    private final OperatorRepositoryPort operatorRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateOperatorUseCase(OperatorRepositoryPort operatorRepository, PasswordEncoder passwordEncoder) {
        this.operatorRepository = operatorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public OperatorResponse execute(CreateOperatorRequest request) {
        if (request == null) {
            throw new InvalidOperationException("Request body cannot be null");
        }
        return execute(request.username(), request.password(), request.permissions());
    }

    public OperatorResponse execute(String username, String rawPassword, List<String> permissions) {
        if (username == null || username.isBlank()) {
            throw new InvalidOperationException("Username cannot be blank");
        }
        String trimmedUsername = username.trim();
        if (trimmedUsername.length() < 3 || trimmedUsername.length() > 50) {
            throw new InvalidOperationException("Username must be between 3 and 50 characters");
        }

        if (rawPassword == null || rawPassword.isBlank() || rawPassword.length() < 4) {
            throw new InvalidOperationException("Password must be at least 4 characters");
        }

        if (operatorRepository.findByUsername(trimmedUsername).isPresent()) {
            throw new InvalidOperationException("Operator with username '" + trimmedUsername + "' already exists");
        }

        List<String> validatedPermissions = (permissions == null)
                ? List.of()
                : permissions.stream()
                        .filter(p -> p != null && !p.isBlank())
                        .map(String::trim)
                        .toList();

        String encodedPassword = passwordEncoder.encode(rawPassword);
        Instant now = Instant.now();
        Operator operator = new Operator(UUID.randomUUID(), trimmedUsername, encodedPassword, validatedPermissions, now, now);
        Operator saved = operatorRepository.save(operator);

        return new OperatorResponse(saved.getId(), saved.getUsername(), saved.getPermissions(), saved.getStatus());
    }
}
