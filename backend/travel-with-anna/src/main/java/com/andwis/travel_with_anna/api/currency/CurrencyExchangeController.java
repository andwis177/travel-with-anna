package com.andwis.travel_with_anna.api.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class CurrencyExchangeController {
    private final CurrencyExchangeService currencyExchangeService;

    @GetMapping("/rates")
    public ResponseEntity<List<CurrencyExchangeDto>> getCurrencyExchangeRates() {
        List<CurrencyExchangeDto> exchangeDtoList = currencyExchangeService.getAllExchangeRates();
        return ResponseEntity.ok(exchangeDtoList);
    }
}
