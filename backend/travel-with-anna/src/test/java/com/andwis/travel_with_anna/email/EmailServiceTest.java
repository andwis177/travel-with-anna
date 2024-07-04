package com.andwis.travel_with_anna.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Email Controller tests")
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    private final String to = "test@example.com";
    private final String userName = "John Doe";
    private final String confirmationUrl = "http://example.com/activate-account";
    private final String activationCode = "12345";
    private final String subject = "Account Activation";
    private static MimeMessage mimeMessage;


    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        Field senderEmailField = EmailService.class.getDeclaredField("senderEmail");
        senderEmailField.setAccessible(true);
        String senderEmail = "noreply@example.com";
        senderEmailField.set(emailService, senderEmail);

        mimeMessage = mock(MimeMessage.class);
    }

    @Test
    void testSendValidationEmail_Success() throws MessagingException {
        //Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenReturn("Processed Template");

        //When
        emailService.sendValidationEmail(to, userName, confirmationUrl, activationCode, subject);

        //Then
        verify(mailSender, times(1)).send(mimeMessage);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine, times(1)).process(eq("activate-account"), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();

        assertEquals(userName, capturedContext.getVariable("userName"));
        assertEquals(confirmationUrl, capturedContext.getVariable("confirmationUrl"));
        assertEquals(activationCode, capturedContext.getVariable("activationCode"));
    }

    @Test
    void testSendValidationEmail_MessagingException() {
        //Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing error"));

        //When
        MessagingException exception = assertThrows(MessagingException.class, () -> {
                    emailService.sendValidationEmail(to, userName, confirmationUrl, activationCode, subject);
                }
        );

        //Then
        assertEquals("Failed to send validation email", exception.getMessage());
    }
}