package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.application.usecase.AuthUseCase;
import com.esgi.lac.architecture.backend.domain.model.AuthResult;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.AuthResponse;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.LoginRequest;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.esgi.lac.architecture.backend.domain.model.UserRole;

import java.util.Arrays;
import java.util.List;

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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResult result = authUseCase.login(request.email(), request.password());
        return ResponseEntity.ok(new AuthResponse(result.token(), result.user().getEmail(), result.user().getRole()));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        List<RoleResponse> roles = Arrays.stream(UserRole.values())
                .map(role -> new RoleResponse(role.name(), role.getMaxNumberOfBookingDays()))
                .toList();
        return ResponseEntity.ok(roles);
    }

    public record RoleResponse(String name, int maxNumberOfBookingDays) {}
}
