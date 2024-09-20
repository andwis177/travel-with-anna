package com.andwis.travel_with_anna.user.admin;

public record UserAdminEditRequest(
        Long userId,
        boolean accountLocked,
        boolean enabled,
        String roleName
) {
}
