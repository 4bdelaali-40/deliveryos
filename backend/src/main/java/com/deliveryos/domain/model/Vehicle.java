package com.deliveryos.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité domaine Vehicle — pure, sans annotation JPA.
 */
@Getter
@Builder
@With
public class Vehicle {

    public enum Type {
        CAR, VAN, BIKE, CARGO_BIKE, ELECTRIC_CAR, ELECTRIC_VAN, MOTORCYCLE
    }

    public enum FuelType {
        DIESEL, PETROL, ELECTRIC, HYBRID
    }

    private final UUID id;
    private final String plateNumber;
    private final Type type;
    private final String brand;
    private final String model;
    private final Integer year;
    private final double capacityKg;
    private final double capacityM3;
    private final double co2PerKm;
    private final FuelType fuelType;
    private final int mileageKm;
    private final LocalDate lastRevisionDate;
    private final Integer nextRevisionKm;
    private final boolean available;
    private final Instant createdAt;
    private final Instant updatedAt;

    public boolean needsMaintenance() {
        return nextRevisionKm != null && mileageKm >= nextRevisionKm;
    }

    public boolean isElectric() {
        return type == Type.ELECTRIC_CAR
                || type == Type.ELECTRIC_VAN
                || fuelType == FuelType.ELECTRIC;
    }

    public boolean isEcoFriendly() {
        return type == Type.BIKE
                || type == Type.CARGO_BIKE
                || isElectric();
    }
}