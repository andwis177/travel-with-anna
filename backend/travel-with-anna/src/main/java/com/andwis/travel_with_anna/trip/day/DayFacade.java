package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DayFacade {

    private final DayService dayService;
    private final ActivityService activityService;

    public void addDay(Long tripId, boolean isFirst, UserDetails connectedUser) {
        dayService.addDay(tripId, isFirst, connectedUser);
    }

    public DayResponse fetchDayById(Long dayId, UserDetails connectedUser) {
        return dayService.getDayById(dayId, connectedUser);
    }

    public List<DayResponse> fetchAllDaysByTripId(Long tripId, UserDetails connectedUser) {
        return dayService.getDays(tripId, connectedUser);
    }

    public void generateDays(DayGeneratorRequest request, UserDetails connectedUser) {
        dayService.generateDays(request,connectedUser);
    }

    public void deleteDayWithActivities(Long tripId, boolean isFirst, UserDetails connectedUser) {
        dayService.deleteFirstOrLastDay(tripId, isFirst, activityService::deleteDayActivities, connectedUser);
    }
}
