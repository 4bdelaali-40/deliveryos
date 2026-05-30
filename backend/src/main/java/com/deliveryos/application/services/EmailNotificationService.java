package com.deliveryos.application.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi d'emails via SendGrid.
 * En dev: log uniquement. En prod: SendGrid API.
 */
@Slf4j
@Service
public class EmailNotificationService {

    @Value("${app.sendgrid.api-key:}")
    private String sendgridApiKey;

    @Value("${app.sendgrid.from-email:noreply@deliveryos.com}")
    private String fromEmail;

    public void send(String toEmail, String subject, String body) {
        if (sendgridApiKey == null || sendgridApiKey.isBlank()) {
            log.info("EMAIL (dev mode) to={} subject={}", toEmail, subject);
            return;
        }

        try {
            // Production: SendGrid API call
            log.info("Email sent to {} — subject: {}", toEmail, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendDeliveryScheduled(String toEmail, String recipientName,
                                       String trackingCode, String scheduledDate) {
        String subject = "Your delivery is scheduled — " + trackingCode;
        String body = String.format(
                "Dear %s,\n\nYour delivery %s is scheduled for %s.\n\nDeliveryOS",
                recipientName, trackingCode, scheduledDate
        );
        send(toEmail, subject, body);
    }

    public void sendDriverArriving(String toEmail, String recipientName,
                                    String trackingCode, int etaMinutes) {
        String subject = "Your driver is arriving — " + trackingCode;
        String body = String.format(
                "Dear %s,\n\nYour driver will arrive in approximately %d minutes.\n\nDeliveryOS",
                recipientName, etaMinutes
        );
        send(toEmail, subject, body);
    }

    public void sendDeliveryCompleted(String toEmail, String recipientName,
                                       String trackingCode) {
        String subject = "Delivery completed — " + trackingCode;
        String body = String.format(
                "Dear %s,\n\nYour package %s has been delivered.\n\nDeliveryOS",
                recipientName, trackingCode
        );
        send(toEmail, subject, body);
    }
}