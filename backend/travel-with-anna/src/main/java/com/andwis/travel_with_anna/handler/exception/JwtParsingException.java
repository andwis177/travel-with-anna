package com.andwis.travel_with_anna.handler.exception;

public class JwtParsingException extends RuntimeException {
    public JwtParsingException(String message) {
        super(message);
    }
}
