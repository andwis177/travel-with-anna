package com.andwis.travel_with_anna.user.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdminView {
    private Long userId;
    private String userName;
    private String email;
    private boolean accountLocked;
    private boolean enabled;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    private String roleName;
    private byte[] cover;
}
