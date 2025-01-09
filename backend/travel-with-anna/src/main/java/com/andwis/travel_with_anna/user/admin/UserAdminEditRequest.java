package com.andwis.travel_with_anna.user.admin;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static com.andwis.travel_with_anna.role.Role.ROLE_LENGTH;

public record UserAdminEditRequest(
        @NotNull(message = "User Id cannot be null")
        Long userId,
        boolean accountLocked,
        boolean enabled,
        @Size(max = ROLE_LENGTH)
        @NotNull(message = ROLE_VALIDATION_MESSAGE)
        @NotEmpty(message = ROLE_VALIDATION_MESSAGE)
        @NotEmpty(message = ROLE_VALIDATION_MESSAGE)
        String roleName
) {
    private static final String ROLE_VALIDATION_MESSAGE =
            "Role name cannot be null or blank and should not be longer then " + ROLE_LENGTH + " characters.";
}
