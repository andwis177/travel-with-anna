package com.andwis.travel_with_anna.trip.day.activity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Set<Activity> findByDayDayIdOrderByBeginTimeAsc(Long dayId);
    int countActivitiesByAddress_AddressId(Long addressId);
}
