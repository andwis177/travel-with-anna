package com.andwis.travel_with_anna.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service("SmtpEmailSender")
@RequiredArgsConstructor()
public class SmtpEmailSender implements EmailService{
    private final JavaMailSender mailSender;
    private final EmailContentCreator contentCreator;

    public void sendEmail(EmailDetails emailDetails) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        helper.setFrom(emailDetails.sendFrom());
        helper.setTo(emailDetails.sendTo());
        helper.setSubject(emailDetails.content());

        String emailContent;
        try {
            emailContent = contentCreator.generateEmailContent(emailDetails);
        } catch (Exception e) {
            throw new MessagingException("Failed to generate email content", e);
        }

        if (emailContent == null) {
            throw new MessagingException("Email content must not be null");
        }
        helper.setText(emailContent, true);

        mailSender.send(mimeMessage);
    }
}