package com.andwis.travel_with_anna.trip.expanse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExpanseFacade {

    private final ExpanseService expanseService;

    public ExpanseResponse createOrUpdateExpanse(ExpanseForItemRequest expanseCreator) {
      return expanseService.createOrUpdateExpanse(expanseCreator);
    }

    public ExpanseResponse getExpanseForItem(Long itemId) {
        return expanseService.getExpanseForItem(itemId);
    }

    public BigDecimal getExchangeRate(String currencyFrom, String currencyTo) {
        return expanseService.getExchangeRate(currencyFrom, currencyTo);
    }
    public ExpanseInTripCurrency getExpanseInTripCurrency(BigDecimal price, BigDecimal paid, BigDecimal exchangeRate) {
        return expanseService.getExpanseInTripCurrency(price, paid, exchangeRate);
    }

}
