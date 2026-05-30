package com.deliveryos.application.services;

import com.deliveryos.application.usecases.NotificationUseCase;
import com.deliveryos.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Orchestrateur de notifications multi-canal.
 * Décide quel canal utiliser selon le type d'alerte.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final NotificationUseCase notificationUseCase;
    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;

    public void dispatchDeliveryAlert(
            UUID userId,
            String title,
            String message,
            String recipientEmail,
            String recipientPhone) {

        // In-app
        notificationUseCase.send(
                userId,
                Notification.Type.ALERT,
                Notification.Channel.IN_APP,
                title,
                message
        );

        // Email
        if (recipientEmail != null && !recipientEmail.isBlank()) {
            emailService.send(recipientEmail, title, message);
        }

        // SMS pour les alertes critiques
        if (recipientPhone != null && !recipientPhone.isBlank()) {
            smsService.send(recipientPhone, message);
        }

        log.info("Notification dispatched: {}", title);
    }

    public void dispatchDriverAlert(
            UUID driverId,
            String title,
            String message) {

        notificationUseCase.send(
                driverId,
                Notification.Type.WARNING,
                Notification.Channel.IN_APP,
                title,
                message
        );
    }

    public void dispatchSystemAlert(
            UUID adminId,
            String title,
            String message) {

        notificationUseCase.send(
                adminId,
                Notification.Type.ALERT,
                Notification.Channel.IN_APP,
                title,
                message
        );
        log.warn("System alert dispatched: {}", title);
    }
}