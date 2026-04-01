package com.esgi.lac.architecture.backend.application.usecase;

import com.esgi.lac.architecture.backend.domain.model.AuthResult;
import com.esgi.lac.architecture.backend.domain.model.UserRole;

public interface AuthUseCase {
    AuthResult register(String email, String password, UserRole role);
    AuthResult login(String email, String password);
}
