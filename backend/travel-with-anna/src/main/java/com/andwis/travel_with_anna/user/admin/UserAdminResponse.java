package com.andwis.travel_with_anna.user.admin;

import java.time.LocalDate;

public record UserAdminResponse (
        Long userId,
        String userName,
        String email,
        byte[] avatar,
        boolean accountLocked,
        boolean enabled,
        LocalDate createdDate,
        LocalDate lastModifiedDate,
        String roleName
) {
}
