package com.andwis.travel_with_anna.trip.day;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DayService {
    private final DayRepository dayRepository;

    public void saveDay(Day day) {
        dayRepository.save(day);
    }
}
