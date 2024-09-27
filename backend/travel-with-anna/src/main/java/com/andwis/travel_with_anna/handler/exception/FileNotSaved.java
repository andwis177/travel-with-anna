package com.andwis.travel_with_anna.handler.exception;

import java.io.IOException;

public class FileNotSaved extends IOException {
    public FileNotSaved(String message) {
        super(message);
    }
}
