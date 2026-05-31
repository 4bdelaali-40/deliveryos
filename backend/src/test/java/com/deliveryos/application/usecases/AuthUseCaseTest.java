package com.deliveryos.application.usecases;

import com.deliveryos.config.JwtService;
import com.deliveryos.domain.model.Role;
import com.deliveryos.domain.model.User;
import com.deliveryos.ports.out.RefreshTokenRepository;
import com.deliveryos.ports.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUseCase Tests")
class AuthUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .role(Role.DISPATCHER)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@deliveryos.com")
                .passwordHash("$2a$12$hashedpassword")
                .active(true)
                .mfaEnabled(false)
                .failedAttempts(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should throw on duplicate email registration")
    void shouldThrowOnDuplicateEmailRegistration() {
        when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(true);

        var request = mock(
                com.deliveryos.adapters.in.web.dto.request.RegisterRequest.class);
        when(request.getEmail()).thenReturn(sampleUser.getEmail());

        assertThatThrownBy(() -> authUseCase.register(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    @DisplayName("User isLocked should return false when lockedUntil is null")
    void userIsLockedShouldReturnFalseWhenLockedUntilIsNull() {
        assertThat(sampleUser.isLocked()).isFalse();
    }

    @Test
    @DisplayName("User isLocked should return true when lockedUntil is in future")
    void userIsLockedShouldReturnTrueWhenLockedUntilIsInFuture() {
        User lockedUser = sampleUser.withLockedUntil(
                Instant.now().plusSeconds(3600));
        assertThat(lockedUser.isLocked()).isTrue();
    }

    @Test
    @DisplayName("User isAdmin should return true for ADMIN role")
    void userIsAdminShouldReturnTrueForAdminRole() {
        User adminUser = sampleUser.withRole(Role.ADMIN);
        assertThat(adminUser.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("User isAdmin should return false for DRIVER role")
    void userIsAdminShouldReturnFalseForDriverRole() {
        User driverUser = sampleUser.withRole(Role.DRIVER);
        assertThat(driverUser.isAdmin()).isFalse();
    }
}