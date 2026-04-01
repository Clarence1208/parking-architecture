package com.esgi.lac.architecture.backend.application.repository;

import com.esgi.lac.architecture.backend.domain.model.User;

public interface TokenProvider {
    String generateToken(User user);
}
