package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.exception.InvalidOperationException;
import com.miranda.luna.lci.domain.exception.ResourceNotFoundException;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import com.miranda.luna.lci.infrastructure.web.dto.UpdateOperatorRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

class UpdateOperatorUseCaseTest {

    private OperatorRepositoryPort operatorRepository;
    private PasswordEncoder passwordEncoder;
    private UpdateOperatorUseCase useCase;

    @BeforeEach
    void setUp() {
        operatorRepository = mock(OperatorRepositoryPort.class);
        passwordEncoder = mock(PasswordEncoder.class);
        useCase = new UpdateOperatorUseCase(operatorRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Successfully updates operator permissions and status")
    void updatesPermissionsAndStatus() {
        UUID id = UUID.randomUUID();
        Operator existing = new Operator(id, "operator1", "existingHash", List.of("runway::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findById(id)).thenReturn(Optional.of(existing));
        when(operatorRepository.save(any(Operator.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateOperatorRequest request = new UpdateOperatorRequest(null, List.of("relate::*", "stacker::*"), "DISABLED");
        OperatorResponse response = useCase.execute(id.toString(), request);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("operator1", response.username());
        assertEquals(List.of("relate::*", "stacker::*"), response.permissions());
        assertEquals("DISABLED", response.status());

        ArgumentCaptor<Operator> captor = ArgumentCaptor.forClass(Operator.class);
        verify(operatorRepository).save(captor.capture());
        assertEquals("existingHash", captor.getValue().getPassword(), "Existing password hash should be preserved");
        assertEquals("DISABLED", captor.getValue().getStatus());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Successfully updates password when provided")
    void updatesPasswordWhenProvided() {
        UUID id = UUID.randomUUID();
        Operator existing = new Operator(id, "operator1", "oldHash", List.of("runway::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findById(id)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$12$newBcryptHashedPassword12345");
        when(operatorRepository.save(any(Operator.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateOperatorRequest request = new UpdateOperatorRequest("newPassword123", List.of("runway::*"), "ACTIVE");
        OperatorResponse response = useCase.execute(id.toString(), request);

        assertNotNull(response);
        ArgumentCaptor<Operator> captor = ArgumentCaptor.forClass(Operator.class);
        verify(operatorRepository).save(captor.capture());
        assertEquals("$2a$12$newBcryptHashedPassword12345", captor.getValue().getPassword());
        verify(passwordEncoder).encode("newPassword123");
    }

    @Test
    @DisplayName("Preserves existing password when password is blank or empty")
    void preservesPasswordWhenBlank() {
        UUID id = UUID.randomUUID();
        Operator existing = new Operator(id, "operator1", "oldHash", List.of("runway::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findById(id)).thenReturn(Optional.of(existing));
        when(operatorRepository.save(any(Operator.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateOperatorRequest request = new UpdateOperatorRequest("   ", List.of("runway::*"), "ACTIVE");
        useCase.execute(id.toString(), request);

        ArgumentCaptor<Operator> captor = ArgumentCaptor.forClass(Operator.class);
        verify(operatorRepository).save(captor.capture());
        assertEquals("oldHash", captor.getValue().getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Protects root operator from removing *::*::* permission")
    void protectsRootOperatorGlobalWildcard() {
        UUID rootId = UUID.randomUUID();
        Operator root = new Operator(rootId, "root", "rootHash", List.of("*::*::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findByUsername("root")).thenReturn(Optional.of(root));

        UpdateOperatorRequest request = new UpdateOperatorRequest(null, List.of("runway::dns::manage"), "ACTIVE");

        InvalidOperationException ex = assertThrows(
                InvalidOperationException.class,
                () -> useCase.execute("root", request, "root")
        );
        assertTrue(ex.getMessage().contains("Root operator must retain global wildcard permission"));
        verify(operatorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Allows updating root operator when *::*::* permission is retained by root caller")
    void allowsUpdatingRootWhenGlobalWildcardRetained() {
        UUID rootId = UUID.randomUUID();
        Operator root = new Operator(rootId, "root", "rootHash", List.of("*::*::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findByUsername("root")).thenReturn(Optional.of(root));
        when(operatorRepository.save(any(Operator.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateOperatorRequest request = new UpdateOperatorRequest(null, List.of("*::*::*", "extra::perm"), "ACTIVE");
        OperatorResponse response = useCase.execute("root", request, "root");

        assertNotNull(response);
        assertTrue(response.permissions().contains("*::*::*"));
        verify(operatorRepository).save(any(Operator.class));
    }

    @Test
    @DisplayName("Throws ForbiddenException when non-root caller attempts to edit root")
    void throwsForbiddenWhenNonRootAttemptsToEditRoot() {
        UUID rootId = UUID.randomUUID();
        Operator root = new Operator(rootId, "root", "rootHash", List.of("*::*::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findByUsername("root")).thenReturn(Optional.of(root));

        UpdateOperatorRequest request = new UpdateOperatorRequest(null, List.of("*::*::*"), "ACTIVE");
        com.miranda.luna.lci.domain.exception.ForbiddenException ex = assertThrows(
                com.miranda.luna.lci.domain.exception.ForbiddenException.class,
                () -> useCase.execute("root", request, "admin_user")
        );
        assertEquals("Only the root operator can modify the root account", ex.getMessage());
        verify(operatorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws ForbiddenException when unauthenticated caller attempts to edit root")
    void throwsForbiddenWhenUnauthenticatedAttemptsToEditRoot() {
        UUID rootId = UUID.randomUUID();
        Operator root = new Operator(rootId, "root", "rootHash", List.of("*::*::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findByUsername("root")).thenReturn(Optional.of(root));

        UpdateOperatorRequest request = new UpdateOperatorRequest(null, List.of("*::*::*"), "ACTIVE");
        com.miranda.luna.lci.domain.exception.ForbiddenException ex = assertThrows(
                com.miranda.luna.lci.domain.exception.ForbiddenException.class,
                () -> useCase.execute("root", request)
        );
        assertEquals("Only the root operator can modify the root account", ex.getMessage());
        verify(operatorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException when operator not found")
    void throwsWhenOperatorNotFound() {
        when(operatorRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UpdateOperatorRequest request = new UpdateOperatorRequest(null, List.of("runway::*"), "ACTIVE");
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute("nonexistent", request));
    }

    @Test
    @DisplayName("Throws InvalidOperationException when permissions list is null")
    void throwsWhenPermissionsNull() {
        UUID id = UUID.randomUUID();
        Operator existing = new Operator(id, "operator1", "hash", List.of("runway::*"), "ACTIVE", Instant.now(), Instant.now());
        when(operatorRepository.findById(id)).thenReturn(Optional.of(existing));

        UpdateOperatorRequest request = new UpdateOperatorRequest(null, null, "ACTIVE");
        assertThrows(InvalidOperationException.class, () -> useCase.execute(id.toString(), request));
    }
}
