package com.andwis.travel_with_anna.role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {
    USER("USER", "User"),
    ADMIN("ADMIN", "Admin");

    private final String roleName;
    private final String authority;
}
