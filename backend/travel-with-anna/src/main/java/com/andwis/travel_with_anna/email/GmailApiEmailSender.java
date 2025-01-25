package com.andwis.travel_with_anna.email;

import com.andwis.travel_with_anna.handler.exception.OAuth2AccessTokenException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

@Service("GmailApiEmailSender")
public class GmailApiEmailSender implements EmailService{

    private final GmailProperties gmailProperties;
    private final EmailContentCreator contentCreator;
    private final HttpClient httpClient;

    public GmailApiEmailSender(GmailProperties gmailProperties, EmailContentCreator contentCreator, HttpClient httpClient) {
        this.gmailProperties = gmailProperties;
        this.contentCreator = contentCreator;
        this.httpClient = httpClient;
    }

    /*
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "backend/travel-with-anna/src/main/resources/credentials.json";
*/

    private static final String PAYLOAD_STRING_FORMAT = "client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token";
    private static final String HTTP_CONTENT_TYPE_HEADER = "Content-Type";
    private static final String HTTP_TOKEN_REQUEST_HEADER_VALUE = "application/x-www-form-urlencoded";
    private static final String HTTP_RESPONSE_KEY = "access_token";
    private static final String OAUTH2_TOKEN_EXCEPTION_RESPONSE = "Access token could not be retrieved";
    private static final String JSON_PAYLOAD_FORMAT = "{\"raw\":\"%s\"}";
    private static final String HTTP_AUTHORIZATION_HEADER = "Authorization";
    private static final String HTTP_EMAIL_SEND_REQUEST_HEADER_VALUE = "application/json";

/*
    //  *** When you use credential.json file ***

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new FileReader(CREDENTIALS_FILE_PATH)
      );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientSecrets,
                Collections.singletonList(gmailProperties.getSend_scope())
        )
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(gmailProperties.getLocal_receiver_port()).build();
        return new com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
*/

    private String getAccessToken() throws IOException, InterruptedException {
        String tokenUri = gmailProperties.getToken_uri();

        String payload = String.format(
                PAYLOAD_STRING_FORMAT,
                gmailProperties.getClientId(),
                gmailProperties.getClient_secret(),
                gmailProperties.getRefresh_token()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUri))
                .header(HTTP_CONTENT_TYPE_HEADER, HTTP_TOKEN_REQUEST_HEADER_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();

        try {
            JSONObject jsonResponseBody = new JSONObject(responseBody);

            if (jsonResponseBody.has(HTTP_RESPONSE_KEY)) {
                return jsonResponseBody.getString(HTTP_RESPONSE_KEY);
            } else {
                throw new OAuth2AccessTokenException(OAUTH2_TOKEN_EXCEPTION_RESPONSE);
            }
        } catch (JSONException e) {
            throw new OAuth2AccessTokenException("Invalid response format: Not a JSON object.", e);
        }
    }

    public void sendEmail(EmailDetails emailDetails) throws MessagingException, IOException, InterruptedException {
/*
        // *** Build a new authorized API client service with credential.json file ***
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);
*/
        String emailContent = contentCreator.generateEmailContent(emailDetails);

        String rawMessage = createEmail(emailDetails.sendFrom(), emailDetails.sendTo(),
                emailDetails.subject(), emailContent);

        String endpoint = gmailProperties.getApi_endpoint_send();
        String requestBody = String.format(JSON_PAYLOAD_FORMAT, rawMessage);
/*
        // *** Request for credentials created out of credential.json file
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header(HTTP_AUTHORIZATION_HEADER, "Bearer " + credential.getAccessToken())
                .header(HTTP_CONTENT_TYPE_HEADER, HTTP_EMAIL_SEND_REQUEST_HEADER_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
*/
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header(HTTP_AUTHORIZATION_HEADER, "Bearer " + getAccessToken())
                .header(HTTP_CONTENT_TYPE_HEADER, HTTP_EMAIL_SEND_REQUEST_HEADER_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String createEmail(String from, String to, String subject, String content) throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setContent(content, "text/html");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = Base64.encodeBase64(buffer.toByteArray());
        return new String(rawMessageBytes);
    }
}