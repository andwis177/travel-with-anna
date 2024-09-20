package com.andwis.travel_with_anna.api.currency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class CurrencyExchangeClient {
    @Value("${my_apis.currency.header.apikey}")
    private String apiKey;
    @Value("${my_apis.currency.currency_url}")
    private String baseUrl;
    private final RestClient restClient;

    public CurrencyExchangeClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<CurrencyExchangeResponse> getAllExchangeRates() {
        CurrencyResponse response = restClient.get()
                .uri(baseUrl + "/latest")
                .header("apikey", apiKey)
                .retrieve()
                .body(CurrencyResponse.class);

        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }

        List<CurrencyExchangeResponse> currencyExchangeResponses = new ArrayList<>();

        response.getData().forEach((code, dto) -> {
            dto.setCode(code);
            currencyExchangeResponses.add(dto);
        });
        currencyExchangeResponses.sort(Comparator.comparing(CurrencyExchangeResponse::getCode));

        return currencyExchangeResponses;
    }
}
