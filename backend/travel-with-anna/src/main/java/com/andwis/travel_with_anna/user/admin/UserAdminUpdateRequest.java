package com.andwis.travel_with_anna.user.admin;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdminUpdateRequest {
    private UserAdminEdit userAdminEdit;
    private String password;
}
