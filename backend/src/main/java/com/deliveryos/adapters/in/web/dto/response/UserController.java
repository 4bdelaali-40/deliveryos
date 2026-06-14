package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.adapters.in.web.dto.response.UserResponse;
import com.deliveryos.adapters.out.persistence.entities.UserEntity;
import com.deliveryos.adapters.out.persistence.repositories.UserJpaRepository;
import com.deliveryos.shared.ApiResponse;
import com.deliveryos.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserJpaRepository userJpaRepository;

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
}
