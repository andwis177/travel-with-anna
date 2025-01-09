package com.andwis.travel_with_anna.handler.exception;

public class UserIsActiveException extends RuntimeException {
    public UserIsActiveException(String message) {
        super(message);
    }
}
