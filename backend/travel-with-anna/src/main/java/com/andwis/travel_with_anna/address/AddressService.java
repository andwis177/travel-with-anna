package com.andwis.travel_with_anna.address;

import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public Address save(Address address) {
        return addressRepository.save(address);
    }

    public Set<Long> extractAddressIdsFromDays(@NotNull Set<Day> days) {
        return days.stream()
                .flatMap(day -> extractAddressIdsFromActivities(day.getActivities()).stream())
                .collect(Collectors.toSet());
    }

    public boolean exists(Long addressId) {
        return addressRepository.existsById(addressId);
    }

    @Transactional
    public void deleteExistingAddressesByIds(Set<Long> addressIds) {
        Set<Long> existingIds = filterExistingAddressIds(addressIds);
        addressRepository.deleteAllByAddressIdIn(existingIds);
    }

    private Set<Long> extractAddressIdsFromActivities(@NotNull Set<Activity> activities) {
        return activities.stream()
                .map(Activity::getAddress)
                .filter(Objects::nonNull)
                .map(Address::getAddressId)
                .collect(Collectors.toSet());
    }

    private Set<Long> filterExistingAddressIds(@NotNull Set<Long> addressIds) {
        return addressIds.stream()
                .filter(this::exists)
                .collect(Collectors.toSet());
    }
}