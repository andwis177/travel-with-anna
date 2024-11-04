package com.andwis.travel_with_anna.address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AddressRepository extends JpaRepository<Address, Long> {
    void deleteAllByAddressIdIn(Set<Long> addressIds);
}
