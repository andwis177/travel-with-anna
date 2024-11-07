package com.andwis.travel_with_anna.address;

import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Address Service Tests")
class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @InjectMocks
    private AddressService addressService;
    private Address testAddress;
    private Set<Day> testDays;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testAddress = new Address();
        testAddress.setAddressId(1L);
        testDays = new HashSet<>();
    }

    @Test
    void testSave() {
        // When
        addressService.save(testAddress);

        // Then
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).save(addressCaptor.capture());
        assertEquals(testAddress, addressCaptor.getValue());
    }

    @Test
    void testGetAddressFromDays() {
        // Given
        Activity activity = new Activity();
        activity.setAddress(testAddress);
        Day day = new Day();
        day.setActivities(Set.of(activity));
        testDays.add(day);

        // When
        Set<Long> result = addressService.getAddressFromDays(testDays);

        // Then
        assertEquals(Set.of(1L), result);
    }

    @Test
    void testExists() {
        // Given
        Long addressId = 1L;
        when(addressRepository.existsById(addressId)).thenReturn(true);

        // When
        boolean exists = addressService.exists(addressId);

        // Then
        assertTrue(exists);
        verify(addressRepository).existsById(addressId);
    }

    @Test
    void testDeleteAllByAddressIdIn() {
        // Given
        Set<Long> addressIds = new HashSet<>();
        addressIds.add(1L);
        when(addressRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.existsById(2L)).thenReturn(false);

        // When
        addressService.deleteAllByAddressIdIn(addressIds);

        // Then
        verify(addressRepository).deleteAllByAddressIdIn(Set.of(1L));
    }
}