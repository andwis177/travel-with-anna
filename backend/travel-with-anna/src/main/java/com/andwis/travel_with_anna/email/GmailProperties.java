package com.andwis.travel_with_anna.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gmail.oauth2")
@Getter
@Setter
public class GmailProperties {
    private String clientId;
    private String token_uri;
    private String send_scope;
    private String api_endpoint_send;
    private String client_secret;
    private String refresh_token;
    private int local_receiver_port;
}
