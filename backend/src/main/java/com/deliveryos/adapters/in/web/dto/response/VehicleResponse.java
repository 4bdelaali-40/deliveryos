package com.deliveryos.adapters.in.web.dto.response;

import com.deliveryos.domain.model.Vehicle;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class VehicleResponse {

    private final UUID id;
    private final String plateNumber;
    private final Vehicle.Type type;
    private final String brand;
    private final String model;
    private final Integer year;
    private final double capacityKg;
    private final double capacityM3;
    private final double co2PerKm;
    private final Vehicle.FuelType fuelType;
    private final int mileageKm;
    private final LocalDate lastRevisionDate;
    private final Integer nextRevisionKm;
    private final boolean available;
    private final boolean needsMaintenance;
    private final boolean ecoFriendly;
    private final Instant createdAt;

    public static VehicleResponse from(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .capacityKg(vehicle.getCapacityKg())
                .capacityM3(vehicle.getCapacityM3())
                .co2PerKm(vehicle.getCo2PerKm())
                .fuelType(vehicle.getFuelType())
                .mileageKm(vehicle.getMileageKm())
                .lastRevisionDate(vehicle.getLastRevisionDate())
                .nextRevisionKm(vehicle.getNextRevisionKm())
                .available(vehicle.isAvailable())
                .needsMaintenance(vehicle.needsMaintenance())
                .ecoFriendly(vehicle.isEcoFriendly())
                .createdAt(vehicle.getCreatedAt())
                .build();
    }
}