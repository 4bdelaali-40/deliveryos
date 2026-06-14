package adapters.in.web.dto.response;

import com.deliveryos.domain.model.Delivery;
import com.deliveryos.domain.model.DeliveryPriority;
import com.deliveryos.domain.model.DeliveryStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@SuppressWarnings("unused")
public class DeliveryResponse {

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
    private final String failedReason;
    private final int attemptCount;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static DeliveryResponse from(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .trackingCode(delivery.getTrackingCode())
                .status(delivery.getStatus())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .recipientEmail(delivery.getRecipientEmail())
                .address(delivery.getAddress())
                .city(delivery.getCity())
                .postalCode(delivery.getPostalCode())
                .latitude(delivery.getLatitude())
                .longitude(delivery.getLongitude())
                .weightKg(delivery.getWeightKg())
                .volumeM3(delivery.getVolumeM3())
                .priority(delivery.getPriority())
                .timeWindowStart(delivery.getTimeWindowStart())
                .timeWindowEnd(delivery.getTimeWindowEnd())
                .scheduledDate(delivery.getScheduledDate())
                .notes(delivery.getNotes())
                .qrCodeUrl(delivery.getQrCodeUrl())
                .proofPhotoUrl(delivery.getProofPhotoUrl())
                .signatureUrl(delivery.getSignatureUrl())
                .deliveredAt(delivery.getDeliveredAt())
                .failedReason(delivery.getFailedReason())
                .attemptCount(delivery.getAttemptCount())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}