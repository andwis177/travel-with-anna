package com.andwis.travel_with_anna.api.client;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
                .build();
    }

    @Bean
    public RestClientCustomizer countryClientCustomizer() {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(10000);

        return (restClientBuilder -> restClientBuilder
                .requestFactory(requestFactory));
    }
}
