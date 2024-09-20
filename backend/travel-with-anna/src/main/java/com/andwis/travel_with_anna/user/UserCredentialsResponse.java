package com.andwis.travel_with_anna.user;

public record UserCredentialsResponse (
        String email,
        String userName,
        String role
) {
}
