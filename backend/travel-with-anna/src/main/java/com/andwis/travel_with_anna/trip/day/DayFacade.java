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

    public void addDay(DayAddDeleteRequest request, UserDetails connectedUser) {
        dayService.addDay(request, connectedUser);
    }

    public DayResponse getDayById(Long dayId, UserDetails connectedUser) {
        return dayService.getDayById(dayId, connectedUser);
    }

    public List<DayResponse> getDays(Long tripId, UserDetails connectedUser) {
        return dayService.getDays(tripId, connectedUser);
    }

    public void generateDays(DayGeneratorRequest request, UserDetails connectedUser) {
        dayService.generateDays(request,connectedUser);
    }

    public void deleteDay(DayAddDeleteRequest request, UserDetails connectedUser) {
        dayService.deleteFirstOrLastDay(request, activityService::deleteDayActivities, connectedUser);
    }
}
