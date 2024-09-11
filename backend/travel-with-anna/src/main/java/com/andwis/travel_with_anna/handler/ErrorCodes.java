package com.andwis.travel_with_anna.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCodes {
    NO_CODE(1000, "No code", NOT_IMPLEMENTED),
    ACCOUNT_LOCKED(1100, "Account is locked", FORBIDDEN),
    BAD_CREDENTIALS(1110, "Login and / or password is incorrect", FORBIDDEN),
    ACCOUNT_DISABLED(1120, "Account is disabled", FORBIDDEN),
    WRONG_CREDENTIALS(1130, "Wrong credentials", NOT_ACCEPTABLE),
    USER_NOT_FOUND(1140, "User not found", NOT_FOUND),
    USER_EXISTS(1150, "User with this name or email already exists", CONFLICT),
    PASSWORD_NOT_MATCH(1160, "Passwords do not match", NOT_ACCEPTABLE),
    INVALID_TOKEN(1300, "Token is invalid", UNAUTHORIZED),
    EXPIRED_TOKEN(1310, "Token has expired", UNAUTHORIZED),
    ROLE_NOT_FOUND(1400, "Role not found", NOT_FOUND),
    MESSAGING_EXCEPTION(1500, "Email was NOT send", INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(1700, "Validation error", BAD_REQUEST),
    JWT_PARSING_ERROR(1800, "JWT parsing error", UNAUTHORIZED),

    AVATAR_NOT_SAVED(2000, "Avatar was not saved", INTERNAL_SERVER_ERROR),
    AVATAR_NOT_FOUND(2100, "Avatar not found", NOT_FOUND),

    TRIP_NOT_FOUND(3000, "Trip not found", NOT_FOUND),
    BACKPACK_NOT_FOUND(3100, "Backpack not found", NOT_FOUND),
    ITEM_NOT_FOUND(3200, "Item not found", NOT_FOUND),
    BUDGET_NOT_FOUND(3300, "Budget not found", NOT_FOUND),
    NOTE_NOT_FOUND(3400, "Note not found", NOT_FOUND),
    CURRENCY_NOT_PROVIDED(3500, "Currency is required", BAD_REQUEST);


    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCodes(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
