package com.andwis.travel_with_anna.trip.expanse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("expanse")
@Tag(name = "Expanse")
public class ExpanseController {
    private final ExpanseFacade facade;

    @PostMapping
    public ResponseEntity<ExpanseResponse> createOrUpdateExpanse(
            @RequestBody @Valid ExpanseRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        ExpanseResponse response = facade.createOrUpdateExpanse(request, connectedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{expanseId}")
    public ResponseEntity<ExpanseResponse> getExpanseById(
            @PathVariable("expanseId") Long expanseId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        ExpanseResponse expanse = facade.getExpanseById(expanseId, connectedUser);
        return ResponseEntity.ok(expanse);
    }

    @GetMapping("{entityId}/type/{entityType}")
    public ResponseEntity<ExpanseResponse> getExpanseByEntity(
            @PathVariable("entityId") Long entityId,
            @PathVariable("entityType") String entityType,
            @AuthenticationPrincipal UserDetails connectedUser) {
        ExpanseResponse expanse = facade.getExpanseByEntityId(entityId, entityType, connectedUser);
        return ResponseEntity.ok(expanse);
    }

    @GetMapping("/exchange")
    public ResponseEntity<ExchangeResponse> getExchangeRate(
            @RequestParam String currencyFrom,
            @RequestParam String currencyTo) {
        ExchangeResponse exchangeResponse = facade.getExchangeRate(currencyFrom, currencyTo);
        return ResponseEntity.ok(exchangeResponse);
    }

    @PostMapping("/trip-currency-values")
    public ResponseEntity<ExpanseInTripCurrency> getTripCurrencyValues(
            @RequestBody @Valid TripCurrencyValuesRequest request) {
        ExpanseInTripCurrency expanseInTripCurrency = facade.getExpanseInTripCurrency(request);
        return ResponseEntity.ok(expanseInTripCurrency);
    }
}
