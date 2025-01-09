package com.andwis.travel_with_anna.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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

    private record EmailDetails(String to, String userName, String code, String subject,
                                EmailTemplateName templateName) {
    }

    private void sendEmail(@NotNull EmailDetails emailDetails) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        helper.setFrom(senderEmail);
        helper.setTo(emailDetails.to);
        helper.setSubject(emailDetails.subject);
        String emailContent = generateEmailContent(emailDetails);
        helper.setText(emailContent, true);

        mailSender.send(mimeMessage);
    }

    private String generateEmailContent(@NotNull EmailDetails emailDetails) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("userName", emailDetails.userName);
        properties.put("code", emailDetails.code);

        Context context = new Context();
        context.setVariables(properties);

        try {
            return templateEngine.process(emailDetails.templateName.getTemplateName(), context);
        } catch (RuntimeException e) {
            throw new MessagingException("Failed to process template", e);
        }
    }

    @Async
    public void sendValidationEmail(
            String to, String userName, String activationCode, String subject
    ) throws MessagingException {
        try {
            sendEmail(new EmailDetails(
                    to, userName, activationCode, subject, EmailTemplateName.ACTIVATE_ACCOUNT)
            );
        } catch (MessagingException exp) {
            throw new MessagingException("Failed to send validation email", exp);
        }
    }

    @Async
    public void sendResetPassword(String to, String userName, String newPassword, String subject) throws MessagingException {
        try {
            sendEmail(new EmailDetails(to, userName, newPassword, subject, EmailTemplateName.RESET_PASSWORD));
        } catch (MessagingException exp) {
            throw new MessagingException("Failed to send email with new password", exp);
        }
    }
}
