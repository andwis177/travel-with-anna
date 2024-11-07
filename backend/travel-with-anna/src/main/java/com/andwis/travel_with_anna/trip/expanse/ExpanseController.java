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

    @PostMapping
    public ResponseEntity<ExpanseResponse> createOrUpdateExpanse(@RequestBody @Valid ExpanseRequest creator) {
        ExpanseResponse response = facade.createOrUpdateExpanse(creator);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{expanseId}")
    public ResponseEntity<ExpanseResponse> getExpanseById(@PathVariable("expanseId") Long expanseId) {
        ExpanseResponse expanse = facade.getExpanseById(expanseId);
        return ResponseEntity.ok(expanse);
    }

    @GetMapping("{entityId}/type/{entityType}")
    public ResponseEntity<ExpanseResponse> getExpanseByEntity(
            @PathVariable("entityId") Long entityId,
            @PathVariable("entityType") String entityType) {
        ExpanseResponse expanse = facade.getExpanseByEntityId(entityId, entityType);
        return ResponseEntity.ok(expanse);
    }

    @GetMapping("/exchange")
    public ResponseEntity<ExchangeResponse> getExchangeRate(@RequestParam String currencyFrom, @RequestParam String currencyTo) {
        ExchangeResponse exchangeResponse = facade.getExchangeRate(currencyFrom, currencyTo);
        return ResponseEntity.ok(exchangeResponse);
    }

    @GetMapping("/trip-currency-values")
    public ResponseEntity<ExpanseInTripCurrency> getTripCurrencyValues(
            @RequestParam BigDecimal price, @RequestParam BigDecimal paid, @RequestParam BigDecimal exchangeRate) {
        ExpanseInTripCurrency expanseInTripCurrency = facade.getExpanseInTripCurrency(price, paid, exchangeRate);
        return ResponseEntity.ok(expanseInTripCurrency);
    }
}
