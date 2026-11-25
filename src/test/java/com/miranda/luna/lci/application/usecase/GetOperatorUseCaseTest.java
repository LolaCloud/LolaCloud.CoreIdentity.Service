package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.exception.ResourceNotFoundException;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetOperatorUseCaseTest {

    private OperatorRepositoryPort operatorRepository;
    private GetOperatorUseCase useCase;

    @BeforeEach
    void setUp() {
        operatorRepository = mock(OperatorRepositoryPort.class);
        useCase = new GetOperatorUseCase(operatorRepository);
    }

    @Test
    @DisplayName("Successfully retrieves operator by UUID")
    void getOperatorByUuid() {
        UUID id = UUID.randomUUID();
        Operator operator = new Operator(id, "operator1", "hash", List.of("runway::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findById(id)).thenReturn(Optional.of(operator));

        OperatorResponse response = useCase.execute(id.toString());

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("operator1", response.username());
        assertEquals(List.of("runway::*"), response.permissions());
        assertEquals("ACTIVE", response.status());
        verify(operatorRepository).findById(id);
    }

    @Test
    @DisplayName("Successfully retrieves operator by username")
    void getOperatorByUsername() {
        UUID id = UUID.randomUUID();
        Operator operator = new Operator(id, "root", "hash", List.of("*::*::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findByUsername("root")).thenReturn(Optional.of(operator));

        OperatorResponse response = useCase.execute("root");

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("root", response.username());
        assertEquals(List.of("*::*::*"), response.permissions());
        verify(operatorRepository).findByUsername("root");
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException when operator does not exist")
    void throwsWhenNotFound() {
        when(operatorRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute("unknown"));
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException when identifier is blank")
    void throwsWhenBlank() {
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute("   "));
    }
}
