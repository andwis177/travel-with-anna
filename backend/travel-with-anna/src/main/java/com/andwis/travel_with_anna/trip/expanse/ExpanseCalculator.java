package com.andwis.travel_with_anna.trip.expanse;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@Data
@Builder
public class ExpanseCalculator {

    private final BigDecimal totalPrice;
    private final BigDecimal totalPaid;
    private final BigDecimal totalPriceInTripCurrency;
    private final BigDecimal totalPaidInTripCurrency;
    private final BigDecimal totalDebt;

    public ExpanseCalculator(BigDecimal totalPrice,
                             BigDecimal totalPaid,
                             BigDecimal totalPriceInTripCurrency,
                             BigDecimal totalPaidInTripCurrency,
                             BigDecimal totalDebt) {
        this.totalPrice = totalPrice;
        this.totalPaid = totalPaid;
        this.totalPriceInTripCurrency = totalPriceInTripCurrency;
        this.totalPaidInTripCurrency = totalPaidInTripCurrency;
        this.totalDebt = totalDebt;
    }

    public ExpanseCalculator add(@NotNull ExpanseCalculator other) {
        return mergeFields(other, BigDecimal::add);
    }

    private ExpanseCalculator mergeFields(@NotNull ExpanseCalculator other,
                                          @NotNull BiFunction<BigDecimal, BigDecimal, BigDecimal> mergeFunction) {
        return ExpanseCalculator.builder()
                .totalPrice(mergeFunction.apply(this.totalPrice, other.totalPrice))
                .totalPaid(mergeFunction.apply(this.totalPaid, other.totalPaid))
                .totalPriceInTripCurrency(mergeFunction.apply(this.totalPriceInTripCurrency, other.totalPriceInTripCurrency))
                .totalPaidInTripCurrency(mergeFunction.apply(this.totalPaidInTripCurrency, other.totalPaidInTripCurrency))
                .totalDebt(mergeFunction.apply(this.totalDebt, other.totalDebt))
                .build();
    }
}