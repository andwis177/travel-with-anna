package com.andwis.travel_with_anna.trip.expanse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("expanse")
@Tag(name = "Expanse")
public class ExpanseController {
    private final ExpanseFacade facade;

    @PostMapping("/save")
    public ResponseEntity<ExpanseResponse> createOrUpdateExpanse(@RequestBody @Valid ExpanseForItemRequest creator) {
        ExpanseResponse respond = facade.createOrUpdateExpanse(creator);
        return ResponseEntity.status(HttpStatus.CREATED).body(respond);
    }

    @GetMapping("/{itemId}/expanse")
    public ResponseEntity<ExpanseResponse> getExpanseForItem(@PathVariable("itemId") Long itemId) {
        ExpanseResponse expanse = facade.getExpanseForItem(itemId);
        return ResponseEntity.ok(expanse);
    }

    @GetMapping("/exchange")
    public ResponseEntity<BigDecimal> getExchangeRate(@RequestParam String currencyFrom, @RequestParam String currencyTo) {
        BigDecimal exchangeRate = facade.getExchangeRate(currencyFrom, currencyTo);
        return ResponseEntity.ok(exchangeRate);
    }

    @GetMapping("/trip-currency-values")
    public ResponseEntity<ExpanseInTripCurrency> getTripCurrencyValues(
            @RequestParam BigDecimal price, @RequestParam BigDecimal paid, @RequestParam BigDecimal exchangeRate) {
        ExpanseInTripCurrency expanseInTripCurrency = facade.getExpanseInTripCurrency(price, paid, exchangeRate);
        return ResponseEntity.ok(expanseInTripCurrency);
    }
}
