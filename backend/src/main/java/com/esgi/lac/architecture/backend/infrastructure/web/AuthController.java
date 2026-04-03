package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.application.usecase.AuthUseCase;
import com.esgi.lac.architecture.backend.domain.model.AuthResult;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.AuthResponse;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.LoginRequest;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.RegisterRequest;
import com.esgi.lac.architecture.backend.infrastructure.web.mapper.AuthControllerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;
    private final AuthControllerMapper authControllerMapper;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResult result = authUseCase.register(request.email(), request.password(), request.role());
        return ResponseEntity.ok(authControllerMapper.toAuthResponse(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResult result = authUseCase.login(request.email(), request.password());
        return ResponseEntity.ok(authControllerMapper.toAuthResponse(result));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return ResponseEntity.ok(authControllerMapper.toRoleResponseList());
    }

    public record RoleResponse(String name, int maxNumberOfBookingDays) {}
}
