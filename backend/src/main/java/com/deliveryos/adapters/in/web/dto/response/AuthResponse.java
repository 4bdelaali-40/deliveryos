package com.deliveryos.adapters.in.web.dto.response;

import com.deliveryos.domain.model.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * DTO réponse — Auth (login + refresh).
 * Contient les tokens + les infos essentielles de l'utilisateur.
 */
@Getter
@Builder
public class AuthResponse {

    // ── Tokens ───────────────────────────────────────────────
    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;
    private final String tokenType;

    // ── User info ────────────────────────────────────────────
    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Role role;
    private final boolean mfaEnabled;

    public static String getDefaultTokenType() {
        return "Bearer";
    }
}