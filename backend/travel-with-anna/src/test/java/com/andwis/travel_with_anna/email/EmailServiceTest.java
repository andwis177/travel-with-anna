package com.andwis.travel_with_anna.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Email Service tests")
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private SpringTemplateEngine templateEngine;
    @InjectMocks
    private EmailService emailService;
    private final String to = "test@example.com";
    private final String userName = "John Doe";
    private final String confirmationUrl = "https://example.com/activate-account";
    private final String loginUrl ="http://localhost:4200/login";
    private final String activationCode = "12345";
    private final String resetPassword = "ResetPassword1234567890";
    private final String subject = "Account Activation";
    private static MimeMessage mimeMessage;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);

        Field senderEmailField = EmailService.class.getDeclaredField("senderEmail");
        senderEmailField.setAccessible(true);
        String senderEmail = "noreply@example.com";
        senderEmailField.set(emailService, senderEmail);

        mimeMessage = mock(MimeMessage.class);
    }

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testSendValidationEmail_Success() throws MessagingException {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenReturn("Processed Template");

        // When
        emailService.sendValidationEmail(to, userName, confirmationUrl, activationCode, subject);

        // Then
        verify(mailSender, times(1)).send(mimeMessage);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine, times(1)).process(eq("activate-account"), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();

        assertEquals(userName, capturedContext.getVariable("userName"));
        assertEquals(confirmationUrl, capturedContext.getVariable("url"));
        assertEquals(activationCode, capturedContext.getVariable("code"));
    }

    @Test
    void testSendResetPassword_Success() throws MessagingException {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenReturn("Processed Template");

        // When
        emailService.sendResetPassword(to, userName, loginUrl, resetPassword, subject);

        // Then
        verify(mailSender, times(1)).send(mimeMessage);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine, times(1)).process(eq("reset-password"), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();

        assertEquals(userName, capturedContext.getVariable("userName"));
        assertEquals(loginUrl, capturedContext.getVariable("url"));
        assertEquals(resetPassword, capturedContext.getVariable("code"));
    }

    @Test
    void testSendValidationEmail_MessagingException() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing error"));

        // When
        MessagingException exception = assertThrows(MessagingException.class, () ->
                emailService.sendValidationEmail(to, userName, confirmationUrl, activationCode, subject));

        // Then
        assertEquals("Failed to send validation email", exception.getMessage());
    }

    @Test
    void testSendResetPasswordEmail_MessagingException() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing error"));

        // When
        MessagingException exception = assertThrows(MessagingException.class, () ->
                emailService.sendResetPassword(to, userName, loginUrl, resetPassword, subject));

        // Then
        assertEquals("Failed to send email with new password", exception.getMessage());
    }
}