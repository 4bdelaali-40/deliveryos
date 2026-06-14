package com.deliveryos.adapters.out.persistence.repositories;

import com.deliveryos.adapters.out.persistence.entities.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleJpaRepository extends JpaRepository<VehicleEntity, UUID> {

    Optional<VehicleEntity> findByPlateNumber(String plateNumber);

    List<VehicleEntity> findByAvailableTrue();
}