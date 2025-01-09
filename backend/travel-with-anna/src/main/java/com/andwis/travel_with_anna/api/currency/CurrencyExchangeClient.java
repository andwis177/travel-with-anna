package com.andwis.travel_with_anna.api.currency;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
@Slf4j
public class CurrencyExchangeClient {

    @Value("${my_apis.currency.header.apikey}")
    private String apiKey;
    @Value("${my_apis.currency.currency_url}")
    private String baseUrl;
    private final RestClient restClient;

    public CurrencyExchangeClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<CurrencyExchangeResponse> fetchAllExchangeRates() {
        try {
            String endpointUrl = baseUrl + "/latest";
            CurrencyResponse response = fetchCurrencyResponse(endpointUrl);

            if (isInvalidResponse(response)) {
                return Collections.emptyList();
            }

            return mapToCurrencyExchangeResponseList(response.getExchangeRates());
        } catch (Exception e) {
            log.error("Error during fetchAllExchangeRates API call: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private CurrencyResponse fetchCurrencyResponse(String url) {
        return restClient.get()
                .uri(url)
                .header("apikey", apiKey)
                .retrieve()
                .body(CurrencyResponse.class);
    }

    private boolean isInvalidResponse(CurrencyResponse response) {
        return response == null || response.getExchangeRates() == null;
    }

    private @NotNull List<CurrencyExchangeResponse> mapToCurrencyExchangeResponseList(
            @NotNull Map<String, CurrencyExchangeResponse> data) {
        List<CurrencyExchangeResponse> currencyExchangeResponses = new ArrayList<>();

        data.forEach((currencyCode, responseDto) -> {
            responseDto.setCode(currencyCode);
            currencyExchangeResponses.add(responseDto);
        });

        currencyExchangeResponses.sort(Comparator.comparing(CurrencyExchangeResponse::getCode));
        return currencyExchangeResponses;
    }
}
