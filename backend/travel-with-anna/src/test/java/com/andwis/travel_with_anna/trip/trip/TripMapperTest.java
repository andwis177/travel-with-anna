package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Trip Mapper Tests")
class TripMapperTest {

    @Test
    void testToTripRequestWithDays() {
        // Given
        Backpack backpack = Backpack.builder().backpackId(1L).build();
        Budget budget = Budget.builder().budgetId(1L).build();
        Day day1 = Day.builder().date(LocalDate.of(2024, 9, 1)).build();
        Day day2 = Day.builder().date(LocalDate.of(2024, 9, 5)).build();
        Trip trip = Trip.builder()
                .tripId(1L)
                .tripName("Vacation")
                .days(Set.of(day1, day2))
                .backpack(backpack)
                .budget(budget)
                .build();

        // When
        TripResponse tripRequest = TripMapper.toTripResponse(trip);

        // Then
        assertNotNull(tripRequest);
        assertEquals(1L, tripRequest.tripId());
        assertEquals("Vacation", tripRequest.tripName());
        assertEquals(LocalDate.of(2024, 9, 1), tripRequest.startDate());
        assertEquals(LocalDate.of(2024, 9, 5), tripRequest.endDate());
        assertEquals(2, tripRequest.amountOfDays());
        assertEquals(1L, tripRequest.backpackId());
        assertEquals(1L, tripRequest.budgetId());
    }

    @Test
    void testToTripRequestWithoutDays() {
        // Given
        Backpack backpack = Backpack.builder().backpackId(1L).build();
        Budget budget = Budget.builder().budgetId(1L).build();
        Trip trip = Trip.builder()
                .tripId(1L)
                .tripName("Vacation")
                .days(null)
                .backpack(backpack)
                .budget(budget)
                .build();

        // When
        TripResponse tripRequest = TripMapper.toTripResponse(trip);

        // Then
        assertNotNull(tripRequest);
        assertEquals(1L, tripRequest.tripId());
        assertEquals("Vacation", tripRequest.tripName());
        assertNull(tripRequest.startDate());
        assertNull(tripRequest.endDate());
        assertEquals(0, tripRequest.amountOfDays());
        assertEquals(1L, tripRequest.backpackId());
        assertEquals(1L, tripRequest.budgetId());
    }

    @Test
    void testToTripRequestWithSingleDay() {
        // Given
        Backpack backpack = Backpack.builder().backpackId(1L).build();
        Budget budget = Budget.builder().budgetId(1L).build();
        Day singleDay = Day.builder().date(LocalDate.of(2024, 9, 1)).build();
        Trip trip = Trip.builder()
                .tripId(1L)
                .tripName("Vacation")
                .days(Set.of(singleDay))
                .backpack(backpack)
                .budget(budget)
                .build();

        // When
        TripResponse tripRequest = TripMapper.toTripResponse(trip);

        // Then
        assertNotNull(tripRequest);
        assertEquals(1L, tripRequest.tripId());
        assertEquals("Vacation", tripRequest.tripName());
        assertEquals(LocalDate.of(2024, 9, 1), tripRequest.startDate());
        assertEquals(LocalDate.of(2024, 9, 1), tripRequest.endDate());
        assertEquals(1, tripRequest.amountOfDays());
        assertEquals(1L, tripRequest.backpackId());
        assertEquals(1L, tripRequest.budgetId());
    }
}