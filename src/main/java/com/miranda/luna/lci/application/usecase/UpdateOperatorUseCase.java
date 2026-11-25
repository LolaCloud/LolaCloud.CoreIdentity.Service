package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.exception.ForbiddenException;
import com.miranda.luna.lci.domain.exception.InvalidOperationException;
import com.miranda.luna.lci.domain.exception.ResourceNotFoundException;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import com.miranda.luna.lci.infrastructure.web.dto.UpdateOperatorRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateOperatorUseCase {

    private final OperatorRepositoryPort operatorRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateOperatorUseCase(OperatorRepositoryPort operatorRepository, PasswordEncoder passwordEncoder) {
        this.operatorRepository = operatorRepository;
        this.passwordEncoder = (passwordEncoder != null) ? passwordEncoder : new BCryptPasswordEncoder(12);
    }

    public OperatorResponse execute(String identifier, UpdateOperatorRequest request) {
        return execute(identifier, request, null);
    }

    public OperatorResponse execute(UUID id, UpdateOperatorRequest request) {
        if (id == null) {
            throw new ResourceNotFoundException("Operator not found: null");
        }
        return execute(id.toString(), request, null);
    }

    public OperatorResponse execute(UUID id, UpdateOperatorRequest request, String callerUsername) {
        if (id == null) {
            throw new ResourceNotFoundException("Operator not found: null");
        }
        return execute(id.toString(), request, callerUsername);
    }

    public OperatorResponse execute(String identifier, UpdateOperatorRequest request, String callerUsername) {
        if (identifier == null || identifier.isBlank()) {
            throw new ResourceNotFoundException("Operator not found: " + identifier);
        }

        if (request == null) {
            throw new InvalidOperationException("Request body cannot be null");
        }

        if (request.permissions() == null) {
            throw new InvalidOperationException("Permissions list is required");
        }

        Operator existing = findByIdentifier(identifier.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found: " + identifier));

        String effectiveCaller = callerUsername;
        if (effectiveCaller == null || effectiveCaller.isBlank()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                effectiveCaller = auth.getName();
            }
        }

        // Rule: The root account can ONLY be edited by root itself
        if (existing.isRoot() || "root".equalsIgnoreCase(existing.getUsername())) {
            if (effectiveCaller == null || !"root".equalsIgnoreCase(effectiveCaller)) {
                throw new ForbiddenException("Only the root operator can modify the root account");
            }
        }

        List<String> cleanedPermissions = request.permissions().stream()
                .filter(p -> p != null && !p.isBlank())
                .map(String::trim)
                .distinct()
                .toList();

        // If operator is root: prevent removing *::*::* permission
        if (existing.isRoot() || "root".equalsIgnoreCase(existing.getUsername())) {
            if (!cleanedPermissions.contains("*::*::*")) {
                throw new InvalidOperationException("Root operator must retain global wildcard permission (*::*::*)");
            }
        }

        // Password handling: if provided and non-blank, hash with BCrypt (cost 12). If blank or null, preserve existing.
        String passwordToSave;
        if (request.password() != null && !request.password().isBlank()) {
            if (request.password().length() < 4) {
                throw new InvalidOperationException("Password must be at least 4 characters");
            }
            passwordToSave = passwordEncoder.encode(request.password());
        } else {
            passwordToSave = existing.getPassword();
        }

        // Status handling
        String statusToSave;
        if (request.status() != null && !request.status().isBlank()) {
            statusToSave = request.status().trim().toUpperCase();
        } else {
            statusToSave = (existing.getStatus() != null && !existing.getStatus().isBlank())
                    ? existing.getStatus()
                    : "ACTIVE";
        }

        Operator updatedOperator = new Operator(
                existing.getId(),
                existing.getUsername(),
                passwordToSave,
                cleanedPermissions,
                statusToSave,
                existing.getCreatedAt(),
                Instant.now()
        );

        Operator saved = operatorRepository.save(updatedOperator);

        return new OperatorResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getPermissions(),
                saved.getStatus()
        );
    }


    private Optional<Operator> findByIdentifier(String identifier) {
        try {
            UUID id = UUID.fromString(identifier);
            Optional<Operator> byId = operatorRepository.findById(id);
            if (byId.isPresent()) {
                return byId;
            }
        } catch (IllegalArgumentException ignored) {
            // Not a valid UUID, search by username
        }
        return operatorRepository.findByUsername(identifier);
    }
}
