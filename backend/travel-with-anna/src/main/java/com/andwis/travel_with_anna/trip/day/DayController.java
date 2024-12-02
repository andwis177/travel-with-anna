package com.andwis.travel_with_anna.trip.day;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/add")
    public ResponseEntity<Void> addDay(
            @RequestBody DayAddDeleteRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.addDay(request, connectedUser);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{dayId}")
    public ResponseEntity<DayResponse> getDayById(
            @PathVariable("dayId") Long dayId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        DayResponse dayResponse = facade.getDayById(dayId, connectedUser);
        return ResponseEntity.ok(dayResponse);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<DayResponse>> getDays(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        ResponseEntity<List<DayResponse>> response = ResponseEntity.ok(facade.getDays(tripId, connectedUser));
        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping("/generate")
    public ResponseEntity<Void> generateDays(
            @RequestBody @Valid DayGeneratorRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.generateDays(request, connectedUser);
        return ResponseEntity.accepted().build();
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteDay(
            @RequestBody @Valid DayAddDeleteRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.deleteDay(request, connectedUser);
        return ResponseEntity.noContent().build();
    }
}
