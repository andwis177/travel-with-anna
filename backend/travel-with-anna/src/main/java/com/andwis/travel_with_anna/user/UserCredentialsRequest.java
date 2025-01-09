package com.andwis.travel_with_anna.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.andwis.travel_with_anna.user.User.MAX_LENGTH;
import static com.andwis.travel_with_anna.user.User.NAME_MAX_LENGTH;

@Getter
@Setter
@Builder
public class UserCredentialsRequest {

    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Size(max = MAX_LENGTH, message = "User name should be " + MAX_LENGTH + " characters long")
    private String email;

    @NotEmpty(message = "User name is required")
    @NotBlank(message = "User name is required")
    @Size(max = NAME_MAX_LENGTH, message = "User name should be " + NAME_MAX_LENGTH + " characters long")
    private String userName;

    private String password;

    private String role;
}
