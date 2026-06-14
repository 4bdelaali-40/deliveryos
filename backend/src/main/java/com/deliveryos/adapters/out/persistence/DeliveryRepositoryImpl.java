package com.deliveryos.adapters.out.persistence;

import com.deliveryos.adapters.out.persistence.mappers.DeliveryMapper;
import com.deliveryos.adapters.out.persistence.repositories.DeliveryJpaRepository;
import com.deliveryos.domain.model.Delivery;
import com.deliveryos.domain.model.DeliveryPriority;
import com.deliveryos.domain.model.DeliveryStatus;
import com.deliveryos.ports.out.DeliveryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final DeliveryJpaRepository jpaRepository;
    private final DeliveryMapper mapper;

    @Override
    public Delivery save(Delivery delivery) {
        var entity = mapper.toEntity(delivery);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Delivery> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Delivery> findByTrackingCode(String trackingCode) {
        return jpaRepository.findByTrackingCode(trackingCode).map(mapper::toDomain);
    }

    @Override
    public Page<Delivery> findAll(
            DeliveryStatus status,
            LocalDate scheduledDate,
            DeliveryPriority priority,
            String city,
            Pageable pageable) {
        return jpaRepository
                .findAllWithFilters(status, scheduledDate, priority, city, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public List<Delivery> findByScheduledDateAndStatus(LocalDate date, DeliveryStatus status) {
        return jpaRepository
                .findByScheduledDateAndStatus(date, status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        if (!jpaRepository.existsById(id)) {
            throw new EntityNotFoundException("Delivery not found with id: " + id);
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long countByStatus(DeliveryStatus status) {
        return jpaRepository.countByStatus(status);
    }
}