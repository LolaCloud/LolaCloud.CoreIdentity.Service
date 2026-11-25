package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.exception.InvalidOperationException;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.infrastructure.web.dto.CreateOperatorRequest;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOperatorUseCaseTest {

    private OperatorRepositoryPort operatorRepository;
    private PasswordEncoder passwordEncoder;
    private CreateOperatorUseCase useCase;

    @BeforeEach
    void setUp() {
        operatorRepository = mock(OperatorRepositoryPort.class);
        passwordEncoder = mock(PasswordEncoder.class);
        useCase = new CreateOperatorUseCase(operatorRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Successfully creates operator with valid data and encodes password")
    void createOperatorSuccessfully() {
        CreateOperatorRequest request = new CreateOperatorRequest(
                "devops-user",
                "securePass123",
                List.of("relate::*", "stacker::container::create")
        );

        when(operatorRepository.findByUsername("devops-user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("securePass123")).thenReturn("$2a$10$encodedHash");
        when(operatorRepository.save(any(Operator.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OperatorResponse response = useCase.execute(request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("devops-user", response.username());
        assertEquals(2, response.permissions().size());
        assertTrue(response.permissions().contains("relate::*"));
        assertTrue(response.permissions().contains("stacker::container::create"));

        verify(operatorRepository).save(any(Operator.class));
        verify(passwordEncoder).encode("securePass123");
    }

    @Test
    @DisplayName("Defaults null permissions to empty list")
    void createOperatorWithNullPermissions() {
        CreateOperatorRequest request = new CreateOperatorRequest("operator2", "secret", null);

        when(operatorRepository.findByUsername("operator2")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(operatorRepository.save(any(Operator.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OperatorResponse response = useCase.execute(request);

        assertNotNull(response);
        assertEquals("operator2", response.username());
        assertNotNull(response.permissions());
        assertTrue(response.permissions().isEmpty());
    }

    @Test
    @DisplayName("Throws InvalidOperationException when operator with username already exists")
    void throwsWhenUsernameAlreadyExists() {
        CreateOperatorRequest request = new CreateOperatorRequest("root", "password", List.of());
        Operator existing = new Operator(UUID.randomUUID(), "root", "hash", List.of("*::*::*"), Instant.now(), Instant.now());

        when(operatorRepository.findByUsername("root")).thenReturn(Optional.of(existing));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> useCase.execute(request));
        assertEquals("Operator with username 'root' already exists", ex.getMessage());
        verify(operatorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws InvalidOperationException when username is blank or too short/long")
    void validatesUsernameConstraints() {
        assertThrows(InvalidOperationException.class, () ->
                useCase.execute(new CreateOperatorRequest("", "validPass", List.of())));
        assertThrows(InvalidOperationException.class, () ->
                useCase.execute(new CreateOperatorRequest("   ", "validPass", List.of())));
        assertThrows(InvalidOperationException.class, () ->
                useCase.execute(new CreateOperatorRequest("ab", "validPass", List.of())));
        assertThrows(InvalidOperationException.class, () ->
                useCase.execute(new CreateOperatorRequest("a".repeat(51), "validPass", List.of())));
    }

    @Test
    @DisplayName("Throws InvalidOperationException when password is blank or shorter than 4 characters")
    void validatesPasswordConstraints() {
        assertThrows(InvalidOperationException.class, () ->
                useCase.execute(new CreateOperatorRequest("validUser", "", List.of())));
        assertThrows(InvalidOperationException.class, () ->
                useCase.execute(new CreateOperatorRequest("validUser", "123", List.of())));
        assertThrows(InvalidOperationException.class, () ->
                useCase.execute(new CreateOperatorRequest("validUser", null, List.of())));
    }
}
