package com.deliveryos.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

/**
 * Entité domaine RefreshToken — pure, sans annotation JPA.
 */
@Getter
@Builder
@With
public class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private final boolean revoked;
    private final Instant revokedAt;
    private final String ipAddress;
    private final String userAgent;
    private final Instant createdAt;

    // ── Business Rules ───────────────────────────────────────

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public RefreshToken revoke() {
        return this.withRevoked(true).withRevokedAt(Instant.now());
    }
}