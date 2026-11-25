package com.miranda.luna.lci.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure Domain Entity representing a LolaCloud Operator.
 * Contains core business logic and permission verification with zero external framework dependencies.
 */
public class Operator {

    private final UUID id;
    private final String username;
    private final String password;
    private final List<String> permissions;
    private final String status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Operator(UUID id, String username, String password, List<String> permissions, Instant createdAt, Instant updatedAt) {
        this(id, username, password, permissions, "ACTIVE", createdAt, updatedAt);
    }

    public Operator(UUID id, String username, String password, List<String> permissions, String status, Instant createdAt, Instant updatedAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.username = Objects.requireNonNull(username, "Username must not be null");
        this.password = Objects.requireNonNull(password, "Password must not be null");
        this.permissions = permissions != null ? Collections.unmodifiableList(permissions) : List.of();
        this.status = (status != null && !status.isBlank()) ? status : "ACTIVE";
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static Operator create(String username, String encodedPassword, List<String> permissions) {
        return create(username, encodedPassword, permissions, "ACTIVE");
    }

    public static Operator create(String username, String encodedPassword, List<String> permissions, String status) {
        Instant now = Instant.now();
        return new Operator(UUID.randomUUID(), username, encodedPassword, permissions, status, now, now);
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isRoot() {
        return "root".equalsIgnoreCase(this.username);
    }

    /**
     * In-memory RBAC evaluation following the hierarchical slug pattern: service::resource::action.
     * Root operator has unconditional bypass.
     * Supports wildcards like '*::*::*', 'runway::*', 'stacker::container::*'.
     */
    public boolean can(String requiredPermission) {
        if (requiredPermission == null || requiredPermission.isBlank()) {
            return false;
        }

        if (isRoot()) {
            return true;
        }

        return permissions.stream().anyMatch(slug -> {
            if ("*::*::*".equals(slug) || slug.equals(requiredPermission)) {
                return true;
            }

            if (slug.contains("*")) {
                String prefix = slug.split("\\*")[0];
                return requiredPermission.startsWith(prefix);
            }

            return false;
        });
    }
}
