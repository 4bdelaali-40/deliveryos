package com.deliveryos.adapters.out.persistence.entities;

import com.deliveryos.domain.model.Vehicle;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Vehicle.Type type;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Column
    private Integer year;

    @Column(name = "capacity_kg", nullable = false)
    private double capacityKg;

    @Column(name = "capacity_m3", nullable = false)
    private double capacityM3;

    @Column(name = "co2_per_km", nullable = false)
    private double co2PerKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private Vehicle.FuelType fuelType;

    @Column(name = "mileage_km", nullable = false)
    private int mileageKm = 0;

    @Column(name = "last_revision_date")
    private LocalDate lastRevisionDate;

    @Column(name = "next_revision_km")
    private Integer nextRevisionKm;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}