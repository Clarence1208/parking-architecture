package com.esgi.lac.architecture.backend.application.repository;

import com.esgi.lac.architecture.backend.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
}
