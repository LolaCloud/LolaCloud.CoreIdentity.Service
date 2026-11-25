package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListOperatorsUseCaseTest {

    private OperatorRepositoryPort operatorRepository;
    private ListOperatorsUseCase useCase;

    @BeforeEach
    void setUp() {
        operatorRepository = mock(OperatorRepositoryPort.class);
        useCase = new ListOperatorsUseCase(operatorRepository);
    }

    @Test
    @DisplayName("Returns empty list when no operators exist")
    void returnsEmptyList() {
        when(operatorRepository.findAll()).thenReturn(List.of());

        List<OperatorResponse> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(operatorRepository).findAll();
    }

    @Test
    @DisplayName("Returns all mapped operators")
    void returnsAllOperators() {
        Operator op1 = new Operator(UUID.randomUUID(), "root", "hash1", List.of("*::*::*"), Instant.now(), Instant.now());
        Operator op2 = new Operator(UUID.randomUUID(), "operator1", "hash2", List.of("relate::*"), Instant.now(), Instant.now());

        when(operatorRepository.findAll()).thenReturn(List.of(op1, op2));

        List<OperatorResponse> result = useCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("root", result.get(0).username());
        assertEquals("operator1", result.get(1).username());
        assertEquals(List.of("*::*::*"), result.get(0).permissions());
        assertEquals(List.of("relate::*"), result.get(1).permissions());
    }
}
