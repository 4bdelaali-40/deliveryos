package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.adapters.in.web.dto.response.UserResponse;
import com.deliveryos.adapters.out.persistence.entities.UserEntity;
import com.deliveryos.adapters.out.persistence.repositories.UserJpaRepository;
import com.deliveryos.shared.ApiResponse;
import com.deliveryos.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    private static final java.util.Set<String> VALID_ROLES =
            java.util.Set.of("SUPER_ADMIN", "ADMIN", "DISPATCHER", "DRIVER", "VIEWER");

    // ─── List / filter ──────────────────────────────────────────

    @GetMapping
    @Operation(summary = "List users, optionally filtered by role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> findAll(
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<UserEntity> result = (role != null && !role.isBlank())
                ? userJpaRepository.findByRole(role, PageRequest.of(page, size))
                : userJpaRepository.findAll(PageRequest.of(page, size));

        Page<UserResponse> mapped = result.map(UserResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(mapped)));
    }

    // ─── Create ─────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new user")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        if (userJpaRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use: " + request.getEmail());
        }

        if (!VALID_ROLES.contains(request.getRole())) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setRole(request.getRole());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        entity.setPhone(request.getPhone());
        entity.setActive(true);
        entity.setMfaEnabled(false);
        entity.setFailedAttempts(0);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        UserEntity saved = userJpaRepository.save(entity);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(saved)));
    }

    // ─── Update role ────────────────────────────────────────────

    @PatchMapping("/{id}/role")
    @Operation(summary = "Change a user's role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request) {

        if (!VALID_ROLES.contains(request.getRole())) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        UserEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        entity.setRole(request.getRole());
        entity.setUpdatedAt(Instant.now());

        UserEntity saved = userJpaRepository.save(entity);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(saved)));
    }

    // ─── Activate / deactivate ──────────────────────────────────

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate or deactivate a user")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {

        UserEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        entity.setActive(request.isActive());
        entity.setUpdatedAt(Instant.now());

        UserEntity saved = userJpaRepository.save(entity);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(saved)));
    }

    // ─── Delete ─────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        if (!userJpaRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        userJpaRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ─── DTOs ───────────────────────────────────────────────────

    @Getter
    @Setter
    @Builder
    public static class CreateUserRequest {
        @NotBlank
        private String role;
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        @NotBlank
        @Email
        private String email;
        @NotBlank
        private String password;
        private String phone;
    }

    @Getter
    @Setter
    public static class UpdateRoleRequest {
        @NotBlank
        private String role;
    }

    @Getter
    @Setter
    public static class UpdateStatusRequest {
        private boolean active;
    }
}
