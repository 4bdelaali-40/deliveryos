package com.deliveryos.adapters.out.persistence.mappers;

import com.deliveryos.adapters.out.persistence.entities.UserEntity;
import com.deliveryos.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .role(entity.getRole())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .phone(entity.getPhone())
                .avatarUrl(entity.getAvatarUrl())
                .active(entity.isActive())
                .mfaSecret(entity.getMfaSecret())
                .mfaEnabled(entity.isMfaEnabled())
                .failedAttempts(entity.getFailedAttempts())
                .lockedUntil(entity.getLockedUntil())
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .role(domain.getRole())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .email(domain.getEmail())
                .passwordHash(domain.getPasswordHash())
                .phone(domain.getPhone())
                .avatarUrl(domain.getAvatarUrl())
                .active(domain.isActive())
                .mfaSecret(domain.getMfaSecret())
                .mfaEnabled(domain.isMfaEnabled())
                .failedAttempts(domain.getFailedAttempts())
                .lockedUntil(domain.getLockedUntil())
                .lastLoginAt(domain.getLastLoginAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}