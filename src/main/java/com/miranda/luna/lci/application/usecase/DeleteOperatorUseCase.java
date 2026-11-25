package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.exception.InvalidOperationException;
import com.miranda.luna.lci.domain.exception.ResourceNotFoundException;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteOperatorUseCase {

    private final OperatorRepositoryPort operatorRepository;

    public DeleteOperatorUseCase(OperatorRepositoryPort operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    public void execute(UUID id) {
        if (id == null) {
            throw new InvalidOperationException("Operator ID cannot be null");
        }

        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found with id: " + id));

        if (operator.isRoot() || "root".equalsIgnoreCase(operator.getUsername())) {
            throw new InvalidOperationException("Cannot delete the root super administrator");
        }

        operatorRepository.deleteById(id);
    }

    public void execute(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new InvalidOperationException("Operator identifier cannot be null");
        }

        Operator operator = findByIdentifier(identifier.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found: " + identifier));

        if (operator.isRoot() || "root".equalsIgnoreCase(operator.getUsername())) {
            throw new InvalidOperationException("Cannot delete the root super administrator");
        }

        operatorRepository.deleteById(operator.getId());
    }

    private java.util.Optional<Operator> findByIdentifier(String identifier) {
        try {
            UUID id = UUID.fromString(identifier);
            java.util.Optional<Operator> byId = operatorRepository.findById(id);
            if (byId.isPresent()) {
                return byId;
            }
        } catch (IllegalArgumentException ignored) {
            // Not a valid UUID, search by username
        }
        return operatorRepository.findByUsername(identifier);
    }
}
