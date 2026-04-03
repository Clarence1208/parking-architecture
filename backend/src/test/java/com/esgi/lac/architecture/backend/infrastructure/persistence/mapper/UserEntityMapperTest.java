package com.esgi.lac.architecture.backend.infrastructure.persistence.mapper;

import com.esgi.lac.architecture.backend.domain.model.User;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityMapperTest {

    private final UserEntityMapper mapper = new UserEntityMapper();

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("maps all fields from entity to domain")
        void mapsAllFields() {
            UUID id = UUID.randomUUID();
            UserEntity entity = UserEntity.builder()
                    .id(id)
                    .email("user@test.com")
                    .password("encoded_pw")
                    .role(UserRole.MANAGER)
                    .build();

            User user = mapper.toDomain(entity);

            assertThat(user.getId()).isEqualTo(id);
            assertThat(user.getEmail()).isEqualTo("user@test.com");
            assertThat(user.getPassword()).isEqualTo("encoded_pw");
            assertThat(user.getRole()).isEqualTo(UserRole.MANAGER);
        }

        @Test
        @DisplayName("maps each role correctly")
        void mapsRoles() {
            for (UserRole role : UserRole.values()) {
                UserEntity entity = UserEntity.builder()
                        .id(UUID.randomUUID())
                        .email("test@test.com")
                        .password("pw")
                        .role(role)
                        .build();

                User user = mapper.toDomain(entity);

                assertThat(user.getRole()).isEqualTo(role);
            }
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("maps all fields from domain to entity")
        void mapsAllFields() {
            UUID id = UUID.randomUUID();
            User user = User.builder()
                    .id(id)
                    .email("user@test.com")
                    .password("encoded_pw")
                    .role(UserRole.SECRETARY)
                    .build();

            UserEntity entity = mapper.toEntity(user);

            assertThat(entity.getId()).isEqualTo(id);
            assertThat(entity.getEmail()).isEqualTo("user@test.com");
            assertThat(entity.getPassword()).isEqualTo("encoded_pw");
            assertThat(entity.getRole()).isEqualTo(UserRole.SECRETARY);
        }

        @Test
        @DisplayName("handles null id for new users")
        void handlesNullId() {
            User user = User.builder()
                    .email("new@test.com")
                    .password("pw")
                    .role(UserRole.EMPLOYEE)
                    .build();

            UserEntity entity = mapper.toEntity(user);

            assertThat(entity.getId()).isNull();
            assertThat(entity.getEmail()).isEqualTo("new@test.com");
        }
    }

    @Nested
    @DisplayName("roundtrip")
    class Roundtrip {

        @Test
        @DisplayName("domain -> entity -> domain preserves all fields")
        void roundtrip() {
            UUID id = UUID.randomUUID();
            User original = User.builder()
                    .id(id)
                    .email("roundtrip@test.com")
                    .password("secret")
                    .role(UserRole.EMPLOYEE)
                    .build();

            User result = mapper.toDomain(mapper.toEntity(original));

            assertThat(result.getId()).isEqualTo(original.getId());
            assertThat(result.getEmail()).isEqualTo(original.getEmail());
            assertThat(result.getPassword()).isEqualTo(original.getPassword());
            assertThat(result.getRole()).isEqualTo(original.getRole());
        }
    }
}
