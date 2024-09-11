package com.andwis.travel_with_anna.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    @NotEmpty(message = "Password should be 8 characters long minimum")
    @NotBlank(message = "Password should be 8 characters long minimum")
    private String currentPassword;
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    @NotEmpty(message = "Password should be 8 characters long minimum")
    @NotBlank(message = "Password should be 8 characters long minimum")
    private String newPassword;
    private String confirmPassword;
}
