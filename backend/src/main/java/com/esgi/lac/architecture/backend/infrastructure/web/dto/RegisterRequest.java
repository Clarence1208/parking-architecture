package com.esgi.lac.architecture.backend.infrastructure.web.dto;

import com.esgi.lac.architecture.backend.domain.model.UserRole;

public record RegisterRequest(String email, String password, UserRole role) {
}
