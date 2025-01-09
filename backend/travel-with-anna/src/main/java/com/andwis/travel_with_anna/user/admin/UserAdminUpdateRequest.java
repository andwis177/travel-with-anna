package com.andwis.travel_with_anna.user.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdminUpdateRequest {

    @NotNull(message = "Edit request cannot be null")
    private UserAdminEditRequest userAdminEditRequest;

    private String password;
}
