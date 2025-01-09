package com.andwis.travel_with_anna.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRequest {

    private final static String EMAIL_MESSAGE = "Email is required";
    private final static String PASSWORD_MESSAGE = "Password is required";

    @Email(message = "Email is not well formatted")
    @NotEmpty(message = EMAIL_MESSAGE)
    @NotBlank(message = EMAIL_MESSAGE)
    private String email;

    @NotEmpty(message = PASSWORD_MESSAGE)
    @NotBlank(message = PASSWORD_MESSAGE)
    private String password;
}
