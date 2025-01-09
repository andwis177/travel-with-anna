package com.andwis.travel_with_anna.trip.expanse;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Data
@Builder
public class ExpanseByCurrency implements Comparable<ExpanseByCurrency> {

    private String currencyCode;
    private BigDecimal totalPrice;
    private BigDecimal totalPaid;
    private BigDecimal totalPriceInTripCurrency;
    private BigDecimal totalPaidInTripCurrency;
    private BigDecimal totalDebt;

    @Override
    public int compareTo(@NotNull ExpanseByCurrency o) {
        return this.currencyCode.compareTo(o.currencyCode);
    }
}
