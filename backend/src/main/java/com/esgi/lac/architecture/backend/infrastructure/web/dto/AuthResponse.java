package com.esgi.lac.architecture.backend.infrastructure.web.dto;

import com.esgi.lac.architecture.backend.domain.model.UserRole;

public record AuthResponse(String token, String email, UserRole role) {
}
