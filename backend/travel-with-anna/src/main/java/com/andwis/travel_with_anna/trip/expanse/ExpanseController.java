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
        return ResponseEntity.status(HttpStatus.CREATED).body(
                facade.createOrUpdateExpanse(request, connectedUser));
    }

    @GetMapping("/{expanseId}")
    public ResponseEntity<ExpanseResponse> getExpanseById(
            @PathVariable("expanseId") Long expanseId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.getExpanseById(expanseId, connectedUser));
    }

    @GetMapping("{entityId}/type/{entityType}")
    public ResponseEntity<ExpanseResponse> getExpanseByEntity(
            @PathVariable("entityId") Long entityId,
            @PathVariable("entityType") String entityType,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.getExpanseByEntityId(entityId, entityType, connectedUser));
    }

    @GetMapping("/exchange")
    public ResponseEntity<ExchangeResponse> getExchangeRate(
            @RequestParam String currencyFrom,
            @RequestParam String currencyTo) {
        return ResponseEntity.ok(facade.getExchangeRate(currencyFrom, currencyTo));
    }

    @PostMapping("/trip-currency-values")
    public ResponseEntity<ExpanseInTripCurrency> getTripCurrencyValues(
            @RequestBody @Valid TripCurrencyValuesRequest request) {
        return ResponseEntity.ok(facade.getExpanseInTripCurrency(request));
    }
}
