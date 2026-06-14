package com.deliveryos.adapters.in.web.dto.response;

import com.deliveryos.adapters.out.persistence.entities.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {

    private final UUID id;
    private final String role;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String avatarUrl;
    private final boolean isActive;
    private final boolean mfaEnabled;
    private final Instant lastLoginAt;
    private final Instant createdAt;

    public static UserResponse from(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .role(entity.getRole())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .avatarUrl(entity.getAvatarUrl())
                .isActive(entity.isActive())
                .mfaEnabled(entity.isMfaEnabled())
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
