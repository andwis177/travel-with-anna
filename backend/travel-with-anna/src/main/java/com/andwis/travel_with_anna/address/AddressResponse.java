package com.andwis.travel_with_anna.address;

public record AddressResponse(
        Long addressId,
        String place,
        String country,
        String countryCode,
        String city,
        String address,
        String website,
        String phoneNumber,
        String email,
        String currency
) {
}
