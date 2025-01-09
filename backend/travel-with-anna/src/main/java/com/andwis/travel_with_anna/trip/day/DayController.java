package com.andwis.travel_with_anna.trip.day;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("day")
@RequiredArgsConstructor
@Tag(name = "Day")
public class DayController {

    private final DayFacade facade;

    @PostMapping("/add/{tripId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDay(
            @PathVariable("tripId") Long tripId, @RequestParam boolean isFirst,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.addDay(tripId, isFirst, connectedUser);
    }

    @GetMapping("/{dayId}")
    public ResponseEntity<DayResponse> getDayById(
            @PathVariable("dayId") Long dayId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.fetchDayById(dayId, connectedUser));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<DayResponse>> getDays(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.fetchAllDaysByTripId(tripId, connectedUser));
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateDays(
            @RequestBody @Valid DayGeneratorRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.generateDays(request, connectedUser);
    }

    @DeleteMapping("/{tripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDay(@PathVariable("tripId") Long tripId, @RequestParam boolean isFirst,
                          @AuthenticationPrincipal UserDetails connectedUser) {
        facade.deleteDayWithActivities(tripId, isFirst, connectedUser);
    }
}
