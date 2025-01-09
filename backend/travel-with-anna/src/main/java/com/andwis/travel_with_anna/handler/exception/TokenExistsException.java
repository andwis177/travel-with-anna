package com.andwis.travel_with_anna.handler.exception;

public class TokenExistsException extends RuntimeException {
    public TokenExistsException(String message) {
        super(message);
    }
}
