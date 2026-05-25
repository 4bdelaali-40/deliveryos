package com.deliveryos.ports.out;

import com.deliveryos.domain.model.Delivery;
import com.deliveryos.domain.model.DeliveryPriority;
import com.deliveryos.domain.model.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);

    Optional<Delivery> findById(UUID id);

    Optional<Delivery> findByTrackingCode(String trackingCode);

    Page<Delivery> findAll(
            DeliveryStatus status,
            LocalDate scheduledDate,
            DeliveryPriority priority,
            String city,
            Pageable pageable
    );

    List<Delivery> findByScheduledDateAndStatus(
            LocalDate date,
            DeliveryStatus status
    );

    void deleteById(UUID id);

    boolean existsById(UUID id);

    long countByStatus(DeliveryStatus status);
}