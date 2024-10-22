package com.andwis.travel_with_anna.address;

public class AddressMapper {

    public static Address toAddress(AddressRequest addressRequest) {
        return Address.builder()
                .place(addressRequest.getPlace())
                .country(addressRequest.getCountry())
                .countryCode(addressRequest.getCountryCode())
                .city(addressRequest.getCity())
                .address(addressRequest.getAddress())
                .website(addressRequest.getWebsite())
                .phone(addressRequest.getPhone())
                .email(addressRequest.getEmail())
                .currency(addressRequest.getCurrency())
                .build();
    }

    public static AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getAddressId(),
                address.getPlace(),
                address.getCountry(),
                address.getCountryCode(),
                address.getCity(),
                address.getAddress(),
                address.getWebsite(),
                address.getPhone(),
                address.getEmail(),
                address.getCurrency()
        );
    }

    public static void updateAddress(Address address, AddressRequest addressRequest) {
        address.setPlace(addressRequest.getPlace());
        address.setCountry(addressRequest.getCountry());
        address.setCountryCode(addressRequest.getCountryCode());
        address.setCity(addressRequest.getCity());
        address.setAddress(addressRequest.getAddress());
        address.setWebsite(addressRequest.getWebsite());
        address.setPhone(addressRequest.getPhone());
        address.setEmail(addressRequest.getEmail());
        address.setCurrency(addressRequest.getCurrency());
    }
}
