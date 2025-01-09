package com.andwis.travel_with_anna.trip.expanse;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
@Data
@Builder
public class ExpanseResponse implements Comparable<ExpanseResponse> {

    private Long expanseId;
    private String expanseName;
    private String expanseCategory;
    private String date;
    private String currency;
    private BigDecimal price;
    private BigDecimal paid;
    private BigDecimal exchangeRate;
    private BigDecimal priceInTripCurrency;
    private BigDecimal paidInTripCurrency;

    @Override
    public int compareTo(@NotNull ExpanseResponse o) {
        return this.date.compareTo(o.date);
    }
}
