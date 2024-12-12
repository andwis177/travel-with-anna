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

    private void mimeMessageCreator(
            String to, String userName, String url, String code, String subject, String templateName)
            throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();
        properties.put("userName", userName);
        properties.put("url", url);
        properties.put("code", code);

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
    }

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
            mimeMessageCreator(to, userName, confirmationUrl, activationCode, subject, templateName);

        } catch (MessagingException exp) {
            throw new MessagingException("Failed to send validation email", exp);
        }
    }

    @Async
    public void sendResetPassword(
            String to,
            String userName,
            String loginUrl,
            String newPassword,
            String subject
    ) throws MessagingException {
        try {
            String templateName =
                    EmailTemplateName.RESET_PASSWORD.getTemplateName();
            mimeMessageCreator(to, userName, loginUrl, newPassword, subject, templateName);
        } catch (MessagingException exp) {
            throw new MessagingException("Failed to send email with new password", exp);
        }
    }
}
