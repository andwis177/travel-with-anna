package com.andwis.travel_with_anna.address;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.andwis.travel_with_anna.address.Address.*;

@Getter
@Setter
@Builder
public class AddressRequest {
    @Size(max = MAX_NAME_LENGTH, message = "Place name should be " +
            MAX_NAME_LENGTH + " characters or less")
    private String place;

    @Size(max = MAX_NAME_LENGTH, message = "Country name should be " +
            MAX_NAME_LENGTH + " characters or less")
    private String country;

    @Size(max = MAX_CODE_LENGTH, message = "Country code should be " +
            MAX_CODE_LENGTH + " characters or less")
    private String countryCode;

    @Size(max = MAX_NAME_LENGTH, message = "City name should be " +
            MAX_NAME_LENGTH + "  characters or less")
    private String city;

    @Size(max = MAX_LENGTH, message = "Address should be " +
            MAX_LENGTH + " characters or less")
    private String address;

    @Size(max = MAX_LENGTH, message = "Website should be " +
            MAX_LENGTH + " characters or less")
    private String website;

    @Size(max = MAX_PHONE_LENGTH, message = "Phone number should be " +
            MAX_PHONE_LENGTH + " characters or less")
    private String phoneNumber;

    @Size(max = MAX_EMAIL_LENGTH, message = "Email address should be " +
            MAX_EMAIL_LENGTH + " characters or less")
    private String email;

    @Size(max = MAX_CURRENCY_LENGTH, message = "Currency should be " +
            MAX_CURRENCY_LENGTH + " characters or less")
    private String currency;
}
