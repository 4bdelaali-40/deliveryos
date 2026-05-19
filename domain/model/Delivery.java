package com.deliveryos.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
@With
public class Delivery {

    private final UUID id;
    private final String trackingCode;
    private final DeliveryStatus status;
    private final String recipientName;
    private final String recipientPhone;
    private final String recipientEmail;
    private final String address;
    private final String city;
    private final String postalCode;
    private final Double latitude;
    private final Double longitude;
    private final Double weightKg;
    private final Double volumeM3;
    private final DeliveryPriority priority;
    private final LocalTime timeWindowStart;
    private final LocalTime timeWindowEnd;
    private final LocalDate scheduledDate;
    private final String notes;
    private final String qrCodeUrl;
    private final String proofPhotoUrl;
    private final String signatureUrl;
    private final Instant deliveredAt;
    private final Double deliveredLatitude;
    private final Double deliveredLongitude;
    private final String failedReason;
    private final int attemptCount;
    private final UUID createdBy;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Delivery transitionTo(DeliveryStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Cannot transition from " + this.status + " to " + newStatus
            );
        }
        return this.withStatus(newStatus).withUpdatedAt(Instant.now());
    }

    public Delivery markAsDelivered(Double lat, Double lng) {
        return this
                .transitionTo(DeliveryStatus.DELIVERED)
                .withDeliveredAt(Instant.now())
                .withDeliveredLatitude(lat)
                .withDeliveredLongitude(lng);
    }

    public Delivery markAsFailed(String reason) {
        return this
                .transitionTo(DeliveryStatus.FAILED)
                .withFailedReason(reason)
                .withAttemptCount(this.attemptCount + 1);
    }

    public boolean isUrgent() {
        return priority == DeliveryPriority.URGENT
                || priority == DeliveryPriority.VIP;
    }

    public boolean hasTimeWindow() {
        return timeWindowStart != null && timeWindowEnd != null;
    }
}