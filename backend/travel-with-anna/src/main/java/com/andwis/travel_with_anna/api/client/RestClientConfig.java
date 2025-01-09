package com.andwis.travel_with_anna.api.client;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    private static final int READ_TIMEOUT = 10_000;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
                .build();
    }

    @Bean
    public RestClientCustomizer customizeRestClient() {
        return restClientBuilder -> {
            JdkClientHttpRequestFactory jdkHttpRequestFactory = new JdkClientHttpRequestFactory();
            jdkHttpRequestFactory.setReadTimeout(READ_TIMEOUT);
            restClientBuilder.requestFactory(jdkHttpRequestFactory);
        };
    }
}
