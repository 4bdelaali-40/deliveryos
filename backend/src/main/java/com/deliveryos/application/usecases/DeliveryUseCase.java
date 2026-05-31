package com.deliveryos.application.usecases;

import com.deliveryos.adapters.in.web.dto.request.CreateDeliveryRequest;
import com.deliveryos.adapters.in.web.dto.request.UpdateDeliveryRequest;
import com.deliveryos.domain.model.Delivery;
import com.deliveryos.domain.model.DeliveryPriority;
import com.deliveryos.domain.model.DeliveryStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import com.deliveryos.ports.out.DeliveryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryUseCase {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public Delivery create(CreateDeliveryRequest request, UUID createdBy) {
        Delivery delivery = Delivery.builder()
                .id(UUID.randomUUID())
                .trackingCode(generateTrackingCode())
                .status(DeliveryStatus.CREATED)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .recipientEmail(request.getRecipientEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .weightKg(request.getWeightKg())
                .volumeM3(request.getVolumeM3())
                .priority(request.getPriority())
                .timeWindowStart(request.getTimeWindowStart())
                .timeWindowEnd(request.getTimeWindowEnd())
                .scheduledDate(request.getScheduledDate())
                .notes(request.getNotes())
                .attemptCount(0)
                .createdBy(createdBy)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Delivery saved = deliveryRepository.save(delivery);
        log.info("Delivery created: {}", saved.getTrackingCode());
        return saved;
    }

    @Transactional(readOnly = true)
    public Delivery findById(UUID id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Delivery not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Delivery findByTrackingCode(String trackingCode) {
        return deliveryRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Delivery not found with tracking code: " + trackingCode));
    }

    @Transactional(readOnly = true)
    public Page<Delivery> findAll(
            DeliveryStatus status,
            LocalDate scheduledDate,
            DeliveryPriority priority,
            String city,
            Pageable pageable) {

        return deliveryRepository.findAll(
                status, scheduledDate, priority, city, pageable);
    }

    @Transactional
    public Delivery update(UUID id, UpdateDeliveryRequest request) {
        Delivery existing = findById(id);

        if (existing.getStatus() == DeliveryStatus.DELIVERED
                || existing.getStatus() == DeliveryStatus.RETURNED) {
            throw new IllegalStateException(
                    "Cannot update a delivery with status: " + existing.getStatus());
        }

        Delivery updated = existing
                .withRecipientName(request.getRecipientName() != null
                        ? request.getRecipientName() : existing.getRecipientName())
                .withRecipientPhone(request.getRecipientPhone() != null
                        ? request.getRecipientPhone() : existing.getRecipientPhone())
                .withRecipientEmail(request.getRecipientEmail() != null
                        ? request.getRecipientEmail() : existing.getRecipientEmail())
                .withAddress(request.getAddress() != null
                        ? request.getAddress() : existing.getAddress())
                .withCity(request.getCity() != null
                        ? request.getCity() : existing.getCity())
                .withPostalCode(request.getPostalCode() != null
                        ? request.getPostalCode() : existing.getPostalCode())
                .withWeightKg(request.getWeightKg() != null
                        ? request.getWeightKg() : existing.getWeightKg())
                .withVolumeM3(request.getVolumeM3() != null
                        ? request.getVolumeM3() : existing.getVolumeM3())
                .withPriority(request.getPriority() != null
                        ? request.getPriority() : existing.getPriority())
                .withTimeWindowStart(request.getTimeWindowStart() != null
                        ? request.getTimeWindowStart() : existing.getTimeWindowStart())
                .withTimeWindowEnd(request.getTimeWindowEnd() != null
                        ? request.getTimeWindowEnd() : existing.getTimeWindowEnd())
                .withScheduledDate(request.getScheduledDate() != null
                        ? request.getScheduledDate() : existing.getScheduledDate())
                .withNotes(request.getNotes() != null
                        ? request.getNotes() : existing.getNotes())
                .withUpdatedAt(Instant.now());

        Delivery saved = deliveryRepository.save(updated);
        log.info("Delivery updated: {}", saved.getTrackingCode());
        return saved;
    }

    @Transactional
    public Delivery updateStatus(UUID id, DeliveryStatus newStatus) {
        Delivery existing = findById(id);
        Delivery updated = existing.transitionTo(newStatus);
        Delivery saved = deliveryRepository.save(updated);
        log.info("Delivery {} status changed: {} -> {}",
                saved.getTrackingCode(), existing.getStatus(), newStatus);
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        Delivery existing = findById(id);
        if (existing.getStatus() != DeliveryStatus.CREATED) {
            throw new IllegalStateException(
                    "Cannot delete a delivery with status: " + existing.getStatus());
        }
        deliveryRepository.deleteById(id);
        log.info("Delivery deleted: {}", existing.getTrackingCode());
    }

    private String generateTrackingCode() {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "DOS-" + timestamp + "-" + random;
    }
}