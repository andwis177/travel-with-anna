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

    FILE_NOT_SAVED(1900, "Avatar was not saved", INTERNAL_SERVER_ERROR),
    FILE_TOO_BIG(1901, "File is too big", BAD_REQUEST),

    AVATAR_NOT_FOUND(2000, "Avatar not found", NOT_FOUND),

    TRIP_NOT_FOUND(3000, "Trip not found", NOT_FOUND),
    BACKPACK_NOT_FOUND(3100, "Backpack not found", NOT_FOUND),
    ITEM_NOT_FOUND(3200, "Item not found", NOT_FOUND),
    BUDGET_NOT_FOUND(3300, "Budget not found", NOT_FOUND),
    CURRENCY_NOT_PROVIDED(3400, "Currency is required", BAD_REQUEST),
    EXPANSE_NOT_FOUND(3500, "Expanse not found", NOT_FOUND),
    EXPANSE_NOT_SAVED(3501, "Expanse was not saved", BAD_REQUEST),

    DAY_NOT_FOUND(4000, "Day not found", NOT_FOUND),
    ACTIVITY_NOT_FOUND(4100, "Activity not found", NOT_FOUND),

    DATA_NOT_VALID(5000, "Data is not valid", BAD_REQUEST),

    MISSING_PARAMETER(6000, "Missing parameter", BAD_REQUEST),
    NOTE_TYPE_NOT_FOUND(6100, "Note type not found", NOT_IMPLEMENTED),
    ENTITY_NOT_FOUND(6200, "Entity not found", NOT_FOUND),

    PDF_REPORT_ERROR(7000, "Error while creating trip PDF", INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCodes(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
