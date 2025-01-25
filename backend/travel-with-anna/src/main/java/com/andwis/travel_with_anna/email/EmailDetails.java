package com.andwis.travel_with_anna.email;

public record EmailDetails(
        String sendTo,
        String userName,
        String sendFrom,
        String content,
        String subject,
        EmailTemplateName templateName) {
}
