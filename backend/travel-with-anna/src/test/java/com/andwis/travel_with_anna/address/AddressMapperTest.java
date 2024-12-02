package com.andwis.travel_with_anna.address;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Address Mapper Tests")
class AddressMapperTest {
    private AddressRequest addressRequest;
    private Address address;

    @BeforeEach
    void setUp() {
        addressRequest = AddressRequest.builder()
                .place("Some Place")
                .country("Some Country")
                .countryCode("SC")
                .city("Some City")
                .address("123 Main St")
                .website("www.example.com")
                .phone("123-456-7890")
                .email("example@example.com")
                .currency("USD")
                .build();

        address = Address.builder()
                .addressId(1L)
                .place("Old Place")
                .country("Old Country")
                .countryCode("OC")
                .city("Old City")
                .address("Old Address")
                .website("www.old-example.com")
                .phone("098-765-4321")
                .email("old@example.com")
                .currency("EUR")
                .build();
    }

    @Test
    void testToAddress() {
        // When
        Address result = AddressMapper.toAddress(addressRequest);

        // Then
        assertEquals(addressRequest.getPlace(), result.getPlace());
        assertEquals(addressRequest.getCountry(), result.getCountry());
        assertEquals(addressRequest.getCountryCode(), result.getCountryCode());
        assertEquals(addressRequest.getCity(), result.getCity());
        assertEquals(addressRequest.getAddress(), result.getAddress());
        assertEquals(addressRequest.getWebsite(), result.getWebsite());
        assertEquals(addressRequest.getPhone(), result.getPhone());
        assertEquals(addressRequest.getEmail(), result.getEmail());
        assertEquals(addressRequest.getCurrency(), result.getCurrency());
    }

    @Test
    void testToAddressResponse() {
        // Given
        Address addressToConvert = Address.builder()
                .addressId(1L)
                .place("Some Place")
                .country("Some Country")
                .countryCode("SC")
                .city("Some City")
                .address("123 Main St")
                .website("www.example.com")
                .phone("123-456-7890")
                .email("example@example.com")
                .currency("USD")
                .build();

        // When
        AddressResponse result = AddressMapper.toAddressResponse(addressToConvert);

        // Then
        assertEquals(addressToConvert.getAddressId(), result.addressId());
        assertEquals(addressToConvert.getPlace(), result.place());
        assertEquals(addressToConvert.getCountry(), result.country());
        assertEquals(addressToConvert.getCountryCode(), result.countryCode());
        assertEquals(addressToConvert.getCity(), result.city());
        assertEquals(addressToConvert.getAddress(), result.address());
        assertEquals(addressToConvert.getWebsite(), result.website());
        assertEquals(addressToConvert.getPhone(), result.phone());
        assertEquals(addressToConvert.getEmail(), result.email());
        assertEquals(addressToConvert.getCurrency(), result.currency());
    }

    @Test
    void testUpdateAddress() {
        // Given
        AddressRequest updateRequest = AddressRequest.builder()
                .place("New Place")
                .country("New Country")
                .countryCode("NC")
                .city("New City")
                .address("456 New St")
                .website("www.new-example.com")
                .phone("321-654-0987")
                .email("new@example.com")
                .currency("EUR")
                .build();

        // When
        AddressMapper.updateAddress(address, updateRequest);

        // Then
        assertEquals(updateRequest.getPlace(), address.getPlace());
        assertEquals(updateRequest.getCountry(), address.getCountry());
        assertEquals(updateRequest.getCountryCode(), address.getCountryCode());
        assertEquals(updateRequest.getCity(), address.getCity());
        assertEquals(updateRequest.getAddress(), address.getAddress());
        assertEquals(updateRequest.getWebsite(), address.getWebsite());
        assertEquals(updateRequest.getPhone(), address.getPhone());
        assertEquals(updateRequest.getEmail(), address.getEmail());
        assertEquals(updateRequest.getCurrency(), address.getCurrency());
    }
}