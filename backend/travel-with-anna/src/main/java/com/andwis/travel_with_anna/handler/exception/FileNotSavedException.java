package com.andwis.travel_with_anna.handler.exception;

import java.io.IOException;

public class FileNotSavedException extends IOException {
    public FileNotSavedException(String message) {
        super(message);
    }
}
