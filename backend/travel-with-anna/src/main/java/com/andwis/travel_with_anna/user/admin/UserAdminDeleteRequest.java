package com.andwis.travel_with_anna.user.admin;

import jakarta.validation.constraints.NotNull;

public record UserAdminDeleteRequest(
        @NotNull(message = "User ID cannot be null")
        Long userId,
        String password
) {
}
