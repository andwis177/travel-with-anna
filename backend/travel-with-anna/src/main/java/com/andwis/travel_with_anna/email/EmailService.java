package com.andwis.travel_with_anna.email;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailService {
    void sendEmail(EmailDetails emailDetails)
            throws MessagingException, IOException, InterruptedException;
}
