package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.application.exception.InvalidCredentialsException;
import com.esgi.lac.architecture.backend.application.usecase.AuthUseCase;
import com.esgi.lac.architecture.backend.domain.model.AuthResult;
import com.esgi.lac.architecture.backend.domain.model.User;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.web.mapper.AuthControllerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthUseCase authUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authUseCase, new AuthControllerMapper()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("returns token and user info on successful registration")
        void successfulRegistration() throws Exception {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .email("new@test.com")
                    .role(UserRole.EMPLOYEE)
                    .build();
            when(authUseCase.register("new@test.com", "pass123", UserRole.EMPLOYEE))
                    .thenReturn(new AuthResult(user, "jwt-token"));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email": "new@test.com", "password": "pass123", "role": "EMPLOYEE"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token"))
                    .andExpect(jsonPath("$.email").value("new@test.com"))
                    .andExpect(jsonPath("$.role").value("EMPLOYEE"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("returns token on successful login")
        void successfulLogin() throws Exception {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .email("user@test.com")
                    .role(UserRole.MANAGER)
                    .build();
            when(authUseCase.login("user@test.com", "password"))
                    .thenReturn(new AuthResult(user, "jwt-token"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email": "user@test.com", "password": "password"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token"))
                    .andExpect(jsonPath("$.email").value("user@test.com"))
                    .andExpect(jsonPath("$.role").value("MANAGER"));
        }

        @Test
        @DisplayName("returns 401 on invalid credentials")
        void returns401OnInvalidCredentials() throws Exception {
            when(authUseCase.login(anyString(), anyString()))
                    .thenThrow(new InvalidCredentialsException("Invalid credentials"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email": "user@test.com", "password": "wrong"}
                                    """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }
    }

    @Nested
    @DisplayName("GET /api/auth/roles")
    class GetRoles {

        @Test
        @DisplayName("returns all available roles")
        void returnsAllRoles() throws Exception {
            mockMvc.perform(get("/api/auth/roles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3));
        }

        @Test
        @DisplayName("each role includes name and max booking days")
        void rolesContainExpectedFields() throws Exception {
            mockMvc.perform(get("/api/auth/roles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.name == 'MANAGER')].maxNumberOfBookingDays").value(30));
        }
    }
}
