package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.application.usecase.AuthUseCase;
import com.esgi.lac.architecture.backend.domain.model.AuthResult;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.AuthResponse;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.LoginRequest;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResult result = authUseCase.register(request.email(), request.password(), request.role());
        return ResponseEntity.ok(new AuthResponse(result.token(), result.user().getEmail(), result.user().getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResult result = authUseCase.login(request.email(), request.password());
            return ResponseEntity.ok(new AuthResponse(result.token(), result.user().getEmail(), result.user().getRole()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(new ErrorResponse(ex.getMessage()));
        }
    }

    public record ErrorResponse(String message) {}
}
