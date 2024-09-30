package com.andwis.travel_with_anna.trip.day.activity.type.travel;

import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.andwis.travel_with_anna.trip.day.activity.badge.Badge.TRAVELING;
import static com.andwis.travel_with_anna.trip.day.activity.type.travel.TravelType.CAR;

@Service
@RequiredArgsConstructor
public class TravelerService {
    private final TravelerRepository travelerRepository;
    private final DayRepository dayRepository;

    public void saveTraveler() {
        Day day = Day.builder()
                .date(LocalDate.now())
                .build();

        Traveler traveler = Traveler.builder()
                .time(LocalTime.now())
                .badge(TRAVELING.getBadge())
                .travelType(CAR.getType())
                .build();


        day.addActivity(traveler);
        dayRepository.save(day);
    }
}
