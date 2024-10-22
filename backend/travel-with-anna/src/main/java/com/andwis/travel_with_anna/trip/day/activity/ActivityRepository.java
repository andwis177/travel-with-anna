package com.andwis.travel_with_anna.trip.day.activity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByDayDayIdOrderByBeginTimeAsc(Long dayId);
}
