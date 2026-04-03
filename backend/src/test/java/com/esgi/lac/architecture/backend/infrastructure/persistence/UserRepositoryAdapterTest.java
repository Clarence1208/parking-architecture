package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.domain.model.User;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.UserEntity;
import com.esgi.lac.architecture.backend.infrastructure.persistence.mapper.UserEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Spy
    private UserEntityMapper userEntityMapper = new UserEntityMapper();

    @InjectMocks
    private UserRepositoryAdapter adapter;

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("maps domain User to entity and back")
        void mapsDomainToEntityAndBack() {
            UUID id = UUID.randomUUID();
            User user = User.builder()
                    .id(id)
                    .email("user@test.com")
                    .password("encoded_pw")
                    .role(UserRole.SECRETARY)
                    .build();

            UserEntity savedEntity = UserEntity.builder()
                    .id(id)
                    .email("user@test.com")
                    .password("encoded_pw")
                    .role(UserRole.SECRETARY)
                    .build();
            when(jpaUserRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

            User result = adapter.save(user);

            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getEmail()).isEqualTo("user@test.com");
            assertThat(result.getPassword()).isEqualTo("encoded_pw");
            assertThat(result.getRole()).isEqualTo(UserRole.SECRETARY);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(jpaUserRepository).save(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo("user@test.com");
        }
    }

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        @DisplayName("returns mapped user when found")
        void returnsMappedUser() {
            UUID id = UUID.randomUUID();
            UserEntity entity = UserEntity.builder()
                    .id(id)
                    .email("found@test.com")
                    .password("pw")
                    .role(UserRole.MANAGER)
                    .build();
            when(jpaUserRepository.findByEmail("found@test.com")).thenReturn(Optional.of(entity));

            Optional<User> result = adapter.findByEmail("found@test.com");

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("found@test.com");
            assertThat(result.get().getRole()).isEqualTo(UserRole.MANAGER);
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(jpaUserRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

            assertThat(adapter.findByEmail("missing@test.com")).isEmpty();
        }
    }
}
