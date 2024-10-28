package com.andwis.travel_with_anna.trip.expanse;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExpanseTotalByBadge {
   private String type;
   private BigDecimal totalPriceInTripCurrency;
   private BigDecimal totalPaidInTripCurrency;
}
