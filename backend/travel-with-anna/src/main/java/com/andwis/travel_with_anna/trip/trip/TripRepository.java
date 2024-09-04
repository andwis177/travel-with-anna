package com.andwis.travel_with_anna.trip.trip;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t WHERE t.owner.userId = :userId")
    Page<Trip> findTripsByOwnerId(@Param("userId") Long userId, Pageable pageable);
}
