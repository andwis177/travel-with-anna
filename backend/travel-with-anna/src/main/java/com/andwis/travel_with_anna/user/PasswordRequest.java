package com.andwis.travel_with_anna.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import static com.andwis.travel_with_anna.user.User.*;

public record PasswordRequest(

        @NotEmpty(message = PASSWORD_VALIDATION_MESSAGE)
        @NotBlank(message = PASSWORD_VALIDATION_MESSAGE)
        String password
) {
}
