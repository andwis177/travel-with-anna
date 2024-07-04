package com.andwis.travel_with_anna.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${spring.mail.username}")
    private String senderEmail;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendValidationEmail(
            String to,
            String userName,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        try {
        String templateName =
                EmailTemplateName.ACTIVATE_ACCOUNT.getTemplateName();
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED,
                    UTF_8.name()
            );
            Map<String, Object> properties = new HashMap<>();
            properties.put("userName", userName);
            properties.put("confirmationUrl", confirmationUrl);
            properties.put("activationCode", activationCode);

            Context context = new Context();
            context.setVariables(properties);

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            String template;
            try {
                template = templateEngine.process(templateName, context);
            } catch (RuntimeException e) {
                throw new MessagingException("Failed to process template", e);
            }

            helper.setText(template, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException exp) {
            throw new MessagingException("Failed to send validation email", exp);
        }
    }
}
