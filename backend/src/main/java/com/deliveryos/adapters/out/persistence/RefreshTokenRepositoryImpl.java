package com.deliveryos.adapters.out.persistence;

import com.deliveryos.adapters.out.persistence.entities.RefreshTokenEntity;
import com.deliveryos.adapters.out.persistence.entities.UserEntity;
import com.deliveryos.adapters.out.persistence.repositories.RefreshTokenJpaRepository;
import com.deliveryos.adapters.out.persistence.repositories.UserJpaRepository;
import com.deliveryos.domain.model.RefreshToken;
import com.deliveryos.ports.out.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public RefreshToken save(RefreshToken token) {
        UserEntity userEntity = userJpaRepository.findById(token.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + token.getUserId()));

        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .id(token.getId())
                .user(userEntity)
                .tokenHash(token.getTokenHash())
                .expiresAt(token.getExpiresAt())
                .revoked(token.isRevoked())
                .revokedAt(token.getRevokedAt())
                .ipAddress(token.getIpAddress())
                .userAgent(token.getUserAgent())
                .build();

        RefreshTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(UUID userId) {
        jpaRepository.revokeAllByUserId(userId, Instant.now());
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        jpaRepository.deleteByExpiresAtBefore(Instant.now());
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .tokenHash(entity.getTokenHash())
                .expiresAt(entity.getExpiresAt())
                .revoked(entity.isRevoked())
                .revokedAt(entity.getRevokedAt())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}