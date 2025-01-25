package com.andwis.travel_with_anna.email;

import com.andwis.travel_with_anna.handler.exception.OAuth2AccessTokenException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("GmailApiEmailSender Service Tests")
class GmailApiEmailSenderTest {

    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String API_SEND_ENDPOINT = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";
    private static final String MOCK_ACCESS_TOKEN_RESPONSE = "{\"access_token\": \"mock-access-token\"}";

    @Mock
    private GmailProperties gmailProperties;

    @Mock
    private EmailContentCreator contentCreator;

    @InjectMocks
    private GmailApiEmailSender emailService;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> tokenResponse;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        MockitoAnnotations.openMocks(this);
        mockGmailProperties();
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(tokenResponse);
    }

    @Test
    void testSendEmail_Success() throws MessagingException, IOException, InterruptedException {
        // Given
        EmailDetails emailDetails = createEmailDetails();
        prepareTokenResponse(MOCK_ACCESS_TOKEN_RESPONSE);
        when(contentCreator.generateEmailContent(emailDetails)).thenReturn("Generated Content");

        // When
        emailService.sendEmail(emailDetails);

        // Then
        verify(httpClient, times(2)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void testSendEmail_InvalidTokenResponse() throws IOException, InterruptedException, MessagingException {
        // Given
        EmailDetails emailDetails = createEmailDetails();
        prepareTokenResponse("200 OK");
        when(contentCreator.generateEmailContent(emailDetails)).thenReturn("Generated Content");

        // When
        OAuth2AccessTokenException exception = assertThrows(OAuth2AccessTokenException.class, () ->
                emailService.sendEmail(emailDetails));

        // Then
        assertEquals("Invalid response format: Not a JSON object.", exception.getMessage());
    }

    private void mockGmailProperties() {
        when(gmailProperties.getToken_uri()).thenReturn(TOKEN_URI);
        when(gmailProperties.getApi_endpoint_send()).thenReturn(API_SEND_ENDPOINT);
        when(gmailProperties.getClientId()).thenReturn("test-client-id");
        when(gmailProperties.getClient_secret()).thenReturn("test-client-secret");
        when(gmailProperties.getRefresh_token()).thenReturn("refresh-token");
    }

    private EmailDetails createEmailDetails() {
        return new EmailDetails(
                "test@example.com",
                "John Doe",
                "from@example.com",
                "Email Content",
                "Subject",
                EmailTemplateName.ACTIVATE_ACCOUNT
        );
    }

    private void prepareTokenResponse(String responseBody) throws IOException, InterruptedException {
        when(httpClient.send(argThat(request -> request.uri().toString().equals(TOKEN_URI)),
                eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(tokenResponse);
        when(tokenResponse.body()).thenReturn(responseBody);
    }
}