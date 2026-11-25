package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.exception.ResourceNotFoundException;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetOperatorUseCase {

    private final OperatorRepositoryPort operatorRepository;

    public GetOperatorUseCase(OperatorRepositoryPort operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    public OperatorResponse execute(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new ResourceNotFoundException("Operator not found: " + identifier);
        }

        Operator operator = findByIdentifier(identifier.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found: " + identifier));

        return new OperatorResponse(
                operator.getId(),
                operator.getUsername(),
                operator.getPermissions(),
                operator.getStatus()
        );
    }

    public OperatorResponse execute(UUID id) {
        if (id == null) {
            throw new ResourceNotFoundException("Operator not found: null");
        }
        return execute(id.toString());
    }

    private Optional<Operator> findByIdentifier(String identifier) {
        try {
            UUID id = UUID.fromString(identifier);
            Optional<Operator> byId = operatorRepository.findById(id);
            if (byId.isPresent()) {
                return byId;
            }
        } catch (IllegalArgumentException ignored) {
            // Not a UUID string, search by username
        }
        return operatorRepository.findByUsername(identifier);
    }
}
