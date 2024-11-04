package com.andwis.travel_with_anna.address;

import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public void save(Address address) {
        addressRepository.save(address);
    }

    public Set<Long> getAddressFromDays(@NotNull Set<Day> days) {
        return days.stream().map(Day::getActivities)
                .flatMap(Set::stream)
                .map(Activity::getAddress).map(Address::getAddressId).collect(Collectors.toSet());
    }

    public boolean exists(Long addressId) {
        return addressRepository.existsById(addressId);
    }

    public void deleteAllByAddressIdIn(Set<Long> addressIds) {
        addressIds = addressIds.stream().filter(
                this::exists).collect(Collectors.toSet());
        addressRepository.deleteAllByAddressIdIn(addressIds);
    }
}
