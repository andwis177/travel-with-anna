package com.andwis.travel_with_anna.trip.expanse;

import lombok.Data;
import java.math.BigDecimal;

@Data
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

    public ExpanseCalculator add(ExpanseCalculator other) {
        return new ExpanseCalculator(
                this.totalPrice.add(other.totalPrice),
                this.totalPaid.add(other.totalPaid),
                this.totalPriceInTripCurrency.add(other.totalPriceInTripCurrency),
                this.totalPaidInTripCurrency.add(other.totalPaidInTripCurrency),
                this.totalDebt.add(other.totalDebt)
        );
    }
}
