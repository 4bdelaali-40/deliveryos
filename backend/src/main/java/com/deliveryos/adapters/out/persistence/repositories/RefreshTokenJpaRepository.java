package com.deliveryos.adapters.out.persistence.repositories;

import com.deliveryos.adapters.out.persistence.entities.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshTokenEntity t SET t.revoked = true, t.revokedAt = :now WHERE t.user.id = :userId AND t.revoked = false")
    void revokeAllByUserId(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity t WHERE t.expiresAt < :now")
    void deleteByExpiresAtBefore(@Param("now") Instant now);
}