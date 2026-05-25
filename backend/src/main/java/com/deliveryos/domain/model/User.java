package com.deliveryos.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

/**
 * Entité domaine User — pure, sans annotation JPA.
 * C'est le coeur du domaine, indépendant de toute infrastructure.
 */
@Getter
@Builder
@With
public class User {

    private final UUID id;
    private final Role role;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String passwordHash;
    private final String phone;
    private final String avatarUrl;
    private final boolean active;
    private final String mfaSecret;
    private final boolean mfaEnabled;
    private final int failedAttempts;
    private final Instant lockedUntil;
    private final Instant lastLoginAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    // ── Business Rules ───────────────────────────────────────

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(Instant.now());
    }

    public boolean isAdmin() {
        return role == Role.SUPER_ADMIN || role == Role.ADMIN;
    }

    public boolean canManageDeliveries() {
        return role == Role.SUPER_ADMIN
                || role == Role.ADMIN
                || role == Role.DISPATCHER;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public User incrementFailedAttempts() {
        return this.withFailedAttempts(this.failedAttempts + 1);
    }

    public User resetFailedAttempts() {
        return this.withFailedAttempts(0).withLockedUntil(null);
    }

    public User lockUntil(Instant until) {
        return this.withLockedUntil(until);
    }
}