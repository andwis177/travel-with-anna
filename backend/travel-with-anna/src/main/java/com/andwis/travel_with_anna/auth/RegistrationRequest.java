package com.andwis.travel_with_anna.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "User name is required")
    @NotBlank(message = "User name is required")
    private String userName;
    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    @NotEmpty(message = "Password should be 8 characters long minimum")
    @NotBlank(message = "Password should be 8 characters long minimum")
    private String password;
    private String confirmPassword;
    @NotEmpty(message = "Role is required")
    @NotBlank(message = "Role is required")
    private String roleName;
}
