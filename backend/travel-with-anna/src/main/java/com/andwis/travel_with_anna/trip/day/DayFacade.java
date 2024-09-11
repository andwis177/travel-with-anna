package com.andwis.travel_with_anna.trip.day;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DayFacade {
    private final DayService dayService;

    public void saveDay(Day day) {
        dayService.saveDay(day);
    }
}
