package com.andwis.travel_with_anna.address;

import org.jetbrains.annotations.NotNull;

public class AddressMapper {

    public static Address toAddress(@NotNull AddressRequest addressRequest) {
        return mapCommonFieldsToBuilder(Address.builder(), addressRequest).build();
    }

    public static @NotNull AddressResponse
    toAddressResponse(@NotNull Address address) {
        return new AddressResponse(
                address.getAddressId(),
                address.getPlace() != null ? address.getPlace() : "",
                address.getCountry() != null ? address.getCountry() : "",
                address.getCountryCode() != null ? address.getCountryCode() : "",
                address.getCity() != null ? address.getCity() : "",
                address.getAddress() != null ? address.getAddress() : "",
                address.getWebsite() != null ? address.getWebsite() : "",
                address.getPhoneNumber() != null ? address.getPhoneNumber() : "",
                address.getEmail() != null ? address.getEmail() : "",
                address.getCurrency() != null ? address.getCurrency() : ""
        );
    }

    public static void updateExistingAddress(
            @NotNull Address address, @NotNull AddressRequest addressRequest) {
        mapCommonFieldsToExistingAddress(address, addressRequest);
    }

    private static Address.AddressBuilder mapCommonFieldsToBuilder(
            Address.@NotNull AddressBuilder builder, @NotNull AddressRequest addressRequest) {
        return builder
                .place(addressRequest.getPlace())
                .country(addressRequest.getCountry())
                .countryCode(addressRequest.getCountryCode())
                .city(addressRequest.getCity())
                .address(addressRequest.getAddress())
                .website(addressRequest.getWebsite())
                .phoneNumber(addressRequest.getPhoneNumber())
                .email(addressRequest.getEmail())
                .currency(addressRequest.getCurrency());
    }

    private static void mapCommonFieldsToExistingAddress(
            @NotNull Address address, @NotNull AddressRequest addressRequest) {
        address.setPlace(addressRequest.getPlace());
        address.setCountry(addressRequest.getCountry());
        address.setCountryCode(addressRequest.getCountryCode());
        address.setCity(addressRequest.getCity());
        address.setAddress(addressRequest.getAddress());
        address.setWebsite(addressRequest.getWebsite());
        address.setPhoneNumber(addressRequest.getPhoneNumber());
        address.setEmail(addressRequest.getEmail());
        address.setCurrency(addressRequest.getCurrency());
    }
}
