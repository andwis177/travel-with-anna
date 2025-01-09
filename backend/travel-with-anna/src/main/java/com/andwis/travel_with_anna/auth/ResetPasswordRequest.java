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

    private static final String MESSAGE_REQUIRED = "User email or user name is required";

    @NotEmpty(message = MESSAGE_REQUIRED)
    @NotBlank(message = MESSAGE_REQUIRED)
    private String credential;
}
