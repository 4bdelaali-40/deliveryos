package com.deliveryos.adapters.out.persistence;

import com.deliveryos.adapters.out.persistence.entities.NotificationEntity;
import com.deliveryos.adapters.out.persistence.repositories.NotificationJpaRepository;
import com.deliveryos.domain.model.Notification;
import com.deliveryos.ports.out.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = toEntity(notification);
        NotificationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<Notification> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDomain);
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        return jpaRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsReadByUserId(UUID userId) {
        jpaRepository.markAllAsReadByUserId(userId, Instant.now());
    }

    @Override
    public void deleteById(UUID id) {
        if (!jpaRepository.existsById(id)) {
            throw new EntityNotFoundException("Notification not found: " + id);
        }
        jpaRepository.deleteById(id);
    }

    private Notification toDomain(NotificationEntity entity) {
        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .channel(entity.getChannel())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .read(entity.isRead())
                .readAt(entity.getReadAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private NotificationEntity toEntity(Notification domain) {
        return NotificationEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .type(domain.getType())
                .channel(domain.getChannel())
                .title(domain.getTitle())
                .message(domain.getMessage())
                .read(domain.isRead())
                .readAt(domain.getReadAt())
                .build();
    }
}