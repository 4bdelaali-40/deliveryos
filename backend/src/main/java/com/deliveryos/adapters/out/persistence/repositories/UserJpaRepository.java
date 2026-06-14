package com.deliveryos.adapters.out.persistence.repositories;

import com.deliveryos.adapters.out.persistence.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour UserEntity.
 * Interface technique — jamais exposee hors de la couche persistence.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<UserEntity> findByRole(String role, Pageable pageable);
}
