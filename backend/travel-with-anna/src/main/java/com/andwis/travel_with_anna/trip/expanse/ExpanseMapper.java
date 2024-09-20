package com.andwis.travel_with_anna.trip.expanse;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ExpanseMapper {

    public static Expanse mapToExpanse(ExpanseRequest expanseRequest) {
        return Expanse.builder()
                .expanseName(expanseRequest.getExpanseName())
                .currency(expanseRequest.getCurrency())
                .price(expanseRequest.getPrice() != null ? expanseRequest.getPrice() : BigDecimal.ZERO)
                .paid(expanseRequest.getPaid() != null ? expanseRequest.getPaid() : BigDecimal.ZERO)
                .exchangeRate(expanseRequest.getExchangeRate() != null ? expanseRequest.getExchangeRate() : BigDecimal.ONE)
                .build();
    }

    public static ExpanseResponse mapToExpanseItem(Expanse expanse) {
        return new ExpanseResponse(
                expanse.getExpanseId(),
                expanse.getExpanseName(),
                expanse.getCurrency(),
                expanse.getPrice(),
                expanse.getPaid(),
                expanse.getExchangeRate(),
                expanse.getPriceInTripCurrency(),
                expanse.getPaidInTripCurrency()
        );

    }

    public static List<ExpanseResponse> mapToExpanseItemList(List<Expanse> expanses) {
        return expanses.stream()
                .map(ExpanseMapper::mapToExpanseItem)
                .collect(Collectors.toList());
    }
}
