package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.exception.InvalidOperationException;
import com.miranda.luna.lci.domain.exception.ResourceNotFoundException;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteOperatorUseCaseTest {

    private OperatorRepositoryPort operatorRepository;
    private DeleteOperatorUseCase useCase;

    @BeforeEach
    void setUp() {
        operatorRepository = mock(OperatorRepositoryPort.class);
        useCase = new DeleteOperatorUseCase(operatorRepository);
    }

    @Test
    @DisplayName("Successfully deletes an existing non-root operator")
    void deletesNonRootOperatorSuccessfully() {
        UUID id = UUID.randomUUID();
        Operator operator = new Operator(id, "operator1", "hash", List.of(), Instant.now(), Instant.now());

        when(operatorRepository.findById(id)).thenReturn(Optional.of(operator));

        useCase.execute(id);

        verify(operatorRepository).deleteById(id);
    }

    @Test
    @DisplayName("Throws InvalidOperationException when attempting to delete the root operator")
    void preventsDeletingRootOperator() {
        UUID rootId = UUID.randomUUID();
        Operator rootOperator = new Operator(rootId, "root", "hash", List.of("*::*::*"), Instant.now(), Instant.now());

        when(operatorRepository.findById(rootId)).thenReturn(Optional.of(rootOperator));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> useCase.execute(rootId));
        assertEquals("Cannot delete the root super administrator", ex.getMessage());
        verify(operatorRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Throws InvalidOperationException when attempting to delete the root operator by username")
    void preventsDeletingRootOperatorByUsername() {
        UUID rootId = UUID.randomUUID();
        Operator rootOperator = new Operator(rootId, "root", "hash", List.of("*::*::*"), Instant.now(), Instant.now());

        when(operatorRepository.findByUsername("root")).thenReturn(Optional.of(rootOperator));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> useCase.execute("root"));
        assertEquals("Cannot delete the root super administrator", ex.getMessage());
        verify(operatorRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException when operator does not exist")
    void throwsWhenOperatorNotFound() {
        UUID randomId = UUID.randomUUID();
        when(operatorRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(randomId));
        verify(operatorRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Throws InvalidOperationException when ID is null")
    void throwsWhenIdIsNull() {
        assertThrows(InvalidOperationException.class, () -> useCase.execute((UUID) null));
        assertThrows(InvalidOperationException.class, () -> useCase.execute((String) null));
        verify(operatorRepository, never()).deleteById(any());
    }
}
