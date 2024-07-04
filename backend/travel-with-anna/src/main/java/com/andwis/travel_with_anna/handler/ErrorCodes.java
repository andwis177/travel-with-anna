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

    INVALID_TOKEN(1300, "Token is invalid", UNAUTHORIZED),
    EXPIRED_TOKEN(1310, "Token has expired", UNAUTHORIZED),

    ROLE_NOT_FOUND(1400, "Role not found", NOT_FOUND),

    MESSAGING_EXCEPTION(1500, "Email was NOT send", INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCodes(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
