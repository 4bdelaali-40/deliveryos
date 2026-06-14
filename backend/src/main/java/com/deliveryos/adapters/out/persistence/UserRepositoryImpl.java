package com.deliveryos.adapters.out.persistence;

import com.deliveryos.adapters.out.persistence.mappers.UserMapper;
import com.deliveryos.adapters.out.persistence.repositories.UserJpaRepository;
import com.deliveryos.domain.model.User;
import com.deliveryos.ports.out.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        if (!jpaRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        jpaRepository.deleteById(id);
    }
}