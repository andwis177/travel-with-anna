package com.andwis.travel_with_anna.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotEmpty(message = "User email or user name is required")
    @NotBlank(message = "User email or user name is required")
    private String credential;
}
