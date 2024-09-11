package com.andwis.travel_with_anna.trip.expanse;

import java.math.BigDecimal;

public class ExpanseMapper {

    public static Expanse mapToExpanse(ExpanseItem expanseRequest) {
        return Expanse.builder()
                .expanseName(expanseRequest.getExpanseName())
                .currency(expanseRequest.getCurrency())
                .price(expanseRequest.getPrice() != null ? expanseRequest.getPrice() : BigDecimal.ZERO)
                .paid(expanseRequest.getPaid() != null ? expanseRequest.getPaid() : BigDecimal.ZERO)
                .exchangeRate(expanseRequest.getExchangeRate() != null ? expanseRequest.getExchangeRate() : BigDecimal.ONE)
                .build();
    }

    public static ExpanseItem mapToExpanseItem(Expanse expanse) {
        return ExpanseItem.builder()
                .expanseName(expanse.getExpanseName())
                .currency(expanse.getCurrency())
                .price(expanse.getPrice())
                .paid(expanse.getPaid())
                .exchangeRate(expanse.getExchangeRate())
                .build();
    }
}
