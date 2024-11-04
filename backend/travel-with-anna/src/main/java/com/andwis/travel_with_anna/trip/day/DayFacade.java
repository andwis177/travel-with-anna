package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DayFacade {
    private final DayService dayService;
    private final ActivityService activityService;

    public void addDay(DayAddDeleteRequest request) {
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

    public void changeDayDate(DayRequest request) {
        dayService.changeDayDate(request);
    }

    public void deleteDay(DayAddDeleteRequest request) {
        dayService.deleteFirstOrLastDay(request, activityService::deleteDayActivities);
    }
}
