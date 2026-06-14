package com.deliveryos.adapters.out.persistence.mappers;

import com.deliveryos.adapters.out.persistence.entities.VehicleEntity;
import com.deliveryos.domain.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toDomain(VehicleEntity entity) {
        return Vehicle.builder()
                .id(entity.getId())
                .plateNumber(entity.getPlateNumber())
                .type(entity.getType())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .year(entity.getYear())
                .capacityKg(entity.getCapacityKg())
                .capacityM3(entity.getCapacityM3())
                .co2PerKm(entity.getCo2PerKm())
                .fuelType(entity.getFuelType())
                .mileageKm(entity.getMileageKm())
                .lastRevisionDate(entity.getLastRevisionDate())
                .nextRevisionKm(entity.getNextRevisionKm())
                .available(entity.isAvailable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public VehicleEntity toEntity(Vehicle domain) {
        return VehicleEntity.builder()
                .id(domain.getId())
                .plateNumber(domain.getPlateNumber())
                .type(domain.getType())
                .brand(domain.getBrand())
                .model(domain.getModel())
                .year(domain.getYear())
                .capacityKg(domain.getCapacityKg())
                .capacityM3(domain.getCapacityM3())
                .co2PerKm(domain.getCo2PerKm())
                .fuelType(domain.getFuelType())
                .mileageKm(domain.getMileageKm())
                .lastRevisionDate(domain.getLastRevisionDate())
                .nextRevisionKm(domain.getNextRevisionKm())
                .available(domain.isAvailable())
                .build();
    }
}