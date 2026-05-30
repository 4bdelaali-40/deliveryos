package com.deliveryos.application.usecases;

import com.deliveryos.adapters.in.web.dto.request.CreateVehicleRequest;
import com.deliveryos.domain.model.Vehicle;
import com.deliveryos.ports.out.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleUseCase {

    private final VehicleRepository vehicleRepository;

    @Transactional
    public Vehicle create(CreateVehicleRequest request) {
        if (vehicleRepository.findByPlateNumber(request.getPlateNumber()).isPresent()) {
            throw new IllegalStateException(
                    "Vehicle already exists with plate: " + request.getPlateNumber());
        }

        Vehicle vehicle = Vehicle.builder()
                .id(UUID.randomUUID())
                .plateNumber(request.getPlateNumber())
                .type(request.getType())
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .capacityKg(request.getCapacityKg())
                .capacityM3(request.getCapacityM3())
                .co2PerKm(request.getCo2PerKm())
                .fuelType(request.getFuelType())
                .mileageKm(0)
                .lastRevisionDate(request.getLastRevisionDate())
                .nextRevisionKm(request.getNextRevisionKm())
                .available(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle created: {}", saved.getPlateNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public Vehicle findById(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vehicle not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findAll(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAvailable() {
        return vehicleRepository.findAvailable();
    }

    @Transactional
    public Vehicle updateMileage(UUID id, int newMileage) {
        Vehicle existing = findById(id);
        Vehicle updated = existing
                .withMileageKm(newMileage)
                .withUpdatedAt(Instant.now());
        return vehicleRepository.save(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Vehicle vehicle = findById(id);
        if (!vehicle.isAvailable()) {
            throw new IllegalStateException(
                    "Cannot delete a vehicle that is currently in use");
        }
        vehicleRepository.deleteById(id);
        log.info("Vehicle deleted: {}", vehicle.getPlateNumber());
    }
}