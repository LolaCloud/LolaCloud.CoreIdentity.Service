package com.miranda.luna.lci.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorTest {

    @Test
    @DisplayName("Root operator should have bypass for any permission")
    void rootOperatorHasUnconditionalBypass() {
        Operator root = Operator.create("root", "hash", List.of());

        assertTrue(root.can("runway::dns::manage"));
        assertTrue(root.can("stacker::container::restart"));
        assertTrue(root.can("relate::db::create"));
        assertTrue(root.can("any::random::permission"));
    }

    @Test
    @DisplayName("Operator with global wildcard *::*::* should have access to all actions")
    void globalWildcardGrantsAccessToAll() {
        Operator admin = Operator.create("admin", "hash", List.of("*::*::*"));

        assertTrue(admin.can("runway::dns::manage"));
        assertTrue(admin.can("stacker::container::restart"));
    }

    @Test
    @DisplayName("Operator with service wildcard should only have access to that service")
    void serviceWildcardGrantsAccessToSpecificServiceOnly() {
        Operator runwayOperator = Operator.create("netops", "hash", List.of("runway::*"));

        assertTrue(runwayOperator.can("runway::dns::manage"));
        assertTrue(runwayOperator.can("runway::tunnel::create"));
        assertFalse(runwayOperator.can("stacker::container::start"));
        assertFalse(runwayOperator.can("relate::db::create"));
    }

    @Test
    @DisplayName("Operator with exact permission should only have access to exact match")
    void exactPermissionMatch() {
        Operator containerRestarter = Operator.create("devops", "hash", List.of("stacker::container::restart"));

        assertTrue(containerRestarter.can("stacker::container::restart"));
        assertFalse(containerRestarter.can("stacker::container::delete"));
    }
}
