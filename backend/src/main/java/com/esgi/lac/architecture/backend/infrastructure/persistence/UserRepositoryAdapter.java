package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.application.repository.UserRepository;
import com.esgi.lac.architecture.backend.domain.model.User;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.UserEntity;
import com.esgi.lac.architecture.backend.infrastructure.persistence.mapper.UserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public User save(User user) {
        UserEntity savedEntity = jpaUserRepository.save(userEntityMapper.toEntity(user));
        return userEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(userEntityMapper::toDomain);
    }
}
