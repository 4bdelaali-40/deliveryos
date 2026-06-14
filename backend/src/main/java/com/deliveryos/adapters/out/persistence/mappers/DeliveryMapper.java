package com.deliveryos.adapters.out.persistence.mappers;

import com.deliveryos.adapters.out.persistence.entities.DeliveryEntity;
import com.deliveryos.domain.model.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public Delivery toDomain(DeliveryEntity entity) {
        return Delivery.builder()
                .id(entity.getId())
                .trackingCode(entity.getTrackingCode())
                .status(entity.getStatus())
                .recipientName(entity.getRecipientName())
                .recipientPhone(entity.getRecipientPhone())
                .recipientEmail(entity.getRecipientEmail())
                .address(entity.getAddress())
                .city(entity.getCity())
                .postalCode(entity.getPostalCode())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .weightKg(entity.getWeightKg())
                .volumeM3(entity.getVolumeM3())
                .priority(entity.getPriority())
                .timeWindowStart(entity.getTimeWindowStart())
                .timeWindowEnd(entity.getTimeWindowEnd())
                .scheduledDate(entity.getScheduledDate())
                .notes(entity.getNotes())
                .qrCodeUrl(entity.getQrCodeUrl())
                .proofPhotoUrl(entity.getProofPhotoUrl())
                .signatureUrl(entity.getSignatureUrl())
                .deliveredAt(entity.getDeliveredAt())
                .deliveredLatitude(entity.getDeliveredLatitude())
                .deliveredLongitude(entity.getDeliveredLongitude())
                .failedReason(entity.getFailedReason())
                .attemptCount(entity.getAttemptCount())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public DeliveryEntity toEntity(Delivery domain) {
        return DeliveryEntity.builder()
                .id(domain.getId())
                .trackingCode(domain.getTrackingCode())
                .status(domain.getStatus())
                .recipientName(domain.getRecipientName())
                .recipientPhone(domain.getRecipientPhone())
                .recipientEmail(domain.getRecipientEmail())
                .address(domain.getAddress())
                .city(domain.getCity())
                .postalCode(domain.getPostalCode())
                .latitude(domain.getLatitude())
                .longitude(domain.getLongitude())
                .weightKg(domain.getWeightKg())
                .volumeM3(domain.getVolumeM3())
                .priority(domain.getPriority())
                .timeWindowStart(domain.getTimeWindowStart())
                .timeWindowEnd(domain.getTimeWindowEnd())
                .scheduledDate(domain.getScheduledDate())
                .notes(domain.getNotes())
                .qrCodeUrl(domain.getQrCodeUrl())
                .proofPhotoUrl(domain.getProofPhotoUrl())
                .signatureUrl(domain.getSignatureUrl())
                .deliveredAt(domain.getDeliveredAt())
                .deliveredLatitude(domain.getDeliveredLatitude())
                .deliveredLongitude(domain.getDeliveredLongitude())
                .failedReason(domain.getFailedReason())
                .attemptCount(domain.getAttemptCount())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}