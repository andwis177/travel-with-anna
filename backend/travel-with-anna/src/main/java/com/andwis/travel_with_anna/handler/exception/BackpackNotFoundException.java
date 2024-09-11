package com.andwis.travel_with_anna.handler.exception;

public class BackpackNotFoundException extends RuntimeException {
    public BackpackNotFoundException() {
        super("Backpack does not exist");
    }
}
