package com.andwis.travel_with_anna.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SmtpEmailSender Service Tests")
class SmtpEmailSenderTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailContentCreator contentCreator;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private SmtpEmailSender emailService;

    private static MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mimeMessage = mock(MimeMessage.class);
    }

    @Test
    void testSendEmail_Success() throws MessagingException {
        // Given
        EmailDetails details = createEmailDetails();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(contentCreator.generateEmailContent(any(EmailDetails.class)))
                .thenReturn("Processed Template");

        // When
        emailService.sendEmail(details);

        // Then
        verify(mailSender, times(1)).send(mimeMessage);
        verify(contentCreator, times(1)).generateEmailContent(details);
    }

    @Test
    void testSendEmail_TemplateProcessingFailure() {
        // Given
        EmailDetails details = createEmailDetails();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing error"));

        // When
        MessagingException exception = assertThrows(MessagingException.class, () ->
                emailService.sendEmail(details));

        // Then
        assertEquals("Email content must not be null", exception.getMessage());
    }

    private EmailDetails createEmailDetails() {
        String subject = "Subject Example";
        String userName = "John Doe";
        String sendTo = "test@example.com";
        return new EmailDetails(
                sendTo,
                userName,
                "sendFromAddress",
                "Some Content",
                subject,
                EmailTemplateName.ACTIVATE_ACCOUNT
        );
    }
}