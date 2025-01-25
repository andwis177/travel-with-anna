package com.andwis.travel_with_anna.handler.exception;

public class OAuth2AccessTokenException extends RuntimeException {
    public OAuth2AccessTokenException(String msg) {
        super(msg);
    }
    public OAuth2AccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
