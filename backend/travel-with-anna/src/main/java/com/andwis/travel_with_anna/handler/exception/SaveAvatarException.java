package com.andwis.travel_with_anna.handler.exception;

import java.io.IOException;

public class SaveAvatarException extends IOException {
    public SaveAvatarException(String message) {
        super(message);
    }

    public SaveAvatarException(String message, Throwable cause) {
        super(message, cause);
    }
}
