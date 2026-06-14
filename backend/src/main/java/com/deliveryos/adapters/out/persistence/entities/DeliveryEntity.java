package com.deliveryos.adapters.out.persistence.entities;

import com.deliveryos.domain.model.DeliveryPriority;
import com.deliveryos.domain.model.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tracking_code", nullable = false, unique = true, length = 50)
    private String trackingCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(name = "recipient_name", nullable = false, length = 200)
    private String recipientName;

    @Column(name = "recipient_phone", length = 20)
    private String recipientPhone;

    @Column(name = "recipient_email", length = 255)
    private String recipientEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "volume_m3")
    private Double volumeM3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryPriority priority;

    @Column(name = "time_window_start")
    private LocalTime timeWindowStart;

    @Column(name = "time_window_end")
    private LocalTime timeWindowEnd;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    @Column(name = "proof_photo_url", length = 500)
    private String proofPhotoUrl;

    @Column(name = "signature_url", length = 500)
    private String signatureUrl;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "delivered_latitude")
    private Double deliveredLatitude;

    @Column(name = "delivered_longitude")
    private Double deliveredLongitude;

    @Column(name = "failed_reason", columnDefinition = "TEXT")
    private String failedReason;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}