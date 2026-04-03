package com.esgi.lac.architecture.backend.infrastructure.web.mapper;

import com.esgi.lac.architecture.backend.domain.model.AuthResult;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.web.AuthController.RoleResponse;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.AuthResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthControllerMapper {

    public AuthResponse toAuthResponse(AuthResult result) {
        return new AuthResponse(
                result.token(),
                result.user().getEmail(),
                result.user().getRole()
        );
    }

    public RoleResponse toRoleResponse(UserRole role) {
        return new RoleResponse(role.name(), role.getMaxNumberOfBookingDays());
    }

    public List<RoleResponse> toRoleResponseList() {
        return Arrays.stream(UserRole.values())
                .map(this::toRoleResponse)
                .toList();
    }
}
