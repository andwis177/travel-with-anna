package com.andwis.travel_with_anna.trip.expanse;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.andwis.travel_with_anna.utility.DateTimeMapper.toLocalDate;

public class ExpanseMapper {

    public static void mapToExpanse(Expanse expanse, ExpanseRequest expanseRequest) {
        if (expanseRequest == null) {
            throw new IllegalArgumentException("Expanse and ExpanseRequest cannot be null");
        }
        if (expanse == null) {
            expanse = Expanse.builder()
                    .expanseName(expanseRequest.getExpanseName())
                    .expanseCategory(expanseRequest.getExpanseCategory())
                    .currency(expanseRequest.getCurrency())
                    .price(expanseRequest.getPrice() != null ? expanseRequest.getPrice() : BigDecimal.ZERO)
                    .paid(expanseRequest.getPaid() != null ? expanseRequest.getPaid() : BigDecimal.ZERO)
                    .exchangeRate(expanseRequest.getExchangeRate() != null ? expanseRequest.getExchangeRate() : BigDecimal.ONE)
                    .build();

            if (expanseRequest.getDate() != null && !expanseRequest.getDate().isBlank()) {
                expanse.setDate(toLocalDate(expanseRequest.getDate()));
            }
        }
        if (expanse != null) {
            updateExpanse(expanse, expanseRequest);
        }
    }

    public static @NotNull ExpanseResponse toExpanseResponse(@NotNull Expanse expanse) {
        String expanseDate;
        if (expanse.getDate() != null) {
            expanseDate = expanse.getDate().toString();
        } else {
            expanseDate = "";
        }

        return new ExpanseResponse(
                expanse.getExpanseId(),
                expanse.getExpanseName(),
                expanse.getExpanseCategory(),
                expanseDate,
                expanse.getCurrency(),
                expanse.getPrice(),
                expanse.getPaid(),
                expanse.getExchangeRate(),
                expanse.calculatePriceInTripCurrency(),
                expanse.calculatePaidInTripCurrency()
        );
    }

    public static void updateExpanse(@NotNull Expanse expanse, @NotNull ExpanseRequest expanseRequest) {
        expanse.setExpanseName(expanseRequest.getExpanseName() != null ? expanseRequest.getExpanseName() : expanse.getExpanseName() );
        expanse.setExpanseCategory(expanseRequest.getExpanseCategory() !=null ? expanseRequest.getExpanseCategory() : "");
        expanse.setCurrency(expanseRequest.getCurrency() !=null ? expanseRequest.getCurrency() : "");
        expanse.setPrice(expanseRequest.getPrice() != null ? expanseRequest.getPrice() : BigDecimal.ZERO);
        expanse.setPaid(expanseRequest.getPaid() != null ? expanseRequest.getPaid() : BigDecimal.ZERO);
        expanse.setExchangeRate(expanseRequest.getExchangeRate() !=null ? expanseRequest.getExchangeRate() : BigDecimal.ONE);

        if (expanseRequest.getDate() != null && !expanseRequest.getDate().isBlank()) {
            expanse.setDate(toLocalDate(expanseRequest.getDate()));
        }
    }

    public static List<ExpanseResponse> mapToExpanseResponseList(@NotNull List<Expanse> expanses) {
        return expanses.stream()
                .map(ExpanseMapper::toExpanseResponse)
                .collect(Collectors.toList());
    }
}
