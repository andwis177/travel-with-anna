package com.andwis.travel_with_anna.trip.day.activity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByDayDayIdOrderByBeginTimeAsc(Long dayId);
    Set<Activity> findAllByActivityIdIn(Set<Long> activityIds);
}
