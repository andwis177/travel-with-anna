package com.andwis.travel_with_anna.email;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailContentCreator {
    private final SpringTemplateEngine templateEngine;

    public String generateEmailContent(EmailDetails emailDetails) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();

        properties.put("userName", emailDetails.userName());
        properties.put("content", emailDetails.content());

        Context context = new Context();
        context.setVariables(properties);

        try {
            return templateEngine.process(emailDetails.templateName().getTemplateName(), context);
        } catch (RuntimeException e) {
            throw new MessagingException("Failed to process email template", e);
        }
    }
}
