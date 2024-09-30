package com.andwis.travel_with_anna.trip.expanse;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ExpanseMapper {

    public static void mapToExpanse(Expanse expanse, ExpanseRequest expanseRequest) {
        if (expanseRequest == null) {
            throw new IllegalArgumentException("Expanse and ExpanseRequest cannot be null");
        }
        if (expanse == null) {
            expanse = Expanse.builder()
                    .expanseName(expanseRequest.getExpanseName())
                    .currency(expanseRequest.getCurrency())
                    .price(expanseRequest.getPrice() != null ? expanseRequest.getPrice() : BigDecimal.ZERO)
                    .paid(expanseRequest.getPaid() != null ? expanseRequest.getPaid() : BigDecimal.ZERO)
                    .exchangeRate(expanseRequest.getExchangeRate() != null ? expanseRequest.getExchangeRate() : BigDecimal.ONE)
                    .build();
        }
        if (expanse != null) {
            updateExpanse(expanse, expanseRequest);
        }
    }


    public static @NotNull ExpanseResponse mapToExpanseResponse(@NotNull Expanse expanse) {
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

    public static void updateExpanse(@NotNull Expanse expanse, @NotNull ExpanseRequest expanseRequest) {
        expanse.setExpanseName(expanseRequest.getExpanseName());
        expanse.setCurrency(expanseRequest.getCurrency());
        expanse.setPrice(expanseRequest.getPrice());
        expanse.setPaid(expanseRequest.getPaid());
        expanse.setExchangeRate(expanseRequest.getExchangeRate());
    }

    public static List<ExpanseResponse> mapToExpanseResponseList(@NotNull List<Expanse> expanses) {
        return expanses.stream()
                .map(ExpanseMapper::mapToExpanseResponse)
                .collect(Collectors.toList());
    }
}
