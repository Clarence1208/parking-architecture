package com.esgi.lac.architecture.backend.infrastructure.web.mapper;

import com.esgi.lac.architecture.backend.domain.model.AuthResult;
import com.esgi.lac.architecture.backend.domain.model.User;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.web.AuthController.RoleResponse;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.AuthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerMapperTest {

    private final AuthControllerMapper mapper = new AuthControllerMapper();

    @Nested
    @DisplayName("toAuthResponse")
    class ToAuthResponse {

        @Test
        @DisplayName("maps AuthResult to AuthResponse with all fields")
        void mapsAllFields() {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .email("user@test.com")
                    .password("encoded")
                    .role(UserRole.EMPLOYEE)
                    .build();
            AuthResult result = new AuthResult(user, "jwt-token-123");

            AuthResponse response = mapper.toAuthResponse(result);

            assertThat(response.token()).isEqualTo("jwt-token-123");
            assertThat(response.email()).isEqualTo("user@test.com");
            assertThat(response.role()).isEqualTo(UserRole.EMPLOYEE);
        }

        @Test
        @DisplayName("maps manager role correctly")
        void mapsManagerRole() {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .email("mgr@test.com")
                    .role(UserRole.MANAGER)
                    .build();
            AuthResult result = new AuthResult(user, "token");

            AuthResponse response = mapper.toAuthResponse(result);

            assertThat(response.role()).isEqualTo(UserRole.MANAGER);
        }
    }

    @Nested
    @DisplayName("toRoleResponse")
    class ToRoleResponse {

        @Test
        @DisplayName("maps UserRole to RoleResponse")
        void mapsRole() {
            RoleResponse response = mapper.toRoleResponse(UserRole.MANAGER);

            assertThat(response.name()).isEqualTo("MANAGER");
            assertThat(response.maxNumberOfBookingDays()).isEqualTo(30);
        }

        @Test
        @DisplayName("maps employee role with correct max days")
        void mapsEmployeeRole() {
            RoleResponse response = mapper.toRoleResponse(UserRole.EMPLOYEE);

            assertThat(response.name()).isEqualTo("EMPLOYEE");
            assertThat(response.maxNumberOfBookingDays()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("toRoleResponseList")
    class ToRoleResponseList {

        @Test
        @DisplayName("returns all roles")
        void returnsAllRoles() {
            List<RoleResponse> roles = mapper.toRoleResponseList();

            assertThat(roles).hasSize(UserRole.values().length);
        }

        @Test
        @DisplayName("contains expected role names")
        void containsExpectedNames() {
            List<RoleResponse> roles = mapper.toRoleResponseList();
            List<String> names = roles.stream().map(RoleResponse::name).toList();

            assertThat(names).containsExactlyInAnyOrder("EMPLOYEE", "SECRETARY", "MANAGER");
        }

        @Test
        @DisplayName("each role has positive max booking days")
        void allHavePositiveMaxDays() {
            List<RoleResponse> roles = mapper.toRoleResponseList();

            assertThat(roles).allMatch(r -> r.maxNumberOfBookingDays() > 0);
        }
    }
}
