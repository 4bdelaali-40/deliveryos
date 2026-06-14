package com.deliveryos.adapters.out.persistence;

import com.deliveryos.adapters.out.persistence.mappers.VehicleMapper;
import com.deliveryos.adapters.out.persistence.repositories.VehicleJpaRepository;
import com.deliveryos.domain.model.Vehicle;
import com.deliveryos.ports.out.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehicleJpaRepository jpaRepository;
    private final VehicleMapper mapper;

    @Override
    public Vehicle save(Vehicle vehicle) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(vehicle)));
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return jpaRepository.findByPlateNumber(plateNumber).map(mapper::toDomain);
    }

    @Override
    public Page<Vehicle> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<Vehicle> findAvailable() {
        return jpaRepository.findByAvailableTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        if (!jpaRepository.existsById(id)) {
            throw new EntityNotFoundException("Vehicle not found: " + id);
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}