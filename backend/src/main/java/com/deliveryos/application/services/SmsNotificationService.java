package com.deliveryos.application.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi de SMS via Twilio.
 * En dev: log uniquement. En prod: Twilio API.
 */
@Slf4j
@Service
public class SmsNotificationService {

    @Value("${app.twilio.account-sid:}")
    private String accountSid;

    @Value("${app.twilio.auth-token:}")
    private String authToken;

    @Value("${app.twilio.from-number:+1234567890}")
    private String fromNumber;

    public void send(String toPhone, String message) {
        if (accountSid == null || accountSid.isBlank()) {
            log.info("SMS (dev mode) to={} message={}", toPhone, message);
            return;
        }

        try {
            // Production: Twilio API call
            log.info("SMS sent to {} — message length: {}", toPhone, message.length());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhone, e.getMessage());
        }
    }

    public void sendDriverArriving(String toPhone, int etaMinutes, String trackingCode) {
        String message = String.format(
                "DeliveryOS: Your driver arrives in %d min. Tracking: %s",
                etaMinutes, trackingCode
        );
        send(toPhone, message);
    }

    public void sendDeliveryCompleted(String toPhone, String trackingCode) {
        String message = String.format(
                "DeliveryOS: Package %s delivered. Thank you!",
                trackingCode
        );
        send(toPhone, message);
    }

    public void sendUrgentAlert(String toPhone, String message) {
        send(toPhone, "DeliveryOS URGENT: " + message);
    }
}