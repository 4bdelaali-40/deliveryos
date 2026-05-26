package com.deliveryos.application.usecases;

import com.deliveryos.domain.model.Notification;
import com.deliveryos.ports.out.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case Notification — création et diffusion des notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Notification send(
            UUID userId,
            Notification.Type type,
            Notification.Channel channel,
            String title,
            String message) {

        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type(type)
                .channel(channel)
                .title(title)
                .message(message)
                .read(false)
                .createdAt(Instant.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        // Diffuser via WebSocket si IN_APP
        if (channel == Notification.Channel.IN_APP) {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    saved
            );
        }

        log.info("Notification sent to user {}: {}", userId, title);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Notification> findByUserId(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public Notification markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification not found: " + id));
        Notification updated = notification.markAsRead();
        return notificationRepository.save(updated);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
        log.info("All notifications marked as read for user: {}", userId);
    }
}