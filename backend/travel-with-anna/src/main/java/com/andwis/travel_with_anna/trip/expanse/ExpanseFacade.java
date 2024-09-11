package com.andwis.travel_with_anna.trip.expanse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExpanseFacade {

    private final ExpanseService expanseService;

    public ExpanseItem createOrUpdateExpanse(ExpanseItemCreator expanseCreator) {
      return expanseService.createOrUpdateExpanse(expanseCreator);
    }

    public ExpanseItem getExpanseForItem(Long itemId) {
        return expanseService.getExpanseForItem(itemId);
    }

    public BigDecimal getExchangeRate(String currencyFrom, String currencyTo) {
        return expanseService.getExchangeRate(currencyFrom, currencyTo);
    }
    public TripCurrencyValue getTripCurrencyValues(BigDecimal price, BigDecimal paid, BigDecimal exchangeRate) {
        return expanseService.getTripCurrencyValues(price, paid, exchangeRate);
    }

}
