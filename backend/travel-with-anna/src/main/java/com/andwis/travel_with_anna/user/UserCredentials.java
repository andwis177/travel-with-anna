package com.andwis.travel_with_anna.user;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCredentials {
    @Email(message = "Email is not well formatted")
    private String email;
    private String userName;
    private String password;
    private String role;
}
