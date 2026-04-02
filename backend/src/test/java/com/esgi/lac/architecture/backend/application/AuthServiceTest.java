package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.application.repository.PasswordEncoderPort;
import com.esgi.lac.architecture.backend.application.repository.TokenProvider;
import com.esgi.lac.architecture.backend.application.repository.UserRepository;
import com.esgi.lac.architecture.backend.application.service.AuthService;
import com.esgi.lac.architecture.backend.domain.model.AuthResult;
import com.esgi.lac.architecture.backend.domain.model.User;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("successfully registers a new user")
        void successfulRegistration() {
            when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encoded_pw");
            User savedUser = User.builder()
                    .id(UUID.randomUUID())
                    .email("new@test.com")
                    .password("encoded_pw")
                    .role(UserRole.EMPLOYEE)
                    .build();
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(tokenProvider.generateToken(savedUser)).thenReturn("jwt-token");

            AuthResult result = authService.register("new@test.com", "password123", UserRole.EMPLOYEE);

            assertThat(result.user().getEmail()).isEqualTo("new@test.com");
            assertThat(result.token()).isEqualTo("jwt-token");
            verify(passwordEncoder).encode("password123");
        }

        @Test
        @DisplayName("throws when email already exists")
        void throwsWhenEmailExists() {
            User existingUser = User.builder().email("dup@test.com").build();
            when(userRepository.findByEmail("dup@test.com")).thenReturn(Optional.of(existingUser));

            assertThatThrownBy(() -> authService.register("dup@test.com", "pass", UserRole.EMPLOYEE))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        @DisplayName("encodes password before saving")
        void encodesPasswordBeforeSaving() {
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode("rawPass")).thenReturn("hashedPass");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(tokenProvider.generateToken(any())).thenReturn("token");

            authService.register("user@test.com", "rawPass", UserRole.EMPLOYEE);

            verify(userRepository).save(argThat(user -> "hashedPass".equals(user.getPassword())));
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("successfully logs in with valid credentials")
        void successfulLogin() {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .email("user@test.com")
                    .password("encoded_pw")
                    .role(UserRole.EMPLOYEE)
                    .build();
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encoded_pw")).thenReturn(true);
            when(tokenProvider.generateToken(user)).thenReturn("jwt-token");

            AuthResult result = authService.login("user@test.com", "password123");

            assertThat(result.user().getEmail()).isEqualTo("user@test.com");
            assertThat(result.token()).isEqualTo("jwt-token");
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login("missing@test.com", "pass"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid credentials");
        }

        @Test
        @DisplayName("throws when password does not match")
        void throwsWhenPasswordWrong() {
            User user = User.builder()
                    .email("user@test.com")
                    .password("encoded_pw")
                    .build();
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongPass", "encoded_pw")).thenReturn(false);

            assertThatThrownBy(() -> authService.login("user@test.com", "wrongPass"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid credentials");
        }
    }
}
