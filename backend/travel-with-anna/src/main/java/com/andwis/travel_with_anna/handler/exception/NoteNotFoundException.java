package com.andwis.travel_with_anna.handler.exception;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(Long id) {
        super("Could not find note with id: " + id);
    }
}
