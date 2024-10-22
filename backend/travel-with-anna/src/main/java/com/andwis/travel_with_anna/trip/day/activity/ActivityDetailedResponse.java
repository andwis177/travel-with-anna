package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressDetail;

import java.math.BigDecimal;
import java.util.List;

public record ActivityDetailedResponse(
        AddressDetail addressDetail,
        List<ActivityResponse> activities,
        BigDecimal totalPrice,
        BigDecimal totalPayment
) {
}
