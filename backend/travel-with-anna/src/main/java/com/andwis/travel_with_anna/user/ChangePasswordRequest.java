package com.andwis.travel_with_anna.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.andwis.travel_with_anna.user.User.*;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    private String currentPassword;

    @Size(min = PASSWORD_MIN_LENGTH, max = MAX_LENGTH, message = PASSWORD_VALIDATION_MESSAGE)
    @NotEmpty(message = PASSWORD_VALIDATION_MESSAGE)
    @NotBlank(message = PASSWORD_VALIDATION_MESSAGE)
    private String newPassword;
    private String confirmPassword;
}
