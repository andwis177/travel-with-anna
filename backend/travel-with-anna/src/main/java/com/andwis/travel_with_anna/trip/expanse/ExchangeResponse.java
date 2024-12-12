package com.andwis.travel_with_anna.trip.expanse;

import java.math.BigDecimal;

public record ExchangeResponse (String errorMsg, BigDecimal exchangeRate) {
}
