package com.deliveryos.application.usecases;

import com.deliveryos.adapters.in.web.dto.request.LoginRequest;
import com.deliveryos.adapters.in.web.dto.request.RegisterRequest;
import com.deliveryos.adapters.in.web.dto.response.AuthResponse;
import com.deliveryos.config.JwtService;
import com.deliveryos.domain.model.RefreshToken;
import com.deliveryos.domain.model.User;
import com.deliveryos.ports.out.RefreshTokenRepository;
import com.deliveryos.ports.out.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Use case Auth — Login, Register, Refresh Token.
 * Contient toute la logique métier d'authentification.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${security.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    // ── Login ────────────────────────────────────────────────

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.isLocked()) {
            throw new IllegalStateException("Account is locked. Try again later.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String rawRefreshToken = generateRawToken();

        saveRefreshToken(user, rawRefreshToken, ipAddress, userAgent);

        log.info("User logged in: {}", user.getEmail());

        return buildAuthResponse(user, accessToken, rawRefreshToken);
    }

    // ── Register ─────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already in use: " + request.getEmail());
        }

        User newUser = User.builder()
                .id(UUID.randomUUID())
                .role(request.getRole())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .active(true)
                .mfaEnabled(false)
                .failedAttempts(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        User saved = userRepository.save(newUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getEmail());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String rawRefreshToken = generateRawToken();

        saveRefreshToken(saved, rawRefreshToken, null, null);

        log.info("User registered: {}", saved.getEmail());

        return buildAuthResponse(saved, accessToken, rawRefreshToken);
    }

    // ── Refresh Token ─────────────────────────────────────────

    @Transactional
    public AuthResponse refresh(String rawToken, String ipAddress, String userAgent) {
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalStateException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new IllegalStateException("Refresh token is expired or revoked");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Rotation — révoque l'ancien, crée un nouveau
        refreshTokenRepository.revokeAllByUserId(user.getId());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRawRefreshToken = generateRawToken();

        saveRefreshToken(user, newRawRefreshToken, ipAddress, userAgent);

        log.info("Token refreshed for user: {}", user.getEmail());

        return buildAuthResponse(user, newAccessToken, newRawRefreshToken);
    }

    // ── Logout ────────────────────────────────────────────────

    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
        log.info("User logged out: {}", userId);
    }

    // ── Private Helpers ───────────────────────────────────────

    private void saveRefreshToken(
            User user,
            String rawToken,
            String ipAddress,
            String userAgent) {

        RefreshToken token = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .tokenHash(hashToken(rawToken))
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .revoked(false)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(Instant.now())
                .build();

        refreshTokenRepository.save(token);
    }

    private AuthResponse buildAuthResponse(
            User user,
            String accessToken,
            String rawRefreshToken) {

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .expiresIn(accessTokenExpirationMs / 1000)
                .tokenType(AuthResponse.getDefaultTokenType())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .mfaEnabled(user.isMfaEnabled())
                .build();
    }

    private String generateRawToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash token", e);
        }
    }
}