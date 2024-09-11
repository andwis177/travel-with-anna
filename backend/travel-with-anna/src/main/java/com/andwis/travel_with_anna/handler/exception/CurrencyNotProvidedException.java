package com.andwis.travel_with_anna.handler.exception;

public class CurrencyNotProvidedException extends RuntimeException {
    public CurrencyNotProvidedException(String message) {
        super(message);
    }
}
