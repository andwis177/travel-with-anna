package com.andwis.travel_with_anna.user.admin;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdminUpdateRequest {
    private UserAdminEditRequest userAdminEditRequest;
    private String password;
}
