package com.miranda.luna.lci.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorSessionTest {

    @Test
    void activeSessionBecomesInactiveWhenRevoked() {
        UUID operatorId = UUID.randomUUID();
        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
        OperatorSession session = OperatorSession.create(
                operatorId, "127.0.0.1", "Desktop", "Chrome", "Windows", "Mozilla/5.0", expiresAt
        );

        assertTrue(session.isActive());
        assertFalse(session.isRevoked());
        assertFalse(session.isExpired());

        session.revoke();

        assertTrue(session.isRevoked());
        assertFalse(session.isActive());
    }

    @Test
    void expiredSessionIsInactive() {
        UUID operatorId = UUID.randomUUID();
        Instant pastExpiry = Instant.now().minus(1, ChronoUnit.HOURS);
        OperatorSession session = OperatorSession.create(
                operatorId, "127.0.0.1", "Desktop", "Firefox", "Linux", "Mozilla/5.0", pastExpiry
        );

        assertTrue(session.isExpired());
        assertFalse(session.isActive());
    }
}
