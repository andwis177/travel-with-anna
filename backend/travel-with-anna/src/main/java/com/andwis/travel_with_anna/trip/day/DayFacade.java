package com.andwis.travel_with_anna.trip.day;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DayFacade {
    private final DayService dayService;

    public void createDay(DayRequest request) {
        dayService.createDay(request);
    }

    public void addDay(DayAddRequest request) {
        dayService.addDay(request);
    }

    public DayResponse getDayById(Long dayId) {
        return dayService.getDayById(dayId);
    }

    public List<DayResponse> getDays(Long tripId) {
        return dayService.getDays(tripId);
    }

    public void generateDays(DayGeneratorRequest request) {
        dayService.generateDays(request);
    }

    public void deleteDay(Long dayId) {
        dayService.deleteDay(dayId);
    }
}
