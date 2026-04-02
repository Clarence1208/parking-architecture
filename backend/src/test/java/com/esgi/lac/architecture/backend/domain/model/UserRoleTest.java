package com.esgi.lac.architecture.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    @DisplayName("EMPLOYEE has a 5-day booking quota")
    void employeeHas5DayQuota() {
        assertThat(UserRole.EMPLOYEE.getMaxNumberOfBookingDays()).isEqualTo(5);
    }

    @Test
    @DisplayName("SECRETARY has a 5-day booking quota")
    void secretaryHas5DayQuota() {
        assertThat(UserRole.SECRETARY.getMaxNumberOfBookingDays()).isEqualTo(5);
    }

    @Test
    @DisplayName("MANAGER has a 30-day booking quota")
    void managerHas30DayQuota() {
        assertThat(UserRole.MANAGER.getMaxNumberOfBookingDays()).isEqualTo(30);
    }

    @Test
    @DisplayName("all roles are present")
    void allRolesPresent() {
        assertThat(UserRole.values()).containsExactlyInAnyOrder(
                UserRole.EMPLOYEE, UserRole.SECRETARY, UserRole.MANAGER
        );
    }
}
