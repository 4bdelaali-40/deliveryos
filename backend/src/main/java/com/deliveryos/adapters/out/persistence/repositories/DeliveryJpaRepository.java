package com.deliveryos.adapters.out.persistence.repositories;

import com.deliveryos.adapters.out.persistence.entities.DeliveryEntity;
import com.deliveryos.domain.model.DeliveryPriority;
import com.deliveryos.domain.model.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryJpaRepository extends JpaRepository<DeliveryEntity, UUID> {

    Optional<DeliveryEntity> findByTrackingCode(String trackingCode);

    @Query("""
            SELECT d FROM DeliveryEntity d
            WHERE (:status IS NULL OR d.status = :status)
            AND (:scheduledDate IS NULL OR d.scheduledDate = :scheduledDate)
            AND (:priority IS NULL OR d.priority = :priority)
            AND (:city IS NULL OR LOWER(d.city) LIKE LOWER(CONCAT('%', :city, '%')))
            """)
    Page<DeliveryEntity> findAllWithFilters(
            @Param("status") DeliveryStatus status,
            @Param("scheduledDate") LocalDate scheduledDate,
            @Param("priority") DeliveryPriority priority,
            @Param("city") String city,
            Pageable pageable
    );

    List<DeliveryEntity> findByScheduledDateAndStatus(
            LocalDate scheduledDate,
            DeliveryStatus status
    );

    long countByStatus(DeliveryStatus status);
}