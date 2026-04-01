package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.application.repository.PasswordEncoderPort;
import com.esgi.lac.architecture.backend.application.repository.TokenProvider;
import com.esgi.lac.architecture.backend.application.repository.UserRepository;
import com.esgi.lac.architecture.backend.application.usecase.AuthUseCase;
import com.esgi.lac.architecture.backend.domain.model.AuthResult;
import com.esgi.lac.architecture.backend.domain.model.User;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    public AuthResult register(String email, String password, UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with email already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateToken(savedUser);

        return new AuthResult(savedUser, token);
    }

    @Override
    public AuthResult login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = tokenProvider.generateToken(user);
        return new AuthResult(user, token);
    }
}
