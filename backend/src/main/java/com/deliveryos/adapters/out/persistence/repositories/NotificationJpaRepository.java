package com.deliveryos.adapters.out.persistence.repositories;

import com.deliveryos.adapters.out.persistence.entities.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {

    Page<NotificationEntity> findByUserIdOrderByCreatedAtDesc(
            UUID userId, Pageable pageable);

    long countByUserIdAndReadFalse(UUID userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.read = true, n.readAt = :now WHERE n.userId = :userId AND n.read = false")
    void markAllAsReadByUserId(
            @Param("userId") UUID userId,
            @Param("now") Instant now);
}