package com.andwis.travel_with_anna.api.currency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CurrencyExchangeService {
    @Value("${my_apis.currency.header.apikey}")
    private String apiKey;
    private final RestClient restClient;

    public CurrencyExchangeService(RestClient restClient) {
        this.restClient = restClient;
//        this.restClient = RestClient.builder()
//                .baseUrl(baseUrl)
//                .build();
        this.restClient.head().header("apikey", apiKey).retrieve();

//        this.apiKey = apiKey;
    }

    public List<CurrencyExchangeDto> getAllExchangeRates() {
        CurrencyResponseDto response = restClient.get()
                .uri("/latest")
                .header("apikey", apiKey)
                .retrieve()
                .body(CurrencyResponseDto.class);

        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }

        List<CurrencyExchangeDto> currencyExchangeDtoList = new ArrayList<>();

        response.getData().forEach((code, dto) -> {
            dto.setCode(code);
            currencyExchangeDtoList.add(dto);
        });

        return currencyExchangeDtoList;
    }
}
