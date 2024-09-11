package com.andwis.travel_with_anna.user;

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
public class UserCredentials {
    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;
    @NotEmpty(message = "User name is required")
    @NotBlank(message = "User name is required")
    @Size(max = 30, message = "User name should be 30 characters long maximum")
    private String userName;
    private String password;
    private String role;
}
