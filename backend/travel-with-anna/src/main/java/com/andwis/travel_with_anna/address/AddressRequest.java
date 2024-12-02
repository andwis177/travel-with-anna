package com.andwis.travel_with_anna.address;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddressRequest {
    @Size(max = 60, message = "Place name should be 60 characters or less")
    private String place;
    @Size(max = 40, message = "Country name should be 40 characters or less")
    private String country;
    @Size(max = 3, message = "Country code should be 3 characters or less")
    private String countryCode;
    @Size(max = 40, message = "City name should be 40 characters or less")
    private String city;
    private String address;
    private String website;
    @Size(max = 30, message = "Phone number should be 30 characters or less")
    private String phone;
    @Size(max = 150, message = "Email address should be 150 characters or less")
    @Email(message = "Email is not valid")
    private String email;
    private String currency;
}
