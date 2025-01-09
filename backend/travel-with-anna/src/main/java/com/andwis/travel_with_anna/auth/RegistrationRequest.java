package com.andwis.travel_with_anna.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    private static final String MESSAGE_REQUIRED = "This field is required";
    private static final String MESSAGE_EMAIL_FORMAT = "Email is not well formatted";
    private static final String MESSAGE_PASSWORD_LENGTH = "Password should be at least 8 characters long";
    private static final String MESSAGE_NAME_LENGTH = "User name should be 30 characters long maximum";

    private static final int SIZE_NAME_LENGTH = 30;
    private static final int SIZE_PASSWORD_MIN_LENGTH = 8;

    @NotBlank(message = MESSAGE_REQUIRED)
    @Size(max = SIZE_NAME_LENGTH, message = MESSAGE_NAME_LENGTH)
    private String userName;

    @NotBlank(message = MESSAGE_REQUIRED)
    @Email(message = MESSAGE_EMAIL_FORMAT)
    private String email;

    @NotBlank(message = MESSAGE_PASSWORD_LENGTH)
    @Size(min = SIZE_PASSWORD_MIN_LENGTH, message = MESSAGE_PASSWORD_LENGTH)
    private String password;

    private String confirmPassword;

    @NotBlank(message = MESSAGE_REQUIRED)
    private String roleName;
}
