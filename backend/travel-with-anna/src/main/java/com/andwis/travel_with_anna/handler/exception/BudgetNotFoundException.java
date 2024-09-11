package com.andwis.travel_with_anna.handler.exception;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException(Long id) {
        super("Could not find budget with id: " + id);
    }
}
