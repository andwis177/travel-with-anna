package com.andwis.travel_with_anna.handler.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException() {
        super("Could not find item with");
    }
}
